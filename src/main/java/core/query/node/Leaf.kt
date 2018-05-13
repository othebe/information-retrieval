package core.query.node

import core.zones.textzone.positionalindex.IPositionalIndex
import core.zones.textzone.positionalindex.Posting
import core.query.Query

class Leaf(term: String) : Query(term) {
    override fun match(positionalIndex: IPositionalIndex): List<Posting> {
        return positionalIndex.get(term).toList()
    }

    override fun toString(): String {
        return term.toString()
    }
}
