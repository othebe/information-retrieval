package core.parser.processor.string;

import core.parser.processor.IProcessor;

public class DigitFilter implements IProcessor<String> {
    @Override
    public String process(String in) {
        String cleaned = in.trim().replaceAll("\\W+", "");
        return (cleaned.matches("\\d+")) ? "" : in;
    }
}
