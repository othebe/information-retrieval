package core;

import org.jetbrains.annotations.NotNull;

public class DocId<T extends Comparable<T>> implements Comparable<DocId> {
    private final T id;

    public DocId(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull DocId o) {
        return id.compareTo((T) o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocId<?> docId = (DocId<?>) o;

        return id.equals(docId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
