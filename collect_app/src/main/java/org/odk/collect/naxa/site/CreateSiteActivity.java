package org.odk.collect.naxa.site;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.DialogFactory;
import org.odk.collect.naxa.common.ViewModelFactory;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.login.model.SiteBuilder;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private File image;
    private CreateSiteViewModel createSiteViewModel;
    private Site site;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_create);
        ButterKnife.bind(this);
        setupToolbar();
        setupViewModel();
        setupSaveBtn();

        createSiteViewModel.getStatus().observe(this, new Observer<CreateSiteFormStatus>() {
            @Override
            public void onChanged(@Nullable CreateSiteFormStatus createSiteFormStatus) {
                if (createSiteFormStatus == null) return;
                switch (createSiteFormStatus) {
                    case SUCCESS:
                        ToastUtils.showShortToastInMiddle("SUCESS");
                        break;
                    case ERROR:
                        break;
                    case EMPTY_SITE_NAME:
                        break;
                    case VALIDATE:

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
                        setSite(site);
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

                Site site = new SiteBuilder().setName(getText(tiSiteName))
                        .setIdentifier(getText(tiSiteIdentifier)).createSite();
                createSiteViewModel.setSite(site);
                createSiteViewModel.saveSite();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


    }

    @OnClick(R.id.btnCollectSiteAddPhoto)
    public void showImageDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Dismiss"};
        DialogFactory.createListActionDialog(this, "Add photo", items, (dialog, which) -> {
            switch (which) {
                case 0:
                    Intent toCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    toCamera.putExtra(MediaStore.EXTRA_OUTPUT, image);
                    startActivityForResult(toCamera, Constant.Key.RC_CAMERA);
                    break;
                case 1:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select an image"), Constant.Key.SELECT_FILE);
                    break;
                default:
                    break;
            }
        });
    }

    @OnClick(R.id.btnCollectSiteRecordLocation)
    public void onBtnCollectSiteRecordLocationClicked() {

    }

    public void setSite(Site site) {
        site.setName(getText(tiSiteName));
        site.setIdentifier(getText(tiSiteIdentifier));
    }

    private String getText(TextInputLayout textInputLayout) {
        return Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
    }
}
