package org.bcss.collect.naxa.migrate;

import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class MigrationHelper {


    public final String ROOT = Environment.getExternalStorageDirectory() + File.separator;
    private static final String OLD_FOLDER = "bcss";
    private static final String NEW_FOLDER = "fieldsight";
    private final String MIGRATE_FROM = ROOT + OLD_FOLDER;
    private final String MIGRATE_TO = ROOT + NEW_FOLDER;
    private final String usernameOrEmail;


    public MigrationHelper(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    String getOldRootPath() {
        return MIGRATE_FROM + File.separator + usernameOrEmail;
    }

    String getMigrateFrom() {
        return MIGRATE_FROM;
    }

    String getMigrateTo() {
        return MIGRATE_TO;
    }

    String getNewRootPath() {
        return MIGRATE_TO + File.separator;
    }

    public List<File> listOldAccount() {
        return listFilesInDir(getFileByPath(MIGRATE_FROM));
    }

    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean hasOldAccount() {
        boolean hasOldAccount = false;
        if (listOldAccount() == null) {
            return false;
        }

        for (File file : listOldAccount()) {
            if (usernameOrEmail.equalsIgnoreCase(file.getName())) {
                hasOldAccount = true;
            }
        }

        return hasOldAccount;
    }


    public static List<File> listFilesInDir(final File dir) {
        return listFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        }, false);
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        if (!isDir(dir)) return null;
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    list.add(file);
                }
                if (isRecursive && file.isDirectory()) {
                    //noinspection ConstantConditions
                    list.addAll(listFilesInDirWithFilter(file, filter, true));
                }
            }
        }
        return list;
    }


    private static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }


    String fixFormAndInstancesPath(String oldPath, String usernameOrEmail) {
        String strToReplace = OLD_FOLDER + "/" + usernameOrEmail;
        return oldPath.replace(strToReplace, NEW_FOLDER);
    }

    String fixSitePhotosPath(String oldPath) {
        String strToReplace = Folder.OLD_SITE_PHOTOS;
        return oldPath.replace(strToReplace, Folder.NEW_SITE_PHOTOS);
    }


    public static class Table {
        public static final String project = "table_project";
        static final String my_site = "table_my_site_detail";
        public static final String notifications = "table_notify";
        public static final String all_contacts = "table_contact";

        public final static String instances = "instances";
        public final static String forms = "forms";
    }

    public static class Folder {
        static final String DB_FOLDER = "records";
        static final String FORMS = "forms";
        static final String INSTANCES = "instances";
        static final String METADATA = "metadata";
        static final String OLD_SITE_PHOTOS = "tempimages";
        static final String NEW_SITE_PHOTOS = "sites";
    }

    public static class Database {
        static final String PROJ_SITES = "fieldsight_notify_schema.db";
        static final String INSTANCES = "instances.db";
        static final String FORMS = "forms.db";
    }

    public static class SiteColumns {
        public static final String KEY_SITE_ID = "KEY_SITE_ID";
        public static final String KEY_SITE_TYPE_ID = "KEY_SITE_TYPE_ID";
        public static final String KEY_SITE_PROJECT_ID = "KEY_SITE_PROJECT_ID";
        public static final String KEY_SITE_MY_BOOLEAN = "KEY_SITE_MY_BOOLEAN";
        public static final String KEY_SITE_NAME = "KEY_SITE_NAME";
        public static final String KEY_SITE_ADD_DESC = "KEY_SITE_ADD_DESC";
        public static final String KEY_SITE_PUBLIC_DESC = "KEY_SITE_PUBLIC_DESC";
        public static final String KEY_SITE_TYPE_LABEL = "KEY_SITE_TYPE_LABEL";
        public static final String KEY_SITE_ADDRESS = "KEY_SITE_ADDRESS";
        public static final String KEY_SITE_PROGRESS = "KEY_SITE_PROGRESS";
        public static final String KEY_SITE_IDENTIFIER = "KEY_SITE_IDENTIFIER";
        public static final String KEY_SITE_ORGANIZATION = "KEY_SITE_ORGANIZATION";
        public static final String KEY_SITE_PHONE = "KEY_SITE_PHONE";
        public static final String KEY_SITE_LOGO = "KEY_SITE_LOGO";
        public static final String KEY_SITE_LOCATION = "KEY_SITE_LOCATION";
        public static final String KEY_SITE_LAT = "KEY_SITE_LAT";
        public static final String KEY_SITE_LONG = "KEY_SITE_LONG";
        public static final String KEY_SITE_BLUE_PRINT = "KEY_SITE_BLUE_PRINT";
        public static final String KEY_IS_OFFLINE_SITE_SYNCED = "KEY_IS_OFFLINE_SITE_SYNCED";
        public static final String KEY_SITE_PHOTO_OFFLINE = "KEY_SITE_PHOTO_OFFLINE";
        public static final String KEY_STAGE_FORM_DEPLOYED_FORM = "general_form_use_from";
        public static final String KEY_SITE_META_ATTRS = "meta_attrs";
        public static final String KEY_SITE_REGION = "region";
        public static final String KEY_IS_EDITED = "is_edited";
    }


    public static class ProjectColumns {
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
        static final String KEY_PROJECT_ORGINATION = "organization_name";
        public static final String KEY_PROJECT_ORGINATION_LOGO = "organization_logo";
        public static final String KEY_PROJECT_META_ATTRS = "meta_attrs";
        public static final String KEY_PROJECT_REGIONS = "regions";
    }

    public static class InstanceColumns {

        public static final String DISPLAY_NAME = "displayName";
        public static final String SUBMISSION_URI = "submissionUri";
        public static final String INSTANCE_FILE_PATH = "instanceFilePath";
        public static final String JR_FORM_ID = "jrFormId";
        public static final String JR_VERSION = "jrVersion";

        public static final String FS_FORM_ID = "fsFormId";
        public static final String FS_SITE_ID = "fsSiteId";
        public static final String FS_FORM_DEPLOYED_FROM = "fsFormDeployedFrom";
        public static final String FS_FORM_PROJECT_ID = "fsProjectId";

        public static final String STATUS = "status";
        public static final String CAN_EDIT_WHEN_COMPLETE = "canEditWhenComplete";
        public static final String LAST_STATUS_CHANGE_DATE = "date";
        public static final String DISPLAY_SUBTEXT = "displaySubtext";
        //public static final String DISPLAY_SUB_SUBTEXT = "displaySubSubtext";

    }


    public static class FormColumns {
        // These are the only things needed for an replace
        public static final String DISPLAY_NAME = "displayName";
        public static final String DESCRIPTION = "description";  // can be null
        public static final String JR_FORM_ID = "jrFormId";
        public static final String JR_VERSION = "jrVersion"; // can be null
        public static final String FORM_FILE_PATH = "formFilePath";
        public static final String SUBMISSION_URI = "submissionUri"; // can be null
        public static final String BASE64_RSA_PUBLIC_KEY = "base64RsaPublicKey"; // can be null

        // these are generated for you (but you can replace something else if you want)
        public static final String DISPLAY_SUBTEXT = "displaySubtext";
        public static final String MD5_HASH = "md5Hash";
        public static final String DATE = "date";
        public static final String JRCACHE_FILE_PATH = "jrcacheFilePath";
        public static final String FORM_MEDIA_PATH = "formMediaPath";


        //FieldSightAccountManager Form Ids
        public static final String FS_FORM_ID = "fsFormId";
        //      public static final String IS_SITE_OFFLINE = "is_site_";


        // this is null on create, and can only be set on an update.
        public static final String LANGUAGE = "language";
    }
}
