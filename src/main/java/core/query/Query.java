package core.query;

import core.search.zones.textzone.positionalindex.IPositionalIndex;
import core.search.zones.textzone.positionalindex.Posting;

import java.util.List;

public abstract class Query {
    private final Query left;
    private final Query right;
    private final String term;

    public Query(Query left, Query right) {
        this.left = left;
        this.right = right;
        this.term = null;
    }

    public Query(String term) {
        this.left = null;
        this.right = null;
        this.term = term;
    }

    public Query getLeft() {
        return left;
    }

    public Query getRight() {
        return right;
    }

    public String getTerm() {
        return term;
    }

    public abstract List<Posting> match(IPositionalIndex positionalIndex);
}
