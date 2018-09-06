package org.bcss.collect.naxa.site;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.activities.FormEntryActivity;
import org.bcss.collect.android.activities.InstanceUploaderActivity;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.android.utilities.ThemeUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.FilterDialogAdapter;
import org.bcss.collect.naxa.common.FilterOption;
import org.bcss.collect.naxa.common.GSONInstance;
import org.bcss.collect.naxa.common.LinearLayoutManagerWrapper;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.data.SiteCluster;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;

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
import timber.log.Timber;

import static org.bcss.collect.android.activities.InstanceUploaderList.INSTANCE_UPLOADER;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

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

        offlineSitesLiveData = SiteLocalSource.getInstance()
                .getByIdAndSiteStatus(loadedProject.getId(), Constant.SiteStatus.IS_UNVERIFIED_SITE);


        assignFilterToList(FilterOption.FilterType.ALL_SITES);

        siteUploadActionModeCallback = new SiteUploadActionModeCallback();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupRecycleView() {
        siteListAdapter = new SiteListAdapter(new ArrayList<>(0), this);
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getActivity()));
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


    @Override
    public void onResume() {
        super.onResume();
        if (bottomSheetDialog == null) {
            setupBottomSheet();
        }
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
                final FilterDialogAdapter adapter = new FilterDialogAdapter(getActivity(), recyclerView, filterOptions, getSelectedFilter(), (holder, position, filterOption) -> {
                    bottomSheetDialog.dismiss();
                    assignFilterToList(filterOption.getType());

                });

                recyclerView.setAdapter(adapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        });


    }

    public MutableLiveData<ArrayList<FilterOption>> getFilterOptionForSites() {

        ArrayList<Pair> sites = new ArrayList<>();
        sites.add(Pair.create(Constant.SiteStatus.IS_UNVERIFIED_SITE, "Offline site(s)"));
        sites.add(Pair.create(Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED, "All site(s)"));

        Type type = new TypeToken<ArrayList<SiteCluster>>() {
        }.getType();
        ArrayList<SiteCluster> siteClusters = GSONInstance.getInstance().fromJson(loadedProject.getSiteClusters(), type);
        MutableLiveData<ArrayList<FilterOption>> sortingOptionsMutableLive = new MutableLiveData<>();


        Observable.just(siteClusters)
                .flatMapIterable((Function<List<SiteCluster>, Iterable<SiteCluster>>) siteClusters1 -> siteClusters1)
                .map((Function<SiteCluster, Pair>) siteCluster -> Pair.create(siteCluster.getId(), siteCluster.getName()))
                .toList()
                .subscribe(new SingleObserver<List<Pair>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Pair> pairs) {
                        ArrayList<FilterOption> filterOptions = new ArrayList<>();

                        filterOptions.add(new FilterOption(FilterOption.FilterType.SELECTED_REGION, "Site Region", pairs));
                        filterOptions.add(new FilterOption(FilterOption.FilterType.OFFLINE_SITES, "Offline Site(s)", new ArrayList<>(0)));
                        filterOptions.add(new FilterOption(FilterOption.FilterType.ALL_SITES, "All Site(s)", new ArrayList<>(0)));

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
        FragmentHostActivity.start(getActivity(), site);
    }

    @Override
    public void onSurveyFormClicked() {

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
                    uploadSelectedSites(siteListAdapter.getSelected());
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

    private void uploadSelectedSites(ArrayList<Site> selected) {
        SiteRemoteSource.getInstance()
                .uploadMultipleSites(selected)
                .flattenAsObservable((Function<List<Site>, Iterable<Site>>) sites -> sites)
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

                        Intent i = new Intent(getActivity(), InstanceUploaderActivity.class);
                        i.putExtra(FormEntryActivity.KEY_INSTANCES, instanceIDs.toArray(new Long[instanceIDs.size()]));
                        startActivityForResult(i, INSTANCE_UPLOADER);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        DialogFactory.createGenericErrorDialog(getActivity(), e.getMessage()).show();
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
        siteListAdapter.clearSelections();
        actionMode.finish();

        actionMode = null;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                siteListAdapter.resetAnimationIndex();

                //siteListAdapter.notifyDataSetChanged();
            }
        });
    }
}
