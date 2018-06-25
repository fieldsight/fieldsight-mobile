package org.odk.collect.naxa.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import org.odk.collect.naxa.database.site.SiteDao;
import org.odk.collect.naxa.database.site.SiteModel;

@Database(entities = {SiteModel.class}, version = 1)
public abstract class FieldSightRoomDatabase extends RoomDatabase {

    public abstract SiteDao siteDao();

    private static FieldSightRoomDatabase INSTANCE;

    public static FieldSightRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FieldSightRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FieldSightRoomDatabase.class, "fieldsight_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
