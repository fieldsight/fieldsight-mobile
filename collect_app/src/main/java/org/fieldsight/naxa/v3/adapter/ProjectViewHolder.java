package org.fieldsight.naxa.v3.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.login.model.Project;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

class ProjectViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.primary_text)
    TextView primaryText;

    @BindView(R.id.sub_text)
    TextView textView;

    @BindView(R.id.chkbx_sync)
    CheckBox chkbxSync;

    @BindView(R.id.tv_synced_date)
    TextView tvSyncedDate;

    @BindView(R.id.iv_card_status)
    AppCompatImageView imageView;

    @BindView(R.id.iv_project_thumbnail)
    ImageView ivThumbnail;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener((v) -> itemClicked(getLayoutPosition()));
        chkbxSync.setOnClickListener((v -> checkBoxChanged(getLayoutPosition(), ((CheckBox) v).isChecked())));
    }

    void bindView(Project project, boolean allTrue) {
        primaryText.setText(project.getName());
        textView.setText(String.format("A PROJECT by %s", project.getOrganizationName()));
        tvSyncedDate.setText(project.getStatusMessage());
        chkbxSync.setChecked(project.isChecked());
        chkbxSync.setVisibility(allTrue ? View.VISIBLE : View.GONE);
//        imageView.setImageResource(PROJECT.isSynced() ? R.drawable.ic_action_check : android.R.drawable.stat_sys_download_done);
        Timber.i("PROJECT image = %s", project.getUrl());
        Glide.with(itemView.getContext()).load(project.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivThumbnail);
    }

    void checkBoxChanged(int index, boolean isChecked) {

    }

    void itemClicked(int index) {

    }

}
