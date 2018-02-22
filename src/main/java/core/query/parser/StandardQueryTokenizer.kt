package core.query.parser

import core.parser.tokenizer.ITokenizer
import core.query.Query
import core.query.node.And
import core.query.node.And.TOKEN_AND
import core.query.node.Leaf
import core.query.node.Or
import core.query.node.Or.TOKEN_OR
import core.query.node.Phrase
import core.query.node.Phrase.TOKEN_PHRASE

class StandardQueryTokenizer : ITokenizer<String, Query> {
    override fun tokenize(`in`: String): List<Query> {
        return listOf(tokenize(`in`.toCharArray(), 0, `in`))
    }

    private fun tokenize(chars: CharArray, ndx: Int, original: String): Query {
        var ndx = ndx
        var terms = emptyList<Query>()

        var startNdx = ndx
        while (ndx < chars.size) {
            val c = chars[ndx]

            if (TOKEN_PHRASE.equals(c) || TOKEN_AND.equals(c) || TOKEN_OR.equals(c)) {
                val token = original.substring(startNdx, ndx)
                startNdx = ndx + 1

                terms = terms.plus(Leaf(token))
            }

            if (TOKEN_AND.equals(c)) {
                return And(
                        terms.reduceRight { acc, query -> Phrase(acc, query) },
                        tokenize(chars, ndx + 1, original))
            }

            if (TOKEN_OR.equals(c)) {
                return Or(
                        terms.reduceRight { acc, query -> Phrase(acc, query) },
                        tokenize(chars, ndx + 1, original))
            }

            ndx++
        }

        terms = terms.plus(Leaf(original.substring(startNdx, ndx)))
        return if (terms.size == 1)
            terms[0]
        else
            terms.reduceRight { acc, query -> Phrase(acc, query) }

    }
}
