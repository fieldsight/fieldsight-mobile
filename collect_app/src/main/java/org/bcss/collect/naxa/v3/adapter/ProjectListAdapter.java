package org.bcss.collect.naxa.v3.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.FragmentHostActivity;
import org.bcss.collect.naxa.site.ProjectDashboardActivity;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.v3.network.ProjectNameTuple;
import org.bcss.collect.naxa.v3.network.SyncActivity;
import org.odk.collect.android.utilities.DateTimeUtils;

import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

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
                Project project = projectList.get(index);
                if (project.isSynced()) {
                    ProjectDashboardActivity.start(viewGroup.getContext(), projectList.get(index));
                    return;
                }
                boolean isSyncRunning = Collect.selectedProjectList != null && Collect.selectedProjectList.size() > 0
                        && Collect.syncableMap != null && Collect.syncableMap.size() > 0;
                Timber.i("ProjectListAdapter, isSyncRunning : " + isSyncRunning);
                if (isSyncRunning) {
                    viewGroup.getContext().startActivity(new Intent(viewGroup.getContext(), SyncActivity.class));
                }
            }
        };
    }

    public void notifyProjectisSynced(List<ProjectNameTuple> projecttuple) {
        for (int i = 0; i < projectList.size(); i++) {
            for (int j = 0; j < projecttuple.size(); j++) {
                if (projectList.get(i).getId().equals(projecttuple.get(j).projectId)) {
                    int status = projecttuple.get(j).status;
                    if(status == Constant.DownloadStatus.RUNNING) {
                        projectList.get(i).setStatusMessage("Syncing project");
                    } else if(status == Constant.DownloadStatus.COMPLETED) {
                        projectList.get(i).setSynced(true);
                        projectList.get(i).setSyncedDate(projecttuple.get(j).created_date);
                        projectList.get(i).setStatusMessage("Synced On " + DateTimeUtils.getFormattedDate("yyyy-MM-dd, HH:mm", projectList.get(i).getSyncedDate()));
                    } else if(status == Constant.DownloadStatus.FAILED) {
                        projectList.get(i).setSynced(false);
                        projectList.get(i).setStatusMessage("Sync failed");
                    } else {
                        projectList.get(i).setStatusMessage("");
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean anyProjectSelectedForSync() {
        boolean found = false;
        for(Project project: this.projectList) {
            if(project.isChecked()) {
                found = true;
                break;
            }
        }
        return found;
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
