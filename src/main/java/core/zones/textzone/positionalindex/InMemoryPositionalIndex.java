package core.zones.textzone.positionalindex;

import core.DocId;

import java.util.*;

public class InMemoryPositionalIndex implements IPositionalIndex {
    private final Map<String, PriorityQueue<Posting>> index;
    private final Set<DocId> docIdSet;

    public InMemoryPositionalIndex() {
        this.index = new LinkedHashMap<>();
        this.docIdSet = new HashSet<>();
    }

    @Override
    public Set<DocId> getDocIds() {
        return docIdSet;
    }

    @Override
    public void add(String key, int position, DocId docId) {
        if (!index.containsKey(key)) {
            index.put(key, new PriorityQueue<Posting>());
        }

        PriorityQueue<Posting> postings = index.get(key);
        Iterator<Posting> iterator = postings.iterator();

        docIdSet.add(docId);

        boolean found = false;
        while (iterator.hasNext()) {
            Posting posting = iterator.next();
            if (posting.getDocId().equals(docId)) {

                if (Arrays.binarySearch(posting.getPositions(), position) < 0) {
                    posting.addPosition(position);
                }

                found = true;
                break;
            }
        }

        if (!found) {
            PriorityQueue<Integer> positions = new PriorityQueue<>();
            positions.add(position);
            postings.add(new Posting(docId, positions));
        }
    }

    @Override
    public Posting[] get(String key) {
        PriorityQueue<Posting> postings = index.get(key);
        if (postings == null) {
            return new Posting[0];
        }

        Posting[] postingArr = new Posting[postings.size()];

        int ndx = 0;
        while (!postings.isEmpty()) {
            postingArr[ndx] = postings.poll();
            ndx++;
        }

        for (Posting posting : postingArr) {
            postings.add(posting);
        }

        return postingArr;
    }

    @Override
    public int getNumDocuments() {
        return docIdSet.size();
    }

    @Override
    public String[] getKeys() {
        String[] keys = new String[index.size()];
        Set<String> keySet = index.keySet();

        int ndx = 0;
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            keys[ndx] = iterator.next();
            ndx++;
        }

        return keys;
    }
}
