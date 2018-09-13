package org.bcss.collect.naxa.network;


import org.bcss.collect.naxa.contact.FieldSightContactModel;
import org.bcss.collect.naxa.firebase.FCMParameter;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.login.model.AuthResponse;
import org.bcss.collect.naxa.login.model.MeResponse;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.notificationslist.NotificationDetail;
import org.bcss.collect.naxa.project.data.MySiteResponse;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.site.SiteType;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.bcss.collect.naxa.stages.data.Stage;
import org.bcss.collect.naxa.submissions.FormHistoryResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

import static org.bcss.collect.naxa.network.APIEndpoint.GET_ALL_CONTACTS;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_CLUSTER_LIST;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_FORM_SCHEDULE;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_GENERAL_FORM;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_PROJECT_SITES;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_SITE_TYPES;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_STAGE_SUB_STAGE;

public interface ApiInterface {

    @GET("/users/me/")
    Observable<MeResponse> getUserInformation();


    @GET(GET_PROJECT_SITES)
    Observable<MeResponse> getUser();

    @GET
    Observable<MySiteResponse> getAssignedSites(@Url String url);


    @FormUrlEncoded
    @POST("/users/api/get-auth-token/")
    Observable<AuthResponse> getAuthToken(
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
    Observable<List<SiteRegion>> getRegionsByProjectId(@Path(value = "project_id", encoded = true) String projectId);

    @GET
    Call<FormHistoryResponse> getFormHistory(@Url String urlNextPage);

    @POST()
    Observable<FCMParameter> postFCMUserParameter(@Url String url, @Body FCMParameter fcmParameter);

    @GET
    Call<NotificationDetail> getNotificationDetail(@Url String url);

    @GET(GET_ALL_CONTACTS)
    Observable<ArrayList<FieldSightContactModel>> getAllContacts();
}
