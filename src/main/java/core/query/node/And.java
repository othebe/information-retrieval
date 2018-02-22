package core.query.node;

import core.search.zones.textzone.positionalindex.IPositionalIndex;
import core.search.zones.textzone.positionalindex.Posting;
import core.query.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class And extends Query {
    public static final Character TOKEN_AND = ' ';

    public And(Query left, Query right) {
        super(left, right);
    }

    @Override
    public List<Posting> match(IPositionalIndex positionalIndex) {
        return intersect(getLeft().match(positionalIndex), getRight().match(positionalIndex));
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", getLeft(), TOKEN_AND, getRight());
    }

    private List<Posting> intersect(List<Posting> left, List<Posting> right) {
        Iterator<Posting> itLeft = left.iterator();
        Iterator<Posting> itRight = right.iterator();

        List<Posting> intersect = new ArrayList<>();

        boolean hasItems = itLeft.hasNext() && itRight.hasNext();
        if (!hasItems) {
            return intersect;
        }

        Posting postingLeft = itLeft.next();
        Posting postingRight = itRight.next();

        while (hasItems) {
            if (postingLeft.equals(postingRight)) {
                intersect.add(postingLeft);
                if (itLeft.hasNext() && itRight.hasNext()) {
                    postingLeft = itLeft.next();
                    postingRight = itRight.next();
                } else {
                    hasItems = false;
                }
            } else if (postingLeft.compareTo(postingRight) < 0) {
                if (itLeft.hasNext()) {
                    postingLeft = itLeft.next();
                } else {
                    hasItems = false;
                }
            } else {
                if (itRight.hasNext()) {
                    postingRight = itRight.next();
                } else {
                    hasItems = false;
                }
            }
        }

        return intersect;
    }
}
