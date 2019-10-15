package org.fieldsight.naxa.common.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.sync.DownloadableItem;
import org.fieldsight.naxa.sync.DownloadableItemDAO;

import java.io.File;

@Database(entities =
        {
                SiteOveride.class,
                ProjectFilter.class,
                DownloadableItem.class,
                SiteUploadHistory.class

        },
        version = 10)

public abstract class FieldSightConfigDatabase extends RoomDatabase {

    private static FieldSightConfigDatabase fieldSightConfigDatabase;

    public abstract SiteOverideDAO getSiteOverideDAO();

    public abstract ProjectFilterDAO getProjectFilterDAO();

    public abstract DownloadableItemDAO getSyncDao();

    public abstract SiteUploadHistoryDAO getSiteUploadHistoryDao();

    private static final String DB_PATH = Collect.METADATA_PATH + File.separator + "fieldsight_cofig";

    public static FieldSightConfigDatabase getDatabase(final Context context) {
        if (fieldSightConfigDatabase == null) {
            synchronized (FieldSightConfigDatabase.class) {
                if (fieldSightConfigDatabase == null) {
                    fieldSightConfigDatabase = Room.databaseBuilder(context.getApplicationContext(),
                            FieldSightConfigDatabase.class, DB_PATH)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return fieldSightConfigDatabase;
    }


}
