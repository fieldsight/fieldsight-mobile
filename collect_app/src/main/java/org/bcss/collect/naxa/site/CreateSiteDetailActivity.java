package org.bcss.collect.naxa.site;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.activities.GeoPointActivity;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.listeners.PermissionListener;
import org.bcss.collect.android.utilities.PermissionUtils;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.common.GlideApp;
import org.bcss.collect.naxa.common.ImageFileUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.common.ViewUtils;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.project.MapActivity;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.android.activities.FormEntryActivity.LOCATION_RESULT;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_EDITED;

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
    private ProgressDialog dialog;

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


        setupViewModel();

        try {
            site = getIntent().getExtras().getParcelable(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            Timber.e("Can't start activity without site extra_object");
            ToastUtils.showLongToast(getString(R.string.msg_failed_to_load));
            finish();
        }

        setupToolbar();

        createSiteDetailViewModel.setSiteMutableLiveData(site);

        ProjectLocalSource.getInstance().getProjectById(site.getProject())
                .observe(this, project -> {
                    boolean isClusterIsEmpty;
                    if (project != null) {
                        isClusterIsEmpty = project.getSiteClusters() == null || project.getSiteClusters().isEmpty();
                        if (!isClusterIsEmpty) {
                            Type type = new TypeToken<ArrayList<SiteRegion>>() {
                            }.getType();
                            ArrayList<SiteRegion> siteRegions = new ArrayList<>(new Gson().fromJson(project.getSiteClusters(), type));
                            createSiteDetailViewModel.setSiteCluster(siteRegions);
                        }
                    }
                });


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
                .observe(this, new Observer<Site>() {
                    @Override
                    public void onChanged(@Nullable Site nSite) {
                        if (nSite == null) return;


                        ilSiteIdentifier.getEditText().setText(nSite.getIdentifier());
                        ilSiteName.getEditText().setText(nSite.getName());
                        ilPhone.getEditText().setText(nSite.getPhone());
                        ilAddress.getEditText().setText(nSite.getAddress());
                        ilSiteType.getEditText().setText(nSite.getTypeLabel());

                        int sucessColor = ContextCompat.getColor(CreateSiteDetailActivity.this, R.color.colorGreenPrimaryLight);

                        if (nSite.getLogo() != null && nSite.getLogo().length() > 0) {
                            File photoFile = new File(nSite.getLogo());
                            if (photoFile.exists()) {
                                GlideApp.with(CreateSiteDetailActivity.this)
                                        .load(new File(nSite.getLogo()))
                                        .into(ivSitePhoto);
                                btnSiteEditAddPhoto.setTextColor(sucessColor);
                                btnSiteEditAddPhoto.setText(getString(R.string.msg_photo_taken));
                            }
                        }
                    }
                });

        createSiteDetailViewModel
                .getSiteTypesMutableLiveData()
                .observe(this, new Observer<List<SiteType>>() {
                    @Override
                    public void onChanged(@Nullable List<SiteType> siteTypes) {
                        boolean show = siteTypes != null && !siteTypes.isEmpty();
                        spinnerSiteType.setVisibility(show ? View.VISIBLE : View.GONE);
                        ilSiteType.setVisibility(show ? View.VISIBLE : View.GONE);
                        if (show) {
                            SiteTypeSpinnerAdapter spinnerAdapter = new SiteTypeSpinnerAdapter(CreateSiteDetailActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, getString(R.string.hint_choose_site_type), siteTypes);
                            spinnerSiteType.setAdapter(spinnerAdapter);
                            spinnerSiteType.setSelection(spinnerAdapter.getCount());
                        }
                    }
                });

        createSiteDetailViewModel
                .getSiteClusterMutableLiveData()
                .observe(this, new Observer<ArrayList<SiteRegion>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<SiteRegion> clusters) {
                        boolean show = clusters != null && !clusters.isEmpty();
                        spinnerSiteCluster.setVisibility(show ? View.VISIBLE : View.GONE);
                        if (show) {
                            SiteClusterSpinnerAdapter spinnerAdapter = new SiteClusterSpinnerAdapter(CreateSiteDetailActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, getString(R.string.hint_choose_site_region), clusters);
                            spinnerSiteCluster.setAdapter(spinnerAdapter);
                            spinnerSiteCluster.setSelection(spinnerAdapter.getCount());

                            for (int pos = 0; pos < clusters.size(); pos++) {
                                SiteRegion siteRegion = clusters.get(pos);
                                if (siteRegion.getId().equals(site.getRegionId())) {
                                    ilRegion.getEditText().setText(siteRegion.getName());
                                    spinnerSiteCluster.setSelection(pos);
                                    break;
                                }
                            }
                        }
                    }
                });

        createSiteDetailViewModel
                .getFormStatus()
                .observe(this, new Observer<CreateSiteDetailFormStatus>() {
                    @Override
                    public void onChanged(@Nullable CreateSiteDetailFormStatus createSiteDetailFormStatus) {
                        if (createSiteDetailFormStatus == null) return;
                        switch (createSiteDetailFormStatus) {
                            case EMPTY_SITE_IDENTIFIER:

                                ilSiteIdentifierEditable.setError(getString(R.string.error_field_required));
                                ilSiteIdentifierEditable.requestFocus();
                                break;
                            case EMPTY_SITE_NAME:
                                ilSiteNameEditable.setError(getString(R.string.error_field_required));
                                ilSiteNameEditable.requestFocus();
                                break;
                            case EMPTY_ADDRESS:
                                ilAddressEditable.setError(getString(R.string.error_field_required));
                                ilAddressEditable.requestFocus();
                                break;
                            case EMPTY_PHONE:
                                ilPhoneEditable.setError(getString(R.string.error_field_required));
                                ilPhoneEditable.requestFocus();
                                break;
                            case SITE_SAVED:
                                onBackPressed();
                                ToastUtils.showLongToast("Changes Saved");
                                break;
                            case SITE_SAVED_FAILED:
                                onBackPressed();
                                break;

                        }
                    }
                });

        createSiteDetailViewModel.getMetaAttributes()
                .observe(this,
                        metaAttributes -> {
                            if (metaAttributes != null) {
                                Observable.just(metaAttributes)
                                        .flatMapIterable((Function<List<SiteMetaAttribute>, Iterable<SiteMetaAttribute>>) metaAttributes1 -> metaAttributes1)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new DisposableObserver<SiteMetaAttribute>() {
                                            @Override
                                            public void onNext(SiteMetaAttribute metaAttribute) {
                                                String question = metaAttribute.getQuestionText();
                                                String submissionTag = metaAttribute.getQuestionName();
                                                String questionType = metaAttribute.getQuestionType();//todo: introduce different widget using type

                                                View view = getTextInputLayout(question, submissionTag, questionType);
                                                layoutSiteDataEdit.addView(view);

                                                createSiteDetailViewModel.appendMetaAttributeViewIds(view.getId());
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                e.printStackTrace();
//                                                createSiteViewModel.getFormStatus().setValue(CreateSiteFormStatus.ERROR);
                                            }

                                            @Override
                                            public void onComplete() {
                                            }
                                        });
                            }
                        });

        SiteTypeLocalSource.getInstance()
                .getByProjectId(site.getProject())
                .observe(this, siteTypes -> {
                    createSiteDetailViewModel.setSiteTypes(siteTypes);
                });

        watchText(ilSiteIdentifierEditable);
        watchText(ilSiteNameEditable);
        watchText(ilPhoneEditable);
        watchText(ilAddressEditable);


        Observable.just(site.getMetaAttributes())
                .subscribeOn(Schedulers.computation())
                .map(new Function<String, View>() {
                    @Override
                    public View apply(String s) throws Exception {
                        return null;
                    }
                })
                .subscribe(new DisposableObserver<View>() {
                    @Override
                    public void onNext(View view) {
                        layoutSiteDataDisplay.addView(view);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void showDialog() {
        dialog = DialogFactory.createProgressDialogHorizontal(this, "Uploading Changes");
        dialog.show();
    }

    private void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }

    public void saveSiteObservable() {
//        SiteLocalSource.getInstance()
//                .getAllByStatus(IS_EDITED)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMapObservable(new Function<Site, ObservableSource<Site>>() {
//                    @Override
//                    public ObservableSource<Site> apply(Site site) throws Exception {
//                        return SiteRemoteSource.getInstance().updateSite(site).subscribeOn(Schedulers.io());
//                    }
//                })
//                .toList()
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        showDialog();
//                    }
//                })
//                .subscribe(new DisposableSingleObserver<List<Site>>() {
//                    @Override
//                    public void onSuccess(List<Site> sites) {
//                        String title = "Site Uploaded";
//                        String msg;
//                        if (sites.size() > 1) {
//                            msg = Collect.getInstance().getString(R.string.msg_multiple_sites_upload, sites.get(0).getName(), sites.size()); //todo: never use context here but .. need to finish feature fast..someone refacotr this later
//                        } else {
//                            msg = Collect.getInstance().getString(R.string.msg_single_site_upload);
//                        }
//
//                        FieldSightNotificationUtils.getINSTANCE().notifyHeadsUp(title,msg);
//
//                        hideDialog();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        Timber.e(e);
//                        String title = "Site upload failed";
//                        String msg;
//                        FieldSightNotificationUtils.getINSTANCE().notifyHeadsUp(title, e.getMessage());
//                        hideDialog();
//                    }
//                });
    }


    private void setupToolbar() {
        toolbarGeneral.setTitle(site.getName());
        setSupportActionBar(toolbarGeneral);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        createSiteDetailViewModel = ViewModelProviders.of(this, factory).get(CreateSiteDetailViewModel.class);
    }

    @OnClick({R.id.btn_site_edit_add_photo, R.id.btn_site_records_location, R.id.fab_activate_edit_mode, R.id.btn_view_site_on_map})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_site_edit_add_photo:
                checkPermissionAndShowDialog();
                break;
            case R.id.btn_site_records_location:
                Intent toGeoPointWidget = new Intent(this, GeoPointActivity.class);
                startActivityForResult(toGeoPointWidget, Constant.Key.GEOPOINT_RESULT_CODE);
                break;
            case R.id.fab_activate_edit_mode:
                fabActivateEditMode.setImageResource(layoutSiteDataEdit.getVisibility() == View.GONE ? R.drawable.ic_check : R.drawable.ic_edit);

                switch (layoutSiteDataEdit.getVisibility()) {
                    case View.GONE:
                        createSiteDetailViewModel.setEditSite(true);
                        break;
                    case View.VISIBLE:
//                        collectMetaAtrributes(createSiteDetailViewModel.getMetaAttributesViewIds().getValue());
//                        collectSpinnerOptions();
                        DialogFactory.createActionDialog(CreateSiteDetailActivity.this, "Save Changes?", "")
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        createSiteDetailViewModel.saveSite();
                                    }
                                })
                                .setNegativeButton(R.string.dialog_action_dismiss, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onBackPressed();
                                    }
                                })
                                .show();

                        break;
                }
                break;
            case R.id.btn_view_site_on_map:
                MapActivity.start(this, nSite);
                break;
        }
    }

    public void checkPermissionAndShowDialog() {
        if (PermissionUtils.checkIfCameraPermissionGranted(this)) {
            showImageDialog();
        } else {
            PermissionUtils.requestCameraPermission(this, new PermissionListener() {
                @Override
                public void granted() {
                    showImageDialog();
                }

                @Override
                public void denied() {

                }
            });
        }
    }

    private void showImageDialog() {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Dismiss"};
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
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        switch (layoutSiteDataEdit.getVisibility()) {
            case View.GONE:
                super.onBackPressed();
                break;
            case View.VISIBLE:
                createSiteDetailViewModel.setEditSite(false);
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void collectMetaAtrributes(ArrayList<Integer> ids) {

        JSONObject jsonObject = new JSONObject();
        Observable.just(ids)
                .flatMapIterable((Function<ArrayList<Integer>, Iterable<Integer>>) viewIds -> viewIds)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer layoutId) throws Exception {
                        return findViewById(layoutId) instanceof TextInputLayout;
                    }
                })
                .map(new Function<Integer, JSONObject>() {
                    @Override
                    public JSONObject apply(Integer layoutId) throws Exception {
                        TextInputLayout textInput = findViewById(layoutId);
                        String answer = textInput.getEditText().getText().toString().trim();
                        String submissionKey = (String) textInput.getTag();
                        return jsonObject.put(submissionKey, answer);
                    }
                })
                .toList()
                .subscribe(new DisposableSingleObserver<List<JSONObject>>() {
                    @Override
                    public void onSuccess(List<JSONObject> jsonObjects) {
                        String serializedString = jsonObject.toString();
                        createSiteDetailViewModel.setMetaAttributesAnswer(serializedString);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void collectSpinnerOptions() {
        if (spinnerSiteCluster.getVisibility() == View.VISIBLE) {
            String selectedCluster = ((SiteRegion) spinnerSiteCluster.getSelectedItem()).getId();
        }

        if (spinnerSiteType.getVisibility() == View.VISIBLE) {
            SiteType siteType = (SiteType) spinnerSiteType.getSelectedItem();
            String siteTypeId = siteType.getId();
            String siteTypeLabel = siteType.getName();
            createSiteDetailViewModel.setSiteType(siteTypeId, siteTypeLabel);
        }

    }

    public View getTextInputLayout(String question, String tag, String type) {

        View view = getLayoutInflater().inflate(R.layout.layout_text_input, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        view.setLayoutParams(lp);


        TextInputLayout textInputLayout = ((TextInputLayout) view);

        textInputLayout.setHint(question);
        textInputLayout.getEditText().setInputType(typeToInputType(type));
        textInputLayout.setTag(tag);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setId(ViewUtils.generateViewId());
        } else {
            view.setId(View.generateViewId());
        }


        return view;
    }


    private int typeToInputType(String type) {
        switch (type) {
            case "Number":
                return InputType.TYPE_CLASS_NUMBER;

            default:
                return InputType.TYPE_CLASS_TEXT;
        }
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
