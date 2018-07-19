package org.odk.collect.naxa.generalforms.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import org.odk.collect.naxa.common.FieldSightDatabase;
import org.odk.collect.naxa.generalforms.GeneralForm;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class GeneralFormRepository {

    private GeneralFormDAO dao;

    public GeneralFormRepository(Application application) {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(application);
        this.dao = database.getProjectGeneralFormDao();
    }


    public void insert(ArrayList<GeneralForm> forms) {
        io.reactivex.Observable.just(forms)
                .flatMap(generalForms -> {
                    dao.insert(generalForms);
                    return io.reactivex.Observable.empty();
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void insert(GeneralForm... forms) {

        io.reactivex.Observable.just(forms)
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


    public LiveData<List<GeneralForm>> getFromProjectId(String id) {
        return dao.getProjectGeneralForms();
    }
}
