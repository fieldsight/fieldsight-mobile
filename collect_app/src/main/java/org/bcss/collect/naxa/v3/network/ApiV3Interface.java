package org.bcss.collect.naxa.v3.network;

import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.network.APIEndpoint;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiV3Interface {
    @GET(APIEndpoint.V3.GET_PROJECTS)
    Single<ResponseBody> getProjects();

    @GET(APIEndpoint.V3.GET_SITES)
    Single<SiteResponse> getSites(@QueryMap Map<String, String> options);

    @GET
    Single<SiteResponse> getSites(@Url String url);

    @GET(APIEndpoint.V3.GET_NOTIFICATION)
    Observable<ResponseBody> getNotification(@QueryMap Map<String, String> queryParams);
}