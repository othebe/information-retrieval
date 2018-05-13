package search;

import core.Record;
import core.zones.Zone;

import java.util.*;

public class Indexer {
    private final Map<String, Zone> zonesByName;

    private Indexer(Set<Zone> zones) {
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
