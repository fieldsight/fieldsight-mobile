package org.odk.collect.naxa.common.database;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.odk.collect.naxa.common.BaseLocalDataSource;
import org.odk.collect.naxa.generalforms.data.GeneralForm;

import java.util.ArrayList;
import java.util.List;

public class SiteOverideRepository  implements BaseLocalDataSource<SiteOveride> {


    @Override
    public LiveData<List<SiteOveride>> getById(boolean forceUpdate, String id) {
        return null;
    }

    @Override
    public LiveData<List<SiteOveride>> getAll() {
        return null;
    }

    @Override
    public void save(SiteOveride... items) {
        //not implemented
    }

    @Override
    public void save(ArrayList<SiteOveride> items) {
        AsyncTask.execute(() -> {
            for (SiteOveride siteOveride: items){
                siteOveride.getGeneralFormIds();
            }
        });
    }

    @Override
    public void updateAll(ArrayList<SiteOveride> items) {

    }
}
