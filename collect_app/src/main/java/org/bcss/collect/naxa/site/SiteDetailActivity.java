//package org.bcss.collect.naxa.site;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.database.ContentObserver;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.CoordinatorLayout;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.TextInputLayout;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//
//import org.bcss.collect.android.R;
//import org.bcss.collect.android.activities.FormEntryActivity;
//import org.bcss.collect.android.activities.GeoPointActivity;
//import org.bcss.collect.android.utilities.ToastUtils;
//import org.bcss.collect.naxa.common.AppBarStateChangeListener;
//import org.bcss.collect.naxa.common.Constant;
//import org.bcss.collect.naxa.common.DialogFactory;
//import org.bcss.collect.naxa.common.ViewUtils;
//import org.bcss.collect.naxa.login.model.Site;
//import org.bcss.collect.naxa.login.model.SiteBuilder;
//import org.bcss.collect.naxa.site.data.SiteCluster;
//import org.bcss.collect.naxa.site.db.SiteLocalSource;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.observers.DisposableObserver;
//import timber.log.Timber;
//
//
//public class SiteDetailActivity extends AppCompatActivity {
//
//    private static final int REQUEST_CAMERA = 1994;
//    private static final int SELECT_FILE = 1993;
//
//    private Site loadedSite;
//    private CoordinatorLayout layoutSiteDetail;
//    private LinearLayout layoutDataDisplay, layoutDataEdit;
//    private TextInputLayout tiSiteNameEditable, tiSiteAddressEditable, tiSiteIdentifierEditable, tiSitePhoneNumberEditable, tiPublicDescEditable;
//    private TextInputLayout tiSiteName, tiSiteAddress, tiSiteIdentifier, tiSitePhoneNumber, tiSitePublicDesc, tiSiteRegion, tiSiteType;
//    private FloatingActionButton fabFormAction;
//    private ImageView ivSitePhotoEditable, ivSitePhoto;
//    private boolean editMode = false;
//    private Toolbar toolbar;
//    private Button btnAddPhoto, btnRecordGPS;
//    private File PhotoToBeUploaded;
//    private Uri ImageToBeUploaded;
//    private String userChoosenTask;
//    private ArrayList<Integer> layoutIdsEditable = new ArrayList<>();
//    private ArrayList<Integer> layoutIds = new ArrayList<>();
//    private Spinner spinnerSiteCluster, spinnerSiteType;
//    private String projectId;
//    private ArrayList<SiteCluster> regionList;
//    private String lat = "0";
//    private String lon = "0";
//    private AppBarLayout appbar;
//    private ProgressDialog progressDialog;
//    private String siteType, siteCluster;
//
//
//    public static void start(Context context, @NonNull String siteId) {
//
//        Intent intent = new Intent(context, SiteDetailActivity.class);
//        intent.putExtra(Constant.EXTRA_ID, siteId);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_site_detail_change);
//        layoutIdsEditable = new ArrayList<>();
//        String siteId = getIntent().getExtras().getString(Constant.EXTRA_ID);
//        SiteLocalSource.getInstance().getById();
//
//        bindUI();
//        setupToolbar();
//        setupSiteInformation(loadedSite);
////        setupClusterSpinner(loadedSite.getProjectId());
////        setupSiteTypeSpinner(loadedSite.getProjectId());
//
//        fabFormAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Site updatedSite = null;
//                toggleEditMode();
//
//            }
//        });
//
//        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showImageOptionsDialog();
//            }
//        });
//
//        btnRecordGPS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toGPSactivity();
//            }
//        });
//
//        appbar.addOnOffsetChangedListener(new AppBarStateChangeListener() {
//            @Override
//            public void onStateChanged(AppBarLayout appBarLayout, int state) {
//                switch (state) {
//                    case AppBarStateChangeListener.State.EXPANDED:
//                        fabFormAction.show();
//                        break;
//                    case AppBarStateChangeListener.State.COLLAPSED:
//                        fabFormAction.hide();
//                        break;
//                    case AppBarStateChangeListener.State.IDLE:
//                        fabFormAction.show();
//                        break;
//                }
//            }
//        });
//
////        ivSitePhoto.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                ArrayList<NotificaitonImage> list = new ArrayList<NotificaitonImage>() {{
////                    add(new NotificaitonImage(0, loadedSite.getSiteImagePath()));
////                }};
////
////                ImageSliderActivity.start(SiteDetailActivity.this, list, 0);
////            }
////        });
////
////        ivSitePhotoEditable.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////
////                ArrayList<NotificaitonImage> list = new ArrayList<NotificaitonImage>() {{
////                    add(new NotificaitonImage(0, loadedSite.getSiteImagePath()));
////                }};
////
////                ImageSliderActivity.start(SiteDetailActivity.this, list, 0);
////            }
////        });
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void setupClusterSpinner(String projectId) {
////        ProjectDaoOld projectDao = new ProjectDaoOld();
////        Cursor cursor = projectDao.getRegionsCursor(projectId, null);
////        regionList = projectDao.getSiteCluster(cursor);
////
////        SiteClusterSpinnerAdapter siteClusterSpinnerAdapter = new SiteClusterSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, regionList);
////        spinnerSiteCluster.setAdapter(siteClusterSpinnerAdapter);
////
////        int selected = siteClusterSpinnerAdapter.getCount();
////        if (regionList != null && regionList.size() > 0) {
////            for (int i = 0; i < regionList.size(); i++) {
////                if (String.valueOf(regionList.get(i).getId()).equals(loadedSite.getSiteCluster())) {
////                    selected = i;
////
////
////                    break;
////                }
////            }
////        }
////
////        int visibility = View.VISIBLE;
////        if (regionList == null || regionList.size() == 1) {
////            visibility = View.GONE;
////        }
////        spinnerSiteCluster.setVisibility(visibility);
////        spinnerSiteCluster.setSelection(selected);
//
//    }
//
//    private void setupSiteTypeSpinner(String projectId) {
////        List<SiteType> siteTypes = new SiteTypeDao().getSiteTypeByProject(projectId);
////        SiteTypeSpinnerAdapter myAdapter = new SiteTypeSpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, siteTypes);
////        spinnerSiteType.setAdapter(myAdapter);
////
////        int selected = myAdapter.getCount();
////        if (siteTypes != null && siteTypes.size() > 0) {
////            for (int i = 0; i < siteTypes.size(); i++) {
////                if (String.valueOf(siteTypes.get(i).getId()).equals(loadedSite.getSiteTypeId())) {
////                    selected = i;
////                    break;
////                }
////            }
////        }
////        int visibility = View.VISIBLE;
////        if (siteTypes == null || siteTypes.size() == 1) {
////            visibility = View.GONE;
////        }
////
////        Timber.i("%s %s", siteTypes == null, siteTypes.size());
////        spinnerSiteType.setVisibility(visibility);
////        spinnerSiteType.setSelection(selected);
//    }
//
//
//    private Site generateUpdatedSite() {
//
//        JSONObject jsonObject = new JSONObject();
//
//        for (Integer layoutId : layoutIdsEditable) {
//            if (findViewById(layoutId) instanceof TextInputLayout) {
//                TextInputLayout textInput = findViewById(layoutId);
//                String answer = textInput.getEditText().getText().toString().trim();
//                String submissionKey = (String) textInput.getHint();
//
//                try {
//
//                    jsonObject.put(submissionKey, answer);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        String metaAttrsAnswer = jsonObject.toString();
//        String imagePath;
//        String latToUpload = "0";
//        String lonToUpload = "0";
//
//        try {
//
//            boolean isOldLat = lat.equalsIgnoreCase(String.valueOf(loadedSite.getLatitude()));
//            boolean isOldLon = lon.equalsIgnoreCase(String.valueOf(loadedSite.getLongitude()));
//
//            if (isOldLat && isOldLon) {
//                latToUpload = String.valueOf(loadedSite.getLatitude());
//                lonToUpload = String.valueOf(loadedSite.getLongitude());
//            } else {
//                latToUpload = lat;
//                lonToUpload = lon;
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        if (PhotoToBeUploaded != null) {
//            imagePath = PhotoToBeUploaded.getAbsolutePath();
//        } else {
//            imagePath = "";
//        }
//
//        String siteTypeId = "", siteClusterId = "", siteTypeLabel = "";
//
//        if (spinnerSiteType.getVisibility() == View.VISIBLE) {
//            SiteType siteType = (SiteType) spinnerSiteType.getSelectedItem();
//            if (siteType != null) {
//                siteTypeId = siteType.getId();
//                siteTypeLabel = siteType.getName();
//            }
//        }
//
//        if (spinnerSiteType.getVisibility() == View.VISIBLE) {
//            SiteCluster siteCluster = (SiteCluster) spinnerSiteCluster.getSelectedItem();
//            if (siteCluster != null) {
//                siteClusterId = siteCluster.getId();
//            }
//        }
//
//
//        Site updatedSite = new SiteBuilder()
//                .setName(getValue(tiSiteNameEditable))
//                .setAddress(getValue(tiSiteAddressEditable))
//                .setPhone(getValue(tiSitePhoneNumberEditable))
//                .setIdentifier(getValue(tiSiteIdentifierEditable))
//                .setMetaAttributes(metaAttrsAnswer)
//                .setId(loadedSite.getId())
//                .setTypeId(siteTypeId)
//                .setTypeLabel(siteTypeLabel)
//                .setPublicDesc(loadedSite.getPublicDesc())
//                .setAdditionalDesc(loadedSite.getAdditionalDesc())
//                .setAddress(loadedSite.getAddress())
//                .setLatitude(latToUpload)
//                .setLongitude(lonToUpload)
//                .setIsSiteVerified(loadedSite.getIsSiteVerified())
//                .setLongitude(imagePath)
//                .createSite();
//
//
//    }
//
//
//    private String getValue(TextInputLayout inputLayout) {
//        return inputLayout.getEditText().getText().toString().trim();
//    }
//
//    private void toggleEditMode() {
//        if (editMode) {
//            validateFormChanges();
//            //reload site on toggle
//            loadedSite = DatabaseHelper.getInstance().getSingleSite(loadedSite.getId());
//            setupSiteInformation(loadedSite);
//        } else {
//            openEditMode();
//        }
//
//        this.editMode = !editMode;
//    }
//
//
//    private void showSaveChangesDialog(final Site updatedSite) {
//        DialogFactory.createActionDialog(SiteDetailActivity.this, "Save changes", "Save updated site information?")
//                .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        saveFormChanges(updatedSite);
//                    }
//                }).setNegativeButton("Discard Changes", null)
//                .setNeutralButton(R.string.dialog_action_dismiss, null)
//                .show();
//    }
//
//    private void saveFormChanges(Site updatedSite) {
//        switch (loadedSite.getIsSiteVerified()) {
//            case Constant.SiteStatus.IS_UNVERIFIED_SITE:
//            case Constant.SiteStatus.IS_VERIFIED_BUT_UNSYNCED:
//                long result = updateSiteInfo(updatedSite);
//                if (result != -1) {
//                    ToastUtils.showLongToast("Changes saved");
//                } else {
//                    ToastUtils.showLongToast("Failed to save changes");
//                }
//
//                closeEditMode();
//                break;
//            default:
//                uploadSite(updatedSite);
//                break;
//        }
//    }
//
//    private void validateFormChanges() {
//        if (validateText(tiSiteNameEditable)
//                && validateText(tiSiteIdentifierEditable)
//                && validateText(tiSitePhoneNumberEditable)
//                && validateText(tiSiteAddressEditable)) {
//
//            Site updatedSite = generateUpdatedSite();
//            saveFormChanges(updatedSite);
////            if (!updatedSite.equals(loadedSite)) {
//            //showSaveChangesDialog(updatedSite);
////            }
//
//        }
//    }
//
//    public static boolean validateText(TextInputLayout textInputLayout) {
//        String string = textInputLayout.getEditText().getText().toString();
//        boolean isViewVisible = textInputLayout.getVisibility() == View.VISIBLE;
//
//        if (!isViewVisible) {
//            return true;
//        }
//
//        Boolean isValid = string.length() > 0;
//        if (isValid) {
//            textInputLayout.setErrorEnabled(false);
//            return true;
//        }
//
//
//        textInputLayout.requestFocus();
//        textInputLayout.setError("This is a required field.");
//        return false;
//    }
//
//    private long updateSiteInfo(Site Site) {
//        return DatabaseHelper.getInstance().updateSite(Site);
//    }
//
//
//    private void showProgressDialog() {
//        progressDialog = DialogFactory.createProgressDialogHorizontal(this, "Saving changes");
//        progressDialog.show();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        closeProgressDialog();
//    }
//
//    private void closeProgressDialog() {
//        if (progressDialog != null) {
//            progressDialog.dismiss();
//        }
//    }
//
//    private void uploadSite(final Site updatedSite) {
//
//
////        new SiteDownload()
////                .updateSite(updatedSite, PhotoToBeUploaded, new SiteDownload.SiteUpdateListener() {
////                    @Override
////                    public void uploadStarted() {
////                        showProgressDialog();
////                    }
////
////                    @Override
////                    public void uploadSucess(Site locationPojo) {
////                        closeProgressDialog();
////                        closeEditMode();
////                        updateSiteInfo(updatedSite);
////                        getSupportActionBar().setTitle(updatedSite.getName());
////                        setupSiteInformation(updatedSite);
////                    }
////
////                    @Override
////                    public void uploadFailed(String errorMsg) {
////                        closeProgressDialog();
////                        DialogFactory
////                                .createSimpleOkErrorDialog(SiteDetailActivity.this, "Site upload failed", "Failed to upload changed site details.")
////                                .show();
////                    }
////                });
//    }
//
//    private void closeEditMode() {
//
//        layoutDataEdit.setVisibility(View.GONE);
//        layoutDataDisplay.setVisibility(View.VISIBLE);
//        fabFormAction.setImageResource(R.drawable.ic_edit);
//
//
//    }
//
//
//    private void openEditMode() {
//        layoutDataDisplay.setVisibility(View.GONE);
//        layoutDataEdit.setVisibility(View.VISIBLE);
//        fabFormAction.setImageResource(R.drawable.ic_check);
//
//    }
//
//    private void bindUI() {
//        toolbar = findViewById(R.id.toolbar);
//        appbar = findViewById(R.id.appbar_flexible);
//        layoutSiteDetail = findViewById(R.id.layout_site_detail);
//        fabFormAction = findViewById(R.id.fab_activate_edit_mode);
//        layoutDataDisplay = findViewById(R.id.layout_site_data_display);
//        layoutDataEdit = findViewById(R.id.layout_site_data_edit);
//        ivSitePhoto = findViewById(R.id.iv_site_photo);
//        tiSiteRegion = findViewById(R.id.il_region);
//        tiSiteType = findViewById(R.id.il_site_type);
//
//        ivSitePhotoEditable = findViewById(R.id.iv_site_photo_editable);
//
//        tiSiteNameEditable = findViewById(R.id.il_site_name_editable);
//        tiSiteIdentifierEditable = findViewById(R.id.il_site_identifier_editable);
//        tiSitePhoneNumberEditable = findViewById(R.id.il_phone_editable);
//        tiSiteAddressEditable = findViewById(R.id.il_address_editable);
//        tiPublicDescEditable = findViewById(R.id.il_public_desc_editable);
//        spinnerSiteCluster = findViewById(R.id.spinnerSiteCluster);
//        spinnerSiteType = findViewById(R.id.spinner_site_type);
//
//
//        tiSiteName = findViewById(R.id.il_site_name);
//        tiSiteIdentifier = findViewById(R.id.il_site_identifier);
//        tiSitePhoneNumber = findViewById(R.id.il_phone);
//        tiSiteAddress = findViewById(R.id.il_address);
//        tiSitePublicDesc = findViewById(R.id.il_public_desc);
//
//        btnAddPhoto = findViewById(R.id.btn_site_edit_add_photo);
//        btnRecordGPS = findViewById(R.id.btn_site_records_location);
//
//    }
//
//    private void setupToolbar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(loadedSite.getName());
//    }
//
//    private void setupSiteInformation(Site updatedSite) {
//
//
//        showOrHideText(tiSiteNameEditable, updatedSite.getName());
//        showOrHideText(tiSiteAddressEditable, updatedSite.getAddress());
//        showOrHideText(tiSiteIdentifierEditable, updatedSite.getIdentifier());
//        showOrHideText(tiSitePhoneNumberEditable, updatedSite.getPhone());
//        showOrHideText(tiPublicDescEditable, updatedSite.getPublicDesc());
//
//        showOrHideText(tiSiteName, updatedSite.getName());
//        showOrHideText(tiSiteAddress, updatedSite.getAddress());
//        showOrHideText(tiSiteIdentifier, updatedSite.getIdentifier());
//        showOrHideText(tiSitePhoneNumber, updatedSite.getPhone());
//        showOrHideText(tiSitePublicDesc, updatedSite.getPublicDesc());
//
//        showOrHideText(tiSiteRegion, siteCluster);
//        showOrHideText(tiSiteType, siteType);
//
//
//        String url = updatedSite.getLogo();
//
//        if (url != null && !url.isEmpty()) {
//            Timber.i("Loading image %s", url);
//            ViewUtils.loadRemoteImage(this, url).into(ivSitePhoto);
//            ViewUtils.loadRemoteImage(this, url).into(ivSitePhotoEditable);
//            showImagePreview();
//        }
//
//        removeInflatedViews();
//        inflateViews();
//    }
//
//    private void inflateViews() {
//        setupMetaAttrsDisplay()
//                .subscribe(new DisposableObserver<View>() {
//                    @Override
//                    public void onNext(View view) {
//                        layoutDataDisplay.addView(view);
//                        layoutIds.add(view.getId());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Timber.e("Failed to generate views in setupMetaAttrsDisplay reason %s", e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//        setupMetaAttrsEditable()
//                .subscribe(new DisposableObserver<View>() {
//                    @Override
//                    public void onNext(View view) {
//                        layoutDataEdit.addView(view);
//                        layoutIdsEditable.add(view.getId());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Timber.e("Failed to generate views in setupMetaAttrsEditable reason %s", e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//
//    }
//
//    private void removeInflatedViews() {
//        for (int id : layoutIdsEditable) {
//            layoutDataEdit.removeView(findViewById(id));
//        }
//
//        for (int id : layoutIds) {
//            layoutDataDisplay.removeView(findViewById(id));
//        }
//    }
//
//
//    private void showOrHideText(TextInputLayout inputLayout, String text) {
//        if (TextUtils.isEmpty(text)) {
//            inputLayout.setVisibility(View.GONE);
//        } else {
//            inputLayout.setVisibility(View.VISIBLE);
//            inputLayout.getEditText().setText(text);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (editMode) {
//            closeEditMode();
//            editMode = !editMode;
//        } else {
//            finish();
//            FragmentHostActivity.start(this, generateUpdatedSite());
//
//        }
//    }
//
//    private Observable<View> setupMetaAttrsDisplay() {
//        return Observable.create(new ObservableOnSubscribe<View>() {
//            @Override
//            public void subscribe(ObservableEmitter<View> emitter) throws Exception {
//                try {
//                    JSONObject answeredMetaAttrs = new JSONObject(loadedSite.getMetaAttrs());
//                    Iterator<String> iter = answeredMetaAttrs.keys();
//                    while (iter.hasNext()) {
//                        String key = iter.next();
//                        String value = (String) answeredMetaAttrs.get(key);
//                        View view = ViewGenerator.getTextInputLayout(getLayoutInflater(), key, value, false);
//                        emitter.onNext(view);
//                    }
//
//                } catch (Exception e) {
//                    emitter.onError(e);
//                } finally {
//                    emitter.onComplete();
//                }
//
//            }
//        });
//    }
//
//
//    private Observable<View> setupMetaAttrsEditable() {
//        return Observable.create(new ObservableOnSubscribe<View>() {
//            @Override
//            public void subscribe(ObservableEmitter<View> emitter) throws Exception {
//                try {
//                    JSONObject answeredMetaAttrs = new JSONObject(loadedSite.getMetaAttrs());
//                    Iterator<String> iter = answeredMetaAttrs.keys();
//                    while (iter.hasNext()) {
//                        String key = iter.next();
//                        String value = (String) answeredMetaAttrs.get(key);
//                        View view = ViewGenerator.getTextInputLayout(getLayoutInflater(), key, value, true);
//                        emitter.onNext(view);
//                    }
//
//                } catch (Exception e) {
//                    emitter.onError(e);
//                } finally {
//                    emitter.onComplete();
//                }
//
//            }
//        });
//    }
//
//
//    private void showImageOptionsDialog() {
//
//        btnAddPhoto.setError(null);
//
//        final CharSequence[] items = {"Take Photo", "Choose from Gallery",
//                "Dismiss"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(SiteDetailActivity.this, R.style.RiseUpDialog);
//        builder.setTitle("Add Photo");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int itemId) {
//                boolean hasRequiredPermission = PermissionUtility.checkPermission(SiteDetailActivity.this);
//                dialog.dismiss();
//
//
//                if (items[itemId].equals("Take Photo")) {
//                    userChoosenTask = "Take Photo";
//                    if (hasRequiredPermission)
//                        toImageCapture();
//                } else if (items[itemId].equals("Choose from Gallery")) {
//                    userChoosenTask = "Choose from Gallery";
//                    if (hasRequiredPermission)
//                        toImageGallery();
//                } else if (items[itemId].equals("Dismiss")) {
//
//                }
//
//
//            }
//        });
//        builder.show();
//    }
//
//
//    private void toImageGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
//
//    }
//
//    private void toImageCapture() {
//        PhotoToBeUploaded = FileUtils.generateImageFile("site");
//        ImageToBeUploaded = FileProvider.getUriForFile(this, getString(R.string.android_file_provider_fieldsight), PhotoToBeUploaded);
//
//        Intent toCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        toCamera.putExtra(MediaStore.EXTRA_OUTPUT, ImageToBeUploaded);
//        startActivityForResult(toCamera, REQUEST_CAMERA);
//    }
//
//    private void toGPSactivity() {
//
//        btnRecordGPS.setError(null);
//
//        Intent toGeoPointWidget = new Intent(this, GeoPointActivity.class);
//        startActivityForResult(toGeoPointWidget,
//                FormEntryActivity.LOCATION_CAPTURE);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//
//            if (requestCode == SELECT_FILE) {
//                try {
//                    onSelectFromGalleryResult(data);
//                    showImagePreview();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            } else if (requestCode == REQUEST_CAMERA) {
//
//                onCaptureImageResult();
//                showImagePreview();
//            }
//
//
//            if (requestCode == FormEntryActivity.LOCATION_CAPTURE) {
//                String locationdata = data.getStringExtra(FormEntryActivity.LOCATION_RESULT);
//                changeLocationBtnColor(locationdata);
//
//                String[] separated = locationdata.split(" ");
//
//                lat = separated[0];
//                lon = separated[1];
//            }
//        }
//
//    }
//
//    private void showImagePreview() {
//        ivSitePhoto.setVisibility(View.VISIBLE);
//        ivSitePhotoEditable.setVisibility(View.VISIBLE);
//
//
//        btnAddPhoto.setText("Change Photo");
//        btnAddPhoto.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight));
//
//
//    }
//
//    private void changeLocationBtnColor(String locationdata) {
//        btnRecordGPS.setText("Location Recorded");
//        btnRecordGPS.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight));
//    }
//
//    private void onCaptureImageResult() {
//
//
//        getContentResolver().notifyChange(ImageToBeUploaded, new ContentObserver(new Handler()) {
//            @Override
//            public boolean deliverSelfNotifications() {
//
//
//                Bitmap reducedSizeBitmap = getBitmap(ImageToBeUploaded);
//                if (reducedSizeBitmap != null) {
//                    ivSitePhotoEditable.setImageBitmap(reducedSizeBitmap);
//                    ivSitePhotoEditable.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                }
//
//
//                return super.deliverSelfNotifications();
//            }
//
//
//        });
//
//
//    }
//
//    private void onSelectFromGalleryResult(Intent data) throws
//            NullPointerException, IOException {
//
//        String filepath = ImageFileUtlis.getPath(this, data.getData());
//        File sourceOfPhotoTobeUploaded = FileUtils.getFileByPath(filepath);
//
//        PhotoToBeUploaded = FileUtils.generateImageFile("site");
//
//        FileUtils.copyFile(sourceOfPhotoTobeUploaded, PhotoToBeUploaded);
//
//        Bitmap bm = null;
//        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
//
//
//        ivSitePhotoEditable.setImageBitmap(bm);
//        ivSitePhotoEditable.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//    }
//
//    private Bitmap getBitmap(Uri uri) {
//
//        InputStream in = null;
//        try {
//            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
//            in = getContentResolver().openInputStream(uri);
//
//            // Decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(in, null, o);
//            in.close();
//
//
//            int scale = 0;
//            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
//                    IMAGE_MAX_SIZE) {
//                scale++;
//            }
//            Timber.d("scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);
//
//            Bitmap b = null;
//            in = getContentResolver().openInputStream(uri);
//            if (scale > 1) {
//                scale--;
//                // scale to max possible inSampleSize that still yields an image
//                // larger than target
//                o = new BitmapFactory.Options();
//                o.inSampleSize = scale;
//                b = BitmapFactory.decodeStream(in, null, o);
//
//                // resize to desired dimensions
//                int height = b.getHeight();
//                int width = b.getWidth();
//                Timber.d("1th scale operation dimenions - width: " + width + ", height: " + height);
//
//                double y = Math.sqrt(IMAGE_MAX_SIZE
//                        / (((double) width) / height));
//                double x = (y / height) * width;
//
//                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
//                        (int) y, true);
//                b.recycle();
//                b = scaledBitmap;
//
//                System.gc();
//            } else {
//                b = BitmapFactory.decodeStream(in);
//            }
//            in.close();
//
//            Timber.d("bitmap size - width: " + b.getWidth() + ", height: " +
//                    b.getHeight());
//            return b;
//        } catch (IOException e) {
//            Timber.e(e.getMessage(), e);
//            return null;
//        }
//    }
//
//
//}
