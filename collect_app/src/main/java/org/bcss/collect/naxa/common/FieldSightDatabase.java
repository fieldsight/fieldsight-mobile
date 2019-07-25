package org.bcss.collect.naxa.common;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

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
import org.bcss.collect.naxa.sync.SyncOLD;
import org.bcss.collect.naxa.v3.network.RegionConverter;
import org.bcss.collect.naxa.v3.network.SyncDaoV3;
import org.bcss.collect.naxa.v3.network.SyncStat;

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
                SubmissionDetail.class,
                SyncStat.class

        },
        version = 18)
@TypeConverters({SiteMetaAttributesTypeConverter.class, RegionConverter.class})


public abstract class FieldSightDatabase extends RoomDatabase {

    private static FieldSightDatabase INSTANCE;

    public abstract SiteDao getSiteDAO();

    public abstract ProjectDao getProjectDAO();

    public abstract SyncOLD getSyncDAO();

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

    public abstract SyncDaoV3 getSyncDaoV3();

    private static final String DB_PATH = Collect.METADATA_PATH + File.separator + "fieldsight_database";

    public static FieldSightDatabase getDatabase(final Context context) {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        synchronized (FieldSightDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        FieldSightDatabase.class, DB_PATH)
                        .allowMainThreadQueries()//used in org.bcss.collect.naxa.jobs.LocalNotificationJob
                        .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9,
                                MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14,
                                MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18)

                        .build();
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

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DELETE FROM sync");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE FieldSightNotification "
                    + " ADD COLUMN `formVersion` TEXT ");
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE FieldSightNotification "
                    + " ADD COLUMN `siteIdentifier` TEXT ");
        }
    };


    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE FieldSightNotification "
                    + " ADD COLUMN `isDeployedFromSite` INTEGER NOT NULL default 0");
        }
    };

    private static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE FieldSightNotification "
                    + " ADD COLUMN `schedule_forms_count` TEXT ");
        }
    };
    private static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `syncstat` (`project_id` TEXT NOT NULL, `type` TEXT NOT NULL, `failed_url` TEXT, `started` INTEGER NOT NULL, `status` INTEGER NOT NULL, `created_date` INTEGER NOT NULL, PRIMARY KEY(`project_id`, `type`))");
        }
    };

    private static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE project"
                    + " ADD COLUMN `url` TEXT");
        }
    };


    private static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE FieldSightNotification");
            database.execSQL("CREATE TABLE IF NOT EXISTS `FieldSightNotification` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `notificationType` TEXT, `notifiedDate` TEXT, `notifiedTime` TEXT, `idString` TEXT, `fsFormId` TEXT, `fsFormIdProject` TEXT, `formName` TEXT, `siteId` TEXT, `siteName` TEXT, `projectId` TEXT, `projectName` TEXT, `formStatus` TEXT, `role` TEXT, `isFormDeployed` TEXT, `details_url` TEXT, `comment` TEXT, `formType` TEXT, `isRead` INTEGER NOT NULL, `formSubmissionId` TEXT, `formVersion` TEXT, `siteIdentifier` TEXT, `receivedDateTime` TEXT, `isDeployedFromSite` INTEGER NOT NULL, `schedule_forms_count` TEXT)");
            database.execSQL("CREATE UNIQUE INDEX `index_FieldSightNotification_receivedDateTime` ON `FieldSightNotification` (`receivedDateTime`)");

        }
    };

    private static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE project"
                    + " ADD COLUMN `regionList` TEXT");
        }
    };


    private static final Migration MIGRATION_14_15 = new Migration(14, 15) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `syncstat` (`project_id` TEXT NOT NULL, `type` TEXT NOT NULL, `failed_url` TEXT, `started` INTEGER NOT NULL, `status` INTEGER NOT NULL, `created_date` INTEGER NOT NULL, PRIMARY KEY(`project_id`, `type`))");
        }
    };

    private static final Migration MIGRATION_15_16 = new Migration(15, 16) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE FieldSightNotification");
            database.execSQL("CREATE TABLE IF NOT EXISTS `FieldSightNotification` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `notificationType` TEXT, `notifiedDate` TEXT, `notifiedTime` TEXT, `idString` TEXT, `fsFormId` TEXT, `fsFormIdProject` TEXT, `formName` TEXT, `siteId` TEXT, `siteName` TEXT, `projectId` TEXT, `projectName` TEXT, `formStatus` TEXT, `role` TEXT, `isFormDeployed` TEXT, `details_url` TEXT, `comment` TEXT, `formType` TEXT, `isRead` INTEGER NOT NULL, `formSubmissionId` TEXT, `formVersion` TEXT, `siteIdentifier` TEXT, `receivedDateTime` TEXT, `isDeployedFromSite` INTEGER NOT NULL, `schedule_forms_count` TEXT, `receivedDateTimeInMillis` INTEGER NOT NULL DEFAULT 0)");
            database.execSQL("CREATE UNIQUE INDEX `index_FieldSightNotification_receivedDateTimeInMillis` ON `FieldSightNotification` (`receivedDateTimeInMillis`)");
        }
    };

    private static final Migration MIGRATION_16_17 = new Migration(16, 17) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE project"
                    + " ADD COLUMN `terms_and_labels` TEXT");
        }
    };

    private static final Migration MIGRATION_17_18 = new Migration(17, 18) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE site"
                    + " ADD COLUMN `enable_subsites` INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE site"
                    + " ADD COLUMN `site` TEXT ");
        }
    };

}
