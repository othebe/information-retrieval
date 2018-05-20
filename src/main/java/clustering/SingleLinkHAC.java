package clustering;

import core.DocId;
import core.zones.Zone;
import core.Indexer;

import java.util.*;

import static core.vectorizer.VectorUtils.getCosineAngle;

public class SingleLinkHAC {
    public static SingleLinkHAC fromIndexer(Indexer indexer) {
        return new SingleLinkHAC(indexer);
    }

    private final Indexer indexer;

    private SingleLinkHAC(Indexer indexer) {
        this.indexer = indexer;
    }

    public Set<DocumentCluster> buildCluster(double minSimilarity) {
        return buildCluster(minSimilarity, -1);
    }

    public Set<DocumentCluster> buildCluster(double minSimilarity, int iterations) {
        final int numDocuments = indexer.getDocIds().size();

        PriorityQueue<PairSimilarity> similarityQueue = new PriorityQueue<>(numDocuments * numDocuments, new Comparator<PairSimilarity>() {
            @Override
            public int compare(PairSimilarity o1, PairSimilarity o2) {
                return Double.compare(o2.score, o1.score);
            }
        });

        // Initialize similarity table.
        double[][] similarityTable = new double[numDocuments][numDocuments];
        for (int i = 0; i < similarityTable.length; i++) {
            for (int j = 0; j < similarityTable.length; j++) {
                similarityTable[i][j] = 0.0;
            }
        }

        // Build initial cluster map.
        Map<Integer, DocumentCluster> documentClusterMap = new HashMap<>();
        for (int i = 0; i < numDocuments; i++) {
            DocId docId = indexer.getDocIds().get(i);

            DocumentCluster documentCluster = new DocumentCluster();
            documentCluster.add(docId);

            documentClusterMap.put(i, documentCluster);
        }

        // Build similarity table.
        for (int i = 0; i < numDocuments; i++) {
            System.out.printf("Building similarity table: %d/%d\n", i, numDocuments);
            for (int j = i; j < numDocuments; j++) {
                for (Zone zone : indexer.getZones()) {
                    double similarity = (i == j) ? 1.0 : getCosineAngle(
                            zone.getVector(indexer.getDocIds().get(i)),
                            zone.getVector(indexer.getDocIds().get(j)));

                    if (similarity < minSimilarity) similarity = 0;

                    similarityTable[i][j] = similarity;
                    similarityTable[j][i] = similarity;

                    similarityQueue.add(new PairSimilarity(i, j, similarity));

                    if (i == 0) {
                        System.out.printf("Vectorizing: %d/%d\n", j, numDocuments);
                    }
                }
            }
        }

        int iteration = 0;
        Set<Integer> mergedClusters = new HashSet<>();
        boolean isClustering = true;
        while (isClustering) {
            iteration++;

            Set<Integer> activeClusters = new HashSet<>();
            List<PairSimilarity> activeMerges = new ArrayList<>();

            // Find merge-able clusters.
            double maxSimilarity = -1;

            while (!similarityQueue.isEmpty()) {
                PairSimilarity pairSimilarity = similarityQueue.poll();

                if (activeClusters.contains(pairSimilarity.i) || activeClusters.contains(pairSimilarity.j)) continue;
                if (mergedClusters.contains(pairSimilarity.i) || mergedClusters.contains(pairSimilarity.j)) continue;

                if (pairSimilarity.i == pairSimilarity.j) continue;

                if (pairSimilarity.score > minSimilarity) {
                    activeClusters.add(pairSimilarity.i);
                    activeClusters.add(pairSimilarity.j);

                    activeMerges.add(pairSimilarity);

                    if (maxSimilarity < 0) maxSimilarity = pairSimilarity.score;
                }
            }

            // Update similarity table.
            for (PairSimilarity mergePair : activeMerges) {
                int i = mergePair.i;
                int j = mergePair.j;

                for (int ndx = 0; ndx < numDocuments; ndx++) {
                    double updatedSimilarity = Math.max(similarityTable[i][ndx], similarityTable[j][ndx]);
                    similarityTable[i][ndx] = updatedSimilarity;
                    similarityTable[ndx][i] = updatedSimilarity;
                    similarityTable[j][ndx] = updatedSimilarity;
                    similarityTable[ndx][j] = updatedSimilarity;

                    similarityQueue.add(new PairSimilarity(j, ndx, updatedSimilarity));
                }
            }

            // Merge active clusters.
            for (PairSimilarity mergePair : activeMerges) {
                int sourceNdx = mergePair.i;
                int targetNdx = mergePair.j;

                DocumentCluster sourceCluster = documentClusterMap.get(sourceNdx);
                DocumentCluster targetCluster = documentClusterMap.get(targetNdx);

                targetCluster.merge(sourceCluster);

                documentClusterMap.remove(sourceNdx);

                mergedClusters.add(sourceNdx);
            }

            isClustering = iterations != -1 && iteration < iterations && !activeClusters.isEmpty();

            System.out.printf("Iter %d: Max score: %f\n", iteration, maxSimilarity);
        }

        return new HashSet<>(documentClusterMap.values());
    }

    private static class PairSimilarity {
        public int i;
        public int j;
        public double score;

        private PairSimilarity(int i, int j, double score) {
            this.i = i;
            this.j = j;
            this.score = score;
        }
    }
}
