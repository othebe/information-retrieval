package core.zones.textzone.positionalindex;

import core.DocId;

import java.util.Set;

public interface IPositionalIndex {
    void add(String key, int position, DocId docId);
    Posting[] get(String key);
    String[] getKeys();
    Set<DocId> getDocIds();
    int getNumDocuments();
}
