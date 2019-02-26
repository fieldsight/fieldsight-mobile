package org.bcss.collect.naxa.site;

import android.arch.lifecycle.LiveData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.google.common.primitives.Longs;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.SiteProfileActivity;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.common.SingleLiveEvent;
import org.bcss.collect.naxa.common.rx.RetrofitException;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;
import org.bcss.collect.naxa.generalforms.GeneralFormsFragment;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.project.MapActivity;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsFragment;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.bcss.collect.naxa.sitedocuments.SiteDocumentsListActivity;
import org.bcss.collect.naxa.stages.StageListFragment;
import org.json.JSONObject;
import org.odk.collect.android.activities.FileManagerTabs;
import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.activities.InstanceChooserList;
import org.odk.collect.android.activities.InstanceUploaderActivity;
import org.odk.collect.android.activities.InstanceUploaderList;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentEnterAnimation;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentExitAnimation;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentPopEnterAnimation;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentPopExitAnimation;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.ViewUtils.showOrHide;
import static org.odk.collect.android.activities.InstanceUploaderList.INSTANCE_UPLOADER;

public class SiteDashboardFragment extends Fragment implements View.OnClickListener {

    private Site loadedSite;
    public File f, f1;
    private ImageButton btnShowInfo;
    private PopupMenu popup;
    private TextView tvSiteName, tvSiteAddress;
    private ToggleButton btnToggleFinalized;
    private TextView tvSiteType;
    private Unbinder unbinder;
    private View rootView;
    private LiveData<Site> siteLiveData;

    public SiteDashboardFragment() {

    }


    public static SiteDashboardFragment newInstance(Site site) {
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
        rootView = inflater.inflate(R.layout.fragment_dashboard_site, container, false);
        //Constants.MY_FRAG = 1;
        unbinder = ButterKnife.bind(this, rootView);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);

        bindUI(rootView);

        /*
         *https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
         */
        SiteLocalSource.getInstance().getBySiteId(loadedSite.getId())
                .observe(getViewLifecycleOwner(), site -> {
                    if (site == null) {
                        return;
                    }

                    this.loadedSite = site;
                    hideSendButtonIfMockedSite(rootView);
                    setupPopup();
                    setupToolbar();

                    tvSiteAddress.setText(loadedSite.getAddress());
                    tvSiteName.setText(loadedSite.getName());
                    showOrHide(tvSiteType, loadedSite.getTypeLabel());

                    tvSiteType.setOnLongClickListener(view -> {
                        ToastUtils.showLongToast(loadedSite.getTypeId());
                        return true;
                    });

                    tvSiteType.setOnClickListener(view -> DialogFactory.createMessageDialog(getActivity(),
                            "Information", String.format("Only %s type sub stages will be displayed for %s", loadedSite.getTypeLabel(), loadedSite.getName())).show());


                    rootView.findViewById(R.id.site_option_btn_delete_site)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showDeleteWarningDialog();
                                }
                            });
                    setupFinalizedButton();
                });

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

        SiteLocalSource.getInstance().delete(loadedSite).observe(this, affectedRows -> {

            if (affectedRows == null) {
                ToastUtils.showShortToast(R.string.dialog_unexpected_error_title);
                return;
            }

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
        });


    }


    private void setupToolbar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_form_types);
        toolbar.setSubtitle(loadedSite.getName());
    }

    private void setupPopup() {

        popup = new PopupMenu(requireActivity(), btnShowInfo);
        popup.getMenuInflater().inflate(R.menu.popup_menu_site_option, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.popup_open_edit:
                        SiteProfileActivity.start(requireActivity(), loadedSite.getId());

                        break;
                    case R.id.popup_open_in_map:
                        MapActivity.start(getActivity(), loadedSite);

                        break;
                    case R.id.popup_view_blue_prints:
                        SiteDocumentsListActivity.start(requireActivity(), loadedSite);
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

        boolean isOfflineSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE;


        if (isOfflineSite) {
            rootView.findViewById(R.id.site_option_frag_btn_send_form).setEnabled(false);
            rootView.findViewById(R.id.site_option_btn_finalize_site).setEnabled(true);
            rootView.findViewById(R.id.site_option_btn_delete_site).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.site_option_btn_upload_site).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.site_option_frag_btn_send_form).setEnabled(true);
            rootView.findViewById(R.id.site_option_btn_finalize_site).setEnabled(false);
            rootView.findViewById(R.id.site_option_btn_delete_site).setVisibility(View.GONE);
            rootView.findViewById(R.id.site_option_btn_upload_site).setVisibility(View.GONE);
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
            case R.id.site_option_frag_btn_info:
                popup.show();

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

    @OnClick(R.id.site_option_btn_upload_site)
    public void showConfirmationDialog() {

        DialogFactory.createActionDialog(requireActivity(), "Upload selected site(s)", "Upload selected site(s) along with their filled form(s) ?")
                .setPositiveButton("Yes, upload Site(s) and Form(s)", (dialog, which) -> {
                    uploadSelectedSites(Collections.singletonList(loadedSite), true);
                })
                .setNegativeButton("No, Upload Site(s) only", (dialog, which) -> {
                    uploadSelectedSites(Collections.singletonList(loadedSite), false);
                })
                .setNeutralButton(R.string.dialog_action_dismiss, null)
                .show();

    }


    private void uploadSelectedSites(List<Site> selected, boolean uploadForms) {

        String progressMessage = "Uploading site(s)";

        final int progressNotifyId = FieldSightNotificationUtils.getINSTANCE().notifyProgress(progressMessage, progressMessage, FieldSightNotificationUtils.ProgressType.UPLOAD);


        Observable<Site> createSiteObservable = SiteRemoteSource.getInstance().uploadMultipleSites(selected);
        createSiteObservable
                .filter(site -> uploadForms)
                .map(site -> getNotUploadedFormForSite(site.getId()))
                .flatMapIterable((Function<ArrayList<Long>, Iterable<Long>>) longs -> longs)
                .toList()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        FlashBarUtils.showFlashbar(requireActivity(), progressMessage);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Long>>() {
                    @Override
                    public void onSuccess(List<Long> instanceIDs) {
                        FieldSightNotificationUtils.getINSTANCE().cancelNotification(progressNotifyId);
                        if (uploadForms) {
                            if (instanceIDs.size() > 0) {
                                Intent i = new Intent(getActivity(), InstanceUploaderActivity.class);
                                i.putExtra(FormEntryActivity.KEY_INSTANCES, Longs.toArray(instanceIDs));
                                startActivityForResult(i, INSTANCE_UPLOADER);
                            } else {
                                FlashBarUtils.showFlashbar(requireActivity(), "There are no forms to upload");
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String errorMessage = RetrofitException.getMessage(e);

                        FieldSightNotificationUtils.getINSTANCE().cancelNotification(progressNotifyId);
                        if (isAdded() && getActivity() != null) {
                            DialogFactory.createMessageDialog(getActivity(), getString(R.string.msg_site_upload_fail), errorMessage).show();
                        } else {
                            FieldSightNotificationUtils.getINSTANCE().notifyHeadsUp(getString(R.string.msg_site_upload_fail), errorMessage);
                        }
                    }
                });
    }


    private ArrayList<Long> getNotUploadedFormForSite(String siteId) {
        String selection;
        String[] selectionArgs;

        selection = InstanceProviderAPI.InstanceColumns.FS_SITE_ID + "=? and (" +
                InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                + InstanceProviderAPI.InstanceColumns.STATUS + "=? )";

        selectionArgs = new String[]{
                siteId,
                InstanceProviderAPI.STATUS_COMPLETE,
                InstanceProviderAPI.STATUS_SUBMISSION_FAILED
        };


        String sortOrder = InstanceProviderAPI.InstanceColumns.DISPLAY_NAME + " ASC";

        Cursor c = getContext().getContentResolver().query(InstanceProviderAPI.InstanceColumns.CONTENT_URI, null, selection,
                selectionArgs, sortOrder);

        ArrayList<Long> instanceIDs = new ArrayList<>();

        int i = 0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int id = c.getInt(c.getColumnIndex(FormsProviderAPI.FormsColumns._ID));
            instanceIDs.add((long) id);
            i = i + 1;
        }

        return instanceIDs;

    }


    private void toForms() {
        GeneralFormsFragment fragment = GeneralFormsFragment.newInstance(loadedSite);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(fragmentEnterAnimation, fragmentExitAnimation, fragmentPopEnterAnimation, fragmentPopExitAnimation);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("generalfrag");
        fragmentTransaction.commit();

    }


    private void toStageList() {

        StageListFragment stageListFragment = StageListFragment.newInstance(loadedSite);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(fragmentEnterAnimation, fragmentExitAnimation, fragmentPopEnterAnimation, fragmentPopExitAnimation)
                .replace(R.id.fragment_container, stageListFragment)
                .addToBackStack("myfrag2").commit();


    }

    private void toScheduleList() {


        ScheduledFormsFragment scheduleFormListFragment = ScheduledFormsFragment
                .newInstance(loadedSite);


        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(fragmentEnterAnimation, fragmentExitAnimation, fragmentPopEnterAnimation, fragmentPopExitAnimation)
                .replace(R.id.fragment_container, scheduleFormListFragment)
                .addToBackStack("myfrag1").commit();


    }
}