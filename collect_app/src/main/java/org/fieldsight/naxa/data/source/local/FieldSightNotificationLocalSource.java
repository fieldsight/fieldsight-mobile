package org.fieldsight.naxa.data.source.local;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Pair;

import androidx.lifecycle.LiveData;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.BaseLocalDataSource;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.data.FieldSightNotification;
import org.fieldsight.naxa.data.FieldSightNotificationBuilder;
import org.fieldsight.naxa.notificationslist.FieldSightNotificationDAO;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.NotificationEvent.ALL_STAGE_DEPLOYED;
import static org.fieldsight.naxa.common.Constant.NotificationEvent.SINGLE_STAGED_FORM_DEPLOYED;
import static org.fieldsight.naxa.common.Constant.NotificationEvent.SINGLE_STAGE_DEPLOYED;
import static org.fieldsight.naxa.common.Constant.NotificationType.ASSIGNED_SITE;
import static org.fieldsight.naxa.common.Constant.NotificationType.DAILY_REMINDER;
import static org.fieldsight.naxa.common.Constant.NotificationType.FORM_ALTERED_PROJECT;
import static org.fieldsight.naxa.common.Constant.NotificationType.FORM_ALTERED_SITE;
import static org.fieldsight.naxa.common.Constant.NotificationType.FORM_FLAG;
import static org.fieldsight.naxa.common.Constant.NotificationType.MONTHLY_REMINDER;
import static org.fieldsight.naxa.common.Constant.NotificationType.NEW_STAGES;
import static org.fieldsight.naxa.common.Constant.NotificationType.PROJECT_FORM;
import static org.fieldsight.naxa.common.Constant.NotificationType.SITE_FORM;
import static org.fieldsight.naxa.common.Constant.NotificationType.UNASSIGNED_SITE;
import static org.fieldsight.naxa.common.Constant.NotificationType.WEEKLY_REMINDER;
import static org.fieldsight.naxa.firebase.FieldSightFirebaseMessagingService.NEW_FORM;


public class FieldSightNotificationLocalSource implements BaseLocalDataSource<FieldSightNotification> {

    private static FieldSightNotificationLocalSource INSTANCE;
    private final FieldSightNotificationDAO dao;


    private String notifyType;
    private String siteId;
    String siteName;
    String projectId;
    String projectName;
    String formStatus;

    String jrFormId;
    String notificationDescriptions;
    String submissionId;
    String submissionDateTime;


    String date_str;
    String localTime;
    Boolean notificationStatus = false;

    String comment;
    String fsFormId;
    String fsFormIdProject;
    String fsFormSubmissionId;
    String formType;
    String formName;
    String formComment;
    String form;
    String formVerion;

    String deleteForm;
    String isDeployed, webDeployedId;
    String notificationDetailsUrl = "";
    String isDeployedFromProject;//todo: this needs to be checked and removed coz we are using isDeployedFromSite in flag forms
    boolean isDeployedFromSite;
    String siteIdentifier = null;

    public static FieldSightNotificationLocalSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightNotificationLocalSource();
        }
        return INSTANCE;
    }

    private FieldSightNotificationLocalSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getFieldSightNotificationDAO();
    }

    public LiveData<Integer> isProjectNotSynced(String siteId, String projectId) {
        return dao.notificationCount(false, siteId, projectId,
                ASSIGNED_SITE,
                UNASSIGNED_SITE);
    }

    public LiveData<Integer> isProjectListNotSynced(String... projectIds) {
        return dao.countNonExistentProjectInNotification(false, ASSIGNED_SITE, projectIds);
    }

    public LiveData<Integer> isSiteNotSynced(String siteId, String projectId) {
        return dao.notificationCount(false, siteId, projectId,
                NEW_STAGES,
                SINGLE_STAGE_DEPLOYED,
                SINGLE_STAGED_FORM_DEPLOYED,
                ALL_STAGE_DEPLOYED,
                NEW_STAGES,
                FORM_ALTERED_SITE,
                FORM_ALTERED_PROJECT,
                SITE_FORM,
                PROJECT_FORM
        );
    }


    public Maybe<Integer> anyProjectSitesOutOfSync() {
        return dao.countForNotificationType(false, ASSIGNED_SITE, UNASSIGNED_SITE);
    }

    public Maybe<Integer> anyFormsOutOfSync() {
        return dao.countForNotificationType(
                false,
                NEW_STAGES,
                SINGLE_STAGE_DEPLOYED,
                SINGLE_STAGED_FORM_DEPLOYED,
                ALL_STAGE_DEPLOYED,
                NEW_STAGES,
                FORM_ALTERED_SITE,
                FORM_ALTERED_PROJECT,
                SITE_FORM,
                PROJECT_FORM);
    }

    public Maybe<Integer> anyFormStatusChangeOutOfSync() {
        return dao.countForNotificationType(false,
                Constant.NotificationType.FORM_FLAG
        );
    }

    public void markFormStatusChangeAsRead() {
        AsyncTask.execute(() -> dao.applyReadToNotificationType(true,
                Constant.NotificationType.FORM_FLAG));
    }

    public void markSitesAsRead() {
        AsyncTask.execute(() -> dao.applyReadToNotificationType(true,
                ASSIGNED_SITE,
                UNASSIGNED_SITE));
    }

    public void markFormsAsRead() {
        AsyncTask.execute(() -> dao.applyReadToNotificationType(true,
                NEW_STAGES,
                SINGLE_STAGE_DEPLOYED,
                SINGLE_STAGED_FORM_DEPLOYED,
                ALL_STAGE_DEPLOYED,
                NEW_STAGES,
                FORM_ALTERED_SITE,
                FORM_ALTERED_PROJECT,
                SITE_FORM,
                PROJECT_FORM));
    }


    @Override
    public LiveData<List<FieldSightNotification>> getAll() {
        return dao.getAll();
    }

    @Override
    public void save(FieldSightNotification... items) {
        AsyncTask.execute(() -> dao.insertOrIgnore(items));
    }

    @Override
    public void save(ArrayList<FieldSightNotification> items) {
        AsyncTask.execute(() -> dao.insertOrIgnore(items));
    }

    @Override
    public void updateAll(ArrayList<FieldSightNotification> items) {
        throw new RuntimeException("Not implemented");
    }

    public void clear() {
        AsyncTask.execute(dao::deleteAll);

    }


    public Pair<String, String> generateNotificationContent(FieldSightNotification notification) {
        Context context = Collect.getInstance();
        String title = "";
        String message = "";
        switch (notification.getNotificationType()) {
            case DAILY_REMINDER:
                title = "Daily Reminder";
                message = Collect.getInstance().getString(R.string.msg_form_reminder_daily, notification.getScheduleFormsCount());
                break;
            case WEEKLY_REMINDER:
                title = "Weekly Reminder";
                message = Collect.getInstance().getString(R.string.msg_form_reminder_weekly, notification.getScheduleFormsCount());
                break;
            case MONTHLY_REMINDER:
                title = "Monthly Reminder";
                message = Collect.getInstance().getString(R.string.msg_form_reminder_monthly, notification.getScheduleFormsCount());
                break;
            case SINGLE_STAGE_DEPLOYED:
                title = context.getString(R.string.notify_title_stage_deployed);
                String deployedFromSiteMsg = context.getString(R.string.notify_message_stage_deployed_site, notification.getSiteName());
                String deployedFromProjectMsg = context.getString(R.string.notify_message_stage_deployed_project, notification.getProjectName());

                message = notification.getSiteId() != null ? deployedFromSiteMsg : deployedFromProjectMsg;
                break;
            case SINGLE_STAGED_FORM_DEPLOYED:
                title = context.getString(R.string.notify_title_substage_deployed);
                message = notification.getSiteId() != null
                        ? context.getString(R.string.notify_message_multiple_substage_deployed_site, notification.getSiteName())
                        : context.getString(R.string.notify_message_multiple_substage_deployed_project, notification.getProjectName());
                break;
            case ALL_STAGE_DEPLOYED:
                title = context.getString(R.string.notify_title_stage_deployed);
                message = notification.getSiteId() != null
                        ? context.getString(R.string.notify_message_multiple_stage_deployed_site, notification.getSiteName())
                        : context.getString(R.string.notify_message_multiple_stage_deployed_project, notification.getProjectName());
                break;
            case FORM_FLAG:
                title = context.getString(R.string.notify_title_submission_result);
                message = generateFormStatusChangeMsg(notification).toString();
                break;
            case SITE_FORM:

                boolean isNewForm = NEW_FORM.equalsIgnoreCase(notification.getFormStatus());
                String siteOrProjectName = TextUtils.isEmpty(notification.getSiteName()) ? notification.getProjectName() : notification.getSiteName();
                title = context.getString(R.string.notify_title_form_deployed, notification.getFormType());
                message = context.getString(R.string.notify_message_form_deployed, notification.getFormName(), siteOrProjectName);
                //todo: download form?

                break;
            case NEW_STAGES:
                break;
            case FORM_ALTERED_SITE:
            case FORM_ALTERED_PROJECT:
            case PROJECT_FORM:
                boolean isDeployed = "true".equalsIgnoreCase(notification.getIsFormDeployed());
                String siteOrProjectName2 = TextUtils.isEmpty(notification.getSiteName()) ? notification.getProjectName() : notification.getSiteName();

                String undeployedTitle = context.getString(R.string.notify_title_form_undeployed, notification.getFormType());
                String undeployedContent = context.getString(R.string.notify_message_form_undeployed, notification.getFormName(), siteOrProjectName2);

                String deployedTitle = context.getString(R.string.notify_title_form_deployed, notification.getFormType());
                String deployedContent = context.getString(R.string.notify_message_form_deployed, notification.getFormName(), siteOrProjectName2);

                title = isDeployed ? deployedTitle : undeployedTitle;
                message = isDeployed ? deployedContent : undeployedContent;

                //todo: delete form?
                break;
            case ASSIGNED_SITE:
                title = context.getString(R.string.notify_title_site_assigned, notification.getSiteName());
                message = context.getString(R.string.notify_message_site_assigned, notification.getSiteName());
                break;
            case UNASSIGNED_SITE:
                //todo: you have been unassigned please upload data those sites to prevent loss
                //todo: do not remove unassinged site's if they have data
                title = context.getString(R.string.notify_title_site_unassigned, notification.getSiteName());
                message = context.getString(R.string.notify_message_site_unassigned, notification.getSiteName());
                break;
            default:
                title = notification.getNotificationType();


        }

        return Pair.create(title, message);
    }


    private String generateFormStatusChangeMsg(FieldSightNotification fieldSightNotification) {
        Context context = Collect.getInstance().getApplicationContext();
        String desc;
        String formStatus = null;


        switch (fieldSightNotification.getFormStatus()) {
            case Constant.FormStatus.Flagged:
                formStatus = context.getResources().getString(R.string.notify_form_flagged);
                break;
            case Constant.FormStatus.Approved:
                formStatus = context.getResources().getString(R.string.notify_form_approved);
                break;
            case Constant.FormStatus.Rejected:
                formStatus = context.getResources().getString(R.string.notify_form_rejected);
                break;
        }


        if (fieldSightNotification.getSiteIdentifier() == null) {
            desc = context.getResources().getString(R.string.notify_submission_result,
                    fieldSightNotification.getFormName(),
                    fieldSightNotification.getProjectName(),
                    formStatus);


        } else {
            desc = context.getResources().getString(R.string.notify_submission_result_with_identifier,
                    fieldSightNotification.getFormName(),
                    fieldSightNotification.getSiteName(),
                    fieldSightNotification.getSiteIdentifier(),
                    formStatus);

        }


        return desc;
    }


    public FieldSightNotification parseNotificationData(JSONObject jsonObject) {

        JSONObject notificationData = jsonObject.optJSONObject("message");
        String receivedDateTime = jsonObject.optString("date");


        if (notificationData.has("notify_type")) {
            notifyType = notificationData.optString("notify_type");
        }
        if (notificationData.has("submission_id")) {
            submissionId = notificationData.optString("submission_id");
        }
        if (notificationData.has("submission_date_time")) {
            submissionDateTime = notificationData.optString("submission_date_time");
        }
        if (notificationData.has("description")) {
            notificationDescriptions = notificationData.optString("description");
        }
        if (notificationData.has("status")) {
            formStatus = notificationData.optString("status");
        }
        if (notificationData.has("form_id")) {
            fsFormId = notificationData.optString("form_id");
        }
        if (notificationData.has("form_type_id")) {
            formType = notificationData.optString("form_type_id");
        }
        if (notificationData.has("site")) {
            String site = notificationData.optString("site");
            try {
                JSONObject siteData = new JSONObject(site);
                if (siteData.has("name")) {
                    siteName = siteData.getString("name");
                }
                if (siteData.has("id")) {
                    siteId = siteData.getString("id");
                }
                if (siteData.has("identifier")) {
                    siteIdentifier = siteData.getString("identifier");
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        if (notificationData.has("project")) {
            String site = notificationData.optString("project");
            try {
                JSONObject siteData = new JSONObject(site);
                if (siteData.has("name")) {
                    projectName = siteData.getString("name");
                }
                if (siteData.has("id")) {
                    projectId = siteData.getString("id");
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        if (notificationData.has("xfid")) {
            jrFormId = notificationData.optString("xfid");
        }
        if (notificationData.has("comment")) {
            formComment = notificationData.optString("comment");
        }
        if (notificationData.has("form_name")) {
            formName = notificationData.optString("form_name");
        }
        if (notificationData.has("form_type")) {
            formType = notificationData.optString("form_type");
        }
        if (notificationData.has("form")) {
            form = notificationData.optString("form");
        }
        if (notificationData.has("is_delete")) {
            deleteForm = notificationData.optString("is_delete");
        }
        if (notificationData.has("is_deployed")) {
            isDeployed = notificationData.optString("is_deployed");
        }
        if (notificationData.has("comment_url")) {
            notificationDetailsUrl = notificationData.optString("comment_url");
        }

        if (notificationData.has("deploy_id")) {
            webDeployedId = notificationData.optString("deploy_id");
            Timber.i("deploy_id %s", webDeployedId);
        }

        if (notificationData.has("is_project")) {
            isDeployedFromProject = notificationData.optString("is_project");
        }

        if (notificationData.has("project_form_id")) {
            fsFormIdProject = notificationData.optString("project_form_id");
        }
        if (notificationData.has("submission_id")) {
            fsFormSubmissionId = notificationData.optString("submission_id");
        }
        if (notificationData.has("version")) {
            formVerion = notificationData.optString("version");
        }
        if (notificationData.has("site_level_form")) {
            String data = notificationData.optString("site_level_form");
            if (!TextUtils.isEmpty(data)) {
                isDeployedFromSite = Boolean.parseBoolean(data);
            }
        }


        FieldSightNotification notification = new FieldSightNotificationBuilder()
                .setDetails_url(notificationDetailsUrl)
                .setNotificationType(notifyType)
                .setFsFormId(fsFormId)
                .setFormName(formName)
                .setSiteId(siteId)
                .setSiteName(siteName)
                .setProjectId(projectId)
                .setProjectName(projectName)
                .setFormStatus(formStatus)
                .setSiteIdentifier(siteIdentifier)
                .setNotifiedDate(date_str)
                .setNotifiedTime(localTime)
                .setIdString(jrFormId)
                .setComment(formComment)
                .setFormType(formType)
                .setIsFormDeployed(isDeployed)
                .setFormSubmissionId(fsFormSubmissionId)
                .setFsFormIdProject(fsFormIdProject)
                .isRead(false)
                .isDeployedFromSite(isDeployedFromSite)
                .setFormVersion(formVerion)
                .setReceivedDateTime(receivedDateTime)

                .setReceivedDateTimeInMillis()

                .createFieldSightNotification();

        return notification;
    }
}
