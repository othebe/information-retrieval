package core.query.node;

import core.DocId;
import core.search.zones.textzone.positionalindex.IPositionalIndex;
import core.search.zones.textzone.positionalindex.InMemoryPositionalIndex;
import core.search.zones.textzone.positionalindex.Posting;
import core.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhraseTest {
    private IPositionalIndex index;
    private Query phrase;

    @BeforeEach
    public void setup() {
        index = new InMemoryPositionalIndex();
    }

    @Test
    public void shouldMatchPhrase() {
        // ARRANGE
        String term1 = "hello";
        int position1 = 0;

        String term2 = "world";
        int position2 = 1;

        DocId docId = new DocId(1);

        index.add(term1, position1, docId);
        index.add(term2, position2, docId);

        phrase = new Phrase(new Leaf(term1), new Leaf(term2));

        // ACT
        List<Posting> matches = phrase.match(index);

        // ASSERT
        assertEquals(1, matches.size());
        assertEquals(docId, matches.get(0).getDocId());
    }

    @Test
    public void shouldNotMatchPhrasesInDifferentDocuments() {
        // ARRANGE
        String term1 = "hello";
        String term2 = "world";

        int position1 = 0;
        int position2 = 1;

        index.add(term1, position1, new DocId(1));
        index.add(term2, position2, new DocId(2));

        phrase = new Phrase(new Leaf(term1), new Leaf(term2));

        // ACT
        List<Posting> matches = phrase.match(index);

        // ASSERT
        assertEquals(0, matches.size());
    }

    @Test
    public void shouldNotMatchPhrasesWhenNotOrdered() {
        // ARRANGE
        String term1 = "hello";
        int position1 = 1;
        DocId docId1 = new DocId(1);

        String term2 = "world";
        int position2 = 0;
        DocId docId2 = new DocId(2);

        index.add(term1, position1, docId1);
        index.add(term2, position2, docId2);

        phrase = new Phrase(new Leaf(term1), new Leaf(term2));

        // ACT
        List<Posting> matches = phrase.match(index);

        // ASSERT
        assertEquals(0, matches.size());
    }
}
