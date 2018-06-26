package org.odk.collect.naxa.network;


import org.odk.collect.naxa.login.model.AuthResponse;
import org.odk.collect.naxa.login.model.MeResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @GET("/users/me/")
    Observable<MeResponse> getUserInformation();


    @FormUrlEncoded
    @POST("/users/api/get-auth-token/")
    Single<AuthResponse> getAuthToken(
            @Field("email_or_username") String username,
            @Field("password") String password
    );
}
