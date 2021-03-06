package org.fieldsight.naxa.network;


import org.fieldsight.naxa.contact.FieldSightContactModel;
import org.fieldsight.naxa.firebase.FCMParameter;
import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.login.model.AuthResponse;
import org.fieldsight.naxa.login.model.MeResponse;
import org.fieldsight.naxa.login.model.MySites;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.login.model.User;
import org.fieldsight.naxa.notificationslist.NotificationDetail;
import org.fieldsight.naxa.previoussubmission.model.LastSubmissionResponse;
import org.fieldsight.naxa.project.data.MySiteResponse;
import org.fieldsight.naxa.scheduled.data.ScheduleForm;
import org.fieldsight.naxa.site.SiteType;
import org.fieldsight.naxa.site.data.SiteRegion;
import org.fieldsight.naxa.stages.data.Stage;
import org.fieldsight.naxa.submissions.FormHistoryResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

import static org.fieldsight.naxa.network.APIEndpoint.GET_ALL_CONTACTS;
import static org.fieldsight.naxa.network.APIEndpoint.GET_CLUSTER_LIST;
import static org.fieldsight.naxa.network.APIEndpoint.GET_EXCHANGE_TOKEN;
import static org.fieldsight.naxa.network.APIEndpoint.GET_FORM_SCHEDULE;
import static org.fieldsight.naxa.network.APIEndpoint.GET_GENERAL_FORM;
import static org.fieldsight.naxa.network.APIEndpoint.GET_MY_SITES_V2;
import static org.fieldsight.naxa.network.APIEndpoint.GET_PROJECT_SITES;
import static org.fieldsight.naxa.network.APIEndpoint.GET_SITE_TYPES;
import static org.fieldsight.naxa.network.APIEndpoint.GET_STAGE_SUB_STAGE;
import static org.fieldsight.naxa.network.APIEndpoint.GET_USER_PROFILE;
import static org.fieldsight.naxa.network.APIEndpoint.REMOVE_FCM;

@SuppressWarnings("PMD.ExcessiveParameterList")
public interface ApiInterface {

    @Deprecated
    @GET("/users/me/")
    Observable<MeResponse> getUserInformation();


    @GET(GET_PROJECT_SITES)
    Observable<MeResponse> getUser();

    @GET
    Observable<MySiteResponse> getAssignedSites(@Url String url);

    @GET(GET_MY_SITES_V2)
    Observable<List<MySites>> getAssignedSites();

    @FormUrlEncoded
    @POST("/users/api/get-auth-token/")
    Observable<AuthResponse> getAuthToken(
            @Field("email_or_username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST(GET_EXCHANGE_TOKEN)
    Observable<AuthResponse> getAuthTokenUsingGoogle(
            @Field("access_token") String gmailAccessToken
    );

    @GET(GET_GENERAL_FORM)
    Observable<ArrayList<GeneralForm>> getGeneralFormsObservable(@Path(value = "is_project", encoded = true) String isProject, @Path("id") String id);

    @GET(GET_FORM_SCHEDULE)
    Observable<ArrayList<ScheduleForm>> getScheduleForms(@Path(value = "is_project", encoded = true) String isProject, @Path("id") String id);


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
                                @Part("public_desc") RequestBody publicDesc,
                                @Part("project") RequestBody project,
                                @Part("type") RequestBody type,
                                @Part("region") RequestBody regionID,
                                @Part("site_meta_attributes_ans") RequestBody metaAttrs);

    @GET(GET_USER_PROFILE)
    Observable<User> getUserProfile();

    @Multipart
    @POST
    Observable<User> updateUserProfileNoImage(@Url String url,
                                              @Part("full_name") RequestBody fullName,
                                              @Part("first_name") RequestBody firstName,
                                              @Part("last_name") RequestBody lastName,
                                              @Part("email") RequestBody email,
                                              @Part("address") RequestBody address,
                                              @Part("gender") RequestBody gender,
                                              @Part("phone") RequestBody phone,
                                              @Part("skype") RequestBody skype,
                                              @Part("primary_number") RequestBody primaryNumber,
                                              @Part("secondary_number") RequestBody secondaryNumber,
                                              @Part("office_number") RequestBody officeNumber,
                                              @Part("viber") RequestBody viber,
                                              @Part("whatsapp") RequestBody whatsapp,
                                              @Part("wechat") RequestBody wechat,
                                              @Part("line") RequestBody line,
                                              @Part("tango") RequestBody tango,
                                              @Part("hike") RequestBody hike,
                                              @Part("qq") RequestBody qq,
                                              @Part("google_talk") RequestBody googleTalk,
                                              @Part("twitter") RequestBody twitter
                                              );

    @Multipart
    @POST
    Observable<User> updateUserProfile(@Url String url,
                                       @Part("first_name") RequestBody firstName,
                                       @Part("last_name") RequestBody lastName,
                                       @Part("address") RequestBody address,
                                       @Part("gender") RequestBody gender,
                                       @Part("phone") RequestBody phone,
                                       @Part("skype") RequestBody skype,
                                       @Part("primary_number") RequestBody primaryNumber,
                                       @Part("secondary_number") RequestBody secondaryNumber,
                                       @Part("office_number") RequestBody officeNumber,
                                       @Part("viber") RequestBody viber,
                                       @Part("whatsapp") RequestBody whatsapp,
                                       @Part("wechat") RequestBody wechat,
                                       @Part("line") RequestBody line,
                                       @Part("tango") RequestBody tango,
                                       @Part("hike") RequestBody hike,
                                       @Part("qq") RequestBody qq,
                                       @Part("google_talk") RequestBody googleTalk,
                                       @Part("twitter") RequestBody twitter,
                                       @Part("organization") RequestBody organization,
                                       @Part MultipartBody.Part file);

    @POST(APIEndpoint.POST_REPORT)
    @FormUrlEncoded
    Observable<ResponseBody> submitReport(@Field("device") String device,
                                          @Field("fcm_reg_id") String fcmRegId,
                                          @Field("app_version") String appVersion,
                                          @Field("app_os_version") String appOSVersion,
                                          @Field("message_type") String messageType,
                                          @Field("message") String message,
                                          @Field("device_name") String deviceName,
                                          @Field("lat") String lat,
                                          @Field("lng") String lng);

    @GET(GET_SITE_TYPES)
    Single<List<SiteType>> getSiteTypes();

    @GET(GET_CLUSTER_LIST)
    Observable<List<SiteRegion>> getRegionsByProjectId(@Path(value = "project_id", encoded = true) String projectId);

    @GET
    Observable<FormHistoryResponse> getFormHistory(@Url String urlNextPage);

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
                              @Part("public_desc") RequestBody publicDesc,
                              @Part("additional_desc") RequestBody addDesc,
//                              @Part("type") RequestBody type,
                              @Part MultipartBody.Part logoFile,
                              @Part("site_meta_attributes_ans") RequestBody metaAns,
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
                                @Part("public_desc") RequestBody publicDesc,
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
                                         @Part("public_desc") RequestBody publicDesc,
                                         @Part("additional_desc") RequestBody addDesc,
//                                         @Part("type") RequestBody type,
                                         @Part("site_meta_attributes_ans") RequestBody metaAns,
                                         @Part("latitude") RequestBody lat,
                                         @Part("longitude") RequestBody lon
    );


    @GET(APIEndpoint.GET_INSTANCE_SUBMISSION_ATTACHMENTS)
    Observable<HashMap<String, String>> getInstanceMediaList(@Path(value = "instance_submission_id", encoded = true) String instanceSubmissionId);


    @GET(APIEndpoint.V3.GET_NOTIFICATION)
    Single<ResponseBody> getNotification(@QueryMap Map<String, String> queryParams);

}

