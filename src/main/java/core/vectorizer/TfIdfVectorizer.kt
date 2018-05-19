package core.vectorizer

import core.DocId
import core.vectorizer.VectorUtils.getUnitVector
import core.zones.textzone.positionalindex.IPositionalIndex

/** TODO: Ignore TF since we will be doing a comparison */
class TfIdfVectorizer {
    fun vectorize(terms: Array<String>, index: IPositionalIndex): SparseVector<Double> {
        val termSet: Set<String> = terms.toSet()

        val keys = index.keys
        val vector = SparseVector<Double>()

        for (i in keys.indices) {
            val key = keys[i]
            if (termSet.contains(key)) {
                val postings = index.get(key)
                val tf = postings.fold(0) { acc, posting -> acc + posting.positions.size }
                val idf = Math.log(index.numDocuments * 1.0 / postings.size)

                vector.put(i.toLong(), tf * idf)
            }
        }

        return getUnitVector(vector)
    }

    fun vectorize(docId: DocId, index: IPositionalIndex): SparseVector<Double> {
        val keys = index.keys
        val vector = SparseVector<Double>()

        for (i in keys.indices) {
            val key = keys[i]
            val postings = index.get(key)

            with (postings.filter { posting -> posting.docId.equals(docId) }.firstOrNull()) {
                val tf = this?.positions?.size ?: 0
                val idf = Math.log(index.numDocuments * 1.0 / postings.size)

                if (tf != 0) {
                    vector.put(i.toLong(), tf * idf)
                }
            }
        }

        return getUnitVector(vector)
    }

    private fun DoubleArray.toUnitVector(): DoubleArray {
        val magnitude: Double = Math.sqrt(fold(0.0) { acc, component -> acc + (component * component) })
        if (magnitude.equals(0.0)) return this

        for (i in indices) {
            this[i] = this[i] / magnitude
        }

        return this
    }
}
