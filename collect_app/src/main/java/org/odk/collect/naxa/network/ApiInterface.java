package org.odk.collect.naxa.network;


import org.odk.collect.naxa.login.model.AuthResponse;
import org.odk.collect.naxa.login.model.MeResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @GET("/users/me/")
    Observable<MeResponse> getUserInformation();


    @FormUrlEncoded
    @POST("api/get-token/")
    Observable<AuthResponse> getAuthToken(
            @Field("username_or_email") String username,
            @Field("password") String password
    );
}
