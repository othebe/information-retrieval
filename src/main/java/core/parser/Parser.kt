package core.parser

import core.parser.processor.IProcessor
import core.parser.tokenizer.ITokenizer

open class Parser<I, O>(private val preProcessors: Array<IProcessor<I>>,
                        private val tokenizer: ITokenizer<I, O>,
                        private val postProcessors: Array<IProcessor<O>>) {

    open fun parse(`in`: I): List<O> {
        val preProcessed = preProcessors
                .fold(`in`) { acc, processor -> processor.process(acc) }

        return tokenizer.tokenize(preProcessed)
                .map {
                    postProcessors.fold(it) { acc, processor -> processor.process(acc) }
                }
    }
}
