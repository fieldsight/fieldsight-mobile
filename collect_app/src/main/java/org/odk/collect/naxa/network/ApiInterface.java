package org.odk.collect.naxa.network;


import org.odk.collect.naxa.generalforms.GeneralForm;
import org.odk.collect.naxa.login.model.AuthResponse;
import org.odk.collect.naxa.login.model.MeResponse;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static org.odk.collect.naxa.network.APIEndpoint.GET_GENERAL_FORM;

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

}
