package core.parser.processor.string;

import core.parser.processor.IProcessor;

public class PunctuationProcessor implements IProcessor<String> {
    private String replaceWithSpaceRegex = ",;=\\-\\+*/.";
    private String replaceWithBlankRegex = "^\\w\\s";

    public PunctuationProcessor(String[] allowed) {
        if (allowed != null) {
            for (String matcher : allowed) {
                replaceWithSpaceRegex = replaceWithSpaceRegex.replaceAll(matcher, "");
                replaceWithBlankRegex = replaceWithBlankRegex + matcher;
            }
        }
    }

    @Override
    public String process(String in) {
        return in
                .replaceAll(String.format("[%s]+", replaceWithSpaceRegex), " ")
                .replaceAll(String.format("[%s]+", replaceWithBlankRegex), "");
    }
}
