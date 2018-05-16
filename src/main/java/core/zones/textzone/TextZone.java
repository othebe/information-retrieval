package core.zones.textzone;

import core.DocId;
import core.zones.Zone;
import core.zones.textzone.positionalindex.IPositionalIndex;
import core.zones.textzone.positionalindex.Posting;
import core.parser.Parser;
import core.query.Query;
import core.vectorizer.TfIdfVectorizer;
import kotlin.Pair;

import java.util.*;

public class TextZone extends Zone<String> {
    private final IPositionalIndex positionalIndex;
    private final Parser<String, Query> queryParser;
    private final Parser<String, String> textParser;
    private final TfIdfVectorizer vectorizer;
    private final Map<DocId, Double[]> documentVectors;

    public TextZone(String name,
                    IPositionalIndex positionalIndex,
                    Parser<String, Query> queryParser,
                    Parser<String, String> textParser) {
        super(name);

        this.positionalIndex = positionalIndex;
        this.queryParser = queryParser;
        this.textParser = textParser;
        this.documentVectors = new HashMap<>();

        this.vectorizer = new TfIdfVectorizer();
    }

    @Override
    public void index(String data, DocId docId) {
        List<String> parsed = textParser.parse(data);
        for (int i = 0; i < parsed.size(); i++) {
            positionalIndex.add(parsed.get(i), i, docId);
        }
        documentVectors.clear();
    }

    @Override
    public List<Pair<DocId, Double>> matchQuery(String query) {
        List<Query> parsed = queryParser.parse(query);
        List<Posting> postingList = parsed.get(0).match(positionalIndex);

        List<String> terms = textParser.parse(query);
        String[] termsArray = new String[terms.size()];
        terms.toArray(termsArray);

        Double[] queryVector = vectorizer.vectorize(termsArray, positionalIndex);

        List<Pair<DocId, Double>> matches = new ArrayList<>();
        for (Posting posting : postingList) {
            Double[] docVector = getVector(posting.getDocId());
            double score = getCosineAngle(queryVector, docVector);
            matches.add(new Pair<>(posting.getDocId(), score));
        }

        Collections.sort(matches, new Comparator<Pair<DocId, Double>>() {
            @Override
            public int compare(Pair<DocId, Double> o1, Pair<DocId, Double> o2) {
                return o2.getSecond().compareTo(o1.getSecond());
            }
        });

        return matches;
    }

    @Override
    public Double[] getVector(DocId docId) {
        if (!documentVectors.containsKey(docId)) {
            documentVectors.put(docId, vectorizer.vectorize(docId, positionalIndex));
        }
        return documentVectors.get(docId);
    }

    private double getCosineAngle(Double[] vecA, Double[] vecB) {
        int vecLength = vecA.length;

        double dotProduct = 0;

        for (int i = 0; i < vecLength; i++) {
            dotProduct += vecA[i] * vecB[i];
        }

        return dotProduct;
    }
}
