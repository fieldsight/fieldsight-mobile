package org.bcss.collect.naxa.site.db;

import android.net.Uri;
import android.support.v4.content.FileProvider;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.utilities.FileUtils;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;

import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    public Single<List<Site>> uploadMultipleSites(List<Site> sites) {

        return Observable.just(sites)
                .flatMapIterable((Function<List<Site>, Iterable<Site>>) sites1 -> sites1)
                .filter(site -> site.getIsSiteVerified() == IS_UNVERIFIED_SITE)
                .flatMap((Function<Site, ObservableSource<Site>>) oldSite -> uploadSite(oldSite)
                        .map(newSite -> {

                            SiteLocalSource.getInstance().setSiteAsVerified(oldSite.getId());
                            SiteLocalSource.getInstance().setSiteId(oldSite.getId(), newSite.getId());

                            return newSite;
                        }))
                .toList();

    }

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
        RequestBody regionId = RequestBody.create(MediaType.parse("text/plain"), siteLocationPojo.getRegionId());

        return getRxClient()
                .create(ApiInterface.class)
                .uploadSite(APIEndpoint.ADD_SITE_URL, body, isSurvey
                        , SiteNameRequest, latRequest, lonRequest, identifierRequest, SitePhoneRequest,
                        SiteAddressRequest, SitePublicDescRequest, projectIdRequest, SiteRequest, regionId, metaAttrs)
                ;
    }
}
