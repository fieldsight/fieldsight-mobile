package org.fieldsight.naxa.site.db;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.BaseRemoteDataSource;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.common.FieldSightNotificationUtils;
import org.fieldsight.naxa.common.database.SiteUploadHistory;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.helpers.FSInstancesDao;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.sync.DownloadableItemLocalSource;
import org.fieldsight.naxa.sync.SyncRepository;
import org.fieldsight.naxa.v3.network.ApiV3Interface;
import org.fieldsight.naxa.v3.network.SiteResponse;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.DownloadUID.EDITED_SITES;
import static org.fieldsight.naxa.common.Constant.DownloadUID.OFFLINE_SITES;
import static org.fieldsight.naxa.common.Constant.SiteStatus.IS_EDITED;
import static org.fieldsight.naxa.common.Constant.SiteStatus.IS_OFFLINE;
import static org.fieldsight.naxa.common.Constant.SiteStatus.IS_ONLINE;
import static org.fieldsight.naxa.network.ServiceGenerator.getRxClient;

public class SiteRemoteSource implements BaseRemoteDataSource<Site> {

    private static SiteRemoteSource siteRemoteSource;


    public synchronized static SiteRemoteSource getInstance() {
        if (siteRemoteSource == null) {
            siteRemoteSource = new SiteRemoteSource();
        }
        return siteRemoteSource;
    }


    @Override
    public void getAll() {


    }


    public void updateAllEditedSite() {
        DisposableSingleObserver<List<Site>> dis = SiteLocalSource.getInstance()
                .getAllByStatus(IS_EDITED)
                .doOnDispose(() -> DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(EDITED_SITES))
                .doOnSubscribe(disposable -> {
                    SyncRepository.getInstance().showProgress(EDITED_SITES);
                    DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsRunning(EDITED_SITES);
                })
                .flattenAsObservable((Function<List<Site>, Iterable<Site>>) sites -> sites)
                .flatMap((Function<Site, ObservableSource<Site>>) this::updateSite)
                .map(site -> {
                    SiteLocalSource.getInstance().updateSiteIdAsync(site.getId(), IS_ONLINE);
                    return site;
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Site>>() {
                    @Override
                    public void onSuccess(List<Site> sites) {

                        if (sites.size() > 0) {
                            String title = Collect.getInstance().getString(R.string.msg_edited_site_uploaded);
                            String msg;
                            if (sites.size() > 1) {
                                msg = Collect.getInstance().getString(R.string.msg_multiple_sites_upload, sites.get(0).getName(), sites.size());
                            } else {
                                msg = Collect.getInstance().getString(R.string.msg_single_site_upload, sites.get(0).getName());
                            }
                            FieldSightNotificationUtils.getINSTANCE().notifyHeadsUp(title, msg);
                            DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(EDITED_SITES);
                        } else {
                            DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(EDITED_SITES);
                        }
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

                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(EDITED_SITES, message);
                    }
                });


        DisposableManager.add(dis);
    }

    public void uploadAllOfflineSite() {
        DisposableSingleObserver<List<Site>> dis = SiteLocalSource.getInstance()
                .getAllByStatus(IS_OFFLINE)
                .doOnDispose(() -> DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(OFFLINE_SITES))
                .doOnSubscribe(disposable -> {
                    SyncRepository.getInstance().showProgress(OFFLINE_SITES);
                    DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsRunning(OFFLINE_SITES);
                })
                .toObservable()
                .flatMap((Function<List<Site>, ObservableSource<Site>>) this::uploadMultipleSites)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribeWith(new DisposableSingleObserver<List<Site>>() {
                    @Override
                    public void onSuccess(List<Site> sites) {
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(OFFLINE_SITES);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String message;
                        if (e instanceof RetrofitException && ((RetrofitException) e).getResponse().errorBody() == null) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(OFFLINE_SITES, message);
                    }
                });

        DisposableManager.add(dis);
    }

    public Observable<Site> uploadMultipleEditedSites(List<Site> sites) {
        return Observable.just(sites)
                .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites1 -> sites1)
                .filter(site -> site.getIsSiteVerified() == IS_EDITED)
                .flatMap((Function<Site, ObservableSource<Site>>) this::updateSite);
    }

    public Observable<Site> uploadMultipleSites(List<Site> sites) {
        FSInstancesDao instancesDao = new FSInstancesDao();
        return Observable.just(sites)
                .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites1 -> sites1)
                .filter(site -> site.getIsSiteVerified() == IS_OFFLINE)
                .flatMap(new Function<Site, Observable<Site>>() {
                    @Override
                    public Observable<Site> apply(Site oldSite) {
                        return uploadSite(oldSite)
                                .flatMap(new Function<Site, ObservableSource<Site>>() {
                                    @Override
                                    public ObservableSource<Site> apply(Site newSite) {
                                        String oldSiteId = oldSite.getId();
                                        String newSiteId = newSite.getId();
                                        return SiteLocalSource.getInstance()
                                                .setSiteAsVerified(oldSiteId)
                                                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                                                    @Override
                                                    public ObservableSource<Integer> apply(Integer integer) {
                                                        return SiteLocalSource.getInstance().updateSiteId(oldSiteId, newSiteId);
                                                    }
                                                })
                                                .flatMap(new Function<Integer, Observable<Long[]>>() {
                                                    @Override
                                                    public Observable<Long[]> apply(Integer affectedRowsCount) {
                                                        return SiteUploadHistoryLocalSource.getInstance().saveAsObservable(new SiteUploadHistory(newSiteId, oldSiteId));
                                                    }
                                                }).flatMap(new Function<Long[], ObservableSource<Integer>>() {
                                                    @Override
                                                    public ObservableSource<Integer> apply(Long[] updatedRows) {
                                                        return instancesDao.cascadedSiteIds(oldSiteId, newSiteId);
                                                    }
                                                })
                                                .map(new Function<Integer, Site>() {
                                                    @Override
                                                    public Site apply(Integer integer) {
                                                        return newSite;
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }


    private Observable<Site> uploadSite(Site siteLocationPojo) {
        RequestBody requestBody;
        RequestBody siteTypeRequest = null;
        MultipartBody.Part body = null;

        File file = FileUtils.getFileByPath(siteLocationPojo.getLogo());

        if (FileUtils.isFileExists(file)) {
            requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            body = MultipartBody.Part.createFormData("logo", file.getName(), requestBody);
        }

        boolean hasSiteType = siteLocationPojo.getTypeId() != null && siteLocationPojo.getTypeId().trim().length() > 0;

        if (hasSiteType) {
            siteTypeRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getTypeId()));
        }

        RequestBody siteNameRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getName());
        RequestBody latRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getLatitude());
        RequestBody lonRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getLongitude());
        RequestBody identifierRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getIdentifier());
        RequestBody sitePhoneRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getPhone());
        RequestBody siteAddressRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getAddress());
        RequestBody sitePublicDescRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getPublicDesc());
        RequestBody projectIdRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getProject());
        RequestBody isSurvey = RequestBody.create(MediaType.parse("text/plain"), "false");
        RequestBody metaAttrs = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getMetaAttributes());
        RequestBody regionId = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getRegionId());


        return getRxClient()
                .create(ApiInterface.class)
                .uploadSite(APIEndpoint.ADD_SITE_URL, body, isSurvey
                        , siteNameRequest, latRequest, lonRequest, identifierRequest, sitePhoneRequest,
                        siteAddressRequest, sitePublicDescRequest, projectIdRequest, siteTypeRequest, regionId, metaAttrs);
    }

    private Observable<Site> updateSite(Site siteLocationPojo) {
        RequestBody requestBody;
        MultipartBody.Part body = null;

        File file = FileUtils.getFileByPath(siteLocationPojo.getLogo());

        if (FileUtils.isFileExists(file)) {
            requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            body = MultipartBody.Part.createFormData("logo", file.getName(), requestBody);
        }

        RequestBody siteNameRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getName());
        RequestBody latRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getLatitude()));
        RequestBody lonRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getLongitude()));
        RequestBody identifierRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getIdentifier());
        RequestBody sitePhoneRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getPhone());
        RequestBody siteAddressRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getAddress());
        RequestBody sitePublicDescRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getPublicDesc());
        RequestBody projectIdRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getProject());
        RequestBody siteRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getTypeId()));
        RequestBody metaAttrs = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getMetaAttributes());
        RequestBody regionId = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getRegionId());


        return getRxClient()
                .create(ApiInterface.class)
                .updateSite(APIEndpoint.SITE_UPDATE_URL.concat(siteLocationPojo.getId()), body
                        , siteNameRequest, latRequest, lonRequest, identifierRequest, sitePhoneRequest,
                        siteAddressRequest, sitePublicDescRequest, projectIdRequest, siteRequest, regionId, metaAttrs);

    }

    public Single<SiteResponse> getSitesByProjectId(String projectId) {
        HashMap<String, String> params = new HashMap<>();
        params.put(APIEndpoint.PARAMS.PROJECT_ID, projectId);
        return getSites(params);
    }


    public Single<SiteResponse> getSitesByRegionId(String regionId) {
        HashMap<String, String> params = new HashMap<>();
        params.put(APIEndpoint.PARAMS.REGION_ID, regionId);
        return getSites(params);
    }

    public Single<SiteResponse> getSitesByURL(String url) {
        return getRxClient()
                .create(ApiV3Interface.class)
                .getSites(url)
                .subscribeOn(Schedulers.io());
    }

    public Single<SiteResponse> getSites(HashMap<String, String> params) {
        return getRxClient()
                .create(ApiV3Interface.class)
                .getSites(params)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Site>> updateEditedSite(String siteId) {
        return SiteLocalSource.getInstance()
                .getSiteByIdAsSingle(siteId)
                .toObservable()
                .flatMap(new Function<Site, ObservableSource<Site>>() {
                    @Override
                    public ObservableSource<Site> apply(Site site) throws Exception {
                        return updateSite(site);
                    }
                })
                .map(site -> {
                    SiteLocalSource.getInstance().updateSiteIdAsync(site.getId(), IS_ONLINE);
                    return site;
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
}
