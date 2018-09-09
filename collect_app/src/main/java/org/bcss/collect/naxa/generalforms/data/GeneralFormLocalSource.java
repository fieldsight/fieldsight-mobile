package org.bcss.collect.naxa.generalforms.data;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class GeneralFormLocalSource implements BaseLocalDataSource<GeneralForm> {

    private static GeneralFormLocalSource INSTANCE;
    private GeneralFormDAO dao;


    private GeneralFormLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getProjectGeneralFormDao();
    }


    public static GeneralFormLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeneralFormLocalSource();
        }
        return INSTANCE;
    }


    public LiveData<List<GeneralForm>> getBySiteId(@NonNull String siteId,String projectId) {

        return dao.getSiteGeneralForms(siteId,projectId);


    }


    public LiveData<List<GeneralForm>> getByProjectId(String projectId) {
        return dao.getProjectGeneralForms(projectId);
    }

    @Override
    public LiveData<List<GeneralForm>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(GeneralForm... items) {
        io.reactivex.Observable.just(items)
                .flatMap(generalForms -> {
                    dao.insert(generalForms);
                    return io.reactivex.Observable.empty();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
    public void updateAll(ArrayList<GeneralForm> items) {

        Observable.just(items)
                .map(new Function<ArrayList<GeneralForm>, Object>() {
                    @Override
                    public Object apply(ArrayList<GeneralForm> generalForms) throws Exception {
                        dao.updateAll(generalForms);
                        return Observable.empty();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    private <T> Observer applySubscriber() {
        return new DisposableObserver() {

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }

        };
    }

    private <T> ObservableTransformer applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T> SingleTransformer applySchedulersSingle() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteAll() {
        AsyncTask.execute(() -> dao.deleteAll());
    }

}
