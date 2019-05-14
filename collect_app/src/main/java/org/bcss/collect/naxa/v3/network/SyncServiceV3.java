package org.bcss.collect.naxa.v3.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class SyncServiceV3 extends IntentService {
    /***
     *
     *
     * @Author: Yubaraj Poudel
     * @Since : 14/05/2019
     */

    int projectIndex= 0;
    int regionIndex = 0;
    ArrayList<Project> selectedProject;
    public SyncServiceV3() {
        super("SyncserviceV3");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            selectedProject = Objects.requireNonNull(intent).getParcelableArrayListExtra("projects");
            Timber.i("SyncServiceV3 slectedProject size = %d", selectedProject.size());

            HashMap<String, List<Syncable>> selectedMap = (HashMap<String, List<Syncable>>) intent.getSerializableExtra("selection");

            for (String key : selectedMap.keySet()) {
                Timber.i(readaableSyncParams(key, selectedMap.get(key)));
            }


//            maintain the sync history
//            for projects
//            iterate through the region



        }catch (NullPointerException e){
            Timber.i("Null Pointer");
            e.printStackTrace();}
    }

     void sartSync() {
        if (projectIndex < selectedProject.size()) {
            Project p = selectedProject.get(projectIndex);
            
        }
     }



    private String readaableSyncParams(String projectName, List<Syncable> list) {
        String logString = "";
        for(Syncable syncable : list) {
            logString += "\n title = " + syncable.getTitle() + ", sync = " + syncable.getSync();
        }
        return String.format("%s \n params = %s", projectName, logString);
    }
}
