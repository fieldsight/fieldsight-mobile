package org.odk.collect.naxa.generalforms;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.generalforms.db.GeneralFormDAO;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class GeneralFormLocalSource implements BaseLocalDataSource<GeneralForm> {

    private static GeneralFormLocalSource INSTANCE;
    private GeneralFormDAO dao;


    public GeneralFormLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getProjectGeneralFormDao();
    }


    public static GeneralFormLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeneralFormLocalSource();
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<GeneralForm>> getById(@NonNull String id) {
        return null;
    }

    @Override
    public LiveData<List<GeneralForm>> getAll() {
        return dao.getProjectGeneralForms();
    }

    @Override
    public void save(GeneralForm... items) {
        io.reactivex.Observable.just(items)
                .flatMap(generalForms -> {
                    dao.insert(generalForms);
                    return io.reactivex.Observable.empty();
                })
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void save(ArrayList<GeneralForm> items) {
        io.reactivex.Observable.just(items)
                .flatMap(generalForms -> {
                    dao.insert(generalForms);
                    return io.reactivex.Observable.empty();
                })
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void refresh() {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void updateAll() {

    }
}
