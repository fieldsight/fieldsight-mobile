package org.bcss.collect.naxa.migrate;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.reflect.TypeToken;

import org.bcss.collect.android.dao.FormsDao;
import org.bcss.collect.android.dao.InstancesDao;
import org.bcss.collect.android.dto.Form;
import org.bcss.collect.android.dto.Instance;
import org.bcss.collect.android.utilities.FileUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.GSONInstance;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.SiteBuilder;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.project.data.ProjectRepository;
import org.bcss.collect.naxa.site.db.SiteLocalSource;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class MigrateFieldSightViewModel extends ViewModel {

    private MutableLiveData<List<File>> oldAccounts = new MutableLiveData<>();
    private String usernameOrEmail = null;
    private MigrationHelper migrationHelper;


    private void copyProjects() {
        SQLiteDatabase db = getProjSiteDB();
        Cursor cursor = null;
        cursor = selectAll(db, MigrationHelper.Table.project);
        ArrayList<Project> projects = new ArrayList<>();

        while (cursor.moveToNext()) {

            Project project = new Project();

            project.setId(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_ID));
            project.setTypeId(Integer.valueOf(
                    getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_TYPE_ID)
            ));
            project.setTypeLabel(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_TYPE_LABEL));
            project.setPhone(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_PHONE));
            project.setName(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_NAME));
            project.setDescription(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_DESC));
            project.setAddress(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_ADDRESS));
            project.setLat(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_LAT));
            project.setLon(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_LON));
            //todo check and migrate photo
            getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_LOGO);
            //todo check and miragte mananger
            getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_MANAGER);
            project.setOrganizationName(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_ORGINATION));

            project.setOrganizationlogourl(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_ORGINATION_LOGO));

            String metaAttrs = getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_META_ATTRS);
            if (metaAttrs != null && metaAttrs.length() > 0) {
                Type siteMetaAttrsList = new TypeToken<List<SiteMetaAttribute>>() {
                }.getType();
                List<SiteMetaAttribute> list = GSONInstance.getInstance().fromJson(metaAttrs, siteMetaAttrsList);
                project.setSiteMetaAttributes(list);
            }


            project.setSiteClusters(getString(cursor, MigrationHelper.ProjectColumns.KEY_PROJECT_REGIONS));
            project.setHasClusteredSites(Boolean.valueOf(getString(cursor, MigrationHelper.ProjectColumns.KEY_HAS_CLUSTER)));
            projects.add(project);

        }


        ProjectRepository.getInstance().save(projects);

        cursor.close();
        db.close();

    }

    private void copySitePhotosFolder() {
        String oldPath = migrationHelper.getMigrateFrom() + File.separator + MigrationHelper.Folder.OLD_SITE_PHOTOS;
        String newPath = migrationHelper.getMigrateTo() + File.separator + MigrationHelper.Folder.NEW_SITE_PHOTOS;
        FileUtils.copyDir(oldPath, newPath);
    }

    private void copySites() {
        SQLiteDatabase db = getProjSiteDB();

        Cursor cursor = null;
        cursor = selectAll(db, MigrationHelper.Table.my_site);

        while (cursor.moveToNext()) {

            Integer curStatus = Integer.valueOf(getString(cursor, MigrationHelper.SiteColumns.KEY_IS_OFFLINE_SITE_SYNCED));
            boolean isOfflineSite = curStatus == Constant.SiteStatus.IS_UNVERIFIED_SITE || curStatus == Constant.SiteStatus.IS_VERIFIED_BUT_UNSYNCED;

            String fixedSitePhotoPath = migrationHelper.fixSitePhotosPath(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_PHOTO_OFFLINE));

            if (isOfflineSite) {
                Site site = new SiteBuilder()
                        .setName(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_NAME))
                        .setAddress(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_ADDRESS))
                        .setAdditionalDesc(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_ADD_DESC))
                        .setId(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_ID))
                        .setIdentifier(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_IDENTIFIER))
                        .setIsSiteVerified(curStatus)
                        .setLatitude(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_LAT))
                        .setLongitude(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_LONG))
                        .setMetaAttributes(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_META_ATTRS))
                        .setPhone(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_PHONE))
                        .setProject(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_PROJECT_ID))
                        .setPublicDesc(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_PUBLIC_DESC))
                        .setTypeId(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_TYPE_ID))
                        .setRegion(getString(cursor, MigrationHelper.SiteColumns.KEY_SITE_REGION))
                        .setGeneralFormDeployedFrom(Constant.FormDeploymentFrom.PROJECT)
                        .setStagedFormDeployedFrom(Constant.FormDeploymentFrom.PROJECT)
                        .setScheduleFormDeployedForm(Constant.FormDeploymentFrom.PROJECT)
                        .setLogo(fixedSitePhotoPath)
                        .createSite();

                SiteLocalSource.getInstance().save(site);
            }



        }

    }

    private void copyInstancesFolder() {
        String oldPath = migrationHelper.getOldRootPath() + File.separator + MigrationHelper.Folder.INSTANCES;
        String newPath = migrationHelper.getNewRootPath() + File.separator + MigrationHelper.Folder.INSTANCES;
        FileUtils.copyDir(oldPath, newPath);
    }

    private void copyFormsFolder() {
        String oldPath = migrationHelper.getOldRootPath() + File.separator + MigrationHelper.Folder.FORMS;
        String newPath = migrationHelper.getNewRootPath() + File.separator + MigrationHelper.Folder.FORMS;
        FileUtils.copyDir(oldPath, newPath);
    }

    private void copyInstances() {
        SQLiteDatabase db = getInstancesDB();
        InstancesDao dao = new InstancesDao();
        Cursor cursor;
        cursor = selectAll(db, MigrationHelper.Table.instances);
        while (cursor.moveToNext()) {

            String formDeployedFrom = getString(cursor, MigrationHelper.InstanceColumns.FS_FORM_DEPLOYED_FROM);
            String fsFormId = getString(cursor, MigrationHelper.InstanceColumns.FS_FORM_ID);
            String siteId = getString(cursor, MigrationHelper.InstanceColumns.FS_SITE_ID);

            String fixedSubmissionUrl = InstancesDao.generateSubmissionUrl(formDeployedFrom, siteId, fsFormId);

            String fixedInstancePath = migrationHelper.fixFormAndInstancesPath(
                    getString(cursor, MigrationHelper.InstanceColumns.INSTANCE_FILE_PATH),
                    usernameOrEmail);

            Long lastStatusChangeDate = Long.valueOf(getString(cursor, MigrationHelper.InstanceColumns.LAST_STATUS_CHANGE_DATE));

            Instance instance = new Instance.Builder()
                    .submissionUri(fixedSubmissionUrl)
                    .instanceFilePath(fixedInstancePath)
                    .displayName(getString(cursor, MigrationHelper.InstanceColumns.DISPLAY_NAME))
                    .canEditWhenComplete(getString(cursor, MigrationHelper.InstanceColumns.CAN_EDIT_WHEN_COMPLETE))
                    .jrFormId(getString(cursor, MigrationHelper.InstanceColumns.JR_FORM_ID))
                    .fieldSightSiteId(getString(cursor, MigrationHelper.InstanceColumns.FS_SITE_ID))
                    .jrVersion(getString(cursor, MigrationHelper.InstanceColumns.JR_VERSION))
                    .status(getString(cursor, MigrationHelper.InstanceColumns.STATUS))
                    .displaySubtext(getString(cursor, MigrationHelper.InstanceColumns.DISPLAY_SUBTEXT))
                    .lastStatusChangeDate(lastStatusChangeDate)
                    .build();

            ContentValues value = dao.getValuesFromInstanceObject(instance);
            dao.saveInstance(value);
        }
    }

    private void copyForms() {
        SQLiteDatabase db = getFormsDB();
        FormsDao dao = new FormsDao();
        Cursor cursor;
        cursor = selectAll(db, MigrationHelper.Table.forms);
        while (cursor.moveToNext()) {

            String fixedFormFilePath = migrationHelper.fixFormAndInstancesPath(getString(cursor, MigrationHelper.FormColumns.FORM_FILE_PATH), usernameOrEmail);
            String fixedFormMediaPath = migrationHelper.fixFormAndInstancesPath(getString(cursor, MigrationHelper.FormColumns.FORM_MEDIA_PATH), usernameOrEmail);
            String fixedJrCacheFilePath = migrationHelper.fixFormAndInstancesPath(getString(cursor, MigrationHelper.FormColumns.JRCACHE_FILE_PATH), usernameOrEmail);

            Form form = new Form.Builder()
                    .formMediaPath(fixedFormMediaPath)
                    .formFilePath(fixedFormFilePath)
                    .jrCacheFilePath(fixedJrCacheFilePath)
                    .displayName(getString(cursor, MigrationHelper.FormColumns.DISPLAY_NAME))
                    .displaySubtext(getString(cursor, MigrationHelper.FormColumns.DISPLAY_SUBTEXT))
                    .description(getString(cursor, MigrationHelper.FormColumns.DESCRIPTION))
                    .jrFormId(getString(cursor, MigrationHelper.FormColumns.JR_FORM_ID))
                    .jrVersion(getString(cursor, MigrationHelper.FormColumns.JR_VERSION))
                    .date(Long.valueOf(getString(cursor, MigrationHelper.FormColumns.DATE)))
                    .language(getString(cursor, MigrationHelper.FormColumns.LANGUAGE))
                    .base64RSAPublicKey(getString(cursor, MigrationHelper.FormColumns.BASE64_RSA_PUBLIC_KEY))

                    .build();
            ContentValues values = dao.getValuesFromFormObject(form);
            dao.saveForm(values);
        }
    }


    private SQLiteDatabase getFormsDB() {
        String dbPath = migrationHelper.getOldRootPath() + File.separator + MigrationHelper.Folder.METADATA + File.separator + MigrationHelper.Database.FORMS;
        File dbfile = new File(dbPath);

        if (!dbfile.exists())
            throw new RuntimeException("Forms Database does not exist for " + usernameOrEmail);

        return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
    }

    private Cursor selectAll(SQLiteDatabase db, String tableName) {
        String sql = String.format("SELECT * FROM %s", tableName);
        return db.rawQuery(sql, null);
    }

    private String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    private SQLiteDatabase getProjSiteDB() {
        File dbfile = new File(migrationHelper.getOldRootPath() +
                File.separator + MigrationHelper.Folder.DB_FOLDER +
                File.separator + MigrationHelper.Database.PROJ_SITES);

        if (!dbfile.exists())
            throw new RuntimeException("Database file does not exist for " + usernameOrEmail);

        return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
    }

    private SQLiteDatabase getInstancesDB() {
        String dbPath = migrationHelper.getOldRootPath() + File.separator + MigrationHelper.Folder.METADATA + File.separator + MigrationHelper.Database.INSTANCES;
        File dbfile = new File(dbPath);

        if (!dbfile.exists())
            throw new RuntimeException("Instance Database does not exist for " + usernameOrEmail);

        return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
    }

    public MigrateFieldSightViewModel() {

    }

    Observable<Integer> copyFromOldAccount() {

        return Observable.create(emitter -> {
            try {
                List<File> files = migrationHelper.listOldAccount();
                for (File file : files) {
                    if (usernameOrEmail.equals(file.getName())) {

                        copyProjects();
                        emitter.onNext(1);

                        copyFormsFolder();
                        emitter.onNext(2);

                        copyForms();
                        emitter.onNext(3);

                        copyInstancesFolder();
                        emitter.onNext(4);

                        copyInstances();
                        emitter.onNext(5);

                        copySites();
                        emitter.onNext(6);

                        copySitePhotosFolder();
                        emitter.onNext(7);

                        FileUtils.deleteDir((new File(migrationHelper.getOldRootPath())));
                        emitter.onNext(8);
                        break;
                    }
                }

                emitter.onComplete();
            } catch (Exception ex) {
                ex.printStackTrace();
                emitter.onError(ex);
            }

        });
    }


    void setUserNameEmail(String userNameOrEmail) {
        this.usernameOrEmail = userNameOrEmail;
        migrationHelper = new MigrationHelper(usernameOrEmail);
        oldAccounts.setValue(migrationHelper.listOldAccount());
    }


}
