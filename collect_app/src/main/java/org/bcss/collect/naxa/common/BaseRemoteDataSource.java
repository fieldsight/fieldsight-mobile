package org.bcss.collect.naxa.common;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public interface BaseRemoteDataSource<T> {

    void getAll();



}
