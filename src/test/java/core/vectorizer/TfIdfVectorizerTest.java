package core.vectorizer;

import core.DocId;
import core.zones.textzone.positionalindex.IPositionalIndex;
import core.zones.textzone.positionalindex.InMemoryPositionalIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TfIdfVectorizerTest {
    private TfIdfVectorizer vectorizer;
    private IPositionalIndex index;

    @BeforeEach
    public void setup() {
        vectorizer = new TfIdfVectorizer();
        index = new InMemoryPositionalIndex();
    }

    @Test
    public void shouldUnitVectorizeTerms() {
        // ARRANGE
        String[] terms = { "green", "eggs", "ham" };
        String[] dictionary = { "green", "eggs", "ham", "jellyfish" };

        index.add(dictionary[0], 0, new DocId(1));
        index.add(dictionary[0], 1, new DocId(2));

        index.add(dictionary[1], 0, new DocId(2));
        index.add(dictionary[1], 1, new DocId(2));

        index.add(dictionary[2], 0, new DocId(1));

        index.add(dictionary[3], 0, new DocId(3));
        index.add(dictionary[3], 1, new DocId(3));

        // ACT
        double[] vector = vectorizer.vectorize(terms, index);

        // ASSERT
        assertEquals(4, vector.length);
        assertEquals(0.313469, vector[0], 0.000001);
        assertEquals(0.849346, vector[1], 0.000001);
        assertEquals(0.424673, vector[2], 0.000001);
        assertEquals(0, vector[3]);
    }

    @Test
    public void shouldUnitVectorizeDocument() {
        // ARRANGE
        String[] dictionary = { "green", "eggs", "ham", "jellyfish" };

        index.add(dictionary[0], 0, new DocId(1));
        index.add(dictionary[0], 1, new DocId(2));

        index.add(dictionary[1], 0, new DocId(2));
        index.add(dictionary[1], 1, new DocId(2));

        index.add(dictionary[2], 0, new DocId(1));

        index.add(dictionary[3], 0, new DocId(3));
        index.add(dictionary[3], 1, new DocId(3));

        // ACT
        double[] vector = vectorizer.vectorize(new DocId(1), index);

        // ASSERT
        assertEquals(4, vector.length);
        assertEquals(0.3462415, vector[0], 0.000001);
        assertEquals(0, vector[1]);
        assertEquals(0.9381453, vector[2], 0.000001);
        assertEquals(0, vector[3]);
    }
}
