package org.bcss.collect.naxa.project.data;

import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.network.ApiV3Interface;
import org.bcss.collect.naxa.network.ServiceGenerator;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class ProjectRemoteSource {

    private static ProjectRemoteSource INSTANCE = null;

    public static ProjectRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectRemoteSource();
        }
        return INSTANCE;
    }

    public Single<List<Project>> getProjects() {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getProjects()
                .subscribeOn(Schedulers.io());
    }
}
