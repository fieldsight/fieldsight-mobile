package org.bcss.collect.naxa.v3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.project.data.ProjectRepository;
import org.bcss.collect.naxa.v3.adapter.ProjectListAdapter;
import org.bcss.collect.naxa.v3.network.LoadProjectCallback;
import org.json.JSONArray;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * pull the project list from the new api. show the list of the project
 * it has two options - 1. sync all project without selection at a time
 * 2. sync project with the selection
 * add the checkbox in the project to allow the user to select and deselect the project
 *
 * @Since 2019-05-09
 * @Author Yubaraj Poudel
 *
 * steps:
 * 1. Create the list of the project for sync {@code Set<String> syncList }
 * 2. Depending upon the selection remove the items from the list
 * 3. create a flag for controlled sync or automatic sync {@code boolean auto = false }
 * 4. if auto is true
 *  4.1 sync all the selected projects
 * 5. if auto is false
 *  5.1 sync projects but allow the user to select what they want to sync
 **/


public class ProjectListActivityV3 extends CollectAbstractActivity {
    @BindView(R.id.rv_projectlist)
    RecyclerView rv_projectlist;

    @BindView(R.id.ll_nodata)
    LinearLayout ll_nodata;

    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    @BindView(R.id.prgbar)
    ProgressBar prgbar;

    ProjectListAdapter adapter = null;
    List<Project> projectList = new ArrayList<>();
    boolean auto = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_simple_recycler_with_nodata);
        ButterKnife.bind(this);
        adapter = new ProjectListAdapter(projectList);
        rv_projectlist.setLayoutManager(new LinearLayoutManager(this));
        rv_projectlist.setAdapter(adapter);
        getDataFromServer();
        manageNodata(true);
    }

   void manageNodata(boolean loading) {
       ll_nodata.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
       tv_nodata.setText(loading ? "Loading data ... " : "Data not found");
   }

    void getDataFromServer() {
        ProjectRepository.getInstance().getAll(new LoadProjectCallback() {
            @Override
            public void onProjectLoaded(List<Project> projects) {
                projectList.addAll(projects);
                adapter.notifyDataSetChanged();
                manageNodata(false);
                Timber.e("data found with %d size", projects.size());
            }

            @Override
            public void onDataNotAvailable() {
                Timber.d("data not available");
                manageNodata(false);
            }
        });
    }

   void manageSyncList() {

   }

   void toggleProjectSelection() {}

}


