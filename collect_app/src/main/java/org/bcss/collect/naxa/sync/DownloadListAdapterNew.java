package org.bcss.collect.naxa.sync;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.OnItemClickListener;
import org.odk.collect.android.utilities.DateTimeUtils;
import org.odk.collect.android.utilities.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.COMPLETED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.DISABLED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.FAILED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.RUNNING;

class DownloadListAdapterNew extends RecyclerView.Adapter<DownloadListAdapterNew.ViewHolder> {
    private final ArrayList<DownloadableItem> syncableItems;

    DownloadListAdapterNew(ArrayList<DownloadableItem> syncableItems) {
        this.syncableItems = syncableItems;
    }

    private OnItemClickListener<DownloadableItem> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<DownloadableItem> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateList(List<DownloadableItem> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DownloadableItemsDiffCallbackNew(newList, syncableItems));
        syncableItems.clear();
        syncableItems.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public DownloadListAdapterNew.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View rootLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_item_new, null);
        return new DownloadListAdapterNew.ViewHolder(rootLayout);
    }

    private void enableOrDisableCard(ViewHolder viewHolder, boolean enabled) {
        viewHolder.statusIcon.setEnabled(enabled);
        viewHolder.displayName.setEnabled(enabled);
        viewHolder.displaySubtext.setEnabled(enabled);
        viewHolder.cardView.setEnabled(enabled);
        viewHolder.checkbox.setEnabled(enabled);

        GradientDrawable shapeDrawable = (GradientDrawable) viewHolder.imageBackground.getBackground();
        Context context = viewHolder.progressBar.getContext();
        ThemeUtils themeUtils = new ThemeUtils(context);

        int color = enabled ? themeUtils.getAccentColor() : ContextCompat.getColor(context, R.color.gray600);
        shapeDrawable.setColor(color);


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DownloadableItem item = syncableItems.get(viewHolder.getAdapterPosition());
        Context context = viewHolder.progressBar.getContext();

        viewHolder.displayName.setText(item.getTitle());
        viewHolder.displaySubtext.setText(item.getDetail());
        viewHolder.checkbox.setChecked(item.isChecked());
        viewHolder.progressBar.setMax(item.getSyncTotal());
        viewHolder.tvOutOfSync.setVisibility(item.isOutOfSync() ? View.VISIBLE : View.GONE);
        viewHolder.progressBar.setVisibility(View.GONE);
        enableOrDisableCard(viewHolder, true);
        viewHolder.progressBar.setIndeterminate(true);

        switch (item.getDownloadingStatus()) {
            case PENDING:

                viewHolder.statusIcon.setImageResource(R.drawable.ic_access_time_black_24dp);
                viewHolder.progressBar.setIndeterminate(false);
                viewHolder.tvUpdatedInfo.setVisibility(View.GONE);
                break;
            case RUNNING:
                viewHolder.progressBar.setVisibility(View.VISIBLE);

                viewHolder.statusIcon.setImageResource(R.drawable.ic_refresh_white_2);
                viewHolder.tvUpdatedInfo.setVisibility(View.GONE);
                //if(item.getUid() != Constant.DownloadUID.ALL_FORMS){
                if(item.isDeterminate()){
                    viewHolder.progressBar.setIndeterminate(false);
                    viewHolder.progressBar.setMax(item.getSyncTotal());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        viewHolder.progressBar.setProgress(item.getSyncProgress(), true);
                    } else {
                        viewHolder.progressBar.setProgress(item.getSyncProgress());
                    }
                }else {
                    viewHolder.progressBar.setProgress(0);
                    viewHolder.progressBar.setIndeterminate(true);

                }

                break;
            case FAILED:
                String formattedMessage = String.format("Failed %s \nReason: %s", DateTimeUtils.getRelativeTime(item.getLastSyncDateTime(), false), item.getErrorMessage());
                viewHolder.statusIcon.setImageResource(R.drawable.exclamation);
                viewHolder.tvUpdatedInfo.setVisibility(View.VISIBLE);
                viewHolder.tvUpdatedInfo.setTextColor(ContextCompat.getColor(context, R.color.red));
                viewHolder.tvUpdatedInfo.setText(formattedMessage);

                break;
            case COMPLETED:
                String message = String.format("Synced %s", DateTimeUtils.getRelativeTime(item.getLastSyncDateTime(), false));

                viewHolder.statusIcon.setImageResource(R.drawable.check);
                viewHolder.tvUpdatedInfo.setVisibility(View.VISIBLE);
                viewHolder.tvUpdatedInfo.setText(message);
                viewHolder.tvUpdatedInfo.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case DISABLED:
                enableOrDisableCard(viewHolder, false);
                viewHolder.tvUpdatedInfo.setVisibility(View.GONE);
                viewHolder.statusIcon.setImageResource(R.drawable.ic_refresh_white_2);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return syncableItems.size();
    }


    public ArrayList<DownloadableItem> getAll() {
        return syncableItems;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

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


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        @OnClick(R.id.download_list_item_card)
        void onCardClick() {
            DownloadableItem downloadableItem = syncableItems.get(getAdapterPosition());
            if (onItemClickListener != null) {
                onItemClickListener.onClickPrimaryAction(downloadableItem);
            }
        }

        @OnClick(R.id.btn_cancel_sync)
        void onCardCancelBtnClick() {
            DownloadableItem downloadableItem = syncableItems.get(getAdapterPosition());
            if (onItemClickListener != null) {
                onItemClickListener.onClickSecondaryAction(downloadableItem);
            }
        }
    }


}
