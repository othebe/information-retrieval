package core.parser.processor.string;

import core.parser.processor.IProcessor;

public class Lowercaser implements IProcessor<String> {
    @Override
    public String process(String in) {
        return in.toLowerCase();
    }
}
