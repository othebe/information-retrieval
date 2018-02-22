package core.search

import core.DocId
import javafx.util.Pair

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
        return query.entries
                .map {
                    zonesByName[it.key]?.match(it.value)
                }
                .reduce { acc, matches ->
                    if (matches == null || acc == null) {
                        emptyList()
                    } else {
                        val constraints: Map<DocId, Double> = acc.associate { it -> it.key to it.value }
                        matches
                                .filter { constraints.contains(it.key) }
                                .map { it -> Pair(it.key, it.value * (constraints[it.key] ?: 0.0)) }
                    }
                }
                .orEmpty()
                .sortedByDescending { it -> it.value }
                .take(limit)
    }
}
