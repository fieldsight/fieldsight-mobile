package org.fieldsight.naxa.contact;

import org.fieldsight.naxa.common.BaseRemoteDataSource;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ContactRemoteSource implements BaseRemoteDataSource<FieldSightContactModel> {

    private static ContactRemoteSource contactRemoteSource;


    public synchronized static ContactRemoteSource getInstance() {
        if (contactRemoteSource == null) {
            contactRemoteSource = new ContactRemoteSource();
        }
        return contactRemoteSource;
    }


    @Override
    public void getAll() {
        ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getAllContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ArrayList<FieldSightContactModel>>() {
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
