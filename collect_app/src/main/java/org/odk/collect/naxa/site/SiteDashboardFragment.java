package org.odk.collect.naxa.site;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.DialogFactory;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.generalforms.GeneralFormListFragment;

import java.io.File;


import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.ViewUtils.showOrHide;

public class SiteDashboardFragment extends Fragment implements View.OnClickListener {

    private Site loadedSite;
    public File f, f1;
    private ImageButton btnShowInfo;
    private PopupMenu popup;
    private TextView tvSiteName, tvSiteAddress;
    private ToggleButton btnToggleFinalized;
    private TextView tvSiteType;

    public SiteDashboardFragment() {

    }


    public static void start(AppCompatActivity context, Site site) {
        SiteDashboardFragment SiteDashboardFragment = getInstance(site);

        FragmentManager fragmentManager = context.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(Constant.ANIM.fragmentEnterAnimation, Constant.ANIM.fragmentExitAnimation,
                Constant.ANIM.fragmentEnterAnimation, Constant.ANIM.fragmentExitAnimation);
        fragmentTransaction.replace(R.id.fragment_container, SiteDashboardFragment);
        fragmentTransaction.addToBackStack("myfrag0");
        fragmentTransaction.commit();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_site, container, false);
        //Constants.MY_FRAG = 1;

        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);


        bindUI(rootView);
        hideSendButtonIfMockedSite(rootView, loadedSite.getId());
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
//        int affectedRows = DatabaseHelper.getInstance().deleteSite(loadedSite);
        int affectedRows = -1;

        switch (affectedRows) {
            case 1:
                ToastUtils.showShortToast(getString(R.string.msg_delete_sucess, loadedSite.getName()));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().onBackPressed();
                    }
                }, 500);
                break;
            default:
                ToastUtils.showShortToast(getString(R.string.msg_delete_failed, loadedSite.getName()));
                break;
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_general);
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
                    //    DatabaseHelper.getInstance().setSiteAsFinalized(loadedSite.getId());
                } else {
                    ToastUtils.showShortToast("Marked (Not) Finalized");
                    //  DatabaseHelper.getInstance().setSiteAsNotFinalized(loadedSite.getId());
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

    private void hideSendButtonIfMockedSite(View rootView, String currentFormSiteId) {

        Boolean isSiteMocked = false;
        //Boolean isSiteMocked = DatabaseHelper.getInstance(getActivity().getApplicationContext()).isThisSiteOffline(currentFormSiteId);

        if (isSiteMocked) {
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.site_option_frag_general_form:
                toForms();
                break;
        }
    }


    private void toForms() {
        Bundle bundle = new Bundle();
        Fragment fragment = new GeneralFormListFragment();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(Constant.ANIM.fragmentEnterAnimation, Constant.ANIM.fragmentExitAnimation, Constant.ANIM.fragmentEnterAnimation, Constant.ANIM.fragmentExitAnimation);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("generalfrag");
        fragmentTransaction.commit();

    }
}