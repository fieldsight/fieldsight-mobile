package org.fieldsight.naxa.site;

import org.fieldsight.naxa.common.BaseRemoteDataSource;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.sync.DownloadableItemLocalSource;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.DownloadUID.SITE_TYPES;

public class SiteTypeRemoteSource implements BaseRemoteDataSource<SiteType> {

    private static SiteTypeRemoteSource siteTypeRemoteSource;

    public synchronized static SiteTypeRemoteSource getSiteTypeRemoteSource() {
        if (siteTypeRemoteSource == null) {
            siteTypeRemoteSource = new SiteTypeRemoteSource();

        }
        return siteTypeRemoteSource;
    }

    @Override
    public void getAll() {
        fetchSiteTypes()
                .doOnDispose(new Action() {
                    @Override
                    public void run() {
                         DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsPending(SITE_TYPES);
                    }
                })
                .subscribe(new SingleObserver<List<SiteType>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsRunning(SITE_TYPES);
                    }

                    @Override
                    public void onSuccess(List<SiteType> siteTypes) {
                        SiteType[] list = siteTypes.toArray(new SiteType[siteTypes.size()]);
                        SiteTypeLocalSource.getInstance().refreshCache(list);
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(SITE_TYPES);
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

                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(SITE_TYPES,message);

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
