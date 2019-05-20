package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.FragmentHostActivity;
import org.bcss.collect.naxa.site.ProjectDashboardActivity;
import org.bcss.collect.naxa.site.db.SiteLocalSource;

import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectViewHolder> {
    private List<Project> projectList;

    public ProjectListAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProjectViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.project_expand, viewGroup, false)) {
            @Override
            void checkBoxChanged(int index, boolean isChecked) {
                projectList.get(index).setChecked(isChecked);
                notifyDataSetChanged();
            }

            @Override
            void itemClicked(int index) {
                super.itemClicked(index);
                Project project = projectList.get(index);
                SiteLocalSource.getInstance().getByIdAsSingle(project.getId())
                        .subscribe(new DisposableSingleObserver<List<Site>>() {
                            @Override
                            public void onSuccess(List<Site> sites) {
                                if (sites.size() > 0) {
                                    ProjectDashboardActivity.start(viewGroup.getContext(), projectList.get(index));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });

            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder projectViewHolder, int i) {
        projectViewHolder.bindView(projectList.get(i));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}
