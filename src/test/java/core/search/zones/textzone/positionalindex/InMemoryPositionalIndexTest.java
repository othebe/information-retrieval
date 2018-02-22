package core.search.zones.textzone.positionalindex;

import core.DocId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryPositionalIndexTest {
    private InMemoryPositionalIndex index;

    @BeforeEach
    public void setup() {
        this.index = new InMemoryPositionalIndex();
    }

    @Test
    public void shouldAdd() {
        // ARRANGE
        DocId docId1 = new DocId(1);
        String term1 = "hello";
        int position1 = 25;

        DocId docId2 = new DocId(2);
        String term2 = "world";
        int position2 = 15;

        // ACT
        index.add(term1, position1, docId1);
        index.add(term2, position2, docId2);

        // ASSERT
        Posting[] postings1 = index.get(term1);
        assertEquals(1, postings1.length);
        Posting posting1 = postings1[0];
        assertEquals(docId1, posting1.getDocId());
        assertEquals(1, posting1.getPositions().length);
        assertEquals(position1, posting1.getPositions()[0].intValue());

        Posting[] postings2 = index.get(term2);
        assertEquals(1, postings2.length);
        Posting posting2 = postings2[0];
        assertEquals(docId2, posting2.getDocId());
        assertEquals(1, posting2.getPositions().length);
        assertEquals(position2, posting2.getPositions()[0].intValue());
    }

    @Test
    public void shouldAddDocumentsInSortedOrder() {
        // ARRANGE
        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);
        DocId docId3 = new DocId(3);
        String term = "abc";
        int position = 5;

        // ACT
        index.add(term, position, docId2);
        index.add(term, position, docId3);
        index.add(term, position, docId1);

        // ASSERT
        Posting[] postings = index.get(term);
        assertEquals(3, postings.length);
        assertEquals(docId1, postings[0].getDocId());
        assertEquals(docId2, postings[1].getDocId());
        assertEquals(docId3, postings[2].getDocId());
    }

    @Test
    public void shouldAddPositionsInSortedOrder() {
        // ARRANGE
        DocId docId = new DocId(1);
        String term = "abc";
        int position1 = 1;
        int position2 = 2;
        int position3 = 3;

        // ACT
        index.add(term, position2, docId);
        index.add(term, position3, docId);
        index.add(term, position1, docId);

        // ASSERT
        Posting[] postings = index.get(term);
        assertEquals(1, postings.length);
        Posting posting = postings[0];
        assertEquals(position1, posting.getPositions()[0].intValue());
        assertEquals(position2, posting.getPositions()[1].intValue());
        assertEquals(position3, posting.getPositions()[2].intValue());
    }

    @Test
    public void shouldNotAddDuplicates() {
        // ARRANGE
        DocId docId = new DocId(1);
        String term = "hello";
        int position = 25;

        // ACT
        index.add(term, position, docId);
        index.add(term, position, docId);

        // ASSERT
        Posting[] postings = index.get(term);
        assertEquals(1, postings.length);
        assertEquals(1, postings[0].getPositions().length);
    }

    @Test
    public void shouldGetNumDocuments() {
        // ARRANGE
        DocId docId1 = new DocId(1);
        DocId docId2 = new DocId(2);
        DocId docId3 = new DocId(3);

        // ACT
        index.add("hello", 0, docId1);
        index.add("hello", 0, docId2);
        index.add("hello", 0, docId3);

        // ASSERT
        assertEquals(3, index.getNumDocuments());
    }
}
