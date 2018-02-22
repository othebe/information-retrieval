package core.parser.processor.string

import core.nlp.Stemmer
import core.parser.processor.IProcessor

class Stemmer : IProcessor<String> {
    override fun process(`in`: String): String {
        return with (Stemmer()) {
            add(`in`)
            stem()
            toString()
        }
    }
}