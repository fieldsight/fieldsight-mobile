package org.odk.collect.naxa.network;


import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.login.model.AuthResponse;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.scheduled.data.ScheduleForm;
import org.odk.collect.naxa.stages.data.Stage;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static org.odk.collect.naxa.network.APIEndpoint.GET_FORM_SCHEDULE;
import static org.odk.collect.naxa.network.APIEndpoint.GET_GENERAL_FORM;
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
    Observable<ArrayList<Stage>> getStageSubStage(String createdFromProject, String creatorsId);
}
