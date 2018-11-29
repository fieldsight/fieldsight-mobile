package org.bcss.collect.naxa.common;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.contact.ContacstDao;
import org.bcss.collect.naxa.contact.FieldSightContactModel;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.educational.EducationalMaterialsDao;
import org.bcss.collect.naxa.generalforms.data.Em;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.generalforms.data.GeneralFormDAO;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;

import org.bcss.collect.naxa.login.model.SiteMetaAttributesTypeConverter;
import org.bcss.collect.naxa.notificationslist.FieldSightNotificationDAO;
import org.bcss.collect.naxa.onboarding.SyncableItem;
import org.bcss.collect.naxa.previoussubmission.SubmissionDetailDAO;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;
import org.bcss.collect.naxa.project.data.ProjectDao;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormDAO;
import org.bcss.collect.naxa.site.SiteClusterDAO;
import org.bcss.collect.naxa.site.SiteType;
import org.bcss.collect.naxa.site.SiteTypeDAO;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.bcss.collect.naxa.site.db.SiteDao;
import org.bcss.collect.naxa.stages.data.Stage;
import org.bcss.collect.naxa.stages.data.StageFormDAO;
import org.bcss.collect.naxa.stages.data.SubStage;
import org.bcss.collect.naxa.substages.data.SubStageDAO;
import org.bcss.collect.naxa.survey.SurveyForm;
import org.bcss.collect.naxa.survey.SurveyFormDAO;
import org.bcss.collect.naxa.sync.SyncDao;

import java.io.File;

@Database(entities =
        {
                Site.class,
                Project.class,
                SyncableItem.class,
                GeneralForm.class,
                ScheduleForm.class,
                Stage.class,
                SubStage.class,
                SurveyForm.class,
                SiteType.class,
                SiteRegion.class,
                FieldSightNotification.class,
                Em.class,
                FieldSightContactModel.class,
                SubmissionDetail.class

        },
        version = 5)
@TypeConverters({SiteMetaAttributesTypeConverter.class})

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

    public abstract SiteClusterDAO getSiteClusterDAO();

    public abstract FieldSightNotificationDAO getFieldSightNotificationDAO();

    public abstract EducationalMaterialsDao getEducationalMaterialDAO();

    public abstract ContacstDao getContactsDao();

    private static final String DB_PATH = Collect.METADATA_PATH + File.separator + "fieldsight_database";

    public static FieldSightDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FieldSightDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FieldSightDatabase.class, DB_PATH)
                            .fallbackToDestructiveMigrationFrom(1)
                            .addMigrations(MIGRATION_4_5)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    public abstract SubmissionDetailDAO getSubmissionDetailDAO();

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DELETE FROM sync");
        }
    };


}
