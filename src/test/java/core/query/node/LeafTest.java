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

public class LeafTest {
    private IPositionalIndex index;
    private Query leaf;

    @BeforeEach
    public void setup() {
        index = new InMemoryPositionalIndex();
    }

    @Test
    public void shouldMatchTerm() {
        // ARRANGE
        String term = "hello";
        int position = 0;
        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);

        index.add(term, position, docId1);
        index.add(term, position, docId2);

        leaf = new Leaf(term);

        // ACT
        List<Posting> matches = leaf.match(index);

        // ASSERT
        assertEquals(2, matches.size());
        assertEquals(docId1, matches.get(0).getDocId());
        assertEquals(docId2, matches.get(1).getDocId());
    }
}
