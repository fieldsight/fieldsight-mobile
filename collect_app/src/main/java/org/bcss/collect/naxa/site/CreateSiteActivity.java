package org.bcss.collect.naxa.site;

import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
import org.bcss.collect.naxa.common.ImageFileUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.common.ViewUtils;
import org.bcss.collect.naxa.login.model.McqOption;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.android.activities.FormEntryActivity.LOCATION_RESULT;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.Constant.MetaAttrsType.NUMBER;
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
    private Site loadedSite;
    private boolean isUpdate = false;


    public static void start(Context context, @NonNull Project project, @Nullable Site site) {
        Intent intent = new Intent(context, CreateSiteActivity.class);
        intent.putExtra(EXTRA_OBJECT, project);
        if (site != null) {
            intent.putExtra("site", site);
        }
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


        setupViewModel();
        setupToolbar();

        loadedSite = getIntent().getExtras().getParcelable("site");
        if (loadedSite != null) {
            loadFormWithValuesSet(loadedSite);
            isUpdate = true;
        }

        boolean isClusterIsEmpty = project.getSiteClusters() == null || project.getSiteClusters().isEmpty();
        if (!isClusterIsEmpty) {
            Type type = new TypeToken<ArrayList<SiteRegion>>() {
            }.getType();
            ArrayList<SiteRegion> siteRegions = new ArrayList<>(new Gson().fromJson(project.getSiteClusters(), type));
            createSiteViewModel.setSiteClusterMutableLiveData(siteRegions);
        }

        createSiteViewModel.setMetaAttributes(project.getSiteMetaAttributes());


        createSiteViewModel.getSiteClusterMutableLiveData().observe(this, siteRegions -> {
            showSiteClusterSpinner(siteRegions);

//            if (loadedSite != null && siteRegions != null) {
//                int selectedItemIndex = 0;
//                if (loadedSite.getRegion() != null) {
//                    for (int i = 0; i <= siteRegions.size(); i++) {
//                        if (loadedSite.getRegion().equals(siteRegions.get(i).getId())) {
//                            selectedItemIndex = Integer.parseInt(siteRegions.get(i).getId());
//                            break;
//                        }
//                    }
//                }
//
//                spinnerSiteCluster.setSelection(selectedItemIndex);
//            }
        });


        createSiteViewModel.getSiteTypesMutableLiveData()
                .observe(this, this::showSiteTypeSpinner);

        createSiteViewModel
                .getFormStatus()
                .observe(this, createSiteFormStatus -> {
                    if (createSiteFormStatus == null) return;
                    switch (createSiteFormStatus) {
                        case SUCCESS:
                            ToastUtils.showShortToastInMiddle("Offline Site Created");
                            finish();
                            break;
                        case ERROR:
                            DialogFactory.createGenericErrorDialog(this, "").show();
                            break;
                        case EMPTY_SITE_NAME:
                            btnCollectSiteRecordLocation.requestFocus();
                            tiSiteName.setError(getString(R.string.error_field_required));

                            break;
                        case EMPTY_SITE_LOCATION:
                            btnCollectSiteRecordLocation.requestFocus();
                            btnCollectSiteRecordLocation.setError(getString(R.string.error_field_required));

                            break;
                        case EMPTY_SITE_IDENTIFIER:
                            tiSiteIdentifier.requestFocus();
                            tiSiteIdentifier.setError(getString(R.string.error_field_required));

                            break;
                        case REGION_NOT_SELECTED:
                            TextView errorText = (TextView) spinnerSiteCluster.getSelectedView();
                            errorText.setError("");
                            errorText.setTextColor(Color.RED);
                            String message = getString(R.string.error_selection_required);
                            errorText.setText(String.format(message, "region"));

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
                                                String questionType = metaAttribute.getQuestionType();
                                                String answer = getAnswerFormMetaAttrs(submissionTag);
                                                View view = null;
                                                switch (questionType) {
                                                    case Constant.MetaAttrsType.TEXT:
                                                    case NUMBER:
                                                        view = getTextInputLayout(question, answer, submissionTag, questionType);
                                                        break;
                                                    case Constant.MetaAttrsType.DATE:
                                                        view = getDateLayout(question, answer, submissionTag);
                                                        break;
                                                    case Constant.MetaAttrsType.MCQ:
                                                        view = getMCQLayout(question, answer, submissionTag, metaAttribute.getMcqOptions());
                                                        break;
                                                }

                                                if (view != null) {
                                                    linearLayoutForm.addView(view);
                                                    createSiteViewModel.appendMetaAttributeViewIds(view.getId());
                                                }


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


    private void loadFormWithValuesSet(Site site) {
        toolbarGeneral.setTitle("Update Site");


        setText(tiSiteIdentifier, site.getIdentifier());
        setText(tiSiteName, site.getName());
        setText(tiSitePhone, site.getPhone());
        setText(tiSiteAddress, site.getAddress());
        setText(tiSitePublicDesc, site.getPublicDesc());

        if (site.getLatitude() != null) {
            createSiteViewModel.setLocation(site.getLatitude(), site.getLongitude());
        }

        new Handler().postDelayed(() -> {
            if (site.getLogo() != null) {
                createSiteViewModel.setPhoto(site.getLogo());
            }

        }, TimeUnit.SECONDS.toMillis(2));

        createSiteViewModel.setFormDeployedFrom(site.getStagedFormDeployedFrom(),site.getGeneralFormDeployedFrom(),site.getScheduleFormDeployedForm());
    }

    private void setText(TextInputLayout textInputLayout, String text) {
        if (text == null || text.trim().length() == 0) return;
        textInputLayout.getEditText().setText(text);
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


    private View getDateLayout(String question, String answer, String submissionTag) {
        View view = getLayoutInflater().inflate(R.layout.layout_text_input, null);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        view.setLayoutParams(lp);


        TextInputLayout textInputLayout = ((TextInputLayout) view);
        textInputLayout.setHint(question);
        textInputLayout.setTag(submissionTag);
        textInputLayout.getEditText().setFocusable(false);
        textInputLayout.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Collect.allowClick(getClass().getName())) {
                    DialogFactory.createDatePickerDialog(CreateSiteActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String formattedDate = String.format(Locale.US, "%d/%d/%d", year, month, dayOfMonth);
                            textInputLayout.getEditText().setText(formattedDate);
                        }
                    }).show();
                }
            }
        });

        if (answer != null) {
            textInputLayout.getEditText().setText(answer);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setId(ViewUtils.generateViewId());
        } else {
            view.setId(View.generateViewId());
        }


        return view;
    }

    private View getMCQLayout(String question, String answer, String submissionTag, List<McqOption> mcqOptions) {
        View view = getLayoutInflater().inflate(R.layout.layout_spinner, null);
        TextView tvLabel = view.findViewById(R.id.spinner_label);
        Spinner spinner = view.findViewById(R.id.spinner);
        spinner.setTag(submissionTag);
        tvLabel.setText(question);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.msg_select_one_option));
        for (McqOption mcqOption : mcqOptions) {
            options.add(mcqOption.getOptionText());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setId(ViewUtils.generateViewId());
        } else {
            view.setId(View.generateViewId());
        }

        try {
            for (int i = 0; i <= options.size(); i++) {
                String curOption = options.get(i);
                if (answer.equals(curOption)) {
                    spinner.setSelection(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }


    public View getTextInputLayout(String question, String answer, String tag, String type) {

        View view = getLayoutInflater().inflate(R.layout.layout_text_input, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        view.setLayoutParams(lp);


        TextInputLayout textInputLayout = ((TextInputLayout) view);

        textInputLayout.setHint(question);
        textInputLayout.getEditText().setInputType(typeToInputType(type));
        textInputLayout.setTag(tag);

        if (answer != null) {
            textInputLayout.getEditText().setText(answer);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setId(ViewUtils.generateViewId());
        } else {
            view.setId(View.generateViewId());
        }


        return view;
    }

    private int typeToInputType(String type) {
        switch (type) {
            case NUMBER:
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSiteTypeSpinner(List<SiteType> siteTypes) {
        boolean show = siteTypes != null && !siteTypes.isEmpty();
        spinnerSiteType.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            SiteTypeSpinnerAdapter spinnerAdapter = new SiteTypeSpinnerAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item, getString(R.string.hint_choose_site_type), siteTypes);
            spinnerSiteType.setAdapter(spinnerAdapter);
            spinnerSiteType.setSelection(spinnerAdapter.getCount());

            loadValueIntoSiteTypeSpinner(siteTypes);
        }

    }

    private void loadValueIntoSiteTypeSpinner(List<SiteType> siteTypes) {
        for (int pos = 0; pos < siteTypes.size(); pos++) {
            SiteType siteType = siteTypes.get(pos);
            if (siteType.getId().equals(loadedSite.getRegionId())) {
                spinnerSiteCluster.setSelection(pos);
                break;
            }
        }
    }

    private void showSiteClusterSpinner(ArrayList<SiteRegion> clusters) {
        boolean show = clusters != null && !clusters.isEmpty();
        spinnerSiteCluster.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            SiteClusterSpinnerAdapter spinnerAdapter = new SiteClusterSpinnerAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item, getString(R.string.hint_choose_site_region), clusters);
            spinnerSiteCluster.setAdapter(spinnerAdapter);
            spinnerSiteCluster.setSelection(spinnerAdapter.getCount());

            loadValueIntoClusterSpinner(clusters);
        }
    }

    private void loadValueIntoClusterSpinner(ArrayList<SiteRegion> clusters) {
        for (int pos = 0; pos < clusters.size(); pos++) {
            SiteRegion siteRegion = clusters.get(pos);
            if (siteRegion.getId().equals(loadedSite.getRegionId())) {
                spinnerSiteCluster.setSelection(pos);
                break;
            }
        }
    }


    private void setupSaveBtn() {
        Button view = (Button) getLayoutInflater().inflate(R.layout.btn_save, null);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        view.setLayoutParams(lp);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Collect.allowClick(getClass().getName())) {

                    collectMetaAtrributes(createSiteViewModel.getMetaAttributesViewIds().getValue());
                    collectSpinnerOptions();

                    if (isUpdate) {


                        createSiteViewModel.setId(loadedSite.getId());

                        createSiteViewModel.updateSite();
                    } else {
                        String mockedSiteId = Site.getMockedId();
                        createSiteViewModel.setId(mockedSiteId);
                        createSiteViewModel.saveSite();
                    }


                }
            }
        });

        linearLayoutForm.addView(view);

    }


    private void collectSpinnerOptions() {
        if (spinnerSiteCluster.getVisibility() == View.VISIBLE) {
            String selectedRegionId = ((SiteRegion) spinnerSiteCluster.getSelectedItem()).getId();
            String selectedRegionLabel = ((SiteRegion) spinnerSiteCluster.getSelectedItem()).getName();


            createSiteViewModel.setSiteRegion(selectedRegionLabel, selectedRegionId);
        }

        if (spinnerSiteType.getVisibility() == View.VISIBLE) {
            SiteType siteType = (SiteType) spinnerSiteType.getSelectedItem();
            String siteTypeId = siteType.getId();
            String siteTypeLabel = siteType.getName();

            boolean isNotHint = !siteTypeLabel.equals(getString(R.string.hint_choose_site_type));
            if (isNotHint) {
                createSiteViewModel.setSiteType(siteTypeId, siteTypeLabel);
            }

        }

    }

    private void collectMetaAtrributes(ArrayList<Integer> ids) {

        JSONObject jsonObject = new JSONObject();
        Observable.just(ids)
                .flatMapIterable((Function<ArrayList<Integer>, Iterable<Integer>>) viewIds -> viewIds)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer layoutId) throws Exception {
                        View view = findViewById(layoutId);
                        String answer;
                        boolean isValid = true;

                        if (view instanceof ConstraintLayout) {
                            Spinner spinner = view.findViewById(R.id.spinner);
                            answer = (String) spinner.getSelectedItem();
                            if (getString(R.string.msg_select_one_option).equals(answer)) {
                                isValid = false;
                            }
                        }

                        return isValid;
                    }
                })
                .map(new Function<Integer, JSONObject>() {
                    @Override
                    public JSONObject apply(Integer layoutId) throws Exception {
                        View view = findViewById(layoutId);
                        String answer;
                        String submissionKey;

                        if (view instanceof TextInputLayout) {
                            TextInputLayout textInputLayout = ((TextInputLayout) view);
                            answer = textInputLayout.getEditText().getText().toString().trim();
                            submissionKey = (String) textInputLayout.getTag();
                        } else if (view instanceof ConstraintLayout) {
                            Spinner spinner = view.findViewById(R.id.spinner);
                            answer = (String) spinner.getSelectedItem();
                            submissionKey = (String) spinner.getTag();

                        } else {
                            throw new RuntimeException("Unexpected view found while collecting answers");
                        }

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
                        Timber.e(e);
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
        if (PermissionUtils.checkIfLocationPermissionsGranted(this)) {
            Intent toGeoPointWidget = new Intent(this, GeoPointActivity.class);
            startActivityForResult(toGeoPointWidget, Constant.Key.GEOPOINT_RESULT_CODE);
        } else {
            PermissionUtils.requestLocationPermissions(this, new PermissionListener() {
                @Override
                public void granted() {
                    onBtnCollectSiteRecordLocationClicked();
                }

                @Override
                public void denied() {

                }
            });
        }

    }


    public String getAnswerFormMetaAttrs(String submissionTag) {
        String answer = null;
        if (loadedSite != null) {
            String value = loadedSite.getMetaAttributes();

            try {
                JSONObject metaAttrsJSON = new JSONObject(value);
                Iterator<String> iter = metaAttrsJSON.keys();
                while (iter.hasNext()) {
                    String curKey = iter.next();
                    if (submissionTag.equals(curKey)) {
                        answer = metaAttrsJSON.getString(curKey);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return answer;
    }
}


