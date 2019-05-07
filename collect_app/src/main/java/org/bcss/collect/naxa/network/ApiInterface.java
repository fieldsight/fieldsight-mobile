package org.bcss.collect.naxa.network;


import org.bcss.collect.naxa.contact.FieldSightContactModel;
import org.bcss.collect.naxa.firebase.FCMParameter;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.login.model.AuthResponse;
import org.bcss.collect.naxa.login.model.MeResponse;
import org.bcss.collect.naxa.login.model.MySites;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.User;
import org.bcss.collect.naxa.notificationslist.NotificationDetail;
import org.bcss.collect.naxa.previoussubmission.model.LastSubmissionResponse;
import org.bcss.collect.naxa.project.data.MySiteResponse;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.site.SiteType;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.bcss.collect.naxa.stages.data.Stage;
import org.bcss.collect.naxa.submissions.FormHistoryResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
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
import static org.bcss.collect.naxa.network.APIEndpoint.GET_EXCHANGE_TOKEN;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_FORM_SCHEDULE;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_GENERAL_FORM;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_MY_SITES_v2;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_PROJECT_SITES;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_SITE_TYPES;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_STAGE_SUB_STAGE;
import static org.bcss.collect.naxa.network.APIEndpoint.GET_USER_PROFILE;
import static org.bcss.collect.naxa.network.APIEndpoint.REMOVE_FCM;

public interface ApiInterface {

    @Deprecated
    @GET("/users/me/")
    Observable<MeResponse> getUserInformation();


    @GET(GET_PROJECT_SITES)
    Observable<MeResponse> getUser();

    @GET
    Observable<MySiteResponse> getAssignedSites(@Url String url);

    @GET(GET_MY_SITES_v2)
    Observable<List<MySites>> getAssignedSites();

    @FormUrlEncoded
    @POST("/users/api/get-auth-token/")
    Observable<AuthResponse> getAuthToken(
            @Field("email_or_username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST(GET_EXCHANGE_TOKEN)
    Observable<AuthResponse> getAuthToken(
            @Field("access_token") String gmailAccessToken
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

    @GET(GET_USER_PROFILE)
    Observable<User> getUserProfile();

    @Multipart
    @POST
    Observable<User> updateUserProfileNoImage(@Url String url,
                                              @Part("first_name") RequestBody first_name,
                                              @Part("last_name") RequestBody last_name,
                                              @Part("address") RequestBody address,
                                              @Part("gender") RequestBody gender,
                                              @Part("phone") RequestBody phone,
                                              @Part("skype") RequestBody skype,
                                              @Part("primary_number") RequestBody primary_number,
                                              @Part("secondary_number") RequestBody secondary_number,
                                              @Part("office_number") RequestBody office_number,
                                              @Part("viber") RequestBody viber,
                                              @Part("whatsapp") RequestBody whatsapp,
                                              @Part("wechat") RequestBody wechat,
                                              @Part("line") RequestBody line,
                                              @Part("tango") RequestBody tango,
                                              @Part("hike") RequestBody hike,
                                              @Part("qq") RequestBody qq,
                                              @Part("google_talk") RequestBody google_talk,
                                              @Part("twitter") RequestBody twitter,
                                              @Part("organization") RequestBody organization);

    @Multipart
    @POST
    Observable<User> updateUserProfile(@Url String url,
                                       @Part("first_name") RequestBody first_name,
                                       @Part("last_name") RequestBody last_name,
                                       @Part("address") RequestBody address,
                                       @Part("gender") RequestBody gender,
                                       @Part("phone") RequestBody phone,
                                       @Part("skype") RequestBody skype,
                                       @Part("primary_number") RequestBody primary_number,
                                       @Part("secondary_number") RequestBody secondary_number,
                                       @Part("office_number") RequestBody office_number,
                                       @Part("viber") RequestBody viber,
                                       @Part("whatsapp") RequestBody whatsapp,
                                       @Part("wechat") RequestBody wechat,
                                       @Part("line") RequestBody line,
                                       @Part("tango") RequestBody tango,
                                       @Part("hike") RequestBody hike,
                                       @Part("qq") RequestBody qq,
                                       @Part("google_talk") RequestBody google_talk,
                                       @Part("twitter") RequestBody twitter,
                                       @Part("organization") RequestBody organization,
                                       @Part MultipartBody.Part file);

    @GET(GET_SITE_TYPES)
    Single<List<SiteType>> getSiteTypes();

    @GET(GET_CLUSTER_LIST)
    Observable<List<SiteRegion>> getRegionsByProjectId(@Path(value = "project_id", encoded = true) String projectId);

    @GET
    Call<FormHistoryResponse> getFormHistory(@Url String urlNextPage);

    @POST()
    Observable<FCMParameter> postFCMUserParameter(@Url String url, @Body FCMParameter fcmParameter);


    @POST(REMOVE_FCM)
    Observable<Response<Void>> deleteFCMUserParameter(@Body FCMParameter fcmParameter);

    @GET
    Single<NotificationDetail> getNotificationDetail(@Url String url);

    @GET(GET_ALL_CONTACTS)
    Observable<ArrayList<FieldSightContactModel>> getAllContacts();

    @GET
    Observable<FormHistoryResponse> getFormResponse(@Url String url);

    @GET
    Observable<LastSubmissionResponse> getAllFormResponses(@Url String url);

    @Multipart
    @POST
    Call<Site> updateSiteInfo(@Url String url,
                              @Part("id") RequestBody id,
                              @Part("name") RequestBody name,
                              @Part("identifier") RequestBody identifier,
                              @Part("address") RequestBody address,
                              @Part("phone") RequestBody phone,
                              @Part("public_desc") RequestBody public_desc,
                              @Part("additional_desc") RequestBody add_desc,
//                              @Part("type") RequestBody type,
                              @Part MultipartBody.Part logoFile,
                              @Part("site_meta_attributes_ans") RequestBody meta_ans,
                              @Part("latitude") RequestBody lat,
                              @Part("longitude") RequestBody lon
    );

    @Multipart
    @POST
    Observable<Site> updateSite(@Url String url,
                                @Part MultipartBody.Part file,
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

    @Multipart
    @POST
    Call<Site> updateSiteInfoWithNoImage(@Url String url,
                                         @Part("id") RequestBody id,
                                         @Part("name") RequestBody name,
                                         @Part("identifier") RequestBody identifier,
                                         @Part("address") RequestBody address,
                                         @Part("phone") RequestBody phone,
                                         @Part("public_desc") RequestBody public_desc,
                                         @Part("additional_desc") RequestBody add_desc,
//                                         @Part("type") RequestBody type,
                                         @Part("site_meta_attributes_ans") RequestBody meta_ans,
                                         @Part("latitude") RequestBody lat,
                                         @Part("longitude") RequestBody lon
    );


    @GET(APIEndpoint.GET_INSTANCE_SUBMISSION_ATTACHMENTS)
    Observable<HashMap<String, String>> getInstanceMediaList(@Path(value = "instance_submission_id", encoded = true) String instanceSubmissionId);



}
