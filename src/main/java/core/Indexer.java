package core;

import core.zones.Zone;

import java.util.*;

public class Indexer {
    private final Map<String, Zone> zonesByName;
    private final List<DocId> docIds;

    private Indexer(Set<Zone> zones) {
        this.docIds = new LinkedList<>();
        this.zonesByName = new HashMap<>();
        for (Zone zone : zones) {
            zonesByName.put(zone.getName(), zone);
        }
    }

    public void add(Record record) {
        Map<String, Object> recordData = record.getDataByZoneName();
        for (String zoneName : recordData.keySet()) {
            if (zonesByName.containsKey(zoneName)) {
                zonesByName.get(zoneName).index(recordData.get(zoneName), record.getDocId());
            }
        }
        docIds.add(record.getDocId());
    }

    public List<DocId> getDocIds() {
        return docIds;
    }

    public Set<Zone> getZones() {
        return new HashSet<>(zonesByName.values());
    }

    public static class Builder {
        private Set<Zone> zones = new HashSet<>();

        public Builder addZone(Zone zone) {
            this.zones.add(zone);
            return this;
        }

        public Indexer build() {
            return new Indexer(zones);
        }
    }
}
