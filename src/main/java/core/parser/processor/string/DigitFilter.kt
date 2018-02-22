package core.parser.processor.string

import core.parser.processor.IProcessor
import org.apache.commons.lang3.math.NumberUtils

class DigitFilter : IProcessor<String> {
    override fun process(`in`: String): String {
        if (NumberUtils.isDigits(`in`))
            return ""
        else
            return `in`
    }
}
