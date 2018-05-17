package core.zones;

import core.DocId;
import core.vectorizer.SparseVector;
import kotlin.Pair;

import java.util.List;

public abstract class Zone<T> {
    private final String name;

    public Zone(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void index(T data, DocId docId);
    public abstract List<Pair<DocId, Double>> matchQuery(T query);
    public abstract SparseVector<Double> getVector(DocId docId);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zone<?> zone = (Zone<?>) o;

        return name.equals(zone.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
