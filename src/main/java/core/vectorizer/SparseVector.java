package core.vectorizer;

import java.util.*;

public class SparseVector<T> {
//    private long length;
//    private T defaultValue;

//    private Map<Long, T> valuesByNdx;

    private List<Data<T>> list;

    public SparseVector() {
//        this.length = length;
//        this.defaultValue = defaultValue;
//        this.valuesByNdx = new HashMap<>();
        this.list = new LinkedList<>();
    }

//    public long getLength() {
//        return length;
//    }

//    public T getDefaultValue() {
//        return defaultValue;
//    }

    public void put(long ndx, T value) {
//        if (value.equals(defaultValue)) {
//            if (valuesByNdx.containsKey(ndx)) {
//                valuesByNdx.remove(ndx);
//            }
//        } else {
//            valuesByNdx.put(ndx, value);
//        }
        list.add(new Data<T>(ndx, value));
    }

//    public T get(long ndx) {
//        return valuesByNdx.containsKey(ndx) ? valuesByNdx.get(ndx) : defaultValue;
//    }
//
//    public boolean has(long ndx) {
//        return valuesByNdx.containsKey(ndx);
//    }

    public Iterator<Data<T>> getIterator() {
        return list.iterator();
    }

    public class Data<T> {
        private long ndx;
        private T value;

        private Data(long ndx, T value) {
            this.ndx = ndx;
            this.value = value;
        }

        public long getNdx() {
            return ndx;
        }

        public T getValue() {
            return value;
        }
    }
}
