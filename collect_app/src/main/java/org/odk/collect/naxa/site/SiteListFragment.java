package org.odk.collect.naxa.site;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.PaginationScrollListener;
import org.odk.collect.naxa.login.model.Project;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.site.db.SiteRemoteSource;
import org.odk.collect.naxa.site.db.SiteViewModel;
import org.odk.collect.naxa.survey.SurveyFormsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;

public class SiteListFragment extends Fragment implements SiteListAdapter.SiteListAdapterListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private List<Site> sitelist = new ArrayList<>();
    private SiteListAdapter siteListAdapter;

    private SiteUploadActionModeCallback siteUploadActionModeCallback;
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

        siteUploadActionModeCallback = new SiteUploadActionModeCallback();

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
                    Site site = new Site();
                    site.setName("survey");
                    sitelist.add(site);
                    if (sites != null) {
                        sitelist.addAll(sites);
                    }
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
        enableActionMode(position);
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
        SurveyFormsActivity.start(getActivity(),loadedProject);
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
                    SiteRemoteSource.getInstance().create(siteListAdapter.getSelected());
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
