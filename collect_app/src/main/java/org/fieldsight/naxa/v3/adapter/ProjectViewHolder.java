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
    TextView primary_text;

    @BindView(R.id.sub_text)
    TextView sub_text;

    @BindView(R.id.chkbx_sync)
    CheckBox chkbx_sync;

    @BindView(R.id.tv_synced_date)
    TextView tv_synced_date;

    @BindView(R.id.iv_card_status)
    AppCompatImageView imageView;

    @BindView(R.id.iv_project_thumbnail)
    ImageView iv_thumbnail;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener((v) -> itemClicked(getLayoutPosition()));
        chkbx_sync.setOnClickListener((v -> checkBoxChanged(getLayoutPosition(), ((CheckBox) v).isChecked())));
    }

    void bindView(Project project, boolean allTrue) {
        primary_text.setText(project.getName());
        sub_text.setText(String.format("A PROJECT by %s", project.getOrganizationName()));
        tv_synced_date.setText(project.getStatusMessage());
        chkbx_sync.setChecked(project.isChecked());
        chkbx_sync.setVisibility(allTrue ? View.VISIBLE : View.GONE);
//        imageView.setImageResource(PROJECT.isSynced() ? R.drawable.ic_action_check : android.R.drawable.stat_sys_download_done);
        Timber.i("PROJECT image = %s", project.getUrl());
        Glide.with(itemView.getContext()).load(project.getUrl()).apply(RequestOptions.circleCropTransform()).into(iv_thumbnail);
    }

    void checkBoxChanged(int index, boolean isChecked) {

    }

    void itemClicked(int index) {

    }

}
