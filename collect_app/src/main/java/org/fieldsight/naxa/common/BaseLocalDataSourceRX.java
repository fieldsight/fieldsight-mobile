package org.fieldsight.naxa.common;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

public interface BaseLocalDataSourceRX<T> {

    LiveData<List<T>> getAll();

    Completable save(T... items);


    void save(ArrayList<T> items);


    void updateAll(ArrayList<T> items);
}
