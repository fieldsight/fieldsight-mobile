package org.odk.collect.naxa.network;


import org.odk.collect.naxa.login.model.MeResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("/users/me/")
    Observable<MeResponse> getUserInformation();


}
