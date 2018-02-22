package core.query.parser;

import core.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.query.node.And.TOKEN_AND;
import static core.query.node.Or.TOKEN_OR;
import static core.query.node.Phrase.TOKEN_PHRASE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardQueryParserTest {
    private StandardQueryParser parser;

    @BeforeEach
    public void setup() {
        parser = new StandardQueryParser();
    }

    @Test
    public void shouldParsePhrase() {
        String in = "green" + TOKEN_PHRASE + "eggs";

        List<Query> queries = parser.parse(in);

        String expected = "green" + TOKEN_PHRASE + "egg";

        assertEquals(1, queries.size());
        assertEquals(expected, queries.get(0).toString());
    }

    @Test
    public void shouldParseMixed() {
        String in = "deviled" + TOKEN_PHRASE + "green" + TOKEN_PHRASE + "eggs" + TOKEN_AND + "ham" + TOKEN_OR + "seabiscuits";

        List<Query> queries = parser.parse(in);

        String expected = "devil" + TOKEN_PHRASE + "green" + TOKEN_PHRASE + "egg" + TOKEN_AND + "ham" + TOKEN_OR + "seabiscuit";

        assertEquals(1, queries.size());
        assertEquals(expected, queries.get(0).toString());
    }
}
