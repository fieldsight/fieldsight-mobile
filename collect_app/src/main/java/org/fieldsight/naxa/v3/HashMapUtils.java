package org.fieldsight.naxa.v3;

import android.util.SparseIntArray;

public class HashMapUtils {

    public void putOrUpdate(SparseIntArray map, Integer id) {
        map.put(id, map.get(id, 0) + 1);
    }

}
