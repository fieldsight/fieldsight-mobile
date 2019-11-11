package org.fieldsight.naxa.flagform;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.BaseActivity;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.common.InternetUtils;
import org.fieldsight.naxa.common.downloader.RxDownloader;
import org.fieldsight.naxa.common.exception.InstanceAttachmentDownloadFailedException;
import org.fieldsight.naxa.common.exception.InstanceDownloadFailedException;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.data.FieldSightNotification;
import org.fieldsight.naxa.helpers.FSInstancesDao;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.notificationslist.NotificationDetail;
import org.fieldsight.naxa.notificationslist.NotificationImage;
import org.fieldsight.naxa.notificationslist.NotificationImageAdapter;
import org.fieldsight.naxa.site.FragmentHostActivity;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dto.Instance;
import org.odk.collect.android.listeners.DownloadFormsTaskListener;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

;


public class FlaggedInstanceActivity extends BaseActivity implements View.OnClickListener, NotificationImageAdapter.OnItemClickListener {

    private static final String TAG = "Comment Activity";
    //constants for form status


    Context context = this;

    TextView tvFormName, tvFormDesc, tvComment, tvFormStatus;
    RecyclerView recyclerViewImages;
    ImageButton imbStatus;
    RelativeLayout relativeStatus;
    RelativeLayout formBox;
    private FieldSightNotification loadedFieldSightNotification;
    private Toolbar toolbar;

    private DownloadFormsTask downloadFormsTask;
    private ProgressDialog dialog;


    private FormsDao formsDao;
    private FSInstancesDao instancesDao;
    private TextView tvSiteIdentifier;
    private TextView tvSiteName;
    private TextView tvIconText;
    private TextView tvSiteAddress;
    private ImageView ivCircleSite;
    private TextView tvSiteMissing;
    private RelativeLayout cardViewSite;
    private String message;


    public static void start(Context context, FieldSightNotification fieldSightNotification) {
        Intent intent = new Intent(context, FlaggedInstanceActivity.class);
        intent.putExtra(Constant.EXTRA_OBJECT, fieldSightNotification);
        context.startActivity(intent);
    }

    public static void startWithForm(FragmentActivity context, FieldSightNotification notification) {
        Intent intent = new Intent(context, FlaggedInstanceActivity.class);
        intent.putExtra(Constant.EXTRA_OBJECT, notification);
        intent.putExtra(Constant.EXTRA_MESSAGE, "open_form");
        context.startActivity(intent);
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Form Flagged");
        initBack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_flag_response);

        bindUI();
        setupToolbar();

        formsDao = new FormsDao();
        instancesDao = new FSInstancesDao();
        formBox.setOnClickListener(this);

        loadedFieldSightNotification = getIntent().getParcelableExtra(Constant.EXTRA_OBJECT);
        message = getIntent().getStringExtra(Constant.EXTRA_MESSAGE);
        if (TextUtils.equals(message, "open_form")) {
            boolean isInstanceDownloadNeeded = !hasFormVersion() || !hasFormInstance();
            Timber.d("hasFormVersion %s hasFormInstance %s, isInstanceDownloadNeeded %s", hasFormVersion(), hasFormInstance(), isInstanceDownloadNeeded);
            findViewById(R.id.root_layout).setVisibility(View.INVISIBLE);
            if (isInstanceDownloadNeeded) {
                InternetUtils.checkInterConnectivity(new InternetUtils.OnConnectivityListener() {
                    @Override
                    public void onConnectionSuccess() {
                        runDownload();
                    }

                    @Override
                    public void onConnectionFailure() {
                        ToastUtils.showLongToast(R.string.no_internet_body);
                        finish();
                    }

                    @Override
                    public void onCheckComplete() {

                    }
                });

            } else {
                loadSavedInstance(loadedFieldSightNotification.getFormSubmissionId(), loadedFieldSightNotification.getIdString());
            }

            return;
        }


        setupData(loadedFieldSightNotification);
        setupSiteCard(loadedFieldSightNotification);

    }

    private void setupSiteCard(FieldSightNotification loadedFieldSightNotification) {
        SiteLocalSource.getInstance().getBySiteId(loadedFieldSightNotification.getSiteId())
                .observe(this, site -> {
                    if (site == null) {
                        cardViewSite.setVisibility(View.GONE);
                        tvSiteMissing.setVisibility(View.VISIBLE);
                        return;
                    }

                    cardViewSite.setVisibility(View.VISIBLE);
                    tvSiteMissing.setVisibility(View.GONE);

                    setSiteData(site.getName(), site.getIdentifier(), site.getAddress());

                    cardViewSite.setOnClickListener(v -> FragmentHostActivity.start(this, site, false));
                });


    }

    private void setSiteData(String siteName, String siteIdentifier, String address) {
        tvSiteName.setText(siteName);
        if (siteName != null && siteName.trim().length() > 0) {
            tvIconText.setText(siteName.substring(0, 1));
        }
        tvSiteIdentifier.setText(siteIdentifier);
        tvSiteAddress.setText(address);
        ivCircleSite.setImageResource(R.drawable.circle_blue);
    }

    @Override
    public void onBackClicked(boolean isHome) {
        finish();
    }

    private void bindUI() {

        //layout element ids
        toolbar = findViewById(R.id.toolbar);
        tvFormName = findViewById(R.id.tv_form_name);
        tvFormDesc = findViewById(R.id.tv_form_desc);
        imbStatus = findViewById(R.id.img_btn_status);
        tvFormStatus = findViewById(R.id.tv_form_status);
        tvComment = findViewById(R.id.tv_comments_txt);
        recyclerViewImages = findViewById(R.id.comment_session_rv_images);

        tvSiteName = findViewById(R.id.tv_site_name);
        tvSiteIdentifier = findViewById(R.id.tv_identifier);
        ivCircleSite = findViewById(R.id.icon_profile);
        tvIconText = findViewById(R.id.icon_text);
        tvSiteAddress = findViewById(R.id.txt_secondary);

        cardViewSite = findViewById(R.id.root_layout_message_list_row);
        tvSiteMissing = findViewById(R.id.tv_msg_site_missing);


        relativeStatus = findViewById(R.id.relativeLayout_status);
        formBox = findViewById(R.id.relative_layout_comment_open_form);
    }

    private void setupData(FieldSightNotification fieldSightNotification) {

        String comment = fieldSightNotification.getComment();
        String formName = fieldSightNotification.getFormName();
        String formStatus = fieldSightNotification.getFormStatus();

        if (TextUtils.isEmpty(fieldSightNotification.getComment())) {
            tvComment.setText("");
        } else {
            tvComment.setText(comment);
        }

        //set values to text view on layout
        tvFormName.setText(formName);
        //  tvFormDesc.setText(jrFormId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imbStatus.setElevation(3);
        }

        if (formStatus != null && formStatus.equals("APPROVED")) {
            imbStatus.setBackgroundResource(R.color.green_approved);
            relativeStatus.setBackgroundResource(R.color.green_approved);
        } else if (formStatus != null && formStatus.equals("Outstanding")) {
            imbStatus.setBackgroundResource(R.color.grey_outstanding);
            relativeStatus.setBackgroundResource(R.color.grey_outstanding);
        } else if (formStatus != null && formStatus.equals("FLAGGED")) {
            imbStatus.setBackgroundResource(R.color.yellow_flagged);
            relativeStatus.setBackgroundResource(R.color.yellow_flagged);
        } else if (formStatus != null && formStatus.equals("REJECTED")) {
            imbStatus.setBackgroundResource(R.color.red_rejected);
            relativeStatus.setBackgroundResource(R.color.red_rejected);
        }

        tvFormStatus.setText(formStatus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.equals(message, "open_form")) {
            getNotificationDetail();
        }

    }

    protected long getFormId(String jrFormId) throws CursorIndexOutOfBoundsException, NullPointerException, NumberFormatException {

        String[] projection = new String[]{FormsProviderAPI.FormsColumns._ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH};
        String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
        String[] selectionArgs = new String[]{jrFormId};

        Cursor cursor = getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                projection,
                selection, selectionArgs, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID);
        long formId = Long.parseLong(cursor.getString(columnIndex));

        cursor.close();

        return formId;
    }


    protected void createNewSubmission(String idString) {
        try {
            long formId = getFormId(idString);
            Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, formId);
            String action = getIntent().getAction();


            if (Intent.ACTION_PICK.equals(action)) {
                // caller is waiting on a picked form
                setResult(RESULT_OK, new Intent().setData(formUri));
            } else {
                // caller wants to view/edit a form, so launch formentryactivity
                Intent toFormEntry = new Intent(Intent.ACTION_EDIT, formUri);
                toFormEntry.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(toFormEntry);
                finish();

            }
        } catch (CursorIndexOutOfBoundsException e) {
            DialogFactory.createGenericErrorDialog(this, getString(R.string.msg_form_not_present)).show();
            Timber.e("Failed to load xml form  %s", e.getMessage());
        } catch (NullPointerException | NumberFormatException e) {
            Timber.e(e);
            DialogFactory.createGenericErrorDialog(this, e.getMessage()).show();
            Timber.e("Failed to load xml form %s", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.relative_layout_comment_open_form) {
            boolean emptyVersion = TextUtils.isEmpty(loadedFieldSightNotification.getFormVersion());
            if (emptyVersion) {
                showFormIsLegacyDialog();
                return;
            }

            boolean isFormApproved = "APPROVED".equals(loadedFieldSightNotification.getFormStatus());
            if (isFormApproved) {
                showFormIsApprovedDialog();
                return;
            }


            boolean isInstanceDownloadNeeded = !hasFormVersion() || !hasFormInstance();
            Timber.d("hasFormVersion %s hasFormInstance %s, isInstanceDownloadNeeded %s", hasFormVersion(), hasFormInstance(), isInstanceDownloadNeeded);
            if (isInstanceDownloadNeeded) {
                showDownloadInstanceDialog();
            } else {
                loadSavedInstance(loadedFieldSightNotification.getFormSubmissionId(), loadedFieldSightNotification.getIdString());
            }
        }
    }

    private void showFormIsApprovedDialog() {
        DialogFactory.createMessageDialog(this, "Cannot open form", "This form has already been approved.").show();
    }

    private void showDownloadInstanceDialog() {
        DialogFactory.createActionDialog(this, getString(R.string.dialog_title_missing_flag_form), getString(R.string.dialog_text_missing_flag_form))
                .setPositiveButton(R.string.dialog_action_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runDownload();
                    }
                })
                .setNegativeButton(R.string.dialog_action_dismiss, null)
                .show();
    }

    private void runDownload() {
        if (hasFormInstance() && !hasFormVersion()) {
            Timber.i("Downloading form version");
            //download form version and load instance
            downloadFormVersion(loadedFieldSightNotification);
        } else if (!hasFormInstance() && hasFormVersion()) {
            Timber.i("Downloading filled form");
            //download form instance and load instance
            downloadInstance(loadedFieldSightNotification);
        } else {
            Timber.i("Downloading form instance and form version");
            downloadFormAndInstance(loadedFieldSightNotification, false);
        }
    }


    private void loadInstance(Uri instanceUri) {
        Intent toEdit = new Intent(Intent.ACTION_EDIT, instanceUri);
        toEdit.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
        toEdit.putExtra("EditedFormID", instanceUri.getLastPathSegment());
        startActivity(toEdit);
    }

    private void showFormIsLegacyDialog() {
        showAskNewSubmissionConsentDialog(getString(R.string.dialog_text_cant_edit_flag_form));
    }

    private void showOnlyNewFormAvaliableDialog() {
        showAskNewSubmissionConsentDialog("This older version of the form cannot be downloaded");
    }

    private void showAskNewSubmissionConsentDialog(String message) {
        DialogFactory.createActionDialog(this, getString(R.string.dialog_title_cant_open_flagged_form), message)
                .setNegativeButton(R.string.dialog_action_make_new_submission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createNewSubmission(loadedFieldSightNotification.getIdString());
                    }
                })
                .setNeutralButton(R.string.dialog_action_dismiss, null)
                .show();
    }

    private void showFormInstanceDownloadFailedDialog() {
        showAskNewSubmissionConsentDialog(getString(R.string.dialog_text_instance_download_failed));
    }

    private void downloadFormVersion(FieldSightNotification loadedFieldSightNotification) {
        downloadFormAndInstance(loadedFieldSightNotification, true);
    }

    private boolean hasFormWithMatchingFormId() {
        String idString = loadedFieldSightNotification.getIdString();
        Cursor cursor = formsDao.getFormsCursor(idString);
        if (cursor != null) {
            return cursor.getCount() > 0;
        }
        return false;
    }

    private boolean hasFormVersion() {

        String jrformId = loadedFieldSightNotification.getIdString();
        String jrformVersion = loadedFieldSightNotification.getFormVersion();
        Cursor cursor = formsDao.getFormsCursor(jrformId, jrformVersion);
        if (cursor != null) {
            return cursor.getCount() > 0;
        }
        return false;
    }

    private boolean hasFormInstance() {

        String fieldSightInstanceId = loadedFieldSightNotification.getFormSubmissionId();

        Cursor cursor = instancesDao.getNotDeletedInstancesCursor(fieldSightInstanceId);
        if (cursor != null) {
            return cursor.getCount() == 1;
        }
        return false;
    }


    private void downloadFormAndInstance(FieldSightNotification notificationFormDetail, boolean loadInstanceAfterDownloadComplete) {

        String formName = notificationFormDetail.getFormName();
        String fsFormSubmissionId = notificationFormDetail.getFormSubmissionId();
        String jrFormId = "";
        String downloadUrl = String.format(FieldSightUserSession.getServerUrl(Collect.getInstance()) + APIEndpoint.GET_FORM_XML + "/%s", fsFormSubmissionId);

        ArrayList<FormDetails> filesToDownload = new ArrayList<FormDetails>();
        FormDetails formDetails = new FormDetails(formName,
                downloadUrl,
                null,
                jrFormId,
                null,
                null,
                null,
                false,
                false);

        filesToDownload.add(formDetails);
        showDialog();
        startFormsDownload(filesToDownload, notificationFormDetail, loadInstanceAfterDownloadComplete);


    }


    private void showDialog() {
        dialog = DialogFactory.createProgressDialogHorizontal(this, "Loading Form");
        dialog.show();
    }

    private void hideDialog() {
        runOnUiThread(() -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.hide();
            }
        });

    }

    private void changeDialogMsg(String message) {
        if (dialog == null || !dialog.isShowing()) {
            showDialog();
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.setTitle(message);
        }
    }

    private void startFormsDownload(@NonNull ArrayList<FormDetails> filesToDownload, FieldSightNotification notification, boolean loadInstanceAfterDownloadComplete) {
        downloadFormsTask = new DownloadFormsTask(true);
        downloadFormsTask.setDownloaderListener(new DownloadFormsTaskListener() {
            @Override
            public void formsDownloadingComplete(HashMap<FormDetails, String> result) {
                if (downloadFormsTask != null) {
                    downloadFormsTask.setDownloaderListener(null);
                }

                handleFormDownloadResposne(result, notification, loadInstanceAfterDownloadComplete);
            }

            @Override
            public void progressUpdate(String currentFile, int progress, int total) {
                changeDialogMsg("Downloading " + currentFile);
            }

            @Override
            public void formsDownloadingCancelled() {
                hideDialog();
                showErrorDialog("Form download was canceled");
            }
        });

        downloadFormsTask.setDownloadAsTemporary();
        downloadFormsTask.execute(filesToDownload);
    }

    private void handleFormDownloadResposne(HashMap<FormDetails, String> result, FieldSightNotification notification, boolean loadInstanceAfterDownloadComplete) {

        for (FormDetails formDetails : result.keySet()) {
            String successKey = result.get(formDetails);
            if (Collect.getInstance().getString(R.string.success).equals(successKey)) {
                if (loadInstanceAfterDownloadComplete) {
                    loadSavedInstance(notification.getFormSubmissionId(),
                            notification.getIdString());
                } else {
                    downloadInstance(notification);
                }
                break;
            } else {
                hideDialog();
                if (hasFormWithMatchingFormId()) {
                    showOnlyNewFormAvaliableDialog();
                } else {
                    showErrorDialog(result.get(formDetails));
                }
            }
        }
    }

    private void downloadInstance(FieldSightNotification notification) {

        String[] nameAndPath = InstanceRemoteSource.getInstanceRemoteSource().getNameAndPath(notification.getFormName());
        String pathToDownload = nameAndPath[1];


        Observable<String> attachedMediaObservable = InstanceRemoteSource.getInstanceRemoteSource()
                .downloadAttachedMedia(notification.getFormSubmissionId())
                .map(HashMap::entrySet)
                .flatMapIterable(entries -> entries)
                .flatMap(new Function<Map.Entry<String, String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Map.Entry<String, String> filenameFilePathMap) {
                        String fileName = filenameFilePathMap.getKey();
                        String downloadUrl = filenameFilePathMap.getValue();

                        Timber.i("Downloading %s from %s and saving in %s", fileName, downloadUrl, pathToDownload);

                        return RxDownloader.getINSTANCE(FlaggedInstanceActivity.this)
                                .download(downloadUrl,
                                        fileName,
                                        pathToDownload,
                                        "*/*",
                                        true);
                    }
                });


        Observable<Instance> instanceObservable = InstanceRemoteSource.getInstanceRemoteSource()
                .downloadInstances(notification, nameAndPath);

        Observable.concat(attachedMediaObservable, instanceObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object comparable) {
                        if (comparable instanceof Instance) {
                            hideDialog();
                            Instance instance = (Instance) comparable;
                            loadSavedInstance(instance.getFieldSightInstanceId(), instance.getJrFormId());

                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e(throwable);
                        hideDialog();

                        if ((throwable instanceof InstanceDownloadFailedException || throwable instanceof InstanceAttachmentDownloadFailedException) && hasFormVersion()) {
                            showFormInstanceDownloadFailedDialog();
                        } else {
                            String message = throwable.getMessage();
                            if (throwable instanceof RetrofitException) {
                                message = ((RetrofitException) throwable).getKind().getMessage();
                            }
                            showErrorDialog(message);
                        }
                    }

                    @Override
                    public void onComplete() {
                        hideDialog();
                    }
                });
    }

    private void showErrorDialog(String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog errorDialog = DialogFactory.createMessageDialog(FlaggedInstanceActivity.this, getString(R.string.msg_download_task_failed), errorMessage);
                errorDialog.show();
            }
        });
    }


    private void initrecyclerViewImages(List<NotificationImage> framelist) {


        NotificationImageAdapter notificationImageAdapter = new NotificationImageAdapter(framelist);

        recyclerViewImages.setAdapter(notificationImageAdapter);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));

        notificationImageAdapter.setOnItemClickListener(this);
        recyclerViewImages.setNestedScrollingEnabled(false);
    }


    private void getNotificationDetail() {

        String url = FieldSightUserSession.getServerUrl(Collect.getInstance()) + loadedFieldSightNotification.getDetails_url();

        ServiceGenerator.getRxClient().create(ApiInterface.class)
                .getNotificationDetail(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<NotificationDetail>() {
                    @Override
                    public void onSuccess(NotificationDetail notificationDetail) {
                        loadImageInView(notificationDetail.getImages());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    private void loadImageInView(List<NotificationImage> urls) {
        Timber.i("%s images are present in notification", urls.size());
        initrecyclerViewImages(urls);
    }

    @Override
    public void onItemClick(View view, int position, List<NotificationImage> urls) {
        Timber.i(" Load item at %s from the list of size %s ", position, urls.size());
        loadSlideShowLayout(position, new ArrayList<NotificationImage>(urls));


    }

    private void loadSlideShowLayout(int position, ArrayList<NotificationImage> urls) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("images", urls);
        bundle.putInt("position", position);

    }


    private void loadSavedInstance(String fsInstanceId, String jrFormId) {
        Uri uri = InstanceProviderAPI.InstanceColumns.CONTENT_URI;
        String selection = InstanceProviderAPI.InstanceColumns.FS_SUBMISSION_INSTANCE_ID + "=?";
        String[] selectionArgs = new String[]{fsInstanceId};
        Cursor cursorInstanceForm = null;
        try {
            cursorInstanceForm = context.getContentResolver()
                    .query(uri, null,
                            selection,
                            selectionArgs, null);

            int count = cursorInstanceForm.getCount();
            List<Instance> instances = instancesDao.getInstancesFromCursor(cursorInstanceForm);

            if (count == 1) {
                Instance instance = instances.get(0);
                boolean doesVersionMatch = hasFormVersion();
                if (doesVersionMatch) {
                    openSavedForm(instance);
                } else {
                    hideDialog();
                    DialogFactory.createActionDialog(FlaggedInstanceActivity.this,
                            getString(R.string.msg_matching_form_verion_not_found),
                            getString(R.string.msg_missing_form_version))
                            .setPositiveButton(R.string.msg_yes_open_missing_version, (dialogInterface, i)
                                    -> openSavedForm(instance))
                            .setNegativeButton(R.string.msg_no_open_missing_version, (dialogInterface, i)
                                    -> createNewSubmission(loadedFieldSightNotification.getIdString()))
                            .show();
                }
            } else {
                createNewSubmission(jrFormId);
            }
        } catch (NullPointerException | CursorIndexOutOfBoundsException e) {
            ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
        } finally {
            if (cursorInstanceForm != null) {
                cursorInstanceForm.close();
            }
        }
    }

    private void openSavedForm(Instance cursorInstanceForm) {

        Toast.makeText(context, "Opening saved form.", Toast.LENGTH_LONG).show();

        Uri instanceUri =
                ContentUris.withAppendedId(InstanceProviderAPI.InstanceColumns.CONTENT_URI,
                        cursorInstanceForm.getDatabaseId());


        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action)) {
            // caller is waiting on a picked form
            setResult(RESULT_OK, new Intent().setData(instanceUri));
        } else {
            // the form can be edited if it is incomplete or if, when it was
            // marked as complete, it was determined that it could be edited
            // later.
            String status = cursorInstanceForm.getStatus();
            String strCanEditWhenComplete =
                    cursorInstanceForm.getCanEditWhenComplete();

            boolean canEdit = status.equals(InstanceProviderAPI.STATUS_INCOMPLETE)
                    || Boolean.parseBoolean(strCanEditWhenComplete);
            if (!canEdit) {
                //this form cannot be edited
                return;
            }

            Intent toEdit = new Intent(Intent.ACTION_EDIT, instanceUri);
            toEdit.putExtra("EditedFormID", cursorInstanceForm.getDatabaseId());
            startActivity(toEdit);
        }
        finish();
    }
}