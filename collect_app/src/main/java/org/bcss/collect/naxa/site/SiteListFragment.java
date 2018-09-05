package org.bcss.collect.naxa.site;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.utilities.ThemeUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FilterDialogAdapter;
import org.bcss.collect.naxa.common.FilterOption;
import org.bcss.collect.naxa.common.LinearLayoutManagerWrapper;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.db.SiteLocalSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

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
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        final RecyclerView recyclerView = sheetView.findViewById(R.id.recyclerView);

        ArrayList<FilterOption> filterOptions = getFilterOptionForSites();


        final FilterDialogAdapter adapter = new FilterDialogAdapter(getActivity(), recyclerView, filterOptions, getSelectedFilter(), (holder, position, filterOption) -> {
            bottomSheetDialog.dismiss();
            assignFilterToList(filterOption.getType());
        });

        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bottomSheetDialog.setContentView(sheetView);
    }

    public ArrayList<FilterOption> getFilterOptionForSites() {

        ArrayList<FilterOption> sortingOptions = new ArrayList<>();
        sortingOptions.add(new FilterOption(FilterOption.FilterType.SELECTED_REGION, "Site Region", new ArrayList<>(0)));
        sortingOptions.add(new FilterOption(FilterOption.FilterType.OFFLINE_SITES, "Offline sites only", new ArrayList<>(0)));
        sortingOptions.add(new FilterOption(FilterOption.FilterType.ALL_SITES, "All sites", new ArrayList<>(0)));

        return sortingOptions;


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

    }

    @Override
    public void onUselessLayoutClicked(Site site) {

    }

    @Override
    public void onSurveyFormClicked() {

    }
}
