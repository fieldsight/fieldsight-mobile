package org.bcss.collect.naxa.contact;

import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.sync.DisposableManager;
import org.bcss.collect.naxa.sync.SyncLocalSource;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.PROJECT_CONTACTS;

public class ContactRemoteSource implements BaseRemoteDataSource<FieldSightContactModel> {

    private static ContactRemoteSource INSTANCE;


    public static ContactRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContactRemoteSource();
        }
        return INSTANCE;
    }


    @Override
    public void getAll() {
        Disposable d = ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getAllContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(new Action() {
                    @Override
                    public void run() {
                        SyncLocalSource.getINSTANCE().markAsFailed(PROJECT_CONTACTS);
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        SyncLocalSource.getINSTANCE().markAsRunning(PROJECT_CONTACTS);
                    }
                })
                .subscribeWith(new DisposableObserver<ArrayList<FieldSightContactModel>>() {
                    @Override
                    public void onNext(ArrayList<FieldSightContactModel> fieldSightContactModels) {
                        ContactLocalSource.getInstance().save(fieldSightContactModels);
                        SyncLocalSource.getINSTANCE().markAsCompleted(PROJECT_CONTACTS);
                    }

                    @Override
                    public void onError(Throwable e) {

                        SyncLocalSource.getINSTANCE().markAsFailed(PROJECT_CONTACTS);

                        String message = e.getMessage();
                        SyncLocalSource.getINSTANCE().addErrorMessage(PROJECT_CONTACTS, message);

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        DisposableManager.add(d);
    }
}
