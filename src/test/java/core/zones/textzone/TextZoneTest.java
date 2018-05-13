package core.zones.textzone;

import core.DocId;
import core.zones.textzone.positionalindex.IPositionalIndex;
import core.zones.textzone.positionalindex.InMemoryPositionalIndex;
import core.parser.Parser;
import core.query.Query;
import core.query.parser.StandardQueryParser;
import kotlin.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static core.query.node.And.TOKEN_AND;
import static core.query.node.Phrase.TOKEN_PHRASE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextZoneTest {
    private TextZone zone;

    @BeforeEach
    public void setup() {
        IPositionalIndex positionalIndex = new InMemoryPositionalIndex();
        Parser<String, Query> queryParser = new StandardQueryParser();
        Parser<String, String> textParser = new StandardTextParser();

        zone = new TextZone("body", positionalIndex, queryParser, textParser);
    }

    @Test
    public void shouldIndexAndMatch() {
        // ARRANGE
        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        zone.index("green" + TOKEN_PHRASE + "eggs" + TOKEN_AND + "purple" + TOKEN_AND + "eggs", docId1);
        zone.index("eggs" + TOKEN_PHRASE + "ham", docId2);
        zone.index("jellyfish" + TOKEN_AND + "porridge", new DocId(3));

        // ACT
        List<Pair<DocId, Double>> matches = zone.matchQuery("eggs");

        // ASSERT
        assertEquals(2, matches.size());
        assertEquals(docId1, matches.get(0).getFirst());
        assertEquals(docId2, matches.get(1).getFirst());
        assertTrue(matches.get(0).getSecond().compareTo(matches.get(1).getSecond()) >= 0);
    }

    @Test
    public void shouldIndexAndMatch_2() {
        // ARRANGE
        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        zone.index("green" + TOKEN_PHRASE + "eggs" + TOKEN_AND + "ham", docId1);
        zone.index("eggs" + TOKEN_PHRASE + "ham", docId2);
        zone.index("jellyfish" + TOKEN_AND + "porridge", new DocId(3));

        // ACT
        List<Pair<DocId, Double>> matches = zone.matchQuery("eggs" + TOKEN_AND + "ham");

        // ASSERT
        assertEquals(2, matches.size());
        assertEquals(docId2, matches.get(0).getFirst());
        assertEquals(docId1, matches.get(1).getFirst());
        assertTrue(matches.get(0).getSecond().compareTo(matches.get(1).getSecond()) >= 0);
    }

    @Test
    public void shouldFindNoMatches() {
        // ARRANGE
        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        zone.index("green" + TOKEN_PHRASE + "eggs" + TOKEN_AND + "ham", docId1);
        zone.index("eggs" + TOKEN_PHRASE + "ham", docId2);

        // ACT
        List<Pair<DocId, Double>> matches = zone.matchQuery("candy");

        // ASSERT
        assertEquals(0, matches.size());
    }
}
