package core;

import org.jetbrains.annotations.NotNull;

public class DocId implements Comparable<DocId> {
    private Comparable id;

    public DocId(Comparable id) {
        this.id = id;
    }

    public Comparable getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull DocId o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocId docId = (DocId) o;

        return id.equals(docId.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
