package org.fieldsight.naxa.v3.adapter;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import timber.log.Timber;

import static org.fieldsight.naxa.common.ViewUtils.loadImageWithFallback;


class ProjectViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.primary_text)
    TextView primaryText;

    @BindView(R.id.sub_text)
    TextView textView;

    @BindView(R.id.iv_sync)
    ImageView ivSync;

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

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener((v) -> itemClicked(getLayoutPosition()));
//        chkbxSync.setOnClickListener((v -> checkBoxChanged(getLayoutPosition(), ((CheckBox) v).isChecked())));
    }

    void bindView(Project project, boolean allTrue) {
        primaryText.setText(project.getName());
        textView.setText(String.format("A project by %s", project.getOrganizationName()));
        tvSyncedDate.setText(project.getStatusMessage());
//        chkbxSync.setChecked(project.isChecked());
//        chkbxSync.setVisibility(allTrue ? View.VISIBLE : View.GONE);
//        imageView.setImageResource(PROJECT.isSynced() ? R.drawable.ic_action_check : android.R.drawable.stat_sys_download_done);
        tvRegions.setText(String.format(Locale.ENGLISH, "%d", project.getTotalRegions()));
        tvUsers.setText(String.format(Locale.ENGLISH, "%d", project.getTotalUsers()));
        tvSubmissions.setText(String.format(Locale.ENGLISH, "%d", project.getTotalSubmissions()));
        tvSites.setText(String.format(Locale.ENGLISH, "%d", project.getTotalSites()));

        if (!TextUtils.isEmpty(project.getUrl())) {
            loadImageWithFallback(itemView.getContext(), project.getUrl()).into(ivThumbnail);
        } else {
           ivThumbnail.setImageResource(R.drawable.fieldsight_logo);
        }

        toggleSelectedColor(project.isChecked());
    }

    private void toggleSelectedColor(boolean checked) {
        Resources resources = itemView.getContext().getResources();
        itemView.setBackgroundColor(checked ? resources.getColor(R.color.new_design_blue) : resources.getColor(android.R.color.white));
        primaryText.setTextColor(checked ? resources.getColor(android.R.color.white) : resources.getColor(R.color.text_primary));
        textView.setTextColor(checked ? resources.getColor(android.R.color.white) : resources.getColor(R.color.new_design_blue));
        tvSites.setTextColor(checked ? resources.getColor(android.R.color.white) : resources.getColor(R.color.text_primary));
        tvSubmissions.setTextColor(checked ? resources.getColor(android.R.color.white) : resources.getColor(R.color.text_primary));
        tvUsers.setTextColor(checked ? resources.getColor(android.R.color.white) : resources.getColor(R.color.text_primary));
        tvRegions.setTextColor(checked ? resources.getColor(android.R.color.white) : resources.getColor(R.color.text_primary));
        ivSync.setVisibility(checked ? View.VISIBLE : View.GONE);
    }


    void checkBoxChanged(int index, boolean isChecked) {

    }

    void itemClicked(int index) {

    }

}
