package org.bcss.collect.naxa.site;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.FileManagerTabs;
import org.bcss.collect.android.activities.InstanceChooserList;
import org.bcss.collect.android.activities.InstanceUploaderList;
import org.bcss.collect.android.utilities.ApplicationConstants;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.generalforms.GeneralFormsFragment;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsFragment;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.stages.StageListFragment;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnLongClick;
import butterknife.Unbinder;

import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentEnterAnimation;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentExitAnimation;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.ViewUtils.showOrHide;

public class SiteDashboardFragment extends Fragment implements View.OnClickListener {

    private Site loadedSite;
    public File f, f1;
    private ImageButton btnShowInfo;
    private PopupMenu popup;
    private TextView tvSiteName, tvSiteAddress;
    private ToggleButton btnToggleFinalized;
    private TextView tvSiteType;
    private Unbinder unbinder;

    public SiteDashboardFragment() {

    }


    public static SiteDashboardFragment getInstance(Site site) {
        SiteDashboardFragment fragment = new SiteDashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, site);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_site, container, false);
        //Constants.MY_FRAG = 1;
        unbinder = ButterKnife.bind(this, rootView);

        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);

        bindUI(rootView);
        hideSendButtonIfMockedSite(rootView);
        setupPopup();
        setupToolbar();

        tvSiteAddress.setText(loadedSite.getAddress());
        tvSiteName.setText(loadedSite.getName());
        showOrHide(tvSiteType, loadedSite.getTypeLabel());

        tvSiteType.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ToastUtils.showLongToast(loadedSite.getTypeId());
                return true;
            }
        });

        tvSiteType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFactory.createMessageDialog(getActivity(),
                        "Information", String.format("Only %s type sub stages will be displayed for %s", loadedSite.getTypeLabel(), loadedSite.getName())).show();
            }
        });


        rootView.findViewById(R.id.site_option_btn_delete_site)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDeleteWarningDialog();
                    }
                });
        setupFinalizedButton();

        return rootView;
    }


    private void showDeleteWarningDialog() {
        DialogFactory.createActionDialog(getContext(), getString(R.string.dialog_title_delete_site, loadedSite.getName()),
                getString(R.string.dialog_msg_delete_site, loadedSite.getName()))
                .setPositiveButton(getString(R.string.dialog_action_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSite(loadedSite);
                    }
                }).setNegativeButton(getString(R.string.dialog_action_dismiss), null).show();
    }


    private void deleteSite(Site loadedSite) {
        int affectedRows = 1;

        switch (affectedRows) {
            case 1:
                ToastUtils.showShortToast(getString(R.string.msg_delete_sucess, loadedSite.getName()));
                new Handler().postDelayed(() -> getActivity().onBackPressed(), 500);
                break;
            case -1:
                ToastUtils.showShortToast(getString(R.string.msg_delete_failed, loadedSite.getName()));
                break;
            default:
                ToastUtils.showShortToast(getString(R.string.dialog_unexpected_error_title));
                break;
        }


    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_form_types);
        toolbar.setSubtitle(loadedSite.getName());
    }

    private void setupPopup() {

        popup = new PopupMenu(getActivity(), btnShowInfo);
        popup.getMenuInflater().inflate(R.menu.popup_menu_site_option, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Bundle bundle = new Bundle();

                switch (item.getItemId()) {

                    case R.id.popup_open_edit:

//                        SiteDetailActivity.start(getActivity(), loadedSite);
//                        getActivity().finish();

                        break;
                    case R.id.popup_open_in_map:
//                        Intent intent = new Intent(getContext(), MapBoxActivity.class);
//                        intent.putExtra(EXTRA_OBJECT, loadedSite);
//                        startActivity(intent);
                        break;
                    case R.id.popup_view_blue_prints:
//                        Intent intent1 = new Intent(getContext(), SiteDocumentsActivity.class);
//
//                        bundle.putString("SiteId", loadedSite.getSiteId());
//                        bundle.putString("SiteBluePrint", loadedSite.getSiteBluePrintListStr());
//                        intent1.putExtras(bundle);
//                        startActivity(intent1);
                        break;
                    case R.id.popup_view_metadata:
                        break;
                }
                return true;
            }
        });
    }

    private void setupFinalizedButton() {
        boolean isFinalizedSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_FINALIZED;
        btnToggleFinalized.setChecked(isFinalizedSite);

        btnToggleFinalized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    ToastUtils.showShortToast("Marked Finalized");
                    SiteLocalSource.getInstance().setSiteAsFinalized(loadedSite.getId());
                } else {
                    ToastUtils.showShortToast("Marked (Not) Finalized");
                    SiteLocalSource.getInstance().setSiteAsNotFinalized(loadedSite.getId());
                }
            }
        });
    }


    private void bindUI(View rootView) {
        tvSiteName = rootView.findViewById(R.id.site_option_frag_site_name);
        tvSiteAddress = rootView.findViewById(R.id.site_option_frag_site_address);
        tvSiteType = rootView.findViewById(R.id.site_option_frag_site_type);

        btnToggleFinalized = rootView.findViewById(R.id.site_option_btn_finalize_site);
        btnShowInfo = rootView.findViewById(R.id.site_option_frag_btn_info);
        btnShowInfo.setOnClickListener(this);


        rootView.findViewById(R.id.site_option_frag_btn_delete_form).setOnClickListener(this);
        rootView.findViewById(R.id.site_option_frag_btn_edit_saved_form).setOnClickListener(this);
        rootView.findViewById(R.id.site_option_frag_btn_send_form).setOnClickListener(this);

        rootView.findViewById(R.id.site_option_frag_general_form).setOnClickListener(this);
        rootView.findViewById(R.id.site_option_frag_schedule_form).setOnClickListener(this);
        rootView.findViewById(R.id.site_option_frag_staged_form).setOnClickListener(this);

    }

    private void hideSendButtonIfMockedSite(View rootView) {

        Boolean isOfflineSite = loadedSite.getIsSiteVerified() != Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED;

        if (isOfflineSite) {
            rootView.findViewById(R.id.site_option_frag_btn_send_form).setEnabled(false);
            rootView.findViewById(R.id.site_option_btn_finalize_site).setEnabled(true);
            rootView.findViewById(R.id.site_option_btn_delete_site).setEnabled(true);
        } else {
            rootView.findViewById(R.id.site_option_frag_btn_send_form).setEnabled(true);
            rootView.findViewById(R.id.site_option_btn_finalize_site).setEnabled(false);
            rootView.findViewById(R.id.site_option_btn_delete_site).setEnabled(false);
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.site_option_frag_general_form:
                toForms();
                break;
            case R.id.site_option_frag_schedule_form:
                toScheduleList();
                break;
            case R.id.site_option_frag_staged_form:
                toStageList();
                break;
            case R.id.site_option_frag_btn_delete_form:

                intent = new Intent(getActivity().getApplicationContext(), FileManagerTabs.class);
                intent.putExtra(EXTRA_OBJECT, loadedSite);
                startActivity(intent);

                break;
            case R.id.site_option_frag_btn_edit_saved_form:
                Intent i = new Intent(getActivity().getApplicationContext(), InstanceChooserList.class);
                i.putExtra(EXTRA_OBJECT, loadedSite);
                i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
                        ApplicationConstants.FormModes.EDIT_SAVED);
                startActivity(i);
                break;
            case R.id.site_option_frag_btn_send_form:
                intent = new Intent(getActivity().getApplicationContext(), InstanceUploaderList.class);
                intent.putExtra(EXTRA_OBJECT, loadedSite);
                startActivity(intent);
                break;
        }
    }

    @OnLongClick(R.id.site_option_frag_btn_info)
    public boolean showSiteDebugInfo() {
        if (BuildConfig.DEBUG) {
            DialogFactory.createSimpleOkErrorDialog(getActivity(), "", loadedSite.toString()).show();
            return true;
        }

        return false;
    }


    private void toForms() {
        GeneralFormsFragment fragment = GeneralFormsFragment.newInstance(loadedSite);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(fragmentEnterAnimation, fragmentExitAnimation, fragmentEnterAnimation, fragmentExitAnimation);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("generalfrag");
        fragmentTransaction.commit();

    }


    private void toStageList() {

        StageListFragment stageListFragment = StageListFragment.newInstance(loadedSite);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(fragmentEnterAnimation, fragmentExitAnimation, fragmentEnterAnimation, fragmentExitAnimation)
                .replace(R.id.fragment_container, stageListFragment)
                .addToBackStack("myfrag2").commit();


    }

    private void toScheduleList() {


        ScheduledFormsFragment scheduleFormListFragment = ScheduledFormsFragment
                .newInstance(loadedSite);


        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(fragmentEnterAnimation, fragmentExitAnimation, fragmentEnterAnimation, fragmentExitAnimation)
                .replace(R.id.fragment_container, scheduleFormListFragment)
                .addToBackStack("myfrag1").commit();


    }
}