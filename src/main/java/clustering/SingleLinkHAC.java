package clustering;

import core.DocId;
import core.zones.Zone;
import kotlin.Pair;
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
        int numDocuments = indexer.getDocIds().size();

        // Initialize similarity table.
        Double[][] similarityTable = new Double[numDocuments][numDocuments];
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
                    similarityTable[i][j] = similarity;
                    similarityTable[j][i] = similarity;
                }
            }
        }

        int iteration = 0;
        Set<Integer> mergedClusters = new HashSet<>();
        boolean isClustering = true;
        while (isClustering) {
            iteration++;

            Set<Integer> activeClusters = new HashSet<>();
            List<Pair<Integer, Integer>> activeMerges = new ArrayList<>();
            Pair<Integer, Integer> pair;

            // Find merge-able clusters.
            double maxSimilarity = -1;
            while ((pair = findMaxSimilarityPair(similarityTable, activeClusters, mergedClusters)) != null) {
                int ndx1 = pair.getFirst();
                int ndx2 = pair.getSecond();

                if (similarityTable[ndx1][ndx2] > minSimilarity) {
                    activeClusters.add(ndx1);
                    activeClusters.add(ndx2);

                    activeMerges.add(pair);
                    if (maxSimilarity < 0) maxSimilarity = similarityTable[ndx1][ndx2];
                } else {
                    break;
                }
            }

            // Update similarity table.
            for (Pair<Integer, Integer> mergePair : activeMerges) {
                int i = mergePair.getFirst();
                int j = mergePair.getSecond();

                for (int ndx = 0; ndx < numDocuments; ndx++) {
                    double updatedSimilarity = Math.max(similarityTable[i][ndx], similarityTable[j][ndx]);
                    similarityTable[i][ndx] = updatedSimilarity;
                    similarityTable[ndx][i] = updatedSimilarity;
                    similarityTable[j][ndx] = updatedSimilarity;
                    similarityTable[ndx][j] = updatedSimilarity;
                }
            }

            // Merge active clusters.
            for (Pair<Integer, Integer> mergePair : activeMerges) {
                int sourceNdx = mergePair.getFirst();
                int targetNdx = mergePair.getSecond();

                DocumentCluster sourceCluster = documentClusterMap.get(sourceNdx);
                DocumentCluster targetCluster = documentClusterMap.get(targetNdx);

                targetCluster.merge(sourceCluster);

                documentClusterMap.remove(sourceNdx);

                mergedClusters.add(sourceNdx);
            }

            isClustering = !activeClusters.isEmpty();

            System.out.printf("Iter %d: Max score: %f\n", iteration, maxSimilarity);
        }

        return new HashSet<>(documentClusterMap.values());
    }

    private static Pair<Integer, Integer> findMaxSimilarityPair(Double[][] similarityTable, Set<Integer> ignoredIndices, Set<Integer> mergedIndices) {
        int maxI = -1;
        int maxJ = -1;
        double maxSimilarity = 0;

        for (int i = 0; i < similarityTable.length; i++) {
            for (int j = i + 1; j < similarityTable.length; j++) {
                if (ignoredIndices.contains(i) || ignoredIndices.contains(j)) {
                    continue;
                }

                if (mergedIndices.contains(i) || mergedIndices.contains(j)) {
                    continue;
                }

                if (similarityTable[i][j] > maxSimilarity) {
                    maxSimilarity = similarityTable[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        if (maxI >= 0 && maxJ >= 0) {
            return new Pair<>(maxI, maxJ);
        } else {
            return null;
        }
    }
}
