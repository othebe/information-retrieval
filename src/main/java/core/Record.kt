package core

data class Record(val docId: DocId, val dataByZoneName: Map<String, Any>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (docId != other.docId) return false

        return true
    }

    override fun hashCode(): Int {
        return docId.hashCode()
    }
}
