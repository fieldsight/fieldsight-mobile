package org.bcss.collect.naxa.notificationslist;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;
import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.activities.FormEntryActivity;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.dao.InstancesDao;
import org.bcss.collect.android.dto.Instance;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.android.utilities.ApplicationConstants;
import org.bcss.collect.android.utilities.FileUtil;
import org.bcss.collect.android.utilities.FileUtils;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static org.bcss.collect.android.activities.FormEntryActivity.EXTRA_TESTING_PATH;
import static org.bcss.collect.android.utilities.QRCodeUtils.QR_CODE_FILEPATH;
import static org.bcss.collect.naxa.network.APIEndpoint.BASE_URL;


public class FlagResposneActivity extends CollectAbstractActivity implements View.OnClickListener, NotificationImageAdapter.OnItemClickListener {

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


    public static void start(Context context, FieldSightNotification fieldSightNotification) {
        Intent intent = new Intent(context, FlagResposneActivity.class);
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


        formBox.setOnClickListener(this);

        loadedFieldSightNotification = getIntent().getParcelableExtra(Constant.EXTRA_OBJECT);
        setupData(loadedFieldSightNotification);

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        noMessage = (TextView) findViewById(R.id.textView6);
        tvFormName = (TextView) findViewById(R.id.tv_form_name);
        tvFormDesc = (TextView) findViewById(R.id.tv_form_desc);
        imbStatus = (ImageButton) findViewById(R.id.img_btn_status);
        tvFormStatus = (TextView) findViewById(R.id.tv_form_status);
        tvComment = (TextView) findViewById(R.id.tv_comments_txt);
        recyclerViewImages = (RecyclerView) findViewById(R.id.comment_session_rv_images);


        relativeStatus = (RelativeLayout) findViewById(R.id.relativeLayout_status);
        formBox = (RelativeLayout) findViewById(R.id.relative_layout_comment_open_form);
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

    /**
     * find the form primary key then open the saved instance of that form (if present)
     * other wise open the form blank
     */
    private void handleFlagForm(String fsFormId) {

        Instance instance = null;
        Cursor cursor = null;
        try {
            InstancesDao dao = new InstancesDao();
            cursor = dao.getInstancesCursor(null, null);
            List<Instance> list = dao.getInstancesFromCursor(cursor);
            for (Instance curInstance : list) {
                String url = curInstance.getSubmissionUri();
                String fsFormIdFromUrl = dao.getFsFormIdFromUrl(url);
                if (fsFormIdFromUrl.equals(fsFormId)) {
                    instance = curInstance;
                    break;
                }
            }


            if (instance != null) {
                openSavedForm(cursor);
            } else {
                openNewForm(instance.getJrFormId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void openSavedForm(Cursor cursorInstanceForm) {

        Toast.makeText(context, "Opening saved form.", Toast.LENGTH_LONG).show();

        cursorInstanceForm.moveToFirst();
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


    protected void fillODKForm(String idString) {
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

            }
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            DialogFactory.createGenericErrorDialog(this, e.getMessage()).show();
            Timber.e("Failed to load xml form %s", e.getMessage());
        } catch (CursorIndexOutOfBoundsException e) {
            DialogFactory.createGenericErrorDialog(this, getString(R.string.form_not_present)).show();
            Timber.e("Failed to load xml form  %s", e.getMessage());
        }


    }

    protected String generateSubmissionUrl(String formDeployedFrom, String siteId, String fsFormId) {
        return InstancesDao.generateSubmissionUrl(formDeployedFrom, siteId, fsFormId);
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
        int id = v.getId();


        handleFlagForm(loadedFieldSightNotification.getFsFormId());
    }

    private void downloadInstance() {
        FlagFormRemoteSource.getINSTANCE()
                .runAll(loadedFieldSightNotification)
                .subscribe(new DisposableObserver<Uri>() {
                    @Override
                    public void onNext(Uri instanceUri) {
                        Timber.i("Downloaded and saved instance at %s", instanceUri);
                        Intent toEdit = new Intent(Intent.ACTION_EDIT, instanceUri);
                        toEdit.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
                        toEdit.putExtra("EditedFormID", instanceUri.getLastPathSegment());
                        startActivity(toEdit);


                        Intent intent = new Intent(Intent.ACTION_EDIT, instanceUri);
                        intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
                                ApplicationConstants.FormModes.EDIT_SAVED);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.i("Error downloading instance, reason: %s", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static String formPath() {
        return Environment.getExternalStorageDirectory().getPath();
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

        String url = loadedFieldSightNotification.getDetails_url();


        ApiInterface apiService = ServiceGenerator.createService(ApiInterface.class);
        Call<NotificationDetail> call = apiService.getNotificationDetail(BASE_URL + url);
        call.enqueue(new Callback<NotificationDetail>() {
            @Override
            public void onResponse(Call<NotificationDetail> call, Response<NotificationDetail> response) {
                handleDetailsResponse(response);
            }

            @Override
            public void onFailure(Call<NotificationDetail> call, Throwable t) {

                DialogFactory.createDataSyncErrorDialog(FlagResposneActivity.this, "Failed to load images", String.valueOf(500)).show();
            }
        });
    }

    private void handleDetailsResponse(Response<NotificationDetail> response) {

        if (response.code() != 200 || response.body() == null) {
            DialogFactory.createDataSyncErrorDialog(FlagResposneActivity.this, "Failed to load images", String.valueOf(500)).show();
            return;
        }

        loadImageInView(response.body().getImages());
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
        DialogFactory.createGenericErrorDialog(this, "Failed to load image").show();
    }
}