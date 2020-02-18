package org.fieldsight.naxa.v3.network;

import org.fieldsight.naxa.network.ServiceGenerator;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ProjectRemoteSource {

    private static ProjectRemoteSource projectRemoteSource;

    public synchronized static ProjectRemoteSource getInstance() {
        if (projectRemoteSource == null) {
            projectRemoteSource = new ProjectRemoteSource();
        }
        return projectRemoteSource;
    }

    public Single<ResponseBody> getProjects() {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getProjects()
                .subscribeOn(Schedulers.io());
    }

    public Single<ResponseBody> getProjectCounts(String projectIdsParams) {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getProjects()
                .subscribeOn(Schedulers.io());
    }
}