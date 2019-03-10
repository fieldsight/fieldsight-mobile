package org.bcss.collect.naxa.common;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

public interface BaseLocalDataSourceRX<T> {

    LiveData<List<T>> getAll();

    Completable save(T... items);

    Completable save(ArrayList<T> items);

    void saveAsAsync(T... items);



    void updateAll(ArrayList<T> items);
}
