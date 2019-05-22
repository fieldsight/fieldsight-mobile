package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.v3.network.Syncable;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SyncViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_project_name)
    TextView tv_project_name;

    @BindView(R.id.tv_project_other)
    TextView tv_project_other;

    @BindView(R.id.iv_cancel)
    ImageView iv_cancel;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.lv_options)
    ListView lv_options;

    @BindView(R.id.iv_avatar)
    ImageView iv_avatar;


    SyncViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindView(Project project, HashMap<String, Integer> progressMap, boolean disable) {
        tv_project_name.setText(project.getName());
        tv_project_other.setText(String.format("A project by %s", project.getOrganizationName()));
        progressBar.setProgress(progressMap.get(project.getId()));
        iv_cancel.setVisibility(disable ? View.GONE : View.VISIBLE);
    }

    void manageChildView(List<Syncable> syncableList, boolean disable) {
        Timber.i("SyncViewHolder, syncablelistsize = %d", syncableList.size());
        lv_options.setAdapter(new ArrayAdapter<Syncable>(itemView.getContext(), R.layout.row_text_checkbox, syncableList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_text_checkbox, null);
                }
                Syncable syncable = getItem(position);
                CheckBox chkbx = convertView.findViewById(R.id.chkbx_sync_select);
                chkbx.setChecked(syncable.getSync());
                ((TextView) convertView.findViewById(R.id.tv_name)).setText(syncable.getTitle());
                convertView.setOnClickListener(v -> {
                    Timber.i("SyncViewHolder clicked");
                    downloadListItemClicked(getLayoutPosition(), position);
                });
                TextView tv_stat = convertView.findViewById(R.id.tv_secondary);
                if (syncable.getStatus() != Constant.DownloadStatus.COMPLETED) {
                    Timber.i("syncable item name = %s and status = %s", syncable.getTitle(), syncable.getStatus());
                }
                tv_stat.setTextColor(syncable.status == Constant.DownloadStatus.FAILED ?
                        getContext().getResources().getColor(R.color.red) :
                        getContext().getResources().getColor(R.color.green));
                tv_stat.setText(Constant.DOWNLOADMAP.get(syncable.getStatus()));
                chkbx.setEnabled(!disable);
                chkbx.setOnClickListener(v -> downloadListItemClicked(getLayoutPosition(), position));
                return convertView;
            }
        });

    }

    public void downloadListItemClicked(int parentPos, int pos) {

    }
}
