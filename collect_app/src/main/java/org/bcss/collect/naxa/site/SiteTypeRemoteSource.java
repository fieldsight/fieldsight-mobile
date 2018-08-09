package org.bcss.collect.naxa.site;

import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.SITE_TYPES;

public class SiteTypeRemoteSource implements BaseRemoteDataSource<SiteType> {

    private static SiteTypeRemoteSource INSTANCE;

    public static SiteTypeRemoteSource getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new SiteTypeRemoteSource();

        }
        return INSTANCE;
    }

    @Override
    public void getAll() {
        fetchSiteTypes()
                .subscribe(new SingleObserver<List<SiteType>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        SyncRepository.getInstance().showProgress(SITE_TYPES);
                    }

                    @Override
                    public void onSuccess(List<SiteType> siteTypes) {
                        SiteType[] list = siteTypes.toArray(new SiteType[siteTypes.size()]);
                        SiteTypeLocalSource.getInstance().save(list);
                        SyncRepository.getInstance().setSuccess(SITE_TYPES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncRepository.getInstance().setError(SITE_TYPES);
                        e.printStackTrace();
                    }
                });
    }

    private Single<List<SiteType>> fetchSiteTypes() {
        return ServiceGenerator.createService(ApiInterface.class)
                .getSiteTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
