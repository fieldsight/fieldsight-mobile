package org.bcss.collect.naxa.sync;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.odk.collect.android.utilities.DateTimeUtils;
import org.odk.collect.android.utilities.ThemeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.COMPLETED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.DISABLED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.FAILED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.RUNNING;

public class DownloadContentViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.image_background)
    LinearLayout imageBackground;
    @BindView(R.id.display_name)
    TextView displayName;
    @BindView(R.id.display_subtext)
    TextView displaySubtext;
    @BindView(R.id.checkbox)
    CheckBox checkbox;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.status_icon)
    ImageView statusIcon;
    @BindView(R.id.close_box)
    ImageView closeButton;


    @BindView(R.id.update_info)
    TextView tvUpdatedInfo;

    @BindView(R.id.tv_out_of_sync)
    TextView tvOutOfSync;

    @BindView(R.id.download_list_item_card)
    CardView cardView;


    DownloadContentViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindView(DownloadableItem item) {
        displayName.setText(item.getTitle());
        displaySubtext.setText(item.getDetail());
        checkbox.setChecked(item.isChecked());
        progressBar.setMax(item.getSyncTotal());
        tvOutOfSync.setVisibility(item.isOutOfSync() ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(View.VISIBLE);

    }

    void enableOrDisableCard(boolean enabled) {
//        viewHolder.statusIcon.setEnabled(enabled);
//        viewHolder.displayName.setEnabled(enabled);
//        viewHolder.displaySubtext.setEnabled(enabled);
//        viewHolder.cardView.setEnabled(enabled);
//        viewHolder.checkbox.setEnabled(enabled);
        itemView.setEnabled(enabled);
        GradientDrawable shapeDrawable = (GradientDrawable) imageBackground.getBackground();
        ThemeUtils themeUtils = new ThemeUtils(itemView.getContext());

        int color = enabled ? themeUtils.getAccentColor() : ContextCompat.getColor(itemView.getContext(), R.color.gray600);
        shapeDrawable.setColor(color);
    }

    private void showProgress(boolean show){
        progressBar.setIndeterminate(show);
    }

    void setStatus(DownloadableItem item) {
        switch (item.getDownloadingStatus()) {
            case PENDING:
                statusIcon.setImageResource(R.drawable.ic_access_time_black_24dp);
                showProgress(false);
                tvUpdatedInfo.setVisibility(View.GONE);
                break;
            case RUNNING:
                showProgress(true);
                statusIcon.setImageResource(R.drawable.ic_refresh_white_2);
                tvUpdatedInfo.setVisibility(View.GONE);
                //if(item.getUid() != Constant.DownloadUID.ALL_FORMS){
                if (item.isDeterminate()) {
                    progressBar.setIndeterminate(false);
                    progressBar.setMax(item.getSyncTotal());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(item.getSyncProgress(), true);
                    } else {
                        progressBar.setProgress(item.getSyncProgress());
                    }
                } else {
                    progressBar.setProgress(0);
                    progressBar.setIndeterminate(true);

                }

                break;
            case FAILED:
                showProgress(false);
                String formattedMessage = String.format("Failed %s \nReason: %s", DateTimeUtils.getRelativeTime(item.getLastSyncDateTime(), false), item.getErrorMessage());
                statusIcon.setImageResource(R.drawable.exclamation);
                tvUpdatedInfo.setVisibility(View.VISIBLE);
                tvUpdatedInfo.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                tvUpdatedInfo.setText(formattedMessage);

                break;
            case COMPLETED:
                showProgress(false);
                String message = String.format("Synced %s", DateTimeUtils.getRelativeTime(item.getLastSyncDateTime(), false));
                statusIcon.setImageResource(R.drawable.check);
                tvUpdatedInfo.setVisibility(View.VISIBLE);
                tvUpdatedInfo.setText(message);
                tvUpdatedInfo.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                break;
            case DISABLED:
                showProgress(false);
                enableOrDisableCard(false);
                tvUpdatedInfo.setVisibility(View.GONE);
                statusIcon.setImageResource(R.drawable.ic_refresh_white_2);
                break;

        }
    }

    void viewItemClicked(int pos) {
//        TODO: do what to do afet viewitem is clicked;

    }

    void onCancelled(int pos) {
//        TODO: what to do in on cancelled
    }

    @OnClick(R.id.download_list_item_card)
    void onCardClick() {
//        DownloadableItem downloadableItem = syncableItems.get(getAdapterPosition());
//        if (onItemClickListener != null) {
//            onItemClickListener.onClickPrimaryAction(downloadableItem);
//        }
        viewItemClicked(getLayoutPosition());

    }

    @OnClick(R.id.btn_cancel_sync)
    void onCardCancelBtnClick() {
//        DownloadableItem downloadableItem = syncableItems.get(getAdapterPosition());
//        if (onItemClickListener != null) {
//            onItemClickListener.onClickSecondaryAction(downloadableItem);
//        }
        onCancelled(getLayoutPosition());
    }
}