package org.bcss.collect.naxa.site;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.primitives.Longs;
import com.google.gson.reflect.TypeToken;

import org.bcss.collect.android.R;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.FieldSightNotificationUtils;
import org.bcss.collect.naxa.common.FilterDialogAdapter;
import org.bcss.collect.naxa.common.FilterOption;
import org.bcss.collect.naxa.common.GSONInstance;
import org.bcss.collect.naxa.common.rx.RetrofitException;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.data.SiteRegion;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.bcss.collect.naxa.survey.SurveyFormsActivity;
import org.json.JSONObject;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.activities.InstanceUploaderActivity;
import org.odk.collect.android.utilities.ThemeUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.PROJECT_SITES;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.android.activities.InstanceUploaderList.INSTANCE_UPLOADER;

public class SiteListFragment extends Fragment implements SiteListAdapter.SiteListAdapterListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Project loadedProject;
    private Unbinder unbinder;
    private SiteListAdapter siteListAdapter;
    private LiveData<List<Site>> allSitesLiveData;
    private LiveData<List<Site>> offlineSitesLiveData;
    private BottomSheetDialog bottomSheetDialog;
    private FilterOption.FilterType selectedFilter;

    private ActionMode actionMode;
    private SiteUploadActionModeCallback siteUploadActionModeCallback;
    private LiveData<List<Site>> filteredSiteLiveData;
    private MenuItem sortActionFilter;


    public static SiteListFragment getInstance(Project project) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, project);
        SiteListFragment siteListFragment = new SiteListFragment();
        siteListFragment.setArguments(bundle);
        return siteListFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        loadedProject = getArguments().getParcelable(EXTRA_OBJECT);
        setupRecycleView();
        setHasOptionsMenu(true);


        allSitesLiveData = SiteLocalSource.getInstance().getById(loadedProject.getId());
        offlineSitesLiveData = SiteLocalSource.getInstance().getByIdAndSiteStatus(loadedProject.getId(), Constant.SiteStatus.IS_OFFLINE);

        collectFilterAndApply(new ArrayList<>(0));
        siteUploadActionModeCallback = new SiteUploadActionModeCallback();


        FieldSightNotificationLocalSource.getInstance()
                .isProjectNotSynced(loadedProject.getId(), loadedProject.getId())
                .observe(this, new Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer integer) {
                        if (integer != null && integer > 0) {

                            FlashBarUtils.showOutOfSyncMsg(PROJECT_SITES, requireActivity(), "Site(s) data is out of sync");
                        }
                    }
                });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void changeActionVisibility(boolean visible) {
        if (sortActionFilter == null) {
            new Handler()
                    .postDelayed(() -> {
                        changeActionVisibility(visible);
                        //it takes a while, for findMenu() to return action menu
                    }, 1000);

            return;
        }
        sortActionFilter.setVisible(visible);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        sortActionFilter = menu.findItem(R.id.action_filter);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    private void setupRecycleView() {
        siteListAdapter = new SiteListAdapter(new ArrayList<>(0), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(siteListAdapter);
    }


    private void assignFilterToList(FilterOption.FilterType type) {
        LiveData<List<Site>> source;

        switch (type) {
            case OFFLINE_SITES:
                source = offlineSitesLiveData;
                break;
            case ALL_SITES:
            default:
                source = allSitesLiveData;
                break;
        }
        source.observe(this, sites -> {
            siteListAdapter.updateList(sites);
        });

    }


    private void collectFilterAndApply(ArrayList<FilterOption> sortList) {
//        String selectedRegion = ProjectFilterLocalSource.getInstance()
//                .getById(loadedProject.getId())
//                .toObservable()
//                .
//                .getSelectedRegionLabel();
//
//        String regionLabel = ProjectFilterLocalSource.getInstance()
//                .getById(loadedProject.getId())
//                .getSelectedRegionId();
        String site = "", selectedRegion = "0", regionLabel = "";

        for (FilterOption filterOption : sortList) {
            switch (filterOption.getType()) {
                case SITE:
                    site = filterOption.getSelectionId();
                    break;
                case SELECTED_REGION:
                    selectedRegion = filterOption.getSelectionId();
                    regionLabel = filterOption.getSelectionLabel();
                    break;
            }
        }

        LiveData<List<Site>> source;
        switch (selectedRegion) {
            case "0":
                source = allSitesLiveData;
                break;
            default:
                source = SiteLocalSource.getInstance()
                        .getByIdStatusAndCluster(loadedProject.getId(), selectedRegion);
                break;
        }

        source.observe(this, sites -> {
            siteListAdapter.updateList(sites);
        });

//        ProjectFilter projectFilter = new ProjectFilter(loadedProject.getId(), selectedRegion, regionLabel);
//        AsyncTask.execute(() -> FieldSightConfigDatabase.getDatabase(Collect.getInstance()).getProjectFilterDAO().insert(projectFilter));

    }


    @Override
    public void onResume() {
        super.onResume();
        if (bottomSheetDialog == null) {
            setupBottomSheet();
        }
        changeActionVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        changeActionVisibility(false);
    }

    private void setupBottomSheet() {
        CollectAbstractActivity activity = (CollectAbstractActivity) getActivity();
        if (activity == null) {
            Timber.e("Activity is null");
            return;
        }
        bottomSheetDialog = new BottomSheetDialog(activity, new ThemeUtils(getContext()).getBottomDialogTheme());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_site_filter, null);
        final RecyclerView recyclerView = sheetView.findViewById(R.id.recyclerView);
        bottomSheetDialog.setContentView(sheetView);


        MutableLiveData<ArrayList<FilterOption>> filterOptions = getFilterOptionForSites();
        filterOptions.observe(this, new Observer<ArrayList<FilterOption>>() {
            @Override
            public void onChanged(@Nullable ArrayList<FilterOption> filterOptions) {
                final FilterDialogAdapter adapter = new FilterDialogAdapter(getActivity(), recyclerView, filterOptions, getSelectedFilter(), new FilterDialogAdapter.RecyclerViewClickListener() {
                    @Override
                    public void onFilterButtonClicked(ArrayList<FilterOption> sortList) {
                        bottomSheetDialog.dismiss();
                        collectFilterAndApply(sortList);
                    }

                    @Override
                    public void onItemClicked(FilterDialogAdapter.ViewHolderText holder, int position, FilterOption filterOption) {
                        bottomSheetDialog.dismiss();
                    }
                });

                recyclerView.setAdapter(adapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

            }
        });


    }


    public MutableLiveData<ArrayList<FilterOption>> getFilterOptionForSites() {


        Type type = new TypeToken<ArrayList<SiteRegion>>() {
        }.getType();

        ArrayList<SiteRegion> siteRegions;
        siteRegions = GSONInstance.getInstance().fromJson(loadedProject.getSiteClusters(), type);
        MutableLiveData<ArrayList<FilterOption>> sortingOptionsMutableLive = new MutableLiveData<>();

        if (siteRegions == null) siteRegions = new ArrayList<>(0);

        Observable.just(siteRegions)
                .flatMapIterable((Function<List<SiteRegion>, Iterable<SiteRegion>>) siteClusters1 -> siteClusters1)
                .map((Function<SiteRegion, Pair>) siteCluster -> Pair.create(siteCluster.getId(), siteCluster.getName()))
                .toList()
                .map(new Function<List<Pair>, List<Pair>>() {
                    @Override
                    public List<Pair> apply(List<Pair> pairs) {
                        pairs.add(Pair.create("0", "All sites"));

                        return pairs;
                    }
                })
                .subscribe(new SingleObserver<List<Pair>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Pair> pairs) {
                        ArrayList<FilterOption> filterOptions = new ArrayList<>();

                        filterOptions.add(new FilterOption(FilterOption.FilterType.SELECTED_REGION, "Region", pairs));
                        filterOptions.add(new FilterOption(FilterOption.FilterType.CONFIRM_BUTTON, "Apply", null));
//                        filterOptions.add(new FilterOption(FilterOption.FilterType.SITE, "Site ", sites));
//                        filterOptions.add(new FilterOption(FilterOption.FilterType.OFFLINE_SITES, "Offline Site(s)", new ArrayList<>(0)));
//                        filterOptions.add(new FilterOption(FilterOption.FilterType.ALL_SITES, "All Site(s)", new ArrayList<>(0)));

                        sortingOptionsMutableLive.setValue(filterOptions);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


        return sortingOptionsMutableLive;


    }

    protected FilterOption.FilterType getSelectedFilter() {
        if (selectedFilter == null) {
            return FilterOption.FilterType.ALL_SITES;
        }
        return selectedFilter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_filter) {
            bottomSheetDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }

    @Override
    public void onUselessLayoutClicked(Site site) {
        if (siteListAdapter.getSelectedItemCount() == 0) {
            FragmentHostActivity.start(getActivity(), site);
        }
    }

    @Override
    public void onSurveyFormClicked() {
        SurveyFormsActivity.start(getActivity(), loadedProject);
    }


    private void enableActionMode(int position) {
        if (actionMode == null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionMode = activity.startSupportActionMode(siteUploadActionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        siteListAdapter.toggleSelection(position);
        int count = siteListAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    public class SiteUploadActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_upload_items, menu);
            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete_sites:
                    return true;
                case R.id.action_upload_sites:
                    showConfirmationDialog();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            disableActionMode();
        }
    }

    private void showConfirmationDialog() {
        DialogFactory.createActionDialog(requireActivity(), "Upload selected site(s)", "Upload selected site(s) along with their filled form(s) ?")
                .setPositiveButton("Yes, upload Site(s) and Form(s)", (dialog, which) -> {
                    uploadSelectedSites(siteListAdapter.getSelected(), true);
                })
                .setNegativeButton("No, Upload Site(s) only", (dialog, which) -> {
                    uploadSelectedSites(siteListAdapter.getSelected(), false);
                })
                .setOnDismissListener(dialog -> actionMode.finish())
                .setNeutralButton(R.string.dialog_action_dismiss, null)
                .show();

    }


    private void uploadSelectedSites(ArrayList<Site> selected, boolean uploadForms) {

        String progressMessage = "Uploading site(s)";

        final int progressNotifyId = FieldSightNotificationUtils.getINSTANCE().notifyProgress(progressMessage, progressMessage, FieldSightNotificationUtils.ProgressType.UPLOAD);


        Observable<Site> createSiteObservable = SiteRemoteSource.getInstance().uploadMultipleSites(selected);
        createSiteObservable
                .filter(site -> uploadForms)
                .map(site -> getNotUploadedFormForSite(site.getId()))
                .flatMapIterable((Function<ArrayList<Long>, Iterable<Long>>) longs -> longs)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Long>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Long> instanceIDs) {
                        FieldSightNotificationUtils.getINSTANCE().cancelNotification(progressNotifyId);

                        if (uploadForms && instanceIDs.size() > 0) {
                            Intent i = new Intent(getActivity(), InstanceUploaderActivity.class);
                            i.putExtra(FormEntryActivity.KEY_INSTANCES, Longs.toArray(instanceIDs));
                            startActivityForResult(i, INSTANCE_UPLOADER);
                        }

                        if (uploadForms && instanceIDs.size() == 0) {
                            FlashBarUtils.showFlashbar(requireActivity(), "There are no forms to upload");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
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

    public void disableActionMode() {

        actionMode.finish();
        actionMode = null;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                setupRecycleView();
                collectFilterAndApply(new ArrayList<>(0));
//                siteListAdapter.clearSelections();
//                siteListAdapter.resetAnimationIndex();
//                siteListAdapter.notifyDataSetChanged();
            }
        });
    }
}
