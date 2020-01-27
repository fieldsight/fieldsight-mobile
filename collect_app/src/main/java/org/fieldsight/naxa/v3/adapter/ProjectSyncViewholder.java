package org.fieldsight.naxa.v3.adapter;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.login.model.Project;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static org.fieldsight.naxa.common.ViewUtils.loadImageWithFallback;


public class ProjectSyncViewholder extends RecyclerView.ViewHolder {
    @BindView(R.id.primary_text)
    TextView primaryText;

    @BindView(R.id.sub_text)
    TextView textView;

    @BindView(R.id.tv_synced_date)
    TextView tvSyncedDate;

    @BindView(R.id.iv_card_status)
    AppCompatImageView imageView;

    @BindView(R.id.iv_project_thumbnail)
    ImageView ivThumbnail;

    @BindView(R.id.tv_regions)
    TextView tvRegions;

    @BindView(R.id.tv_submissions)
    TextView tvSubmissions;

    @BindView(R.id.tv_sites)
    TextView tvSites;

    @BindView(R.id.tv_users)
    TextView tvUsers;

    @BindView(R.id.tv_label_regions)
    TextView regionLabel;

    @BindView(R.id.tv_label_submission)
    TextView submissionLabel;

    @BindView(R.id.tv_label_sites)
    TextView sitesLabel;

    @BindView(R.id.tv_label_users)
    TextView userLabel;

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;

    @BindView(R.id.ll_downloading_section)
    LinearLayout downloadingSection;

    public ProjectSyncViewholder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindView(Project project, boolean allTrue) {
        primaryText.setText(project.getName());
        textView.setText(String.format("%s", project.getOrganizationName()));
        tvSyncedDate.setText(project.getStatusMessage());
        Timber.i("project image = %s", project.getUrl());
        Glide.with(itemView.getContext()).load(project.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivThumbnail);
        tvRegions.setText(String.format(Locale.ENGLISH, "%d", project.getTotalRegions()));
        tvUsers.setText(String.format(Locale.ENGLISH, "%d", project.getTotalUsers()));
        tvSubmissions.setText(String.format(Locale.ENGLISH, "%d", project.getTotalSubmissions()));
        tvSites.setText(String.format(Locale.ENGLISH, "%d", project.getTotalSites()));

        if (!TextUtils.isEmpty(project.getUrl())) {
            loadImageWithFallback(itemView.getContext(), project.getUrl()).into(ivThumbnail);
        } else {
           ivThumbnail.setImageResource(R.drawable.fieldsight_logo);
        }

        downloadingSection.setVisibility(project.isSynced() ? View.GONE : View.VISIBLE);
    }
}
