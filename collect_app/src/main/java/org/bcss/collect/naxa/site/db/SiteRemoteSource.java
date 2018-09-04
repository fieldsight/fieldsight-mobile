package org.bcss.collect.naxa.site.db;

import android.arch.lifecycle.MediatorLiveData;
import android.content.ContentValues;

import org.bcss.collect.android.dao.InstancesDao;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.data.Resource;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_UNVERIFIED_SITE;
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

    public void create(List<Site> sites) {

        MediatorLiveData<Resource<Site>> result = new MediatorLiveData<>();



        Observable.just(sites)
                .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites1 -> sites1)
                .filter(site -> site.getIsSiteVerified() == IS_UNVERIFIED_SITE)
                .flatMap((Function<Site, ObservableSource<Site>>) oldSite -> uploadSite(oldSite)
                        .map(newSite -> {
                            String uploadError = newSite.getSiteTypeError();
                            if ("identifier".contains(uploadError)) {
                                throw new RuntimeException("Bad identifier");
                            }

                            if ("Invalid pk".contains(uploadError)) {
                                throw new RuntimeException("Invalid pk");
                            }

                            SiteLocalSource.getInstance().setSiteAsVerified(newSite.getId());
                            SiteLocalSource.getInstance().setSiteId(oldSite.getId(), newSite.getId());

                            return newSite;
                        }))
                .toList()
                .subscribe(new SingleObserver<List<Site>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        result.setValue(Resource.loading(null));
                    }

                    @Override
                    public void onSuccess(List<Site> sites) {
                        Timber.i("OnSucess");
                        result.setValue(Resource.success(null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        result.setValue(Resource.error(e.getMessage(), null));
                    }
                });


    }

    private Observable<Site> uploadSite(Site siteLocationPojo) {
        String imagePath = siteLocationPojo.getLogo();
        final File ImageFile = new File(imagePath);
        RequestBody imageRequestBody = null;
        String imageName = "";
        MultipartBody.Part body = null;

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
        RequestBody regionId = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getRegion());

        return getRxClient()
                .create(ApiInterface.class)
                .uploadSite(APIEndpoint.ADD_SITE_URL, body, isSurvey
                        , SiteNameRequest, latRequest, lonRequest, identifierRequest, SitePhoneRequest,
                        SiteAddressRequest, SitePublicDescRequest, projectIdRequest, SiteRequest, regionId, metaAttrs);
    }
}
