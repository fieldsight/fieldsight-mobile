package org.bcss.collect.naxa.site;

import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.rx.RetrofitException;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.common.DisposableManager;
import org.bcss.collect.naxa.sync.DownloadableItemLocalSource;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
                    public void run() {
                         DownloadableItemLocalSource.getINSTANCE().markAsPending(SITE_TYPES);
                    }
                })
                .subscribe(new SingleObserver<List<SiteType>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        DownloadableItemLocalSource.getINSTANCE().markAsRunning(SITE_TYPES);
                    }

                    @Override
                    public void onSuccess(List<SiteType> siteTypes) {
                        SiteType[] list = siteTypes.toArray(new SiteType[siteTypes.size()]);
                        SiteTypeLocalSource.getInstance().save(list);
                        DownloadableItemLocalSource.getINSTANCE().markAsCompleted(SITE_TYPES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String message;
                        if (e instanceof RetrofitException) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }

                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(SITE_TYPES,message);

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
