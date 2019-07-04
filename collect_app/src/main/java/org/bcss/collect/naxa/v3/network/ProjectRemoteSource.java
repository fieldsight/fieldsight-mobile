package org.bcss.collect.naxa.v3.network;

import org.bcss.collect.naxa.network.ServiceGenerator;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ProjectRemoteSource {

    private static ProjectRemoteSource INSTANCE = null;

    public static ProjectRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectRemoteSource();
        }
        return INSTANCE;
    }

    public Single<ResponseBody> getProjects() {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getProjects()
                .subscribeOn(Schedulers.io());
    }
}