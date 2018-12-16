package org.bcss.collect.naxa.site;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import org.bcss.collect.android.activities.FileManagerTabs;
import org.bcss.collect.android.activities.FormEntryActivity;
import org.bcss.collect.android.activities.InstanceChooserList;
import org.bcss.collect.android.activities.InstanceUploaderActivity;
import org.bcss.collect.android.activities.InstanceUploaderList;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.android.utilities.ApplicationConstants;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.firebase.NotificationUtils;
import org.bcss.collect.naxa.generalforms.GeneralFormsFragment;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.project.MapActivity;
import org.bcss.collect.naxa.scheduled.data.ScheduledFormsFragment;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.bcss.collect.naxa.sitedocuments.SiteDocumentsListActivity;
import org.bcss.collect.naxa.stages.StageListFragment;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import timber.log.Timber;

import static org.bcss.collect.android.activities.InstanceUploaderList.INSTANCE_UPLOADER;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentEnterAnimation;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentExitAnimation;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentPopEnterAnimation;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentPopExitAnimation;
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
        View rootView = inflater.inflate(R.layout.fragment_dashboard_site, container, false);
        //Constants.MY_FRAG = 1;
        unbinder = ButterKnife.bind(this, rootView);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
        bindUI(rootView);

        SiteLocalSource.getInstance().getBySiteId(loadedSite.getId()).observe(requireActivity(), site -> {
            if(site==null)return;
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
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_form_types);
        toolbar.setSubtitle(loadedSite.getName());
    }

    private void setupPopup() {

        popup = new PopupMenu(requireActivity(), btnShowInfo);
        popup.getMenuInflater().inflate(R.menu.popup_menu_site_option, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Bundle bundle = new Bundle();

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

        Boolean isOfflineSite = loadedSite.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE;

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
    public void uploadSite() {

        String progressMessage = "Uploading site(s)";
        String completedMessage = "Site(s) Uploaded";
        String failedMessage = "Site(s) upload failed";
        final int progressNotifyId = 987876756;

        ArrayList<Site> list = new ArrayList<>();
        list.add(loadedSite);
        SiteRemoteSource.getInstance()
                .uploadMultipleSites(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Site, ArrayList<Long>>() {
                    @Override
                    public ArrayList<Long> apply(Site site) throws Exception {
                        return getNotUploadedFormForSite(site.getId());
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
//                        FieldSightNotificationUtils.getINSTANCE().notifyProgress(progressNotifyId, progressMessage);
                    }
                })
                .subscribe(new DisposableObserver<ArrayList<Long>>() {
                    @Override
                    public void onNext(ArrayList<Long> instanceIDs) {
                        NotificationUtils.cancelNotification(progressNotifyId);
                        FieldSightNotificationUtils.getINSTANCE().notifyNormal(completedMessage, completedMessage);


                        if (isAdded()) {
                            DialogFactory.createActionDialog(getActivity(), "Upload instances", "Upload form instance(s) belonging to offline site(s)")
                                    .setPositiveButton("Upload ", (dialog, which) -> {
                                        Intent i = new Intent(getActivity(), InstanceUploaderActivity.class);
                                        i.putExtra(FormEntryActivity.KEY_INSTANCES, Longs.toArray(instanceIDs));
                                        startActivityForResult(i, INSTANCE_UPLOADER);
                                    }).setNegativeButton("Not now", null).show();
                        } else {
                            FieldSightNotificationUtils.getINSTANCE().notifyHeadsUp("Unable to start upload", "Unable to start upload for offline site");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String errorMessage = e.getMessage();
                        NotificationUtils.cancelNotification(progressNotifyId);
                        FieldSightNotificationUtils.getINSTANCE().notifyNormal(failedMessage, errorMessage);

                        if (e instanceof HttpException) {
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            errorMessage = getErrorMessage(responseBody);
                        }

                        e.printStackTrace();
                        if (isAdded()) {
                            DialogFactory.createMessageDialog(getActivity(), getString(R.string.msg_site_upload_fail), errorMessage).show();
                        }
                    }

                    @Override
                    public void onComplete() {

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

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("non_field_errors");
        } catch (Exception e) {
            return e.getMessage();
        }
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