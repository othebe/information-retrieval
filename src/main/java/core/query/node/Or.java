package core.query.node;

import core.search.zones.textzone.positionalindex.IPositionalIndex;
import core.search.zones.textzone.positionalindex.Posting;
import core.query.Query;

import java.util.*;

public class Or extends Query {
    public static final Character TOKEN_OR = '|';

    public Or(Query left, Query right) {
        super(left, right);
    }

    @Override
    public List<Posting> match(IPositionalIndex positionalIndex) {
        return union(getLeft().match(positionalIndex), getRight().match(positionalIndex));
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", getLeft(), TOKEN_OR, getRight());
    }

    private List<Posting> union(List<Posting> left, List<Posting> right) {
        Set<Posting> union = new HashSet<>();
        union.addAll(left);
        union.addAll(right);

        return new ArrayList<>(union);
    }
}
