package org.bcss.collect.naxa.site;

import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.rx.RetrofitException;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.sync.DisposableManager;
import org.bcss.collect.naxa.sync.SyncLocalSource;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDU_MATERIALS;
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
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                         SyncLocalSource.getINSTANCE().markAsPending(SITE_TYPES);
                    }
                })
                .subscribe(new SingleObserver<List<SiteType>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        SyncRepository.getInstance().showProgress(SITE_TYPES);

                        SyncLocalSource.getINSTANCE().markAsRunning(SITE_TYPES);
                    }

                    @Override
                    public void onSuccess(List<SiteType> siteTypes) {
                        SiteType[] list = siteTypes.toArray(new SiteType[siteTypes.size()]);
                        SiteTypeLocalSource.getInstance().save(list);
                        SyncRepository.getInstance().setSuccess(SITE_TYPES);

                        SyncLocalSource.getINSTANCE().markAsCompleted(SITE_TYPES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncRepository.getInstance().setError(SITE_TYPES);
                        e.printStackTrace();

                        String message = ((RetrofitException) e).getMessage();
                        SyncLocalSource.getINSTANCE().addErrorMessage(SITE_TYPES, message);


                        SyncLocalSource.getINSTANCE().markAsFailed(SITE_TYPES);


                    }
                });
    }

    private Single<List<SiteType>> fetchSiteTypes() {
        return ServiceGenerator.getRxClient()
                .create(ApiInterface.class)
                .getSiteTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
