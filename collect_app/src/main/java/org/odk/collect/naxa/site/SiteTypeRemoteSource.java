package org.odk.collect.naxa.site;

import org.odk.collect.naxa.common.BaseRemoteDataSource;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SiteTypeRemoteSource implements BaseRemoteDataSource<SiteType> {
    @Override
    public void getAll() {
        fetchSiteTypes()
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

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

    public Observable<Object> fetchSiteTypes() {
        return ServiceGenerator.createService(ApiInterface.class)
                .getSiteTypes()
                .map((Function<List<SiteType>, Object>) siteTypes -> {
                    SiteType[] list = siteTypes.toArray(new SiteType[siteTypes.size()]);
                    SiteTypeLocalSource.getInstance().save(list);
                    return Observable.empty();
                })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
