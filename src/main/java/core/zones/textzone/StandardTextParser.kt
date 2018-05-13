package core.zones.textzone

import core.parser.Parser
import core.parser.processor.*
import core.parser.processor.string.DigitFilter
import core.parser.processor.string.Lowercaser
import core.parser.processor.string.PunctuationProcessor
import core.parser.processor.string.Stemmer
import core.parser.processor.string.StopWordFilter
import core.parser.tokenizer.WhitespaceTokenizer

open class StandardTextParser : Parser<String, String>(
        arrayOf<IProcessor<String>>(PunctuationProcessor(null), Lowercaser()),
        WhitespaceTokenizer(),
        arrayOf<IProcessor<String>>(DigitFilter(), StopWordFilter(), Stemmer())) {

    override fun parse(`in`: String): List<String> {
        return super.parse(`in`)
                .filter { it.isNotEmpty() }
    }
}
