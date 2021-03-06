package org.fieldsight.naxa.common;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;

import android.content.Context;

import androidx.annotation.NonNull;

import org.fieldsight.naxa.forms.data.local.FieldSightFormDetailDAO;
import org.fieldsight.naxa.forms.data.local.FieldSightFormDetailDAOV3;
import org.fieldsight.naxa.forms.data.local.FieldSightFormDetails;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.contact.ContacstDao;
import org.fieldsight.naxa.contact.FieldSightContactModel;
import org.fieldsight.naxa.data.FieldSightNotification;
import org.fieldsight.naxa.educational.EducationalMaterialsDao;
import org.fieldsight.naxa.generalforms.data.Em;
import org.fieldsight.naxa.generalforms.data.GeneralForm;
import org.fieldsight.naxa.generalforms.data.GeneralFormDAO;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.login.model.SiteMetaAttributesTypeConverter;
import org.fieldsight.naxa.notificationslist.FieldSightNotificationDAO;
import org.fieldsight.naxa.onboarding.SyncableItem;
import org.fieldsight.naxa.previoussubmission.SubmissionDetailDAO;
import org.fieldsight.naxa.previoussubmission.model.SubmissionDetail;
import org.fieldsight.naxa.project.data.ProjectDao;
import org.fieldsight.naxa.scheduled.data.ScheduleForm;
import org.fieldsight.naxa.scheduled.data.ScheduledFormDAO;
import org.fieldsight.naxa.site.SiteClusterDAO;
import org.fieldsight.naxa.site.SiteType;
import org.fieldsight.naxa.site.SiteTypeDAO;
import org.fieldsight.naxa.site.data.SiteRegion;
import org.fieldsight.naxa.site.db.SiteDao;
import org.fieldsight.naxa.stages.data.Stage;
import org.fieldsight.naxa.stages.data.StageFormDAO;
import org.fieldsight.naxa.stages.data.SubStage;
import org.fieldsight.naxa.substages.data.SubStageDAO;
import org.fieldsight.naxa.survey.SurveyForm;
import org.fieldsight.naxa.survey.SurveyFormDAO;
import org.fieldsight.naxa.sync.SyncOLD;
import org.fieldsight.naxa.v3.network.RegionConverter;
import org.fieldsight.naxa.v3.network.SyncDaoV3;
import org.fieldsight.naxa.v3.network.SyncStat;

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
                SyncStat.class,
                FieldSightFormDetails.class,
                FieldsightFormDetailsv3.class

        },
        version = 27)
@TypeConverters({SiteMetaAttributesTypeConverter.class, RegionConverter.class})


public abstract class FieldSightDatabase extends RoomDatabase {

    private static FieldSightDatabase fieldSightDatabase;

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

    public abstract FieldSightFormDetailDAO getFieldSightFormDAO();
    public abstract FieldSightFormDetailDAOV3 getFieldSightFOrmDAOV3();

    private static final String DB_PATH = Collect.METADATA_PATH + File.separator + "fieldsight_database";

    public static FieldSightDatabase getDatabase(final Context context) {
        if (fieldSightDatabase != null) {
            return fieldSightDatabase;
        }

        synchronized (FieldSightDatabase.class) {
            if (fieldSightDatabase == null) {
                fieldSightDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        FieldSightDatabase.class, DB_PATH)
                        .allowMainThreadQueries()//used in org.bcss.naxa.jobs.LocalNotificationJob
                        .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9,
                                MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14,
                                MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19, MIGRATION_19_20, MIGRATION_20_21,
                                MIGRATION_21_22, MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25, MIGRATION_25_26, MIGRATION_26_27)

                        .build();
            }
        }

        return fieldSightDatabase;
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
            database.execSQL("ALTER TABLE sites"
                    + " ADD COLUMN `site` TEXT ");
        }
    };

    private static final Migration MIGRATION_18_19 = new Migration(18, 19) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `fieldsight_formv3` (`id` TEXT NOT NULL, `site` TEXT, `project` TEXT, `site_project_id` TEXT, `type` TEXT, `em` TEXT, `description` TEXT, `settings` TEXT, `formDetails` TEXT, `metaAttributes` TEXT, PRIMARY KEY(`id`))");

        }
    };

    private static final Migration MIGRATION_19_20 = new Migration(19, 20) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE syncstat"
                    + " ADD COLUMN `total` INTEGER NOT NULL DEFAULT 0");

            database.execSQL("ALTER TABLE syncstat"
                    + " ADD COLUMN `progress` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_20_21 = new Migration(20, 21) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `fieldsight_forms` (`fieldSightFormId` TEXT NOT NULL, `formDeployedSiteId` TEXT, `formDeployedProjectId` TEXT, `projectId` INTEGER, `odkFormName` TEXT, `formDescriptionText` TEXT, `odkFormVersion` TEXT, `metadata` TEXT, `formOrder` INTEGER, `formType` TEXT, `totalFormsInProject` INTEGER NOT NULL, `errorStr` TEXT, `formName` TEXT, `downloadUrl` TEXT, `manifestUrl` TEXT, `formID` TEXT, `formVersion` TEXT, `hash` TEXT, `manifestFileHash` TEXT, `isNewerFormVersionAvailable` INTEGER NOT NULL, `areNewerMediaFilesAvailable` INTEGER NOT NULL, PRIMARY KEY(`fieldSightFormId`))");
        }
    };

    private static final Migration MIGRATION_21_22 = new Migration(21, 22) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE sites" + " ADD COLUMN `users` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_22_23 = new Migration(22, 23) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE sites" + " ADD COLUMN `current_progress` REAL NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_23_24 = new Migration(23, 24) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE sites" + " ADD COLUMN `submissions` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_24_25 = new Migration(24, 25) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE sites" + " ADD COLUMN `site_logo` TEXT ");
        }
    };

    private static final Migration MIGRATION_25_26 = new Migration(25, 26) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE project" + " ADD COLUMN `total_regions` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE project" + " ADD COLUMN `total_sites` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE project" + " ADD COLUMN `total_users` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE project" + " ADD COLUMN `total_submissions` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_26_27 = new Migration(26, 27) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE syncstat" + " ADD COLUMN `cancel_by_user` INTEGER NOT NULL DEFAULT 0");
        }
    };

}
