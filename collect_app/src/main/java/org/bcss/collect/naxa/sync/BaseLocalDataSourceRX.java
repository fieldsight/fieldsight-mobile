package org.bcss.collect.naxa.sync;

import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

interface BaseLocalDataSourceRX<T> {

    LiveData<List<T>> getAll();

    Completable save(T... items);

    Completable save(ArrayList<T> items);

    void saveAsAsync(T... items);



    void updateAll(ArrayList<T> items);
}
