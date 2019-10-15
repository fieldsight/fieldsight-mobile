package org.fieldsight.naxa.contact;

import org.fieldsight.naxa.common.BaseRemoteDataSource;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
                .subscribeWith(new DisposableObserver<ArrayList<FieldSightContactModel>>() {
                    @Override
                    public void onNext(ArrayList<FieldSightContactModel> fieldSightContactModels) {
                        ContactLocalSource.getInstance().updateAll(fieldSightContactModels);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }
}
