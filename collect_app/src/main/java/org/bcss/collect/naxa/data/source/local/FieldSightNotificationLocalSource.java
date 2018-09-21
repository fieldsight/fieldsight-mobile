package org.bcss.collect.naxa.data.source.local;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseLocalDataSource;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.notificationslist.FieldSightNotificationDAO;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;

import static org.bcss.collect.naxa.common.Constant.NotificationEvent.ALL_STAGE_DEPLOYED;
import static org.bcss.collect.naxa.common.Constant.NotificationEvent.SINGLE_STAGED_FORM_DEPLOYED;
import static org.bcss.collect.naxa.common.Constant.NotificationEvent.SINGLE_STAGE_DEPLOYED;
import static org.bcss.collect.naxa.common.Constant.NotificationType.ASSIGNED_SITE;
import static org.bcss.collect.naxa.common.Constant.NotificationType.FORM_ALTERED_PROJECT;
import static org.bcss.collect.naxa.common.Constant.NotificationType.FORM_ALTERED_SITE;
import static org.bcss.collect.naxa.common.Constant.NotificationType.FORM_FLAG;
import static org.bcss.collect.naxa.common.Constant.NotificationType.NEW_STAGES;
import static org.bcss.collect.naxa.common.Constant.NotificationType.PROJECT_FORM;
import static org.bcss.collect.naxa.common.Constant.NotificationType.SITE_FORM;
import static org.bcss.collect.naxa.common.Constant.NotificationType.UNASSIGNED_SITE;
import static org.bcss.collect.naxa.common.Truss.makeSectionOfTextBold;
import static org.bcss.collect.naxa.firebase.FieldSightFirebaseMessagingService.NEW_FORM;


public class FieldSightNotificationLocalSource implements BaseLocalDataSource<FieldSightNotification> {

    private static FieldSightNotificationLocalSource INSTANCE;
    private final FieldSightNotificationDAO dao;

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
        AsyncTask.execute(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<FieldSightNotification> items) {
        AsyncTask.execute(() -> dao.insert(items));
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
            case SINGLE_STAGE_DEPLOYED:
                title = context.getString(R.string.notify_title_stage_deployed);
                message = context.getString(R.string.notify_message_stage_deployed, notification.getSiteName());
                break;
            case SINGLE_STAGED_FORM_DEPLOYED:
                break;
            case ALL_STAGE_DEPLOYED:
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


    private SpannableStringBuilder generateFormStatusChangeMsg(FieldSightNotification fieldSightNotification) {
        Context context = Collect.getInstance().getApplicationContext();
        String desc;
        SpannableStringBuilder formattedDesc = null;

        switch (fieldSightNotification.getFormStatus()) {
            case Constant.FormStatus.Flagged:
                desc = context.getResources().getString(R.string.notify_submission_result,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_flagged));

                formattedDesc = makeSectionOfTextBold(desc,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_flagged));
                break;

            case Constant.FormStatus.Approved:

                desc = context.getResources().getString(R.string.notify_submission_result,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_approved) + ".");

                formattedDesc = makeSectionOfTextBold(desc,
                        fieldSightNotification.getSiteName(),
                        fieldSightNotification.getFormName(), context.getResources().getString(R.string.notify_form_approved));
                break;
            case Constant.FormStatus.Rejected:
                String form_rejected_response = context.getResources().getString(R.string.notify_submission_result,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_rejected) + ".");

                formattedDesc = makeSectionOfTextBold(form_rejected_response,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_rejected));
                break;

            default:
                formattedDesc = SpannableStringBuilder.valueOf("Unknown deployment");
                break;
        }
        return formattedDesc;
    }
}
