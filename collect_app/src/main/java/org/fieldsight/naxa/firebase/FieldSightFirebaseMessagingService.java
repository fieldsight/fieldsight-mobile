package org.fieldsight.naxa.firebase;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.FieldSightNotificationUtils;
import org.fieldsight.naxa.common.SharedPreferenceUtils;
import org.fieldsight.naxa.data.FieldSightNotificationBuilder;
import org.fieldsight.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.fieldsight.naxa.previoussubmission.LastSubmissionLocalSource;
import org.fieldsight.naxa.previoussubmission.model.SubmissionDetail;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static org.fieldsight.naxa.common.Constant.NotificationType.FORM_FLAG;

public class FieldSightFirebaseMessagingService extends FirebaseMessagingService {


    private final static AtomicInteger NOTIFICATION_ID = new AtomicInteger(0);

    public static final String NEW_FORM = "New Form";


    String notifyType;
    String siteId;
    String siteName;
    String projectId;
    String projectName;
    String formStatus;

    String jrFormId;
    String notificationDescriptions;
    String submissionId;
    String submissionDateTime;


    String dateStr;
    String localTime;


    DateFormat dateFormat, date1;
    Date date, currentLocalTime;
    Calendar cal;

    Uri notificationSound;
    Ringtone ringtonePlayer;

    private String fsFormId;
    private String fsFormIdProject;
    private String fsFormSubmissionId;
    private String formType;
    private String formName;
    private String formComment;
 
    private String formVerion;


    private String isDeployed, webDeployedId;
    private String notificationDetailsUrl = "";

    private boolean isDeployedFromSite;
    private String siteIdentifier;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtonePlayer = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        getAndSetDateTime();
        FieldSightNotificationBuilder builder = new FieldSightNotificationBuilder();

        String msg = remoteMessage.getData().toString();


        Timber.i("Firebase notification %s", msg);

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> notificationData = remoteMessage.getData();
            parseNotificationData(notificationData);

            builder.setDetailsUrl(notificationDetailsUrl)
                    .setNotificationType(notifyType)
                    .setFsFormId(fsFormId)
                    .setFormName(formName)
                    .setSiteId(siteId)
                    .setSiteName(siteName)
                    .setProjectId(projectId)
                    .setProjectName(projectName)
                    .setFormStatus(formStatus)
                    .setSiteIdentifier(siteIdentifier)
                    .setNotifiedDate(dateStr)
                    .setNotifiedTime(localTime)
                    .setIdString(jrFormId)
                    .setComment(formComment)
                    .setFormType(formType)
                    .setIsFormDeployed(isDeployed)
                    .setFormSubmissionId(fsFormSubmissionId)
                    .setFsFormIdProject(fsFormIdProject)
                    .isRead(false)
                    .isDeployedFromSite(isDeployedFromSite)
                    .setFormVersion(formVerion);


            Pair<String, String> titleContent = FieldSightNotificationLocalSource.getInstance()
                    .generateNotificationContent(builder.createFieldSightNotification());

            String title = titleContent.first;
            String content = titleContent.second;

            FieldSightNotificationUtils.getINSTANCE().notifyNormal(title, content);

            switch (notifyType) {
                case FORM_FLAG:
                    SubmissionDetail submissionDetail = new SubmissionDetail();
                    submissionDetail.setProjectFsFormId(fsFormIdProject);
                    submissionDetail.setSiteFsFormId(fsFormId);
                    submissionDetail.setSite(siteId);
                    submissionDetail.setProject(projectId);
                    submissionDetail.setSubmissionDateTime(submissionDateTime);
                    submissionDetail.setStatusDisplay(formStatus);

                    LastSubmissionLocalSource.getInstance().save(submissionDetail);

                    break;
            }

        }

        if (remoteMessage.getData().containsValue("Flag")) {


        }

        if (remoteMessage.getData().containsValue("FormDeleted")) {
            //hello
        }
    }


    private void parseNotificationData(Map<String, String> notificationData) {

        if (notificationData.containsKey("notify_type")) {
            notifyType = notificationData.get("notify_type");
        }
        if (notificationData.containsKey("submission_id")) {
            submissionId = notificationData.get("submission_id");
        }
        if (notificationData.containsKey("submission_date_time")) {
            submissionDateTime = notificationData.get("submission_date_time");
        }
        if (notificationData.containsKey("description")) {
            notificationDescriptions = notificationData.get("description");
        }
        if (notificationData.containsKey("status")) {
            formStatus = notificationData.get("status");
        }
        if (notificationData.containsKey("form_id")) {
            fsFormId = notificationData.get("form_id");
        }
        if (notificationData.containsKey("form_type_id")) {
            formType = notificationData.get("form_type_id");
        }
        if (notificationData.containsKey("site")) {
            String site = notificationData.get("site");
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
                Timber.e(e);

            }
        }
        if (notificationData.containsKey("PROJECT")) {
            String site = notificationData.get("PROJECT");
            try {
                JSONObject siteData = new JSONObject(site);
                if (siteData.has("name")) {
                    projectName = siteData.getString("name");
                }
                if (siteData.has("id")) {
                    projectId = siteData.getString("id");
                }
            } catch (JSONException e) {
                Timber.e(e);

            }
        }
        if (notificationData.containsKey("xfid")) {
            jrFormId = notificationData.get("xfid");
        }
        if (notificationData.containsKey("comment")) {
            formComment = notificationData.get("comment");
        }
        if (notificationData.containsKey("form_name")) {
            formName = notificationData.get("form_name");
        }
        if (notificationData.containsKey("form_type")) {
            formType = notificationData.get("form_type");
        }
        if (notificationData.containsKey("is_deployed")) {
            isDeployed = notificationData.get("is_deployed");
        }
        if (notificationData.containsKey("comment_url")) {
            notificationDetailsUrl = notificationData.get("comment_url");
        }

        if (notificationData.containsKey("deploy_id")) {
            webDeployedId = notificationData.get("deploy_id");
            Timber.i("deploy_id %s", webDeployedId);
        }
        if (notificationData.containsKey("project_form_id")) {
            fsFormIdProject = notificationData.get("project_form_id");
        }
        if (notificationData.containsKey("submission_id")) {
            fsFormSubmissionId = notificationData.get("submission_id");
        }
        if (notificationData.containsKey("version")) {
            formVerion = notificationData.get("version");
        }
        if (notificationData.containsKey("site_level_form")) {
            String data = notificationData.get("site_level_form");
            if (!TextUtils.isEmpty(data)) {
                isDeployedFromSite = Boolean.parseBoolean(data);
            }
        }
    }


    public static int getID() {
        return NOTIFICATION_ID.incrementAndGet();
    }


    private void getAndSetDateTime() {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        date = new Date();
        dateStr = dateFormat.format(date);
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-4:00"));
        currentLocalTime = cal.getTime();
        date1 = new SimpleDateFormat("hh:mm a", Locale.US);
        date1.setTimeZone(TimeZone.getTimeZone("GMT+5:45"));
        localTime = date1.format(currentLocalTime);

    }

    @Override
    public void onNewToken(String fcmToken) {
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, fcmToken);
        Timber.i("Messaging service, firebase %s",fcmToken);    }
}
