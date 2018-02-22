package core.parser.processor

interface IProcessor<T> {
    fun process(`in`: T): T
}
