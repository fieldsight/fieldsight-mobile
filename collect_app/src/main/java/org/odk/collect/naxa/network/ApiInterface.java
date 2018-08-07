package org.odk.collect.naxa.network;


import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.login.model.AuthResponse;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.scheduled.data.ScheduleForm;
import org.odk.collect.naxa.site.SiteType;
import org.odk.collect.naxa.site.data.SiteCluster;
import org.odk.collect.naxa.stages.data.Stage;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

import static org.odk.collect.naxa.network.APIEndpoint.GET_CLUSTER_LIST;
import static org.odk.collect.naxa.network.APIEndpoint.GET_FORM_SCHEDULE;
import static org.odk.collect.naxa.network.APIEndpoint.GET_GENERAL_FORM;
import static org.odk.collect.naxa.network.APIEndpoint.GET_SITE_TYPES;
import static org.odk.collect.naxa.network.APIEndpoint.GET_STAGE_SUB_STAGE;

public interface ApiInterface {

    @GET("/users/me/")
    Observable<MeResponse> getUserInformation();


    @FormUrlEncoded
    @POST("/users/api/get-auth-token/")
    Single<AuthResponse> getAuthToken(
            @Field("email_or_username") String username,
            @Field("password") String password
    );

    @GET(GET_GENERAL_FORM)
    Observable<ArrayList<GeneralForm>> getGeneralFormsObservable(@Path(value = "is_project", encoded = true) String is_project, @Path("id") String id);

    @GET(GET_FORM_SCHEDULE)
    Observable<ArrayList<ScheduleForm>> getScheduleForms(@Path(value = "is_project", encoded = true) String is_project, @Path("id") String id);


    @GET(GET_STAGE_SUB_STAGE)
    Observable<ArrayList<Stage>> getStageSubStage(@Path(value = "is_project", encoded = true) String createdFromProject, @Path("id") String creatorsId);

    @Multipart
    @POST
    Observable<Site> uploadSite(@Url String url,
                                @Part MultipartBody.Part file,
                                @Part("is_survey") RequestBody isSurvey,
                                @Part("name") RequestBody name,
                                @Part("latitude") RequestBody latitude,
                                @Part("longitude") RequestBody longitude,
                                @Part("identifier") RequestBody identifier,
                                @Part("phone") RequestBody phone,
                                @Part("address") RequestBody address,
                                @Part("public_desc") RequestBody public_desc,
                                @Part("project") RequestBody project,
                                @Part("type") RequestBody type,
                                @Part("region") RequestBody regionID,
                                @Part("site_meta_attributes_ans") RequestBody metaAttrs);

    @GET(GET_SITE_TYPES)
    Single<List<SiteType>> getSiteTypes();

    @GET(GET_CLUSTER_LIST)
    Observable<List<SiteCluster>> getClusterByProjectId(@Path(value = "project_id", encoded = true) String projectId);
}
