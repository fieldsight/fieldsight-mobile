package org.odk.collect.naxa.site;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.PaginationScrollListener;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.site.db.SiteViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;

public class SiteListFragment extends Fragment implements SiteListAdapter.SiteListAdapterListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private List<Site> sitelist = new ArrayList<>();
    private SiteListAdapter siteListAdapter;

    private ActionModeCallback actionModeCallback;
    private android.support.v7.view.ActionMode actionMode;
    private String projectId;

    private ProgressDialog DownloadProgressDialog;
    private ProgressDialog uploadProgressDialog;

    private LinearLayoutManager mLayoutManager;
    private boolean isLoading = false;

    private boolean isLastPage = false;
    private String paginationUniqueLastKey = "";
    private Project loadedProject;

    public static SiteListFragment getInstance(String projectId, Project project) {

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
        setHasOptionsMenu(true);
        paginateSitesList();
        return view;
    }

    private void setupRecycleView() {
        siteListAdapter = new SiteListAdapter(getActivity(), sitelist, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(siteListAdapter);
    }

    private void setupPagination() {
        recyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                paginateSitesList();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void paginateSitesList() {
        new SiteViewModel(Collect.getInstance())
                .getSiteByProject(loadedProject)
                .observe(this, sites -> {
                    sitelist = sites;
                    setupRecycleView();
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onRowLongClicked(int position) {

    }

    @Override
    public void onUselessLayoutClicked(Site site) {


        FragmentHostActivity.start(getActivity(),site);
    }

    @Override
    public void onSurveyFormClicked() {

    }
}
