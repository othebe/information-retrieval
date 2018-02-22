package core.parser.tokenizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhitespaceTokenizerTest {
    private WhitespaceTokenizer tokenizer;

    @BeforeEach
    public void setup() {
        tokenizer = new WhitespaceTokenizer();
    }

    @Test
    public void shouldTokenizeOnWhitespace() {
        // ARRANGE
        String in = "  AB    CD E  ";

        // ACT
        List<String> tokens = tokenizer.tokenize(in);

        // ASSERT
        assertEquals(tokens.size(), 3);
        assertEquals("AB", tokens.get(0));
        assertEquals("CD", tokens.get(1));
        assertEquals("E", tokens.get(2));
    }
}
