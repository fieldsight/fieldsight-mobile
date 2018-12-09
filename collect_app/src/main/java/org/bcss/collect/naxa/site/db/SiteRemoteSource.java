package org.bcss.collect.naxa.site.db;

import android.net.Uri;
import android.support.v4.content.FileProvider;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.dao.InstancesDao;
import org.bcss.collect.android.utilities.FileUtils;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;

import org.bcss.collect.naxa.common.database.SiteUploadHistory;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;

import java.io.File;
import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_EDITED;
import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_OFFLINE;
import static org.bcss.collect.naxa.firebase.NotificationUtils.notifyHeadsUp;
import static org.bcss.collect.naxa.network.ServiceGenerator.getRxClient;

public class SiteRemoteSource implements BaseRemoteDataSource<Site> {

    private static SiteRemoteSource INSTANCE;
    private SiteDao dao;


    public static SiteRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteRemoteSource();
        }
        return INSTANCE;
    }


    @Override
    public void getAll() {


    }


    public void updateAllEditedSite() {
        SiteLocalSource.getInstance()
                .getAllByStatus(IS_EDITED)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapObservable(new Function<Site, ObservableSource<Site>>() {
                    @Override
                    public ObservableSource<Site> apply(Site site) throws Exception {
                        return SiteRemoteSource.getInstance().updateSite(site).subscribeOn(Schedulers.io());
                    }
                })
                .toList()
                .subscribe(new DisposableSingleObserver<List<Site>>() {
                    @Override
                    public void onSuccess(List<Site> sites) {
                        String title = "Site Uploaded";
                        String msg;
                        if (sites.size() > 1) {
                            msg = Collect.getInstance().getString(R.string.msg_multiple_sites_upload, sites.get(0).getName(), sites.size());
                        } else {
                            msg = Collect.getInstance().getString(R.string.msg_single_site_upload, sites.get(0).getName());
                        }

                        notifyHeadsUp(title, msg);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Timber.e(e);
                    }
                });
    }


    public void uploadEditedSites(List<Site> sites) {
        InstancesDao instancesDao = new InstancesDao();


    }




    public Observable<Site> uploadMultipleSites(List<Site> sites) {

        InstancesDao instancesDao = new InstancesDao();
        return Observable.just(sites)
                .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites1 -> sites1)
                .filter(site -> site.getIsSiteVerified() == IS_OFFLINE)
                .flatMap(new Function<Site, Observable<Site>>() {
                    @Override
                    public Observable<Site> apply(Site oldSite) throws Exception {
                        return uploadSite(oldSite)
                                .flatMap(new Function<Site, ObservableSource<Site>>() {
                                    @Override
                                    public ObservableSource<Site> apply(Site newSite) throws Exception {
                                        String oldSiteId = oldSite.getId();
                                        String newSiteId = newSite.getId();
                                        return SiteLocalSource.getInstance().setSiteAsVerified(oldSiteId)
                                                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                                                    @Override
                                                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                                                        return SiteLocalSource.getInstance().updateSiteId(oldSiteId, newSiteId);
                                                    }
                                                })
                                                .flatMap(new Function<Integer, Observable<Long[]>>() {
                                                    @Override
                                                    public Observable<Long[]> apply(Integer affectedRowsCount) throws Exception {
                                                        return SiteUploadHistoryLocalSource.getInstance().saveAsObservable(new SiteUploadHistory(newSiteId, oldSiteId));
                                                    }
                                                }).flatMap(new Function<Long[], ObservableSource<Integer>>() {
                                                    @Override
                                                    public ObservableSource<Integer> apply(Long[] updatedRows) throws Exception {
                                                        return instancesDao.cascadedSiteIds(oldSiteId, newSiteId);
                                                    }
                                                })
                                                .map(new Function<Integer, Site>() {
                                                    @Override
                                                    public Site apply(Integer integer) throws Exception {
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

        RequestBody SiteNameRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getName());
        RequestBody latRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getLatitude()));
        RequestBody lonRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getLongitude()));
        RequestBody identifierRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getIdentifier());
        RequestBody SitePhoneRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getPhone());
        RequestBody SiteAddressRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getAddress());
        RequestBody SitePublicDescRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getPublicDesc());
        RequestBody projectIdRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getProject());
        RequestBody isSurvey = RequestBody.create(MediaType.parse("text/plain"), "false");
        RequestBody metaAttrs = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getMetaAttributes());
        RequestBody regionId = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getRegionId());


        return getRxClient()
                .create(ApiInterface.class)
                .uploadSite(APIEndpoint.ADD_SITE_URL, body, isSurvey
                        , SiteNameRequest, latRequest, lonRequest, identifierRequest, SitePhoneRequest,
                        SiteAddressRequest, SitePublicDescRequest, projectIdRequest, siteTypeRequest, regionId, metaAttrs)
                ;
    }

    public Observable<Site> updateSite(Site siteLocationPojo) {
        RequestBody requestBody;
        MultipartBody.Part body = null;

        File file = FileUtils.getFileByPath(siteLocationPojo.getLogo());

        if (FileUtils.isFileExists(file)) {
            requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            body = MultipartBody.Part.createFormData("logo", file.getName(), requestBody);
        }

        RequestBody SiteNameRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getName());
        RequestBody latRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getLatitude()));
        RequestBody lonRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getLongitude()));
        RequestBody identifierRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getIdentifier());
        RequestBody SitePhoneRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getPhone());
        RequestBody SiteAddressRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getAddress());
        RequestBody SitePublicDescRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getPublicDesc());
        RequestBody projectIdRequest = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getProject());
        RequestBody SiteRequest = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(siteLocationPojo.getTypeId()));
        RequestBody metaAttrs = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getMetaAttributes());
        RequestBody regionId = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getRegionId());


        return getRxClient()
                .create(ApiInterface.class)
                .updateSite(APIEndpoint.SITE_UPDATE_URL.concat(siteLocationPojo.getId()), body
                        , SiteNameRequest, latRequest, lonRequest, identifierRequest, SitePhoneRequest,
                        SiteAddressRequest, SitePublicDescRequest, projectIdRequest, SiteRequest, regionId, metaAttrs);

    }
}
