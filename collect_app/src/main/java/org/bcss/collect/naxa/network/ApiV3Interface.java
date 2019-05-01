package org.bcss.collect.naxa.network;

import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.project.data.model.ProjectResponseV3;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiV3Interface {

    @GET(APIEndpoint.V3.GET_PROJECTS)
    Single<List<Project>> getProjects();
}
