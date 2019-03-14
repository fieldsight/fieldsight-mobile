package org.bcss.collect.naxa.common;

import org.bcss.collect.android.R;

public class Constant {


    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_FORM_DEPLOYED_FORM = "deployed_from";
    public static final String DEFAULT_SITE_TYPE = "0";
    public static final String KEY_BASE_URL = "fieldsight_server_url";
    public static int selectedFragmentId;
    public static final String EXTRA_RECEIVER = "extra_receiver";
    public static final String KEY_ANIM_TYPE = "anim_type";
    public static final String KEY_TITLE = "anim_title";

    public enum TransitionType {
        ExplodeJava, ExplodeXML
    }

    public static class Notification {
        public static final int FOREGROUND_SERVICE = 123;
    }

    public static class PrefKey {
        public static final String token = "token";
    }

    public static class Key {
        public static final int RC_CAMERA = 1234;
        public static final int RC_STORAGE = 1235;
        public static final int RC_LOCATION = 1236;
        public static final int GEOPOINT_RESULT_CODE = 1994;
        public static final int SELECT_FILE = 1993;

        public static int LOCATION_CAPTURE = 1995;
    }

    public final static class ANIM {


        public final static int fragmentEnterAnimation = R.anim.enter_from_right;
        public final static int fragmentExitAnimation = R.anim.exit_to_left;
        public final static int fragmentPopEnterAnimation = R.anim.enter_from_left;
        public final static int fragmentPopExitAnimation = R.anim.exit_to_right;


    }

    public static final class NotificationEvent {
        public static final String SINGLE_STAGE_DEPLOYED = "deploy_ms";
        public static final String SINGLE_STAGED_FORM_DEPLOYED = "deploy_ss";
        public static final String ALL_STAGE_DEPLOYED = "deploy_all";
    }

    public static final class NotificationType {
        public static final String ASSIGNED_SITE = "Assign Site";
        public static final String UNASSIGNED_SITE = "UnAssign Site";
        public static final String NEW_STAGES = "Stages Ready";
        public static final String FORM_ALTERED_SITE = "Form Altered";
        public static final String FORM_ALTERED_PROJECT = "Form Altered Project";

        public static final String SITE_FORM = "Form";
        public static final String PROJECT_FORM = "ProjectForm";
        public static final String FORM_FLAG = "Form_Flagged";
    }

    public static final class FormType {
        public static final String SCHEDULE = "schedule_form";
        public static final String STAGED = "staged_form";
        public static final String GENERAl = "general_form";
        public static final String SURVEY = "survey_form";
    }

    public final static class FormDeploymentFrom {
        public final static String PROJECT = "project";
        public final static String SITE = "site";
    }

    public final static class SiteStatus {
        public static final int IS_OFFLINE = 0;
        public static final int IS_ONLINE = 1;
        public static final int IS_FINALIZED = 3;
        public static final int IS_EDITED = -1;

        public static final int IS_VERIFIED_BUT_UNSYNCED = 2;
    }

    public final static class BundleKey {
        public static final String IS_DEPLOYED_FROM_PROJECT = "is_project";
        public static final String KEY_FS_FORM_ID = "fsFormId";
        public static final String KEY_FS_FORM_NAME = "fsFormName";
        public static final String KEY_FS_FORM_RECORD_NAME = "fsFormRecordName";
        public static final String KEY_SITE_ID = "siteId";
        public static final String KEY_TABLE_NAME = "table_name";
        public static final String FORM_STATUS_OUTSTANDING = "Outstanding";
        public static final String FORM_STATUS_NOT_SENT = "Not Sent";
        public static final String FORM_STATUS_PENDING_REVIEW = "Pending Review";
        public static final String FORM_STATUS_SENDING_FAILED = "Sending Failed";
        public static final String FORM_STATUS_FLAGGED = "Flagged";
        public static final String FORM_STATUS_APPROVED = "Approved";
        public static final String FORM_STATUS_REJECTED = "Rejected";
    }


    public static final String EXTRA_OBJECT = "extra_object";
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_MESSAGE = "extra_msg";
    public static final String EXTRA_PROJECT_ID = "extra_msg";

    //todo: replace with enum
    public final static class DownloadUID {
        public static final int PROJECT_SITES = 1;
        public static final int ODK_FORMS = 2;
        public static final int GENERAL_FORMS = 3;
        public static final int SCHEDULED_FORMS = 4;
        public static final int STAGED_FORMS = 5;
        public static final int PROJECT_CONTACTS = 6;
        public static final int SITE_TYPES = 7;
        public static final int ALL_FORMS = 8;
        public static final int EDU_MATERIALS = 9;
        public static final int PREV_SUBMISSION = 10;
        public static final int EDITED_SITES = 11;
        public static final int OFFLINE_SITES = 12;
    }


    public final static class DownloadStatus {
        public static final int PENDING = 1;
        public static final int FAILED = 2;
        public static final int RUNNING = 3;
        public static final int COMPLETED = 4;
        public static final int DISABLED = 5;
    }

    public final static class FormStatus {
        public final static String Approved = "Approved";
        public final static String Flagged = "Flagged";
        public final static String Rejected = "Rejected";
        public final static String Pending = "Pending";
    }

    public final static class SyncStatus {
        public final static String PENDING = "pending";
        public final static String RUNNING = "running";
        public final static String COMPLETED = "completed";
        public final static String FAILED = "failed";
    }

    public final static class MetaAttrsType {
        public final static String TEXT = "Text";
        public final static String DATE = "Date";
        public final static String MCQ = "MCQ";
        public final static String NUMBER = "Number";
    }
}
