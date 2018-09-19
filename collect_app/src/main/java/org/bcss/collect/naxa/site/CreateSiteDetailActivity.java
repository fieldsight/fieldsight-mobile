package org.bcss.collect.naxa.site;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.activities.GeoPointActivity;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.ImageFileUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.login.model.Site;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static org.bcss.collect.android.activities.FormEntryActivity.LOCATION_RESULT;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class CreateSiteDetailActivity extends CollectAbstractActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbarGeneral;

    @BindView(R.id.appbar_flexible)
    AppBarLayout appbarGeneral;
    @BindView(R.id.il_site_identifier)
    TextInputLayout ilSiteIdentifier;
    @BindView(R.id.il_site_name)
    TextInputLayout ilSiteName;
    @BindView(R.id.il_phone)
    TextInputLayout ilPhone;
    @BindView(R.id.il_address)
    TextInputLayout ilAddress;
    @BindView(R.id.il_public_desc)
    TextInputLayout ilPublicDesc;
    @BindView(R.id.il_region)
    TextInputLayout ilRegion;
    @BindView(R.id.il_site_type)
    TextInputLayout ilSiteType;
    @BindView(R.id.btn_view_site_on_map)
    Button btnViewSiteOnMap;
    @BindView(R.id.iv_site_photo)
    ImageView ivSitePhoto;
    @BindView(R.id.il_site_identifier_editable)
    TextInputLayout ilSiteIdentifierEditable;
    @BindView(R.id.il_site_name_editable)
    TextInputLayout ilSiteNameEditable;
    @BindView(R.id.il_phone_editable)
    TextInputLayout ilPhoneEditable;
    @BindView(R.id.il_address_editable)
    TextInputLayout ilAddressEditable;
    @BindView(R.id.il_public_desc_editable)
    TextInputLayout ilPublicDescEditable;
    @BindView(R.id.spinnerSiteCluster)
    Spinner spinnerSiteCluster;
    @BindView(R.id.spinner_site_type)
    Spinner spinnerSiteType;
    @BindView(R.id.btn_site_edit_add_photo)
    Button btnSiteEditAddPhoto;
    @BindView(R.id.iv_site_photo_editable)
    ImageView ivSitePhotoEditable;
    @BindView(R.id.btn_site_records_location)
    Button btnSiteRecordsLocation;
    @BindView(R.id.btnCollectSiteSendForm)
    Button btnCollectSiteSendForm;
    @BindView(R.id.card_site_edit_form)
    CardView cardSiteEditForm;
    @BindView(R.id.fab_activate_edit_mode)
    FloatingActionButton fabActivateEditMode;
    @BindView(R.id.layout_site_detail)
    CoordinatorLayout layoutSiteDetail;
    @BindView(R.id.layout_site_data_display)
    LinearLayout layoutSiteDataDisplay;
    @BindView(R.id.layout_site_data_edit)
    LinearLayout layoutSiteDataEdit;

    private CreateSiteDetailViewModel createSiteDetailViewModel;
    private Site site, nSite;
    private File photoToUpload;

    private String latitude, longitude, accurary;
    private Uri phototoUploadUri;

    public static void start(Context context, @NonNull Site site) {
        Intent intent = new Intent(context, CreateSiteDetailActivity.class);
        intent.putExtra(EXTRA_OBJECT, site);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail_change);
        ButterKnife.bind(this);

        setupToolbar();
        setupViewModel();
//        setupSaveBtn();

        try {
            site = getIntent().getExtras().getParcelable(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            Timber.e("Can't start activity without site extra_object");
            ToastUtils.showLongToast(getString(R.string.msg_failed_to_load));
            finish();
        }

        createSiteDetailViewModel.setSiteMutableLiveData(site);

        createSiteDetailViewModel
                .getEditSite()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean aBoolean) {
                        if (aBoolean) {
                            layoutSiteDataDisplay.setVisibility(View.GONE);
                            layoutSiteDataEdit.setVisibility(View.VISIBLE);

                            ilSiteIdentifierEditable.getEditText().setText(nSite.getIdentifier());
                            ilSiteNameEditable.getEditText().setText(nSite.getName());
                            ilPhoneEditable.getEditText().setText(nSite.getPhone());
                            ilAddressEditable.getEditText().setText(nSite.getAddress());
                        } else {
                            layoutSiteDataEdit.setVisibility(View.GONE);
                            layoutSiteDataDisplay.setVisibility(View.VISIBLE);
                        }
                    }
                });

        createSiteDetailViewModel
                .getSiteRepository()
                .getSiteById(site.getId())
                .observe(this, new Observer<List<Site>>() {
                    @Override
                    public void onChanged(@Nullable List<Site> sites) {
                        nSite = sites.get(0);
                        ilSiteIdentifier.getEditText().setText(nSite.getIdentifier());
                        ilSiteName.getEditText().setText(nSite.getName());
                        ilPhone.getEditText().setText(nSite.getPhone());
                        ilAddress.getEditText().setText(nSite.getAddress());
                        ilRegion.getEditText().setText(nSite.getRegion());
                        ilSiteType.getEditText().setText(nSite.getTypeId());

                    }
                });

        watchText(ilSiteIdentifierEditable);
        watchText(ilSiteNameEditable);
        watchText(ilPhoneEditable);
        watchText(ilAddressEditable);

    }

    private void setupToolbar() {
        toolbarGeneral.setTitle(R.string.toolbar_title_offline_site_edit);
        setSupportActionBar(toolbarGeneral);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        createSiteDetailViewModel = ViewModelProviders.of(this, factory).get(CreateSiteDetailViewModel.class);
    }

    @OnClick({R.id.btn_view_site_on_map, R.id.btn_site_edit_add_photo, R.id.btn_site_records_location, R.id.btnCollectSiteSendForm, R.id.fab_activate_edit_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_view_site_on_map:
                break;
            case R.id.btn_site_edit_add_photo:
                final CharSequence[] items = {"Take Photo", "Choose siteName Gallery", "Dismiss"};
                DialogFactory.createListActionDialog(this, "Add photo", items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            photoToUpload = createSiteDetailViewModel.generateImageFile("site");
                            phototoUploadUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoToUpload);
                            Intent toCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            toCamera.putExtra(MediaStore.EXTRA_OUTPUT, phototoUploadUri);
                            startActivityForResult(toCamera, Constant.Key.RC_CAMERA);
                            break;
                        case 1:
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select site image"), Constant.Key.SELECT_FILE);
                            break;
                        default:
                            break;
                    }
                }).show();
                break;
            case R.id.btn_site_records_location:
                Intent toGeoPointWidget = new Intent(this, GeoPointActivity.class);
                startActivityForResult(toGeoPointWidget, Constant.Key.GEOPOINT_RESULT_CODE);
                break;
            case R.id.fab_activate_edit_mode:
                switch (layoutSiteDataEdit.getVisibility()) {
                    case View.GONE:
                        createSiteDetailViewModel.setEditSite(true);
                        break;
                    case View.VISIBLE:
                        createSiteDetailViewModel.saveSite();
                        createSiteDetailViewModel.setEditSite(false);
                        break;
                }
                break;
            case R.id.btnCollectSiteSendForm:
                break;
        }
    }

    private void setupSaveBtn() {
        View view = getLayoutInflater().inflate(R.layout.btn_save, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        view.setLayoutParams(lp);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSiteDetailViewModel.setEditSite(false);
            }
        });
        layoutSiteDataEdit.addView(view);
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
                        if (textInputLayout.getError() != null)
                            textInputLayout.setError(null);

                        switch (textInputLayout.getId()) {
                            case R.id.il_site_identifier_editable:
                                createSiteDetailViewModel.setIdentifier(text);
                                break;
                            case R.id.il_site_name_editable:
                                createSiteDetailViewModel.setSiteName(text);
                                break;
                            case R.id.il_phone_editable:
                                createSiteDetailViewModel.setSitePhone(text);
                                break;
                            case R.id.il_address_editable:
                                createSiteDetailViewModel.setSiteAddress(text);
                                break;
//                            case R.id.collect_site_ti_public_desc:
//                                createSiteDetailViewModel.setSitePublicDesc(text);
//                                break;

                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case Constant.Key.RC_CAMERA:
                createSiteDetailViewModel.setPhoto(photoToUpload.getAbsolutePath());
                break;
            case Constant.Key.SELECT_FILE:
                Uri uri = data.getData();
                String path = ImageFileUtils.getPath(this, uri);
                createSiteDetailViewModel.setPhoto(path);
                break;
            case Constant.Key.GEOPOINT_RESULT_CODE:
                String location = data.getStringExtra(LOCATION_RESULT);
                String[] locationSplit = location.split(" ");
                latitude = locationSplit[0];
                longitude = locationSplit[1];
                accurary = locationSplit[3];
                createSiteDetailViewModel.setLocation(latitude, longitude);
                break;
        }
    }
}
