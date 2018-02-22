package core.query;

import core.DocId;
import core.search.zones.textzone.StandardTextParser;
import core.search.zones.textzone.TextZone;
import core.search.zones.textzone.positionalindex.IPositionalIndex;
import core.search.zones.textzone.positionalindex.InMemoryPositionalIndex;
import core.search.zones.textzone.positionalindex.Posting;
import core.parser.Parser;
import core.query.parser.StandardQueryParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.query.node.And.TOKEN_AND;
import static core.query.node.Or.TOKEN_OR;
import static core.query.node.Phrase.TOKEN_PHRASE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryTest {
    private IPositionalIndex index;

    @BeforeEach
    public void setup() {
        index = new InMemoryPositionalIndex();
    }

    @Test
    public void shouldMatchMixedQuery_1() {
        // ARRANGE
        Parser<String, Query> queryParser = new StandardQueryParser();
        Parser<String, String> textParser = new StandardTextParser();

        TextZone zone = new TextZone("body", index, queryParser, textParser);

        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        zone.index("green" + TOKEN_PHRASE + "eggs" + TOKEN_AND + "ham", docId1);
        zone.index("eggs" + TOKEN_AND + "ham", docId2);

        // ACT
        List<Query> query = queryParser.parse("green" + TOKEN_PHRASE + "eggs");

        // ASSERT
        assertEquals(1, query.size());
        List<Posting> postingList = query.get(0).match(index);
        assertEquals(1, postingList.size());
    }

    @Test
    public void shouldMatchMixedQuery_2() {
        // ARRANGE
        Parser<String, Query> queryParser = new StandardQueryParser();
        Parser<String, String> textParser = new StandardTextParser();

        TextZone zone = new TextZone("body", index, queryParser, textParser);

        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        zone.index("green" + TOKEN_PHRASE + "eggs" + TOKEN_AND + "ham", docId1);
        zone.index("eggs" + TOKEN_PHRASE + "ham", docId2);

        // ACT
        List<Query> query = queryParser.parse("green" + TOKEN_AND + "eggs" + TOKEN_PHRASE + "ham");

        // ASSERT
        assertEquals(1, query.size());
        List<Posting> postingList = query.get(0).match(index);
        assertEquals(1, postingList.size());
    }

    @Test
    public void shouldMatchMixedQuery_3() {
        // ARRANGE
        Parser<String, Query> queryParser = new StandardQueryParser();
        Parser<String, String> textParser = new StandardTextParser();

        TextZone zone = new TextZone("body", index, queryParser, textParser);

        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        zone.index("green" + TOKEN_PHRASE + "eggs" + TOKEN_AND + "ham", docId1);
        zone.index("eggs" + TOKEN_PHRASE + "ham", docId2);

        // ACT
        List<Query> query = queryParser.parse("green" + TOKEN_OR + "eggs" + TOKEN_OR + "ham");

        // ASSERT
        assertEquals(1, query.size());
        List<Posting> postingList = query.get(0).match(index);
        assertEquals(2, postingList.size());
    }
}
