package org.bcss.collect.naxa.site;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.activities.GeoPointActivity;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.ImageFileUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.common.ViewUtils;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.site.data.SiteCluster;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.android.activities.FormEntryActivity.LOCATION_RESULT;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.ViewUtils.loadLocalImage;

public class CreateSiteActivity extends CollectAbstractActivity {

    @BindView(R.id.toolbar)
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

    private String latitude, longitude, accurary;
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


        int sucessColor = ContextCompat.getColor(CreateSiteActivity.this, R.color.colorGreenPrimaryLight);

        setupToolbar();
        setupViewModel();


        boolean isClusterIsEmpty = project.getSiteClusters() == null || project.getSiteClusters().isEmpty();
        if (!isClusterIsEmpty) {
            Type type = new TypeToken<ArrayList<SiteCluster>>() {
            }.getType();
            ArrayList<SiteCluster> siteClusters = new ArrayList<>(new Gson().fromJson(project.getSiteClusters(), type));
            createSiteViewModel.setSiteClusterMutableLiveData(siteClusters);
        }

        createSiteViewModel.setMetaAttributes(project.getSiteMetaAttributes());

        createSiteViewModel
                .getSiteClusterMutableLiveData()
                .observe(this, this::showSiteClusterSpinner);

        createSiteViewModel.getSiteTypesMutableLiveData()
                .observe(this, this::showSiteTypeSpinner);

        createSiteViewModel.getFormStatus().observe(this, createSiteFormStatus -> {
            if (createSiteFormStatus == null) return;
            switch (createSiteFormStatus) {
                case SUCCESS:
                    ToastUtils.showShortToastInMiddle("Offline Site Created");
                    break;
                case ERROR:
                    DialogFactory.createGenericErrorDialog(this, "").show();
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
                case LOCATION_RECORDED:
                    btnCollectSiteRecordLocation.setTextColor(sucessColor);
                    btnCollectSiteRecordLocation.setText(getString(R.string.msg_location_recorded, accurary));
                    break;
                case PHOTO_TAKEN:
                    btnCollectSiteAddPhoto.setTextColor(sucessColor);
                    btnCollectSiteAddPhoto.setText(getString(R.string.msg_photo_taken));
                    String path = createSiteViewModel.getSite().getValue().getLogo();
                    loadLocalImage(this, path).into(imageVideoThumb);
                    imageVideoThumb.setVisibility(View.VISIBLE);
                    break;
                case VALIDATED:
                    finish();
                    break;

            }
        });


        createSiteViewModel.getMetaAttributesMutableLiveData()
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
                                                linearLayoutForm.addView(view);

                                                createSiteViewModel.appendMetaAttributeViewIds(view.getId());


                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                e.printStackTrace();
                                                createSiteViewModel.getFormStatus().setValue(CreateSiteFormStatus.ERROR);
                                            }

                                            @Override
                                            public void onComplete() {
                                                setupSaveBtn();
                                            }
                                        });
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


        SiteTypeLocalSource.getInstance()
                .getByProjectId(project.getId())
                .observe(this, siteTypes -> {
                    createSiteViewModel.setSiteTypes(siteTypes);
                });


        watchText(tiSiteIdentifier);
        watchText(tiSiteName);
        watchText(tiSitePhone);
        watchText(tiSiteAddress);
        watchText(tiSitePublicDesc);

        createSiteViewModel.getProjectMutableLiveData().setValue(project);

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


    private void showSiteTypeSpinner(List<SiteType> siteTypes) {
        boolean show = siteTypes != null && !siteTypes.isEmpty();
        spinnerSiteType.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            SiteTypeSpinnerAdapter spinnerAdapter = new SiteTypeSpinnerAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item, getString(R.string.hint_choose_site_type), siteTypes);
            spinnerSiteType.setAdapter(spinnerAdapter);
            spinnerSiteType.setSelection(spinnerAdapter.getCount());
        }

    }

    private void showSiteClusterSpinner(ArrayList<SiteCluster> clusters) {
        boolean show = clusters != null && !clusters.isEmpty();
        spinnerSiteCluster.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            SiteClusterSpinnerAdapter spinnerAdapter = new SiteClusterSpinnerAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item, getString(R.string.hint_choose_site_cluster), clusters);
            spinnerSiteCluster.setAdapter(spinnerAdapter);
            spinnerSiteCluster.setSelection(spinnerAdapter.getCount());
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
                String mockedSiteId = String.valueOf(System.currentTimeMillis());
                createSiteViewModel.setId(mockedSiteId);
                collectMetaAtrributes(createSiteViewModel.getMetaAttributesViewIds().getValue());
                collectSpinnerOptions();

                createSiteViewModel.saveSite();
            }
        });

        linearLayoutForm.addView(view);

    }


    private void collectSpinnerOptions() {
        if (spinnerSiteCluster.getVisibility() == View.VISIBLE) {
            String selectedCluster = ((SiteCluster) spinnerSiteCluster.getSelectedItem()).getId();

        }

        if (spinnerSiteType.getVisibility() == View.VISIBLE) {
            SiteType siteType = (SiteType) spinnerSiteType.getSelectedItem();
            String siteTypeId = siteType.getId();
            String siteTypeLabel = siteType.getName();
            createSiteViewModel.setSiteType(siteTypeId, siteTypeLabel);
        }

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
                        createSiteViewModel.setMetaAttributesAnswer(serializedString);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
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

    public String getPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Timber.e("getPathFromURI Exception : %s ", e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case Constant.Key.RC_CAMERA:
                createSiteViewModel.setPhoto(photoToUpload.getAbsolutePath());
                break;
            case Constant.Key.SELECT_FILE:
                Uri uri = data.getData();
                String path = ImageFileUtils.getPath(this, uri);
                createSiteViewModel.setPhoto(path);

                break;
            case Constant.Key.GEOPOINT_RESULT_CODE:
                String location = data.getStringExtra(LOCATION_RESULT);
                String[] locationSplit = location.split(" ");
                latitude = locationSplit[0];
                longitude = locationSplit[1];
                accurary = locationSplit[3];
                createSiteViewModel.setLocation(latitude, longitude);
                break;
        }
    }

    @OnClick(R.id.btnCollectSiteRecordLocation)
    public void onBtnCollectSiteRecordLocationClicked() {
        Intent toGeoPointWidget = new Intent(this, GeoPointActivity.class);
        startActivityForResult(toGeoPointWidget, Constant.Key.GEOPOINT_RESULT_CODE);

    }


}


