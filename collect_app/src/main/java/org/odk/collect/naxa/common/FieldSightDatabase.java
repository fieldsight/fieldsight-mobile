package org.odk.collect.naxa.common;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.onboarding.SyncableItems;
import org.odk.collect.naxa.project.db.ProjectDao;
import org.odk.collect.naxa.site.db.SiteDao;
import org.odk.collect.naxa.sync.SyncDao;

import java.io.File;

@Database(entities = {Site.class, Project.class, SyncableItems.class}, version = 3)
public abstract class FieldSightDatabase extends RoomDatabase {

    private static FieldSightDatabase INSTANCE;

    public abstract SiteDao getSiteDAO();

    public abstract ProjectDao getProjectDAO();

    public abstract SyncDao getSyncDAO();

    private static final String DB_PATH = Collect.METADATA_PATH + File.separator + "fieldsight_database";

    public static FieldSightDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FieldSightDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FieldSightDatabase.class, DB_PATH)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
