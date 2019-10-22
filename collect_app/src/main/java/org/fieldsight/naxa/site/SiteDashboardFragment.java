package org.fieldsight.naxa.site;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.common.primitives.Longs;

import org.fieldsight.collect.android.BuildConfig;
import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.FSInstanceChooserList;
import org.fieldsight.naxa.FSInstanceUploaderListActivity;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.common.FieldSightNotificationUtils;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.forms.ui.FieldSightFormListFragment;
import org.fieldsight.naxa.login.model.Site;

import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.fieldsight.naxa.site.db.SiteRemoteSource;
import org.fieldsight.naxa.sitedocuments.SiteDocumentsListActivity;
import org.fieldsight.naxa.stages.StageListFragment;
import org.odk.collect.android.SiteProfileActivity;
import org.odk.collect.android.activities.FileManagerTabs;
import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.activities.InstanceUploaderActivity;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.PermissionUtils;
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
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static org.fieldsight.naxa.common.Constant.ANIM.FRAGMENT_ENTER_ANIMATION;
import static org.fieldsight.naxa.common.Constant.ANIM.FRAGMENT_EXIT_ANIMATION;
import static org.fieldsight.naxa.common.Constant.ANIM.FRAGMENT_POP_ENTER_ANIMATION;
import static org.fieldsight.naxa.common.Constant.ANIM.FRAGMENT_POP_EXIT_ANIMATION;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.fieldsight.naxa.common.ViewUtils.showOrHide;
import static org.odk.collect.android.utilities.PermissionUtils.checkIfLocationPermissionsGranted;

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

    boolean isParent ;


    public static SiteDashboardFragment newInstance(Site site, boolean isParent) {
        SiteDashboardFragment fragment = new SiteDashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isParent", isParent);
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
        isParent = getArguments().getBoolean("isParent");

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
                    new Handler().postDelayed(() -> requireActivity().finish(), 500);
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
                        checkPermissionAndOpenMap();

                        break;
                    case R.id.popup_view_blue_prints:
                        SiteDocumentsListActivity.start(requireActivity(), loadedSite);
                        break;


                }
                return true;
            }
        });
    }
    private void checkPermissionAndOpenMap() {
        if (!checkIfLocationPermissionsGranted(requireActivity())) {
            new PermissionUtils().requestLocationPermissions(requireActivity(), new PermissionListener() {
                @Override
                public void granted() {
//                    MapActivity.start(getActivity(), loadedSite);
                    ToastUtils.showLongToast("Map has been disabled");
                }
                @Override
                public void denied() {
                    //unused
                }
            });
        }
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
        CardView cvStageform = rootView.findViewById(R.id.cv_stageform);

        Timber.d("SitesdashboardFragment, isParentsite = %s", isParent);
        cvStageform.setVisibility(isParent ? View.GONE : View.VISIBLE);

        rootView.findViewById(R.id.site_option_frag_btn_delete_form).setOnClickListener(this);
        rootView.findViewById(R.id.site_option_frag_btn_edit_saved_form).setOnClickListener(this);
        rootView.findViewById(R.id.site_option_frag_btn_send_form).setOnClickListener(this);

        rootView.findViewById(R.id.site_option_frag_general_form).setOnClickListener(this);
        rootView.findViewById(R.id.site_option_frag_schedule_form).setOnClickListener(this);
        rootView.findViewById(R.id.site_option_frag_staged_form).setOnClickListener(this);

    }

    private void hideSendButtonIfMockedSite(View rootView) {

        boolean isOfflineSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE;
        boolean isEditedSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_EDITED;

        if (isOfflineSite) {
            rootView.findViewById(R.id.site_option_frag_btn_send_form).setEnabled(false);
            rootView.findViewById(R.id.site_option_btn_finalize_site).setEnabled(true);
            rootView.findViewById(R.id.site_option_btn_delete_site).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.site_option_btn_upload_site).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.site_option_btn_upload_edited_site).setVisibility(View.GONE);
        } else if (isEditedSite) {
            rootView.findViewById(R.id.site_option_btn_upload_edited_site).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.site_option_btn_delete_site).setVisibility(View.GONE);
            rootView.findViewById(R.id.site_option_btn_upload_site).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.site_option_frag_btn_send_form).setEnabled(true);
            rootView.findViewById(R.id.site_option_btn_finalize_site).setEnabled(false);
            rootView.findViewById(R.id.site_option_btn_delete_site).setVisibility(View.GONE);
            rootView.findViewById(R.id.site_option_btn_upload_site).setVisibility(View.GONE);
            rootView.findViewById(R.id.site_option_btn_upload_edited_site).setVisibility(View.GONE);
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

                intent = new Intent(requireActivity(), FileManagerTabs.class);
                intent.putExtra(EXTRA_OBJECT, loadedSite);
                startActivity(intent);

                break;
            case R.id.site_option_frag_btn_edit_saved_form:
                Intent i = new Intent(requireActivity(), FSInstanceChooserList.class);
                i.putExtra(EXTRA_OBJECT, loadedSite);
                i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
                        ApplicationConstants.FormModes.EDIT_SAVED);
                startActivity(i);
                break;
            case R.id.site_option_frag_btn_send_form:
                intent = new Intent(requireActivity(), FSInstanceUploaderListActivity.class);
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

        DialogFactory.createActionDialog(requireActivity(), getString(R.string.dialog_title_upload_sites), getString(R.string.dialog_msg_upload_sites))
                .setPositiveButton(R.string.dialog_action_upload_site_and_form, (dialog, which) -> {
                    uploadSelectedSites(Collections.singletonList(loadedSite), true);

                })
                .setNegativeButton(R.string.dialog_action_only_upload_site, (dialog, which) -> {
                    uploadSelectedSites(Collections.singletonList(
                            loadedSite), false);
                })
                .setNeutralButton(R.string.dialog_action_dismiss, null)
                .show();


    }

    @OnClick(R.id.site_option_btn_upload_edited_site)
    public void showEditedSiteUploadDialog() {

        DialogFactory.createActionDialog(requireActivity(), getString(R.string.dialog_title_upload_sites), getString(R.string.dialog_msg_upload_edited_sites))
                .setPositiveButton(R.string.upload, (dialog, which) -> {
                    uploadEditedSites(Collections.singletonList(loadedSite));
                })
                .setNegativeButton(R.string.dialog_action_dismiss, null)
                .show();
    }

    private void uploadEditedSites(List<Site> sites) {
        String progressMessage = getString(R.string.dialog_msg_uploading_sites);
        final int progressNotifyId = FieldSightNotificationUtils.getINSTANCE().notifyProgress(progressMessage, progressMessage, FieldSightNotificationUtils.ProgressType.UPLOAD);

        SiteRemoteSource.getInstance().uploadMultipleEditedSites(sites)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<Site, ObservableSource<?>>) site -> SiteLocalSource.getInstance().updateSiteStatus(site.getId(), Constant.SiteStatus.IS_ONLINE))
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        FieldSightNotificationUtils.getINSTANCE().cancelNotification(progressNotifyId);
                        requireActivity().onBackPressed();
                        ToastUtils.showLongToast(R.string.msg_site_upload_sucess);

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String message;
                        if (e instanceof RetrofitException && ((RetrofitException) e).getResponse().errorBody() == null) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }

                        FieldSightNotificationUtils.getINSTANCE().cancelNotification(progressNotifyId);
                        if (isAdded() && getActivity() != null) {
                            DialogFactory.createMessageDialog(getActivity(), getString(R.string.msg_site_upload_fail), message).show();
                        } else {
                            FieldSightNotificationUtils.getINSTANCE().notifyHeadsUp(getString(R.string.msg_site_upload_fail), message);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    private void uploadSelectedSites(List<Site> selected, boolean uploadForms) {

        String progressMessage = getString(R.string.dialog_msg_uploading_sites);

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
                        SnackBarUtils.showFlashbar(requireActivity(), progressMessage);
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
                                startActivityForResult(i, FSInstanceUploaderListActivity.INSTANCE_UPLOADER);
                            } else {

                                SnackBarUtils.showFlashbar(requireActivity(), "There are no FORMS to upload");
                                SnackBarUtils.showFlashbar(requireActivity(), requireActivity().getString(R.string.msg_site_upload_sucess));

                                requireActivity().onBackPressed();
                            }
                        } else {
                            SnackBarUtils.showFlashbar(requireActivity(), requireActivity().getString(R.string.msg_site_upload_sucess));
                            requireActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String message;
                        if (e instanceof RetrofitException && ((RetrofitException) e).getResponse().errorBody() == null) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }

                        FieldSightNotificationUtils.getINSTANCE().cancelNotification(progressNotifyId);
                        if (isAdded() && getActivity() != null) {
                            DialogFactory.createMessageDialog(getActivity(), getString(R.string.msg_site_upload_fail), message).show();
                        } else {
                            FieldSightNotificationUtils.getINSTANCE().notifyHeadsUp(getString(R.string.msg_site_upload_fail), message);
                        }
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FSInstanceUploaderListActivity.INSTANCE_UPLOADER) {
            switch (resultCode) {
                case RESULT_OK:
                case RESULT_CANCELED:
                    requireActivity().onBackPressed();
                    break;
            }
        }
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
        FieldSightFormListFragment fragment = FieldSightFormListFragment.newInstance(Constant.FormType.GENERAL, loadedSite);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(FRAGMENT_ENTER_ANIMATION, FRAGMENT_EXIT_ANIMATION, FRAGMENT_POP_ENTER_ANIMATION, FRAGMENT_POP_EXIT_ANIMATION);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("generalfrag");
        fragmentTransaction.commit();
    }


    private void toStageList() {
        StageListFragment stageListFragment = StageListFragment.newInstance(loadedSite);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(FRAGMENT_ENTER_ANIMATION, FRAGMENT_EXIT_ANIMATION, FRAGMENT_POP_ENTER_ANIMATION, FRAGMENT_POP_EXIT_ANIMATION)
                .replace(R.id.fragment_container, stageListFragment)
                .addToBackStack("myfrag2").commit();
    }

    private void toScheduleList() {
        FieldSightFormListFragment fragment = FieldSightFormListFragment.newInstance(Constant.FormType.SCHEDULE, loadedSite);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(FRAGMENT_ENTER_ANIMATION, FRAGMENT_EXIT_ANIMATION, FRAGMENT_POP_ENTER_ANIMATION, FRAGMENT_POP_EXIT_ANIMATION)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("myfrag1").commit();


    }
}