package org.fieldsight.naxa.v3;

import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.HashMap;

public class HashMapUtils {

    public void putOrUpdate(SparseIntArray map, Integer id) {
        map.put(id, map.get(id, 0) + 1);
    }

    public void putOrUpdate(HashMap<Integer, ArrayList<String>> map, Integer key, String listValue) {
        ArrayList<String> value = map.get(key);
        if (value == null) {
            ArrayList<String> list = new ArrayList<>();
            list.add(listValue);
            map.put(key, list);
        } else {
            value.add(listValue);
            map.put(key, value);
        }
    }

    public void putOrUpdate(SparseArray<ArrayList<String>> map, Integer key, String listValue) {
        ArrayList<String> value = map.get(key);
        if (value == null) {
            ArrayList<String> list = new ArrayList<>();
            list.add(listValue);
            map.put(key, list);
        } else {
            value.add(listValue);
            map.put(key, value);
        }
    }

}
