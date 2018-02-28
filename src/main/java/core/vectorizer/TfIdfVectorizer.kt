package core.vectorizer

import core.DocId
import core.search.zones.textzone.positionalindex.IPositionalIndex

/** TODO: Ignore TF since we will be doing a comparison */
class TfIdfVectorizer {
    fun vectorize(terms: Array<String>, index: IPositionalIndex): Array<Double> {
        val termSet: Set<String> = terms.toSet()

        val keys = index.keys
        val vector: Array<Double> = Array<Double>(keys.size, { 0.0 })

        for (i in keys.indices) {
            val key = keys[i]
            if (termSet.contains(key)) {
                val postings = index.get(key)
                val tf = postings.fold(0) { acc, posting -> acc + posting.positions.size }
                val idf = Math.log(index.numDocuments * 1.0 / postings.size)

                vector[i] = tf * idf
            }
        }

        return vector.toUnitVector()
    }

    fun vectorize(docId: DocId<*>, index: IPositionalIndex): Array<Double> {
        val keys = index.keys
        val vector: Array<Double> = Array<Double>(keys.size, { 0.0 })

        for (i in keys.indices) {
            val key = keys[i]
            val postings = index.get(key)

            with (postings.filter { posting -> posting.docId.equals(docId) }.firstOrNull()) {
                val tf = this?.positions?.size ?: 0
                val idf = Math.log(index.numDocuments * 1.0 / postings.size)

                vector[i] = tf * idf
            }
        }

        return vector.toUnitVector()
    }

    private fun Array<Double>.toUnitVector(): Array<Double> {
        val magnitude: Double = Math.sqrt(fold(0.0) { acc, component -> acc + (component * component) })
        if (magnitude.equals(0.0)) return this

        for (i in indices) {
            this[i] = this[i] / magnitude
        }

        return this
    }
}
