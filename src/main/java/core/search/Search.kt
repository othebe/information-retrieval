package core.search

import core.DocId

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
                        val constraints: Map<DocId, Double> = acc.associate { it -> it.first to it.second }
                        matches
                                .filter { constraints.contains(it.first) }
                                .map { it -> Pair(it.first, it.second * (constraints[it.first] ?: 0.0)) }
                    }
                }
                .orEmpty()
                .sortedByDescending { it -> it.second }
                .take(limit)
    }
}
