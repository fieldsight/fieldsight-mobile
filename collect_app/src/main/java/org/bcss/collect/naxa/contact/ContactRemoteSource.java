package org.bcss.collect.naxa.contact;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDU_MATERIALS;
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
        ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getAllContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<FieldSightContactModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        SyncRepository.getInstance().showProgress(PROJECT_CONTACTS);
                    }

                    @Override
                    public void onNext(ArrayList<FieldSightContactModel> fieldSightContactModels) {
                        ContactLocalSource.getInstance().save(fieldSightContactModels);
                        SyncRepository.getInstance().setSuccess(PROJECT_CONTACTS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        SyncRepository.getInstance().setError(PROJECT_CONTACTS);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
