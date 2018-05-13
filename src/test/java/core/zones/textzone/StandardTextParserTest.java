package core.zones.textzone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardTextParserTest {
    private StandardTextParser parser;

    @BeforeEach
    public void setup() {
        parser = new StandardTextParser();
    }

    @Test
    public void shouldParseText() {
        String in = "Hello, w0rld! 13+4 = 17";

        List<String> out = parser.parse(in);

        assertEquals(2, out.size());
        assertEquals("hello", out.get(0));
        assertEquals("w0rld", out.get(1));
    }
}
