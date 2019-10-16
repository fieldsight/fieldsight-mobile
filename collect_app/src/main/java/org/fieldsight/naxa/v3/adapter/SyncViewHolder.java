package org.fieldsight.naxa.v3.adapter;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.NetworkUtils;
import org.fieldsight.naxa.v3.network.Syncable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SyncViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_project_name)
    TextView tvProjectName;

    @BindView(R.id.tv_project_other)
    TextView tvProjectOther;

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.lv_options)
    ListView lvOptions;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;

    @BindView(R.id.tv_project_progress_percentage)
    TextView tvProjectProgressPercentage;


    private Project project;

    SyncViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindView(Project project, HashMap<String, Integer> progressMap, boolean disable) {
        this.project = project;

        tvProjectName.setText(project.getName());
        tvProjectOther.setText(String.format("By %s", project.getOrganizationName()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(progressMap.get(project.getId()), true);
        } else {
            progressBar.setProgress(progressMap.get(project.getId()));
        }
        ivCancel.setVisibility(disable ? View.GONE : View.VISIBLE);
        tvProjectProgressPercentage.setText(progressMap.get(project.getId()) + "%");
        Timber.i("SyncViewHolder, projectImage = %s", project.getUrl());
        Glide.with(itemView.getContext()).load(project.getUrl()).
                apply(RequestOptions.circleCropTransform()).into(ivAvatar);
    }

    void manageChildView(List<Syncable> syncableList, boolean disable) {
        Timber.i("SyncViewHolder, syncablelistsize = %d", syncableList.size());
        lvOptions.setAdapter(new ArrayAdapter<Syncable>(itemView.getContext(), R.layout.row_text_checkbox_v2, syncableList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_text_checkbox_v2, null);
                }
                Syncable syncable = getItem(position);
                CheckBox chkbx = convertView.findViewById(R.id.chkbx_sync_select);
                chkbx.setChecked(syncable.isSync());
                ((TextView) convertView.findViewById(R.id.tv_name)).setText(syncable.getTitle());
                convertView.setOnClickListener(v -> {
                    Timber.i("SyncViewHolder clicked");
                    downloadListItemClicked(getLayoutPosition(), position);
                });
                TextView tvStat = convertView.findViewById(R.id.tv_secondary);
                if (syncable.getStatus() != Constant.DownloadStatus.COMPLETED) {
                    Timber.i("syncable item name = %s and status = %s", syncable.getTitle(), syncable.getStatus());
                }
                tvStat.setTextColor(syncable.status == Constant.DownloadStatus.FAILED ?
                        getContext().getResources().getColor(R.color.red_500) :
                        getContext().getResources().getColor(R.color.green));


                if (syncable.getStatus() == Constant.DownloadStatus.RUNNING) {
                    String messageWithProgress = "";
                    if (syncable.isProgressBarEnabled()) {
                        messageWithProgress = "(" + syncable.getProgress() + "/" + syncable.getTotal() + ")";
                    }

                    tvStat.setText(String.format(Objects.requireNonNull(Constant.DOWNLOADMAP.get(syncable.getStatus())), messageWithProgress));
                } else {
                    tvStat.setText(Constant.DOWNLOADMAP.get(syncable.getStatus()));
                }

                chkbx.setEnabled(!disable);
                chkbx.setOnClickListener(v -> {
                    if (!disable) {
                        downloadListItemClicked(getLayoutPosition(), position);
                    }
                });

                TextView btnRetry = convertView.findViewById(R.id.btn_retry);
                View finalConvertView = convertView;

                ProgressBar progressBar = convertView.findViewById(R.id.progress_bar_row_text_checkbox_v2);


                boolean hasFailedUrls = syncable.getFailedUrl().size() > 0 && TextUtils.equals("Forms", syncable.getTitle());


                if (hasFailedUrls) {
                    btnRetry.setText(finalConvertView.getContext().getString(R.string.retry_forms, syncable.getFailedUrl().size()));
                    btnRetry.setVisibility(View.VISIBLE);
                    btnRetry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!NetworkUtils.isNetworkConnected()) {
                                Toast.makeText(btnRetry.getContext(), btnRetry.getContext().getString(R.string.no_internet_body), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String[] failedUrls = syncable.getFailedUrl().toArray(new String[0]);
                            retryButtonClicked(project, failedUrls);
                        }
                    });
                } else {
                    btnRetry.setVisibility(View.GONE);
                }


                progressBar.setVisibility(syncable.isProgressBarEnabled() ? View.GONE : View.GONE);

                if (syncable.isProgressBarEnabled()) {

                    progressBar.setMax(syncable.getTotal());
                    progressBar.setProgress(syncable.getProgress());
                }


                return convertView;
            }
        });

    }

    public void retryButtonClicked(Project project, String[] failedUrls) {

    }

    public void downloadListItemClicked(int parentPos, int pos) {

    }
}
