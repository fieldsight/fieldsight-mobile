package org.bcss.collect.naxa.common;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public interface BaseRepository<T> {

    LiveData<List<T>> getAll(boolean forceUpdate);

    void save(T... items);

    void save(ArrayList<T> items);

    void updateAll(ArrayList<T> items);
}
