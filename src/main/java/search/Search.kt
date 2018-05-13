package search

import core.DocId
import core.zones.Zone

class Search(zones: Set<Zone<Any>>) {
    private val zonesByName: Map<String, Zone<Any>>

    companion object {
        fun fromIndexer(indexer: Indexer): Search {
            return Search(indexer.zones)
        }
    }

    init {
        zonesByName = zones.associateBy { it -> it.name }
    }

    fun search(query: Map<String, Any>, limit: Int): List<Pair<DocId, Double>> {
        val matchesByZone = query.mapValues { zonesByName[it.key]?.matchQuery(it.value).orEmpty() }

        val isInZone = { docId: DocId, zone: List<Pair<DocId, Double>> ->
            zone.map { it.first }.contains(docId)
        }

        val isInAllZones = { docId: DocId, zones: Map<String, List<Pair<DocId, Double>>> ->
            zones.values.fold(true) { isFound, zoneResults ->
                isFound && isInZone(docId, zoneResults)
            }
        }

        return matchesByZone.values
                .flatMap { it }
                .filter { isInAllZones(it.first, matchesByZone) }
                .groupBy({ it.first }, { it.second })
                .map { Pair(it.key, it.value.fold(1.0) { acc, score -> acc * score }) }
                .sortedByDescending { it.second }
                .take(limit)
    }
}
