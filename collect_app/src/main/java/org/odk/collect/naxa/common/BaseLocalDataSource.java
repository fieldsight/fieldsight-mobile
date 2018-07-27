package org.odk.collect.naxa.common;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public interface BaseLocalDataSource<T> {

    LiveData<List<T>> getAll();

    void save(T... items);

    void save(ArrayList<T> items);

    void updateAll(ArrayList<T> items);

}
