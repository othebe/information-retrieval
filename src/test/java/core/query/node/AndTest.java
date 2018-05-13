package core.query.node;

import core.DocId;
import core.zones.textzone.positionalindex.IPositionalIndex;
import core.zones.textzone.positionalindex.InMemoryPositionalIndex;
import core.zones.textzone.positionalindex.Posting;
import core.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AndTest {
    private IPositionalIndex index;
    private Query and;

    @BeforeEach
    public void setup() {
        index = new InMemoryPositionalIndex();
    }

    @Test
    public void shouldIntersect() {
        // ARRANGE
        String term = "hello";
        DocId docId = new DocId(1);

        index.add(term, 0, docId);

        and = new And(new Leaf(term), new Leaf(term));

        // ACT
        List<Posting> matches = and.match(index);

        // ASSERT
        assertEquals(1, matches.size());
        assertEquals(docId, matches.get(0).getDocId());
    }

    @Test
    public void shouldNotIntersect() {
        // ARRANGE
        String term1 = "hello";
        String term2 = "world";

        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        index.add(term1, 0, docId1);
        index.add(term2, 0, docId2);

        and = new And(new Leaf(term1), new Leaf(term2));

        // ACT
        List<Posting> matches = and.match(index);

        // ASSERT
        assertEquals(0, matches.size());
    }
}
