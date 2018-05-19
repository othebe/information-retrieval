package core.vectorizer;

import core.DocId;
import core.zones.textzone.positionalindex.IPositionalIndex;
import core.zones.textzone.positionalindex.InMemoryPositionalIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

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
        SparseVector<Double> vector = vectorizer.vectorize(terms, index);

        // ASSERT
        Iterator<SparseVector<Double>.Data<Double>> it = vector.getIterator();

        SparseVector<Double>.Data<Double> data0 = it.next();
        assertEquals(0, data0.getNdx());
        assertEquals(0.313469, data0.getValue(), 0.000001);

        SparseVector<Double>.Data<Double> data1 = it.next();
        assertEquals(1, data1.getNdx());
        assertEquals(0.849346, data1.getValue(), 0.000001);

        SparseVector<Double>.Data<Double> data2 = it.next();
        assertEquals(2, data2.getNdx());
        assertEquals(0.424673, data2.getValue(), 0.000001);
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
        SparseVector<Double> vector = vectorizer.vectorize(new DocId(1), index);

        // ASSERT
        Iterator<SparseVector<Double>.Data<Double>> it = vector.getIterator();

        SparseVector<Double>.Data<Double> data0 = it.next();
        assertEquals(0, data0.getNdx());
        assertEquals(0.3462415, data0.getValue(), 0.000001);

        SparseVector<Double>.Data<Double> data2 = it.next();
        assertEquals(2, data2.getNdx());
        assertEquals(0.9381453, data2.getValue(), 0.000001);
    }
}
