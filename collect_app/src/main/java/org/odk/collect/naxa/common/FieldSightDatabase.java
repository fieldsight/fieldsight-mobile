package org.odk.collect.naxa.common;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.generalforms.data.GeneralFormDAO;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.onboarding.SyncableItems;
import org.odk.collect.naxa.project.data.ProjectDao;
import org.odk.collect.naxa.scheduled.data.ScheduleForm;
import org.odk.collect.naxa.scheduled.data.ScheduledFormDAO;
import org.odk.collect.naxa.site.SiteType;
import org.odk.collect.naxa.site.SiteTypeDAO;
import org.odk.collect.naxa.site.db.SiteDao;
import org.odk.collect.naxa.stages.data.Stage;
import org.odk.collect.naxa.stages.data.StageFormDAO;
import org.odk.collect.naxa.stages.data.SubStage;
import org.odk.collect.naxa.substages.data.SubStageDAO;
import org.odk.collect.naxa.survey.SurveyForm;
import org.odk.collect.naxa.survey.SurveyFormDAO;
import org.odk.collect.naxa.sync.SyncDao;

import java.io.File;

@Database(entities =
        {
                Site.class,
                Project.class,
                SyncableItems.class,
                GeneralForm.class,
                ScheduleForm.class,
                Stage.class,
                SubStage.class,
                SurveyForm.class,
                SiteType.class

        },
        version = 1)

public abstract class FieldSightDatabase extends RoomDatabase {

    private static FieldSightDatabase INSTANCE;

    public abstract SiteDao getSiteDAO();

    public abstract ProjectDao getProjectDAO();

    public abstract SyncDao getSyncDAO();

    public abstract GeneralFormDAO getProjectGeneralFormDao();

    public abstract ScheduledFormDAO getProjectScheduledFormsDAO();

    public abstract StageFormDAO getStageDAO();

    public abstract SubStageDAO getSubStageDAO();

    public abstract SurveyFormDAO getSurveyDAO();

    public abstract SiteTypeDAO getSiteTypesDAO();

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
