package search

import core.DocId
import core.Record
import core.zones.Zone
import core.query.parser.StandardQueryParser
import core.zones.textzone.StandardTextParser
import core.zones.textzone.TextZone
import core.zones.textzone.positionalindex.InMemoryPositionalIndex
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.ArrayList
import java.util.HashMap
import kotlin.test.assertEquals

class SearchTest {

    private lateinit var search: Search
    private lateinit var indexer: Indexer

    companion object {
        private val ZONE_NAME_MESSAGE = "message"
        private val ZONE_NAME_TIMESTAMP = "timestamp"
    }

    @BeforeEach
    fun setup() {
        val messageZone = TextZone(ZONE_NAME_MESSAGE, InMemoryPositionalIndex(), StandardQueryParser(), StandardTextParser())
        val timestampZone = TimestampZone()

        indexer = Indexer.Builder()
                .addZone(messageZone)
                .addZone(timestampZone)
                .build()

        search = Search.fromIndexer(indexer)
    }

    @Test
    fun shouldFindMessageWithoutFilter() {
        // ARRANGE
        val recordData1 = mapOf(ZONE_NAME_MESSAGE to "green eggs and ham")
        val recordData2 = mapOf(
                ZONE_NAME_MESSAGE to "jellyfish and seabiscuits",
                ZONE_NAME_TIMESTAMP to 45L)

        indexer.add(Record(DocId(1), recordData1))
        indexer.add(Record(DocId(2), recordData2))

        // ACT
        val results = search.search(mapOf(ZONE_NAME_MESSAGE to "Jellyfish  seabiscuit"), 5)

        // ASSERT
        assertEquals(1, results.size)
        assertEquals(DocId(2), results.get(0).first)
    }

    @Test
    fun shouldFindMessageWithFilter() {
        // ARRANGE
        val recordData1 = mapOf(
                ZONE_NAME_MESSAGE to "green eggs and ham",
                ZONE_NAME_TIMESTAMP to 12345L)

        val recordData2 = mapOf(
                ZONE_NAME_MESSAGE to "green eggs and spam",
                ZONE_NAME_TIMESTAMP to 45L)

        indexer.add(Record(DocId(1), recordData1))
        indexer.add(Record(DocId(2), recordData2))

        // ACT
        val results = search.search(mapOf(
                ZONE_NAME_MESSAGE to "eggs",
                ZONE_NAME_TIMESTAMP to 45L), 5)

        // ASSERT
        assertEquals(1, results.size)
        assertEquals(DocId(2), results.get(0).first)
    }

    private inner class TimestampZone : Zone<Long>(ZONE_NAME_TIMESTAMP) {
        private var index: Map<Long, DocId>

        init {
            this.index = HashMap()
        }

        override fun index(data: Long, docId: DocId) {
            index = index.plus(data to docId)
        }

        override fun matchQuery(query: Long): List<Pair<DocId, Double>> {
            val matches = ArrayList<Pair<DocId, Double>>()

            if (index.containsKey(query)) {
                val docId: DocId = index[query] ?: throw RuntimeException("Invalid test")
                val result = Pair(docId, 1.0)
                matches.add(result)
            }

            return matches
        }

        override fun matchData(data: Long?): List<Pair<DocId, Double>> {
            return emptyList()
        }
    }
}
