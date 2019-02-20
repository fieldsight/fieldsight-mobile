package org.bcss.collect.naxa.notificationslist;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;
import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.listeners.DownloadFormsTaskListener;
import org.bcss.collect.android.logic.FormDetails;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.RxDownloader.RxDownloader;
import org.bcss.collect.naxa.common.rx.RetrofitException;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.site.FragmentHostActivity;
import org.bcss.collect.naxa.site.SiteListAdapter;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.tasks.DownloadFormListTask;
import org.odk.collect.android.tasks.DownloadFormsTask;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static org.bcss.collect.naxa.network.APIEndpoint.BASE_URL;


public class FlaggedInstanceActivity extends CollectAbstractActivity implements View.OnClickListener, NotificationImageAdapter.OnItemClickListener {

    private static String TAG = "Comment Activity";
    //constants for form status
    private final String FLAGGED_FORM = "Flagged";
    private final String OUTSTANDING_FORM = "Outstanding";
    private final String REJECTED_FORM = "Rejected";
    private final String APPROVED_FORM = "Approved";

    Context context = this;

    TextView noMessage, tvFormName, tvFormDesc, tvComment, tvFormStatus;
    RecyclerView recyclerViewImages;
    ImageButton imbStatus;
    RelativeLayout relativeStatus;
    RelativeLayout formBox;
    private FieldSightNotification loadedFieldSightNotification;
    private Toolbar toolbar;


    private DownloadFormListTask downloadFormListTask;
    private DownloadFormsTask downloadFormsTask;
    private ProgressDialog dialog;
    private HashMap<String, Boolean> formResult;
    private Dialog errorDialog;
    private FormsDao formsDao;
    private InstancesDao instancesDao;
    private TextView tvSiteIdentifier;
    private TextView tvSiteName;
    private TextView tvIconText;
    private TextView tvSiteAddress;
    private ImageView ivCircleSite;
    private TextView tvSiteMissing;
    private RelativeLayout cardViewSite;


    public static void start(Context context, FieldSightNotification fieldSightNotification) {
        Intent intent = new Intent(context, FlaggedInstanceActivity.class);
        intent.putExtra(Constant.EXTRA_OBJECT, fieldSightNotification);
        context.startActivity(intent);
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Form Flagged");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_flag_response);

        bindUI();
        setupToolbar();

        formsDao = new FormsDao();
        instancesDao = new InstancesDao();
        formBox.setOnClickListener(this);

        loadedFieldSightNotification = getIntent().getParcelableExtra(Constant.EXTRA_OBJECT);
        formResult = new HashMap<>();
        setupData(loadedFieldSightNotification);
        setupSiteCard(loadedFieldSightNotification);

    }

    private void setupSiteCard(FieldSightNotification loadedFieldSightNotification) {
        String siteName = loadedFieldSightNotification.getSiteName();
        String siteIdentifier = loadedFieldSightNotification.getSiteIdentifier();

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

                    cardViewSite.setOnClickListener(v -> FragmentHostActivity.start(FlaggedInstanceActivity.this, site));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindUI() {

        //layout element ids
        toolbar = findViewById(R.id.toolbar);
        noMessage = findViewById(R.id.textView6);
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
            noMessage.setText(R.string.comments_default_comment);
            noMessage.setVisibility(View.VISIBLE);
            tvComment.setText("");
        } else {
            noMessage.setVisibility(View.GONE);
            tvComment.setText(comment);
        }

        //set values to text view on layout
        tvFormName.setText(formName);
        //  tvFormDesc.setText(jrFormId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imbStatus.setElevation(3);
        }

        if (formStatus != null && formStatus.equals("Approved")) {
            imbStatus.setBackgroundResource(R.color.green_approved);
            relativeStatus.setBackgroundResource(R.color.green_approved);
        } else if (formStatus != null && formStatus.equals("Outstanding")) {
            imbStatus.setBackgroundResource(R.color.grey_outstanding);
            relativeStatus.setBackgroundResource(R.color.grey_outstanding);
        } else if (formStatus != null && formStatus.equals("Flagged")) {
            imbStatus.setBackgroundResource(R.color.yellow_flagged);
            relativeStatus.setBackgroundResource(R.color.yellow_flagged);
        } else if (formStatus != null && formStatus.equals("Rejected")) {
            imbStatus.setBackgroundResource(R.color.red_rejected);
            relativeStatus.setBackgroundResource(R.color.red_rejected);
        }

        tvFormStatus.setText(formStatus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            getNotificationDetail();
        } catch (NullPointerException e) {
            DialogFactory.createDataSyncErrorDialog(this, "Failed to load images", String.valueOf(500)).show();
        }
    }

    protected long getFormId(String jrFormId) throws CursorIndexOutOfBoundsException, NullPointerException, NumberFormatException {

        String[] projection = new String[]{FormsProviderAPI.FormsColumns._ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH};
        String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
        String[] selectionArgs = new String[]{jrFormId};
        String sortOrder = FormsProviderAPI.FormsColumns.JR_VERSION + " DESC LIMIT 1";

        Cursor cursor = getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                projection,
                selection, selectionArgs, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID);
        long formId = Long.parseLong(cursor.getString(columnIndex));

        cursor.close();

        return formId;
    }

    private void openNewForm(String jsFormId) {


        Toast.makeText(context, "No, saved form found.", Toast.LENGTH_LONG).show();


        Cursor cursorForm = context.getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI, null,
                FormsProviderAPI.FormsColumns.JR_FORM_ID + " =?",
                new String[]{jsFormId}, null);


        if (cursorForm != null && cursorForm.getCount() != 1) {
            //bad data
            //fix the error later
            return;
        }

        cursorForm.moveToFirst();
        long idFormsTable = Long.parseLong(cursorForm.getString(cursorForm.getColumnIndex(FormsProviderAPI.FormsColumns._ID)));
        Timber.d("Opening new form with _ID%s", idFormsTable);

        Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, idFormsTable);

        String action = getIntent().getAction();

        if (Intent.ACTION_PICK.equals(action)) {
            // caller is waiting on a picked form
            setResult(RESULT_OK, new Intent().setData(formUri));
        } else {
            // caller wants to view/edit a form, so launch formentryactivity

            Intent toFormEntry = new Intent(Intent.ACTION_EDIT, formUri);
            toFormEntry.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(toFormEntry);

        }

        cursorForm.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_layout_comment_open_form:
                boolean emptyVersion = TextUtils.isEmpty(loadedFieldSightNotification.getFormVersion());
                if (emptyVersion) {
                    showFormIsLegacyDialog();
                    return;
                }

                boolean isInstanceDownloadNeeded = !hasFormVersion() || !hasFormInstance();
                Timber.d("hasFormVersion %s hasFormInstance %s, isInstanceDownloadNeeded %s", hasFormVersion(), hasFormInstance(), isInstanceDownloadNeeded);
                if (isInstanceDownloadNeeded) {
                    showDownloadInstanceDialog();
                } else {
                    loadSavedInstance(loadedFieldSightNotification.getFormSubmissionId(), loadedFieldSightNotification.getIdString());
                }

                break;
        }
    }

    private void showDownloadInstanceDialog() {
        DialogFactory.createActionDialog(this, getString(R.string.dialog_title_missing_flag_form), getString(R.string.dialog_text_missing_flag_form))
                .setPositiveButton(R.string.dialog_action_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                })
                .setNegativeButton(R.string.dialog_action_dismiss, null)
                .show();
    }


    private void loadInstance(Uri instanceUri) {
        Intent toEdit = new Intent(Intent.ACTION_EDIT, instanceUri);
        toEdit.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
        toEdit.putExtra("EditedFormID", instanceUri.getLastPathSegment());
        startActivity(toEdit);
    }

    private void showFormIsLegacyDialog() {
        DialogFactory.createActionDialog(this, getString(R.string.dialog_title_cant_open_flagged_form), getString(R.string.dialog_text_cant_edit_flag_form))
                .setPositiveButton(R.string.dialog_action_view_data, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.dialog_action_make_new_submission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openNewForm(loadedFieldSightNotification.getFsFormId());
                    }
                })
                .setNeutralButton(R.string.dialog_action_dismiss, null)
                .show();
    }

    private void downloadFormVersion(FieldSightNotification loadedFieldSightNotification) {
        downloadFormAndInstance(loadedFieldSightNotification, true);
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

        Cursor cursor = instancesDao.getInstancesCursor(fieldSightInstanceId);
        if (cursor != null) {
            return cursor.getCount() == 1;
        }
        return false;
    }


    private void downloadFormAndInstance(FieldSightNotification notificationFormDetail, boolean loadInstanceAfterDownloadComplete) {

        String formName = notificationFormDetail.getFormName();
        String fsFormSubmissionId = notificationFormDetail.getFormSubmissionId();
        String jrFormId = "";
        String downloadUrl = String.format(FieldSightUserSession.getServerUrl(Collect.getInstance()) + "/forms/api/instance/download_xml_version/%s", fsFormSubmissionId);

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
        dialog = DialogFactory.createProgressDialogHorizontal(FlaggedInstanceActivity.this, "Loading Form");
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

                for (FormDetails formDetails : result.keySet()) {
                    String successKey = result.get(formDetails);
                    if (Collect.getInstance().getString(R.string.success).equals(successKey)) {
                        if (loadInstanceAfterDownloadComplete) {
                            loadSavedInstance(notification.getFormSubmissionId(), notification.getIdString());
                        } else {
                            downloadInstance(notification);
                        }
                        break;
                    } else {
                        hideDialog();
                        showErrorDialog(result.get(formDetails));
                    }
                }
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

    private void downloadInstance(FieldSightNotification notification) {

        String[] nameAndPath = InstanceRemoteSource.getINSTANCE().getNameAndPath(notification.getFormName());
        String pathToDownload = nameAndPath[1];


        Observable<String> attachedMediaObservable = InstanceRemoteSource.getINSTANCE()
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


        Observable<Uri> instanceObservable = InstanceRemoteSource.getINSTANCE()
                .downloadInstances(notification, nameAndPath);

        Observable.concat(attachedMediaObservable, instanceObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Comparable<? extends Comparable<?>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        changeDialogMsg("Downloading filled form");
                    }

                    @Override
                    public void onNext(Comparable<? extends Comparable<?>> comparable) {
                        if (comparable instanceof Uri) {
                            hideDialog();
                            Uri instanceUri = (Uri) comparable;
                            loadInstance(instanceUri);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e(throwable);
                        hideDialog();
                        String errorMessage = throwable.getMessage();
                        if (throwable instanceof RetrofitException) {
                            RetrofitException retrofitException = ((RetrofitException) throwable);
                            errorMessage = retrofitException.getKind().toString();
                        }
                        showErrorDialog(errorMessage);

                    }

                    @Override
                    public void onComplete() {
                        hideDialog();
                    }
                });

    }

    private String createInstanceFolder(FieldSightNotification notification) {
        return null;
    }

    @Nullable
    private String getFilePathFromUri(Uri instanceUri) {

        String instanceFolderPath = null;
        String uriMimeType = null;

        if (instanceUri != null) {
            uriMimeType = getContentResolver().getType(instanceUri);
        }

        if (uriMimeType != null
                && uriMimeType.equals(InstanceProviderAPI.InstanceColumns.CONTENT_ITEM_TYPE)) {
            Cursor instance = null;
            try {
                instance = getContentResolver().query(instanceUri,
                        null, null, null, null);
                if (instance != null && instance.getCount() == 1) {
                    instance.moveToFirst();
                    instanceFolderPath = instance
                            .getString(instance
                                    .getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH));
                    String fileName = FilenameUtils.getName(instanceFolderPath);
                    instanceFolderPath = instanceFolderPath.replace(fileName, "");
                }
            } finally {
                if (instance != null) {
                    instance.close();
                }
            }
        }


        return instanceFolderPath;
    }


    private void showErrorDialog(String errorMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                errorDialog = DialogFactory.createMessageDialog(FlaggedInstanceActivity.this,getString(R.string.msg_download_task_failed), errorMessage);
                errorDialog.show();
            }
        });


    }


    private void initrecyclerViewImages(List<NotificationImage> framelist) {


        NotificationImageAdapter notificationImageAdapter = new NotificationImageAdapter(framelist);

        recyclerViewImages.setAdapter(notificationImageAdapter);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        notificationImageAdapter.setOnItemClickListener(this);
        //       recyclerViewImages.addItemDecoration(new LinePagerIndicatorDecoration());

        recyclerViewImages.setNestedScrollingEnabled(false);
    }


    private void getNotificationDetail() throws NullPointerException {

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
                        String errorMessage = RetrofitException.getMessage(e);
                        DialogFactory.createMessageDialog(FlaggedInstanceActivity.this, getString(R.string.msg_site_upload_fail), errorMessage).show();

                    }
                });
    }

    private void loadImageInView(List<NotificationImage> urls) {
        Timber.i("%s images are present in notification", urls.size());
        initrecyclerViewImages(urls);
    }

    @Override
    public void onItemClick(View view, int position, List<NotificationImage> urls) {
        Timber.i(" Load item at %s siteName the list of size %s ", position, urls.size());
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
            if (count == 1) {
                openSavedForm(cursorInstanceForm);
            } else {
                openNewForm(jrFormId);
            }
        } catch (NullPointerException | CursorIndexOutOfBoundsException e) {
            ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
        } finally {
            if (cursorInstanceForm != null) {
                cursorInstanceForm.close();
            }
        }
    }

    /**
     * find the form primary key then open the saved instance of that form (if present)
     * other wise open the form blank
     */
    private void handleFlagForm(String fsFormId, String jrFormId, String siteId) {

        Uri uri = InstanceProviderAPI.InstanceColumns.CONTENT_URI;
        String selection = InstanceProviderAPI.InstanceColumns.SUBMISSION_URI + " LIKE ?"
                + " AND " + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + " =? ";
        String[] selectionArgs = new String[]{"%/" + fsFormId + "/%", siteId};

        Cursor cursorInstanceForm = null;
        try {
            cursorInstanceForm = context.getContentResolver()
                    .query(uri, null,
                            selection,
                            selectionArgs, null);

            Timber.i("Found %s instances", cursorInstanceForm.getCount());
            int count = cursorInstanceForm.getCount();
            if (count >= 1) {
                //todo atm opens the latest saved need to compare timestamp with server submission to open exact instance
                openSavedForm(cursorInstanceForm);
            } else {
                openNewForm(jrFormId);
            }
        } catch (NullPointerException | CursorIndexOutOfBoundsException e) {
            ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
        } finally {
            if (cursorInstanceForm != null) {
                cursorInstanceForm.close();
            }
        }

    }


    private void openSavedForm(Cursor cursorInstanceForm) {

        Toast.makeText(context, "Opening saved form.", Toast.LENGTH_LONG).show();

        cursorInstanceForm.moveToLast();
        long idFormsTable = Long.parseLong(cursorInstanceForm.getString(cursorInstanceForm.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID)));
        Log.d(TAG, "Opening saved form with _ID" + idFormsTable);

        Uri instanceUri =
                ContentUris.withAppendedId(InstanceProviderAPI.InstanceColumns.CONTENT_URI,
                        idFormsTable);


        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action)) {
            // caller is waiting on a picked form
            setResult(RESULT_OK, new Intent().setData(instanceUri));
        } else {
            // the form can be edited if it is incomplete or if, when it was
            // marked as complete, it was determined that it could be edited
            // later.
            String status = cursorInstanceForm.getString(cursorInstanceForm.getColumnIndex(InstanceProviderAPI.InstanceColumns.STATUS));
            String strCanEditWhenComplete =
                    cursorInstanceForm.getString(cursorInstanceForm.getColumnIndex(InstanceProviderAPI.InstanceColumns.CAN_EDIT_WHEN_COMPLETE));

            boolean canEdit = status.equals(InstanceProviderAPI.STATUS_INCOMPLETE)
                    || Boolean.parseBoolean(strCanEditWhenComplete);
            if (!canEdit) {
                //this form cannot be edited
                return;
            }

            // caller wants to view/edit a form, so launch FormEntryActivity
            //send the slected id to the upload button
            //Susan

            Long selectedFormId = cursorInstanceForm.getLong(cursorInstanceForm.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID));
            Intent toEdit = new Intent(Intent.ACTION_EDIT, instanceUri);
            toEdit.putExtra("EditedFormID", selectedFormId);
            startActivity(toEdit);
        }
        finish();
    }
}