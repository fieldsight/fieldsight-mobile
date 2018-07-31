package org.odk.collect.naxa.site;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.odk.collect.android.BuildConfig;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.activities.GeoPointActivity;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.DialogFactory;
import org.odk.collect.naxa.common.ImageUtils;
import org.odk.collect.naxa.common.ViewModelFactory;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static org.odk.collect.android.activities.FormEntryActivity.LOCATION_RESULT;
import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;

public class CreateSiteActivity extends CollectAbstractActivity {

    @BindView(R.id.toolbar_general)
    Toolbar toolbarGeneral;
    @BindView(R.id.tv_toolbar_message)
    TextView tvToolbarMessage;
    @BindView(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @BindView(R.id.appbar_general)
    AppBarLayout appbarGeneral;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.ILSiteIdentifier)
    TextInputLayout tiSiteIdentifier;
    @BindView(R.id.ILSiteName)
    TextInputLayout tiSiteName;
    @BindView(R.id.collect_site_ti_phone)
    TextInputLayout tiSitePhone;
    @BindView(R.id.collect_site_ti_address)
    TextInputLayout tiSiteAddress;
    @BindView(R.id.collect_site_ti_public_desc)
    TextInputLayout tiSitePublicDesc;
    @BindView(R.id.spinnerSiteType)
    Spinner spinnerSiteType;
    @BindView(R.id.spinnerSiteCluster)
    Spinner spinnerSiteCluster;
    @BindView(R.id.imageVideoThumb)
    ImageView imageVideoThumb;
    @BindView(R.id.btnCollectSiteAddPhoto)
    Button btnCollectSiteAddPhoto;
    @BindView(R.id.btnCollectSiteRecordLocation)
    Button btnCollectSiteRecordLocation;
    @BindView(R.id.linear_layout_form)
    LinearLayout linearLayoutForm;

    private CreateSiteViewModel createSiteViewModel;

    private Project project;
    private File photoToUpload;

    private String latitude, longitude;
    private Uri phototoUploadUri;

    public static void start(Context context, @NonNull Project project) {
        Intent intent = new Intent(context, CreateSiteActivity.class);
        intent.putExtra(EXTRA_OBJECT, project);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_create);
        ButterKnife.bind(this);

        try {
            project = getIntent().getExtras().getParcelable(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            Timber.e("Can't start activity without project extra_object");
            ToastUtils.showLongToast(getString(R.string.msg_failed_to_load));
            finish();
        }


        setupToolbar();
        setupViewModel();
        setupSaveBtn();

        createSiteViewModel.getFormStatus().observe(this, new Observer<CreateSiteFormStatus>() {
            @Override
            public void onChanged(@Nullable CreateSiteFormStatus createSiteFormStatus) {
                if (createSiteFormStatus == null) return;
                switch (createSiteFormStatus) {
                    case SUCCESS:
                        ToastUtils.showShortToastInMiddle("Offline Site Created");
                        break;
                    case ERROR:
                        break;
                    case EMPTY_SITE_NAME:
                        tiSiteName.setError(getString(R.string.error_field_required));
                        btnCollectSiteRecordLocation.requestFocus();
                        break;
                    case EMPTY_SITE_LOCATION:
                        btnCollectSiteRecordLocation.setError(getString(R.string.error_field_required));
                        btnCollectSiteRecordLocation.requestFocus();
                        break;
                    case EMPTY_SITE_IDENTIFIER:
                        tiSiteIdentifier.setError(getString(R.string.error_field_required));
                        tiSiteIdentifier.requestFocus();
                        break;

                }
            }
        });

        createSiteViewModel.getState().observe(this, new Observer<FormState>() {
            @Override
            public void onChanged(@Nullable FormState formState) {
                if (formState == null) return;
                handleViewState(formState);
            }
        });

        createSiteViewModel.getSite()
                .observe(this, new Observer<Site>() {
                    @Override
                    public void onChanged(@Nullable Site site) {
                        if (site != null) {
                        }
                    }
                });

        createSiteViewModel.getProjectMutableLiveData().setValue(project);

        watchText(tiSiteIdentifier);
        watchText(tiSiteName);
        watchText(tiSitePhone);
        watchText(tiSiteAddress);
        watchText(tiSitePublicDesc);
    }


    public void watchText(TextInputLayout textInputLayout) {


        textInputLayout
                .getEditText()
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        String text = s.toString();
                        if (textInputLayout.getError() != null) textInputLayout.setError(null);

                        switch (textInputLayout.getId()) {
                            case R.id.ILSiteIdentifier:
                                createSiteViewModel.setIdentifier(text);
                                break;
                            case R.id.ILSiteName:
                                createSiteViewModel.setSiteName(text);

                                break;
                            case R.id.collect_site_ti_phone:
                                createSiteViewModel.setSitePhone(text);

                                break;
                            case R.id.collect_site_ti_address:
                                createSiteViewModel.setSiteAddress(text);

                                break;
                            case R.id.collect_site_ti_public_desc:
                                createSiteViewModel.setSitePublicDesc(text);

                                break;

                        }
                    }
                });
    }


    private void handleViewState(FormState formState) {
        if (formState.isProgressIndicatorShown()) {
            showProgress();
        } else if (formState.isSiteClusterShown()) {
            showSiteClusterSpinner();
        } else if (formState.isSiteTypeShown()) {
            showSiteTypeSpinner();
        }
    }

    private void showSiteTypeSpinner() {
    }

    private void showSiteClusterSpinner() {

    }

    private void showProgress() {

    }

    private void setupSaveBtn() {
        View view = getLayoutInflater().inflate(R.layout.btn_save, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        view.setLayoutParams(lp);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mockedSiteId = String.valueOf(System.currentTimeMillis());
                createSiteViewModel.setId(mockedSiteId);
                createSiteViewModel.saveSite();
                finish();
            }
        });

        linearLayoutForm.addView(view);

    }

    private void setupViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        createSiteViewModel = ViewModelProviders.of(this, factory).get(CreateSiteViewModel.class);
    }

    private void setupToolbar() {
        toolbarGeneral.setTitle(R.string.toolbar_title_offline_site);
        setSupportActionBar(toolbarGeneral);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.btnCollectSiteAddPhoto)
    public void showImageDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Dismiss"};
        DialogFactory.createListActionDialog(this, "Add photo", items, (dialog, which) -> {
            switch (which) {
                case 0:
                    photoToUpload = createSiteViewModel.generateImageFile("site");
                    phototoUploadUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), photoToUpload);


                    Intent toCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    toCamera.putExtra(MediaStore.EXTRA_OUTPUT, phototoUploadUri);
                    startActivityForResult(toCamera, Constant.Key.RC_CAMERA);
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select site image"), Constant.Key.SELECT_FILE);
                    break;
                default:
                    break;
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case Constant.Key.RC_CAMERA:
                createSiteViewModel.setPhoto(photoToUpload.getAbsolutePath());
                Glide.with(this).load(photoToUpload.getAbsoluteFile()).into(imageVideoThumb);
                imageVideoThumb.setVisibility(View.VISIBLE);

                break;
            case Constant.Key.RC_STORAGE:
                createSiteViewModel.setPhoto(photoToUpload.getAbsolutePath());
                break;
            case Constant.Key.GEOPOINT_RESULT_CODE:
                String location = data.getStringExtra(LOCATION_RESULT);
                String[] locationSplit = location.split(" ");
                latitude = locationSplit[0];
                longitude = locationSplit[1];
                createSiteViewModel.setLocation(latitude, longitude);
                break;
        }
    }

    @OnClick(R.id.btnCollectSiteRecordLocation)
    public void onBtnCollectSiteRecordLocationClicked() {
        Intent toGeoPointWidget = new Intent(this, GeoPointActivity.class);
        startActivityForResult(toGeoPointWidget, Constant.Key.LOCATION_CAPTURE);

    }


}


