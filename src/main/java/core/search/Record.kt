package core.search

import core.DocId

data class Record(val docId: DocId<*>, val dataByZoneName: Map<String, Any>)
