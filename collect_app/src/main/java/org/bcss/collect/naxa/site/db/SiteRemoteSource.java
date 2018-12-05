package org.bcss.collect.naxa.site.db;

import org.bcss.collect.android.dao.InstancesDao;
import org.bcss.collect.android.utilities.FileUtils;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;

import org.bcss.collect.naxa.common.database.SiteUploadHistory;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_OFFLINE;
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


//    public Single<List<Site>> uploadMultipleSites(List<Site> sites) {
//
//        InstancesDao instancesDao = new InstancesDao();
//        return Observable.just(sites)
//                .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites1 -> sites1)
//                .filter(site -> site.getIsSiteVerified() == IS_OFFLINE)
//                .flatMap((Function<Site, ObservableSource<Site>>) oldSite -> uploadSite(oldSite)
//                        .flatMapCompletable(new Function<Site, CompletableSource>() {
//                            @Override
//                            public CompletableSource apply(Site newSite) throws Exception {
//                                String oldSiteId = oldSite.get();
//                                String newSiteId = newSite.getId();
//                                return SiteLocalSource.getInstance().setSiteAsVerified(oldSiteId, newSiteId);
//                            }
//                        }).
//                        .map(newSite -> {
//
//
//                            SiteLocalSource.getInstance().setSiteAsVerified(oldSiteId, newSiteId);
//
//                            instancesDao
//                                    .cascadedSiteIds(oldSiteId, newSiteId)
//                                    .subscribe(new DisposableObserver<String>() {
//                                        @Override
//                                        public void onNext(String s) {
//
//                                        }
//
//                                        @Override
//                                        public void onError(Throwable e) {
//                                            e.printStackTrace();
//                                        }
//
//                                        @Override
//                                        public void onComplete() {
//
//                                        }
//                                    });
//
//                            SiteUploadHistoryLocalSource.getInstance().save(new SiteUploadHistory(newSiteId, oldSiteId));
//
//                            return newSite;
//                        }))
//                .toList();
//
//    }

    private Observable<Site> uploadSite(Site siteLocationPojo) {
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
        RequestBody isSurvey = RequestBody.create(MediaType.parse("text/plain"), "false");
        RequestBody metaAttrs = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getMetaAttributes());
//        RequestBody regionId = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getRegionId());
        RequestBody regionId = null;

        return getRxClient()
                .create(ApiInterface.class)
                .uploadSite(APIEndpoint.ADD_SITE_URL, body, isSurvey
                        , SiteNameRequest, latRequest, lonRequest, identifierRequest, SitePhoneRequest,
                        SiteAddressRequest, SitePublicDescRequest, projectIdRequest, SiteRequest, regionId, metaAttrs)
                ;
    }
}
