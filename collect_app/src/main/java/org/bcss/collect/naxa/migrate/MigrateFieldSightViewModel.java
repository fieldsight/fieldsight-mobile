package org.bcss.collect.naxa.migrate;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bcss.collect.android.tasks.sms.models.SmsSubmission;
import org.bcss.collect.android.utilities.FileUtils;
import org.bcss.collect.naxa.common.GSONInstance;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.project.data.ProjectRepository;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class MigrateFieldSightViewModel extends ViewModel {

    private MutableLiveData<List<File>> oldAccounts = new MutableLiveData<>();
    private String usernameOrEmail = null;
    private MigrationHelper migrationHelper;


    private void copyProjects() {
        SQLiteDatabase db = getProjSiteDB(usernameOrEmail);
        Cursor cursor = null;
        cursor = selectAll(db, Table.project);
        ArrayList<Project> projects = new ArrayList<>();

        while (cursor.moveToNext()) {

            Project project = new Project();

            project.setId(getString(cursor, ProjectColumns.KEY_PROJECT_ID));
            project.setTypeId(Integer.valueOf(
                    getString(cursor, ProjectColumns.KEY_PROJECT_TYPE_ID)
            ));
            project.setTypeLabel(getString(cursor, ProjectColumns.KEY_PROJECT_TYPE_LABEL));
            project.setPhone(getString(cursor, ProjectColumns.KEY_PROJECT_PHONE));
            project.setName(getString(cursor, ProjectColumns.KEY_PROJECT_NAME));
            project.setDescription(getString(cursor, ProjectColumns.KEY_PROJECT_DESC));
            project.setAddress(getString(cursor, ProjectColumns.KEY_PROJECT_ADDRESS));
            project.setLat(getString(cursor, ProjectColumns.KEY_PROJECT_LAT));
            project.setLon(getString(cursor, ProjectColumns.KEY_PROJECT_LON));
            //todo check and migrate photo
            getString(cursor, ProjectColumns.KEY_PROJECT_LOGO);
            //todo check and miragte mananger
            getString(cursor, ProjectColumns.KEY_PROJECT_MANAGER);
            project.setOrganizationName(getString(cursor, ProjectColumns.KEY_PROJECT_ORGINATION));
            project.setOrganizationName(getString(cursor, ProjectColumns.KEY_PROJECT_ORGINATION));
            project.setOrganizationlogourl(getString(cursor, ProjectColumns.KEY_PROJECT_ORGINATION_LOGO));

            String metaAttrs = getString(cursor, ProjectColumns.KEY_PROJECT_META_ATTRS);
            if (metaAttrs != null && metaAttrs.length() > 0) {
                Type siteMetaAttrsList = new TypeToken<List<SiteMetaAttribute>>() {
                }.getType();
                List<SiteMetaAttribute> list = GSONInstance.getInstance().fromJson(metaAttrs, siteMetaAttrsList);
                project.setSiteMetaAttributes(list);
            }


            project.setSiteClusters(getString(cursor, ProjectColumns.KEY_PROJECT_REGIONS));
            project.setHasClusteredSites(Boolean.valueOf(getString(cursor, ProjectColumns.KEY_HAS_CLUSTER)));
            projects.add(project);

        }


        ProjectRepository.getInstance().save(projects);

        cursor.close();
        db.close();

    }


    private void copySites() {

    }

    private void copyInstancesFolder() {
        String oldPath = migrationHelper.getOldRootPath() + File.separator + Folder.INSTANCES;
        String newPath = migrationHelper.getNewRootPath() + File.separator + Folder.INSTANCES;
        FileUtils.copyDir(oldPath, newPath);
    }

    private void copyFormsFolder() {
        String oldPath = migrationHelper.getOldRootPath() + File.separator + Folder.FORMS;
        String newPath = migrationHelper.getNewRootPath() + File.separator + Folder.FORMS;
        FileUtils.copyDir(oldPath, newPath);
    }

    private void copyInstances() {

    }

    private void copyForms() {

    }

    private Cursor selectAll(SQLiteDatabase db, String tableName) {
        String sql = String.format("SELECT * FROM %s", tableName);
        return db.rawQuery(sql, null);
    }

    private String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    private SQLiteDatabase getProjSiteDB(String usernameOrEmail) {
        File dbfile = new File(migrationHelper.getOldRootPath() +
                File.separator + Folder.DB_FOLDER +
                File.separator + Database.PROJ_SITES);

        if (!dbfile.exists())
            throw new RuntimeException("Database file does not exist for " + usernameOrEmail);

        return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
    }

    public MigrateFieldSightViewModel() {

    }

    public Observable<Integer> copyFromOldAccount() {

        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    List<File> files = migrationHelper.listOldAccount();
                    for (File file : files) {
                        if (usernameOrEmail.equals(file.getName())) {

                            copyProjects();
                            emitter.onNext(1);

                            copyFormsFolder();
                            emitter.onNext(2);

                            copyInstancesFolder();
                            emitter.onNext(3);
                            break;
                        }
                    }

                    emitter.onComplete();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    emitter.onError(ex);
                }

            }
        });
    }


    public void setUserNameEmail(String userNameOrEmail) {
        this.usernameOrEmail = userNameOrEmail;
        migrationHelper = new MigrationHelper(usernameOrEmail);
        oldAccounts.setValue(migrationHelper.listOldAccount());
    }


    public static class Table {
        public static final String project = "table_project";
        public static final String my_site = "table_my_site_detail";
        public static final String notifications = "table_notify";
        public static final String all_contacts = "table_contact";

    }

    public static class Folder {
        public static final String DB_FOLDER = "records";
        public static final String FORMS = "forms";
        public static final String INSTANCES = "instances";
    }

    public static class Database {
        public static final String PROJ_SITES = "fieldsight_notify_schema.db";
    }


    private static class ProjectColumns {
        public static final String KEY_PROJECT_ID = "KEY_PROJECT_ID";
        public static final String KEY_PROJECT_TYPE_ID = "KEY_PROJECT_TYPE_ID";
        public static final String KEY_PROJECT_TYPE_LABEL = "KEY_PROJECT_TYPE_LABEL";
        public static final String KEY_PROJECT_PHONE = "KEY_PROJECT_PHONE";
        public static final String KEY_PROJECT_NAME = "KEY_PROJECT_NAME";
        public static final String KEY_PROJECT_DESC = "KEY_PROJECT_DESC";
        public static final String KEY_PROJECT_ADDRESS = "KEY_PROJECT_ADDRESS";
        public static final String KEY_PROJECT_LAT = "KEY_PROJECT_LAT";
        public static final String KEY_PROJECT_LON = "KEY_PROJECT_LON";
        public static final String KEY_PROJECT_LOGO = "KEY_PROJECT_LOGO";
        public static final String KEY_PROJECT_EMAIL = "KEY_PROJECT_EMAIL";
        public static final String KEY_PROJECT_MANAGER = "KEY_PROJECT_MANAGER";
        public static final String KEY_HAS_CLUSTER = "has_clusters";
        private static final String KEY_PROJECT_ORGINATION = "organization_name";
        private static final String KEY_PROJECT_ORGINATION_LOGO = "organization_logo";
        private static final String KEY_PROJECT_META_ATTRS = "meta_attrs";
        public static final String KEY_PROJECT_REGIONS = "regions";
    }

}
