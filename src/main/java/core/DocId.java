package core;

import org.jetbrains.annotations.NotNull;

public class DocId implements Comparable<DocId> {
    private final int id;

    public DocId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull DocId o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocId docId = (DocId) o;

        return id == docId.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
