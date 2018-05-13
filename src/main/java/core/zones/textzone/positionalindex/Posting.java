package core.zones.textzone.positionalindex;

import core.DocId;
import org.jetbrains.annotations.NotNull;

import java.util.PriorityQueue;

public class Posting implements Comparable<Posting> {
    private final DocId docId;
    private final PriorityQueue<Integer> positions;

    public Posting(DocId docId, PriorityQueue<Integer> positions) {
        this.docId = docId;
        this.positions = positions;
    }

    public void addPosition(Integer position) {
        positions.add(position);
    }

    public DocId getDocId() {
        return docId;
    }

    public Integer[] getPositions() {
        Integer[] positionArr = new Integer[positions.size()];

        int ndx = 0;
        while (!positions.isEmpty()) {
            positionArr[ndx] = positions.poll();
            ndx++;
        }

        for (Integer position : positionArr) {
            positions.add(position);
        }

        return positionArr;
    }

    @Override
    public int compareTo(@NotNull Posting o) {
        return docId.compareTo(o.docId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Posting posting = (Posting) o;

        return docId.equals(posting.docId);
    }

    @Override
    public int hashCode() {
        return docId.hashCode();
    }
}
