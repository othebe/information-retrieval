# What is this?
This is a library that provides a mechanism to index and search for data. Results are provided by finding documents that match a Boolean search, and then ordering them by a scoring scheme.

# Tell me more.
Ok.

## Indexing and Zones
The concept of data and metadata is handled by the concept of a ```Zone```. Data for a ```Record``` is indexed by zone, each representing a separate field. For example, when indexing an email, the body could be indexed as a ```TextZone```, while the timestamp could be indexed under a ```Long``` zone. This approach means that there is no distinction between data and metadata, and all zones are weighted equally to support filtering by Zones as default.

A [Zone](https://github.com/othebe/information-retrieval/blob/master/src/main/java/core/search/Zone.java) is identified by a ```String name``` and indexes data of type ```T```, and must handle indexing and searching via ```void index(T data, DocId docId)``` and ```List<Pair<DocId, Double>> match(T query)```. A default [TextZone](https://github.com/othebe/information-retrieval/blob/master/src/main/java/core/search/zones/textzone/TextZone.java) is provided for handling full-text data, but additional zones can be defined easily such as [TimestampZone](https://github.com/othebe/information-retrieval/blob/master/src/main/java/core/search/zones/textzone/TextZone.java) to index and search by ```Long timestamp```.

An [Indexer](https://github.com/othebe/information-retrieval/blob/master/src/main/java/core/search/Indexer.java) defines all the zones indexed, and allows a ```Record``` to be added according to zones. Since every zone has a name, a record can be represented as a map of zone names to data.

## Parsing Data
A parsing operation is defined as one that takes input of type ```I```, runs it through some pre-processors, tokenizes it to a ```List<O>```, then runs post-processors on every element. This operation is defined within a [Parser](https://github.com/othebe/information-retrieval/blob/master/src/main/java/core/parser/Parser.kt) object. ```TextZone``` uses two different parsers, one for parsing text data in  a record, and another for parsing a query.

### Parsing Text Records
```StandardTextParser``` offers some common text processing to parse text, such as filtering out punctuation, digits, stopwords, applying stemming, and lowercasing text.

### Parsing Text Queries
```StandardQueryParser``` runs its own gamut of processors, and is used to convert a String of terms into a Boolean query that can be used to match against an index.

## Matching on Boolean Queries
A ```Query``` represents a query term, and can be used to find documents that match the given terms. Query terms can be expressed as a combination of [And](https://github.com/othebe/information-retrieval/blob/master/src/main/java/core/query/node/And.java), [Or](https://github.com/othebe/information-retrieval/blob/master/src/main/java/core/query/node/Or.java), and [Phrase](https://github.com/othebe/information-retrieval/blob/master/src/main/java/core/query/node/Phrase.java) nodes. The ```StandardQueryParser``` parses a String into a ```Query``` tree.

## Scoring Matches
A ```Zone``` returns an ordered list of ```<DocId, Double>``` pairs, sorted by descending Double scores. How this is scored is up to the implementation of the Zone object, but TextZone uses a Tf-Idf scoring mechanism by default. A ```IPositionalIndex``` implementation is used to represent the document as a vector, with each component representing a Tf-Idf score. The final score for the TextZone is the cosine similarity between the query and document vector. The takeaway behind this approach is that this should extend to any scoring mechanism such as BM-25, LPS, boosting etc. as long as we are able to vectorize the document.

# Enough exposition, show me an example!
Ok.

In this example, we index/search some text that includes a ```Long timestamp```. The text is indexed by a TextZone, and we will define a ```Zone``` for storing the timestamp. 

We define our zone names:

    private val ZONE_NAME_MESSAGE = "message"
    private val ZONE_NAME_TIMESTAMP = "timestamp"

Define a ```Zone``` for the timestamp. In this example, we return a score of 1 if a document is found, else 0.

    class TimestampZone : Zone<Long>(ZONE_NAME_TIMESTAMP) {
        private var index: Map<Long, DocId>

        init {
            this.index = HashMap()
        }

        override fun index(data: Long, docId: DocId) {
            index = index.plus(data to docId)
        }

        override fun match(query: Long): List<Pair<DocId, Double>> {
            val matches = ArrayList<Pair<DocId, Double>>()

            if (index.containsKey(query)) {
                val docId: DocId = index[query] ?: throw RuntimeException("Invalid test")
                val result = Pair(docId, 1.0)
                matches.add(result)
            }

            return matches
        }
    }
    
Build an ```Indexer``` from the two zones.

    val indexer = Indexer.Builder()
                .addZone(messageZone)
                .addZone(timestampZone)
                .build()
                
Build a ```Search``` object that uses the zones we defined.

    val search = Search.fromIndexer(indexer)
    
Let's index some data (finally!). Here, we create a record that holds data for our two zones.

    val recordDataEggsAndSpam = mapOf(
                ZONE_NAME_MESSAGE to "green eggs and spam",
                ZONE_NAME_TIMESTAMP to 45L)
    val recordDataEggsAndHam = mapOf(
                ZONE_NAME_MESSAGE to "green eggs and ham",
                ZONE_NAME_TIMESTAMP to 30L)
                
    indexer.add(Record(DocId(1), recordData))
    indexer.add(Record(DocId(2), recordData))
                
And finally, we search for it!

    val results = search.search(mapOf(
                ZONE_NAME_MESSAGE to "eggs",
                ZONE_NAME_TIMESTAMP to 45L), 5 /** NumResults */)  // => DocId(1)
                
A full example using a sample set of Wikipedia articles can be found in [WikipediaSearchExample](https://github.com/othebe/information-retrieval/blob/master/src/main/java/example/WikipediaSearchExample.java)

# Recommended reading
[Stanford's NLP book](https://nlp.stanford.edu/IR-book/html/htmledition/contents-1.html) is an excellent reference to Information Retrieval and was the basis for this project, and is highly recommended.

The [ElasticSearch Definitive Guide](https://www.elastic.co/guide/en/elasticsearch/guide/current/index.html) is also a good reference.
