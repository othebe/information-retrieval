package core.query.parser;

import core.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.query.node.And.TOKEN_AND;
import static core.query.node.Or.TOKEN_OR;
import static core.query.node.Phrase.TOKEN_PHRASE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardQueryTokenizerTest {
    private StandardQueryTokenizer tokenizer;

    @BeforeEach
    public void setup() {
        tokenizer = new StandardQueryTokenizer();
    }

    @Test
    public void shouldTokenizePhrase() {
        String in = "green" + TOKEN_PHRASE + "eggs";

        List<Query> queries = tokenizer.tokenize(in);

        assertEquals(1, queries.size());
        assertEquals(in, queries.get(0).toString());
    }

    @Test
    public void shouldTokenizeMixed() {
        String in = "deviled" + TOKEN_PHRASE + "green" + TOKEN_PHRASE + "eggs" + TOKEN_AND + "ham" + TOKEN_OR + "seabiscuits";

        List<Query> queries = tokenizer.tokenize(in);

        assertEquals(1, queries.size());
        assertEquals(in, queries.get(0).toString());
    }
}
