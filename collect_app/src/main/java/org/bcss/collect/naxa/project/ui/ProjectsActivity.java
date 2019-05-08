package org.bcss.collect.naxa.project.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.BaseRecyclerViewAdapter;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.project.adapter.ProjectViewHolder;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.project.data.ProjectRepository;
import org.bcss.collect.naxa.project.data.model.SiteResponse;
import org.bcss.collect.naxa.project.viewmodel.ProjectViewModel;
import org.bcss.collect.naxa.site.db.SiteRemoteSource;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.Arrays;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

public class ProjectsActivity extends CollectAbstractActivity {

    private RecyclerView recyclerView;
    private boolean isDown = false;
    private BaseRecyclerViewAdapter<Project, ProjectViewHolder> adapter;
    private MovieCategoryAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_projects);
        bindUI();
        Movies movie_one = new Movies("The Shawshank Redemption");
        Movies movie_two = new Movies("The Godfather");
        Movies movie_three = new Movies("The Dark Knight");
        Movies movie_four = new Movies("Schindler's List ");
        Movies movie_five = new Movies("12 Angry Men ");
        Movies movie_six = new Movies("Pulp Fiction");
        Movies movie_seven = new Movies("The Lord of the Rings: The Return of the King");
        Movies movie_eight = new Movies("The Good, the Bad and the Ugly");
        Movies movie_nine = new Movies("Fight Club");
        Movies movie_ten = new Movies("Star Wars: Episode V - The Empire Strikes");
        Movies movie_eleven = new Movies("Forrest Gump");
        Movies movie_tweleve = new Movies("Inception");

        MovieCategory molvie_category_one = new MovieCategory("Drama", Arrays.asList(movie_one, movie_two, movie_three, movie_four));
        MovieCategory molvie_category_two = new MovieCategory("Action", Arrays.asList(movie_five, movie_six, movie_seven, movie_eight));
        MovieCategory molvie_category_three = new MovieCategory("History", Arrays.asList(movie_nine, movie_ten, movie_eleven, movie_tweleve));
        MovieCategory molvie_category_four = new MovieCategory("Thriller", Arrays.asList(movie_one, movie_five, movie_nine, movie_tweleve));


        final List<MovieCategory> movieCategories = Arrays.asList(molvie_category_one, molvie_category_two, molvie_category_three, molvie_category_four);

        recyclerView = findViewById(R.id.recycler_view_projects_activity);
        mAdapter = new MovieCategoryAdapter(this, movieCategories);
        mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
                MovieCategory expandedMovieCategory = movieCategories.get(position);

            }

            @Override
            public void onListItemCollapsed(int position) {
                MovieCategory collapsedMovieCategory = movieCategories.get(position);


            }
        });

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAdapter.onRestoreInstanceState(savedInstanceState);
    }


    private void bindUI() {
        recyclerView = findViewById(R.id.recycler_view_projects_activity);
    }

    private void populateData() {
        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        ProjectViewModel projectViewModel = ViewModelProviders.of(this, factory).get(ProjectViewModel.class);

        ProjectLocalSource.getInstance().deleteAll();
        projectViewModel.getAllProjects(new ProjectRepository.LoadProjectCallback() {
            @Override
            public void onProjectLoaded(List<Project> projects) {
                Timber.d("Loaded %d projects", projects.size());
                setupRecyclerView(projects);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    private void setupRecyclerView(List<Project> projects) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new BaseRecyclerViewAdapter<Project, ProjectViewHolder>(projects, R.layout.project_list_item) {
            @Override
            public void viewBinded(ProjectViewHolder projectViewHolder, Project project) {
                projectViewHolder.bindView(project);
            }

            @Override
            public ProjectViewHolder attachViewHolder(View view) {
                return new ProjectViewHolder(view);
            }

            @Override
            public void onSyncButtonTap(Project l) {
                super.onSyncButtonTap(l);
                requestSiteDataFromRemote(l.getId());
            }

            private void requestSiteDataFromRemote(String id) {
                SiteRemoteSource.getInstance()
                        .getSitesByProjectId(id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<SiteResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(SiteResponse siteResponse) {
                                Timber.d("%d sites found", siteResponse.getCount());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e);
                            }
                        });
            }
        };
        recyclerView.setAdapter(adapter);
    }

}
