package org.odk.collect.naxa.common;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public interface BaseRemoteDataSource<T> {

    void getAll();

  }
