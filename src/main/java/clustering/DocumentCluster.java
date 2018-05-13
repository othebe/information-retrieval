package clustering;

import core.DocId;

import java.util.*;

public class DocumentCluster {
    private Set<DocId> docIds;

    public DocumentCluster() {
        this.docIds = new HashSet<>();
    }

    public void add(DocId docId) {
        docIds.add(docId);
    }

    public void merge(DocumentCluster other) {
        docIds.addAll(other.docIds);
    }

    public Set<DocId> getDocIds() {
        return docIds;
    }
}
