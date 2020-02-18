package org.fieldsight.naxa.v3.network;

import org.fieldsight.naxa.network.APIEndpoint;

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

    @GET(APIEndpoint.V4.GET_SITES)
    Single<SiteResponse> getSites(@QueryMap Map<String, String> options);

    @GET
    Single<SiteResponse> getSites(@Url String url);

    @GET(APIEndpoint.V3.GET_NOTIFICATION)
    Single<ResponseBody> getNotification(@QueryMap Map<String, String> queryParams);

    @GET(APIEndpoint.V3.GET_SITE_DOCUMENTS)
    Single<ResponseBody> getSiteDocuments(@QueryMap Map<String, String> queryParams);

    @GET(APIEndpoint.V3.GET_PROJECT_ATTR_COUNT+"/{projectIdsParams}")
    Single<ResponseBody> getProjectAttrCount(@Path("projectIdsParams") String projectIdParams);

    @GET
    Observable<ResponseBody> getFormsFromUrlAsRaw(@Url String url);

    @GET
    Observable<ResponseBody> getMyFlaggedSubmissionAsRaw(@Url String url);

    @GET(APIEndpoint.V3.GET_PROJECT_DASHBOARD+"/{projectId}")
    Observable<ResponseBody> getProjectDashboardStat(@Path("projectId") String projectId);
}