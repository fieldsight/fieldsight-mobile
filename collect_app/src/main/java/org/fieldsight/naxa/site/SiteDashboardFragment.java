package org.fieldsight.naxa.site;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.common.primitives.Longs;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.FSInstanceChooserList;
import org.fieldsight.naxa.FSInstanceUploaderListActivity;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.common.FieldSightNotificationUtils;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.forms.ui.FieldSightFormListFragment;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.project.ProjectMapActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import static org.fieldsight.naxa.common.Constant.EXTRA_PROJECT;
import static org.odk.collect.android.utilities.PermissionUtils.checkIfLocationPermissionsGranted;

;

public class SiteDashboardFragment extends Fragment /*implements View.OnClickListener */ {

    private Site loadedSite;
    public File f, f1;
    private ToggleButton btnToggleFinalized;
    private Unbinder unbinder;
    private View rootView;
    private Project project;

    boolean isParent;
    @BindView(R.id.tv_site_name)
    TextView tv_site_name;

    @BindView(R.id.tv_sub_site_identifier)
    TextView tv_sub_site_identifier;

    @BindView(R.id.iv_site_image)
    ImageView iv_site_image;

    @BindView(R.id.tv_region_name)
    TextView tv_region_name;

    @BindView(R.id.tv_site_type)
    TextView tv_site_type;

    @BindView(R.id.tv_site_progress)
    TextView tv_progress;

    @BindView(R.id.tv_site_no_of_users)
    TextView tv_site_no_of_users;

    @BindView(R.id.tv_submissions)
    TextView tv_submissions;

    @BindView(R.id.iv_more)
    ImageView iv_more;

    @BindView(R.id.site_option_frag_btn_send_form)
    Button btnUpload;
    private Menu menu;


    public static SiteDashboardFragment newInstance(Site site, boolean isParent, Project project) {
        SiteDashboardFragment fragment = new SiteDashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isParent", isParent);
        bundle.putParcelable(EXTRA_OBJECT, site);
        bundle.putParcelable(EXTRA_PROJECT, project);
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
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
        isParent = getArguments().getBoolean("isParent");
        project = getArguments().getParcelable(EXTRA_PROJECT);



        /*
         *https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
         */
        SiteLocalSource.getInstance().getBySiteId(loadedSite.getId())
                .observe(getViewLifecycleOwner(), site -> {
                    if (site == null) {
                        return;
                    }
                    this.loadedSite = site;

                    updateUi(loadedSite);
                    setupToolbar();
                });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_site_map:
                checkPermissionAndOpenMap();
                break;
            case R.id.menu_upload:
                boolean isOfflineSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE;
                boolean isEditedSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_EDITED;

                if (isOfflineSite) {
                    showSiteUploadConfirmationDialog();
                } else if (isEditedSite) {
                    showEditedSiteUploadDialog();
                }
                break;
            case R.id.menu_delete:
                showDeleteWarningDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_site_dashboard, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private String checkIfEmpty(String text) {
        return TextUtils.isEmpty(text) ? "N/A" : text;
    }

    private void updateUi(Site site) {
        tv_progress.setText(checkIfEmpty(site.getCurrent_progress() + "%"));
        tv_site_name.setText(checkIfEmpty(site.getName()));
        tv_sub_site_identifier.setText(site.getIdentifier());
        tv_region_name.setText(checkIfEmpty(site.getRegion()));
        tv_site_no_of_users.setText(checkIfEmpty(site.getUsers() + ""));
        tv_submissions.setText(checkIfEmpty(site.getSubmissions() + ""));
        tv_site_type.setText(checkIfEmpty(site.getTypeLabel()));

        // load site image
        Glide.with(this)
                .load(site.getSite_logo())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_fieldsight).error(R.drawable.ic_launcher_fieldsight))
                .into(iv_site_image);
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
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("");
    }

    private void setupPopup() {
        if (this.menu == null) {
            return;
        }

        boolean isOfflineSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE;
        boolean isEditedSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_EDITED;


        if (isOfflineSite) {
            this.menu.findItem(R.id.menu_delete).setEnabled(true);
            this.menu.findItem(R.id.menu_upload).setEnabled(true);

            btnUpload.setEnabled(false);

        } else if (isEditedSite) {
            btnUpload.setEnabled(true);
            this.menu.findItem(R.id.menu_delete).setEnabled(false);
            this.menu.findItem(R.id.menu_upload).setEnabled(true);
        } else {
            btnUpload.setEnabled(true);
            this.menu.findItem(R.id.menu_delete).setEnabled(false);
            this.menu.findItem(R.id.menu_upload).setEnabled(false);
        }
    }


    private void checkPermissionAndOpenMap() {
        if (!checkIfLocationPermissionsGranted(requireActivity())) {
            new PermissionUtils().requestLocationPermissions(requireActivity(), new PermissionListener() {
                @Override
                public void granted() {
                    ProjectMapActivity.start(getActivity(), loadedSite);
                }

                @Override
                public void denied() {
                    //unused
                }
            });
        } else {
            ProjectMapActivity.start(getActivity(), loadedSite);
        }

    }

    @OnClick(R.id.tv_site_documents)
    void openSiteDocuments() {
        SiteDocumentsListActivity.start(requireActivity(), loadedSite);
    }

    @OnClick(R.id.tv_view_more)
    void viewMore() {
        SiteProfileActivity.start(requireActivity(), loadedSite.getId());
    }

    @OnClick(R.id.ll_stage_form)
    void openStageFormList() {
        toStageList();
    }

    @OnClick(R.id.ll_scheduled_form)
    void openScheduleFormList() {
        toScheduleList();
    }

    @OnClick(R.id.ll_general_form)
    void openGeneralFormPage() {
        toForms();

    }

    @OnClick(R.id.site_option_frag_btn_delete_form)
    void deleteForm() {
        Intent intent = new Intent(requireActivity(), FileManagerTabs.class);
        intent.putExtra(EXTRA_OBJECT, loadedSite);
        startActivity(intent);
    }

    @OnClick(R.id.site_option_frag_btn_edit_saved_form)
    void editForm() {
        Intent i = new Intent(requireActivity(), FSInstanceChooserList.class);
        i.putExtra(EXTRA_OBJECT, loadedSite);
        i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE,
                ApplicationConstants.FormModes.EDIT_SAVED);
        startActivity(i);
    }

    @OnClick(R.id.iv_back)
    void finishActivity() {
        Timber.i("SiteDashboardFragment, back option selected");
        if (getActivity() instanceof FragmentHostActivity) {
            ((FragmentHostActivity) getActivity()).openFragment();
        } else {
            Timber.i("HostActivity not known");
        }
    }

    @OnClick(R.id.site_option_frag_btn_send_form)
    void sendForm() {
        Intent intent = new Intent(requireActivity(), FSInstanceUploaderListActivity.class);
        intent.putExtra(EXTRA_OBJECT, loadedSite);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    void showSiteUploadConfirmationDialog() {

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

    void showEditedSiteUploadDialog() {

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
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        disableMenuItems(menu);
        this.menu = menu;
        setupPopup();
    }

    private void disableMenuItems(Menu menu) {
        Integer[] itemsToRemove = new Integer[]{
                R.id.action_refresh,
                R.id.action_logout,
                R.id.action_setting,
                R.id.action_notificaiton,
        };

        for (Integer itemId : itemsToRemove) {
            MenuItem menuItem = menu.findItem(itemId);
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
        }
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
        FieldSightFormListFragment fragment = FieldSightFormListFragment.newInstance(Constant.FormType.GENERAL, loadedSite, project);
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
        FieldSightFormListFragment fragment = FieldSightFormListFragment.newInstance(Constant.FormType.SCHEDULE, loadedSite, project);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(FRAGMENT_ENTER_ANIMATION, FRAGMENT_EXIT_ANIMATION, FRAGMENT_POP_ENTER_ANIMATION, FRAGMENT_POP_EXIT_ANIMATION)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("myfrag1").commit();


    }

}