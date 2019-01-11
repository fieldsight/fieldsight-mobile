package org.bcss.collect.naxa.common.database;

import android.arch.lifecycle.LiveData;

import org.bcss.collect.naxa.common.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class SiteOverideRepository implements BaseRepository<SiteOveride> {

    @Override
    public LiveData<List<SiteOveride>> getAll(boolean forceUpdate) {
        return null;
    }

    @Override
    public void save(SiteOveride... items) {

    }

    @Override
    public void save(ArrayList<SiteOveride> items) {

    }

    @Override
    public void updateAll(ArrayList<SiteOveride> items) {

    }

}
