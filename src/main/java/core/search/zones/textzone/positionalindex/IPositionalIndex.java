package core.search.zones.textzone.positionalindex;

import core.DocId;

public interface IPositionalIndex {
    void add(String key, int position, DocId docId);
    Posting[] get(String key);
    String[] getKeys();
    int getNumDocuments();
}
