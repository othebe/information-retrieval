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

public class OrTest {
    private IPositionalIndex index;
    private Query or;

    @BeforeEach
    public void setup() {
        index = new InMemoryPositionalIndex();
    }

    @Test
    public void shouldUnion() {
        // ARRANGE
        String term1 = "hello";
        String term2 = "world";

        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        index.add(term1, 0, docId1);
        index.add(term2, 1, docId1);
        index.add(term2, 1, docId2);

        or = new Or(new Leaf(term1), new Leaf(term2));

        // ACT
        List<Posting> matches = or.match(index);

        // ASSERT
        assertEquals(2, matches.size());
        assertEquals(docId1, matches.get(0).getDocId());
        assertEquals(docId2, matches.get(1).getDocId());
    }
}
