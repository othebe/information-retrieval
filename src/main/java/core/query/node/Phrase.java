package core.query.node;

import core.search.zones.textzone.positionalindex.IPositionalIndex;
import core.search.zones.textzone.positionalindex.Posting;
import core.query.Query;

import java.util.*;

public class Phrase extends Query {
    public static final Character TOKEN_PHRASE = '+';

    public Phrase(Query left, Query right) {
        super(left, right);
    }

    @Override
    public List<Posting> match(IPositionalIndex positionalIndex) {
        return orderedUnion(getLeft().match(positionalIndex), getRight().match(positionalIndex));
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", getLeft(), TOKEN_PHRASE, getRight());
    }

    private List<Posting> orderedUnion(List<Posting> left, List<Posting> right) {
        Set<Posting> orderedUnion = new HashSet<>();

        Iterator<Posting> itLeft = left.iterator();
        while (itLeft.hasNext()) {
            Posting postingLeft = itLeft.next();

            for (int positionLeft : postingLeft.getPositions()) {
                Iterator<Posting> itRight = right.iterator();

                while (itRight.hasNext()) {
                    Posting postingRight = itRight.next();

                    if (!postingLeft.getDocId().equals(postingRight.getDocId())) {
                        continue;
                    }

                    for (int positionRight : postingRight.getPositions()) {
                        if (positionRight - positionLeft == 1) {
                            orderedUnion.add(postingLeft);
                            orderedUnion.add(postingRight);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(orderedUnion);
    }
}
