package core.vectorizer;

import java.util.HashMap;
import java.util.Map;

public class SparseVector<T> {
    private long length;
    private T defaultValue;

    private Map<Long, T> valuesByNdx;

    public SparseVector(long length, T defaultValue) {
        this.length = length;
        this.defaultValue = defaultValue;
        this.valuesByNdx = new HashMap<>();
    }

    public long getLength() {
        return length;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void put(long ndx, T value) {
        if (value.equals(defaultValue)) {
            if (valuesByNdx.containsKey(ndx)) {
                valuesByNdx.remove(ndx);
            }
        } else {
            valuesByNdx.put(ndx, value);
        }
    }

    public T get(long ndx) {
        return valuesByNdx.containsKey(ndx) ? valuesByNdx.get(ndx) : defaultValue;
    }
}
