package core.parser.processor;

import core.parser.processor.string.PunctuationProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PunctuationProcessorTest {
    private final String[] ALLOWED = new String[] { "\\+", "\\|" };

    private PunctuationProcessor punctuationProcessor;

    @BeforeEach
    public void setup() {
        punctuationProcessor = new PunctuationProcessor(ALLOWED);
    }

    @Test
    public void shouldReplaceWithSpace() {
        String in = "This is the computer's decision-Ultimately we let the computer;;;decide,0101!";

        String out = punctuationProcessor.process(in);

        assertEquals("This is the computers decision Ultimately we let the computer decide 0101", out);
    }

    @Test
    public void shouldStripPunctuation() {
        String in = " 123^&@#$ABC";

        String out = punctuationProcessor.process(in);

        assertEquals(" 123ABC", out);
    }

    @Test
    public void shouldKeepAllowedCharacters() {
        String in = "Washing + Machine|Machines";

        String out = punctuationProcessor.process(in);

        assertEquals("Washing + Machine|Machines", out);
    }
}
