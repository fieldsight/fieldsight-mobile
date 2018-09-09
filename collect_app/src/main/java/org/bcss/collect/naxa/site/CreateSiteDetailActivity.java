package org.bcss.collect.naxa.site;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.login.model.Site;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class CreateSiteDetailActivity extends CollectAbstractActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbarGeneral;
    @BindView(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @BindView(R.id.tv_toolbar_message)
    TextView tvToolbarMessage;
    @BindView(R.id.appbar_general)
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
    private Site site;

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
        setupSaveBtn();

        try {
            site = getIntent().getExtras().getParcelable(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            Timber.e("Can't start activity without site extra_object");
            ToastUtils.showLongToast(getString(R.string.msg_failed_to_load));
            finish();
        }

        createSiteDetailViewModel
                .getEditSite()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean aBoolean) {
                        if (aBoolean) {
                            layoutSiteDataDisplay.setVisibility(View.GONE);
                            layoutSiteDataEdit.setVisibility(View.VISIBLE);
                            fabActivateEditMode.setVisibility(View.GONE);

                        } else {
                            fabActivateEditMode.setVisibility(View.VISIBLE);
                            layoutSiteDataDisplay.setVisibility(View.VISIBLE);
                            layoutSiteDataEdit.setVisibility(View.GONE);
                        }
                    }
                });

        createSiteDetailViewModel
                .getSiteRepository()
                .getSiteById(site.getId())
                .observe(this, new Observer<List<Site>>() {
                    @Override
                    public void onChanged(@Nullable List<Site> sites) {
                        Site tSite = sites.get(0);
                        ilSiteIdentifier.getEditText().setText(site.getIdentifier());
                        ilSiteName.getEditText().setText(site.getName());
                        ilPhone.getEditText().setText(site.getPhone());
                        ilAddress.getEditText().setText(site.getAddress());
                        ilRegion.getEditText().setText(site.getRegion());
                        ilSiteType.getEditText().setText(site.getTypeId());
                    }
                });
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
                break;
            case R.id.btn_site_records_location:
                break;
            case R.id.btnCollectSiteSendForm:
                break;
            case R.id.fab_activate_edit_mode:
                createSiteDetailViewModel.setEditSite(true);

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

}
