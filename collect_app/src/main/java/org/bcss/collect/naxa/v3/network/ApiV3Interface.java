package org.bcss.collect.naxa.v3.network;

import org.bcss.collect.naxa.network.APIEndpoint;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiV3Interface {
    @GET(APIEndpoint.V3.GET_PROJECTS)
    Single<ResponseBody> getProjects();

    @GET(APIEndpoint.V3.GET_SITES)
    Single<SiteResponse> getSites(@QueryMap Map<String, String> options);
}