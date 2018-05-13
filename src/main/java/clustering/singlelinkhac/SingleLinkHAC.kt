package clustering.singlelinkhac

import clustering.DocumentCluster
import core.DocId
import core.Record
import core.zones.Zone
import java.util.HashSet
import kotlin.math.max

class SingleLinkHAC private constructor(private val zones: HashSet<Zone<String>>) {
    fun buildCluster(records: List<Record>, minSimilarity: Float): Set<DocumentCluster> {
        // Index.
        val zonesByName = zones.associateBy { it.name }
        for (record in records) {
            record.dataByZoneName.entries.map { zoneData ->
                val zoneToIndex = zonesByName[zoneData.key] ?: throw RuntimeException("Invalid zone index")
                zoneToIndex.index(zoneData.value as String, record.docId)
            }
        }

        // Build similarity table.
        val similarityArray = Array<Array<Double>>(records.size) { Array(records.size, { 0.toDouble() }) }
        for (i in records.indices) {
            System.out.printf("Building similarity: [%d/%d]\n", i, records.size)

            val record = records[i]
            val similarities = record.dataByZoneName.entries
                    .flatMap { zoneData -> zonesByName[zoneData.key]?.matchData(zoneData.value as String) ?: emptyList() }
                    .groupBy ({ it.first }, { it.second })
                    .mapValues { it.value.fold(1.0) { acc, score -> acc * score } }

            for (j in records.indices) {
                similarityArray[i][j] = similarities[records[j].docId] ?: 0.toDouble()
                similarityArray[j][i] = similarities[records[j].docId] ?: 0.toDouble()
            }
        }

        // Build clusters.
        val clusters = mutableMapOf<Int, DocumentCluster>()
        for (i in records.indices) {
            val record = records[i]
            clusters.put(i, DocumentCluster().apply { add(record.docId) })
        }

        var iteration = 0
        var maxSimilarity = 0.toDouble()

        var allowClustering = true
        while (allowClustering) {
            iteration = iteration + 1

            // Find merge-able clusters.
            val activeClusters = mutableSetOf<Int>()
            val merges = mutableListOf<Pair<Int, Int>>()
            for (i in similarityArray.indices) {
                val maxPair = findMaxSimilarity(similarityArray, activeClusters, clusters)

                // No eligible pairs found.
                if (maxPair.first < 0 || maxPair.second < 0) break

                // Minimum threshold not met.
                maxSimilarity = max(similarityArray[maxPair.first][maxPair.second], maxSimilarity)
                if (similarityArray[maxPair.first][maxPair.second] < minSimilarity) break

                merges.add(maxPair)
                activeClusters.add(maxPair.first)
                activeClusters.add(maxPair.second)
            }

            // Update similarity table.
            for (merge in merges) {
                for (i in similarityArray.indices) {
                    similarityArray[merge.first][i] = max(similarityArray[merge.first][i], similarityArray[merge.second][i])
                    similarityArray[i][merge.first] = similarityArray[merge.first][i]

                    similarityArray[merge.second][i] = similarityArray[merge.first][i]
                    similarityArray[i][merge.first] = similarityArray[merge.first][i]
                }
            }

            // Merge clusters.
            for (merge in merges) {
                val clusterIn = clusters[merge.second]
                        ?: throw RuntimeException("Cluster not found! Should not happen.")
                val clusterOut = clusters[merge.first]
                        ?: throw RuntimeException("Cluster not found! Should not happen.")

                clusterOut.merge(clusterIn)

                clusters[merge.second] = clusterOut
            }

            allowClustering = activeClusters.isNotEmpty()

            System.out.printf("Iter %d: Max score: %f\n", iteration, maxSimilarity)
        }

        return clusters.values.toSet()
    }

    private fun findMaxSimilarity(similarities: Array<Array<Double>>, ignoreIndices: Set<Int>, clusters: Map<Int, DocumentCluster>): Pair<Int, Int> {
        var maxValue: Double = 0.toDouble()
        var maxPair = Pair(-1, -1)
        for (i in similarities.indices) {
            for (j in similarities.indices) {
                if (i == j) continue

                if (ignoreIndices.contains(i) || ignoreIndices.contains(j)) continue

                if (clusters[i]?.equals(clusters[j]) ?: true) continue

                if (similarities[i][j] > maxValue) {
                    maxValue = similarities[i][j]
                    maxPair = Pair(i, j)
                }
            }
        }

        return maxPair
    }

    private data class DocumentSimilarity constructor(val docIdA: DocId, val docIdB: DocId, val score: Double)

    class Builder {
        private val zones = HashSet<Zone<String>>()

        fun addZone(zone: Zone<String>): Builder {
            this.zones.add(zone)
            return this
        }

        fun build(): SingleLinkHAC {
            return SingleLinkHAC(zones)
        }
    }
}
