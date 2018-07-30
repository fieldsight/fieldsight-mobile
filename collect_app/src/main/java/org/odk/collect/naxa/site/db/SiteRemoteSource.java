package org.odk.collect.naxa.site.db;

import org.odk.collect.naxa.common.BaseRemoteDataSource;
import org.odk.collect.naxa.login.model.Site;

public class SiteRemoteSource implements BaseRemoteDataSource<Site> {

    private static SiteRemoteSource INSTANCE;
    private SiteDao dao;


    public static SiteRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SiteRemoteSource();
        }
        return INSTANCE;
    }



    @Override
    public void getAll() {

    }

    public void create() {

    }
}
