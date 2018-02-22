package core.parser.tokenizer;

import java.util.Arrays;
import java.util.List;

public class WhitespaceTokenizer implements ITokenizer<String, String> {
    @Override
    public List<String> tokenize(String in) {
        String[] tokens = in.trim().split("\\s+");
        return Arrays.asList(tokens);
    }
}
