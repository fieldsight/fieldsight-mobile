package org.bcss.collect.naxa.sync;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.odk.collect.android.utilities.DateTimeUtils;
import org.odk.collect.android.utilities.ThemeUtils;
import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.common.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.COMPLETED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.FAILED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.RUNNING;

class DownloadListAdapterNew extends RecyclerView.Adapter<DownloadListAdapterNew.ViewHolder> {
    private final ArrayList<Sync> syncableItems;

    DownloadListAdapterNew(ArrayList<Sync> syncableItems) {
        this.syncableItems = syncableItems;
    }

    OnItemClickListener<Sync> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<Sync> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateList(List<Sync> newList) {
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GradientDrawable shapeDrawable = (GradientDrawable) viewHolder.imageBackground.getBackground();
        Context context = viewHolder.progressBar.getContext();

        Sync item = syncableItems.get(viewHolder.getAdapterPosition());

        shapeDrawable.setColor(new ThemeUtils(context).getAccentColor());


        viewHolder.displayName.setText(item.getTitle());
        viewHolder.displaySubtext.setText(item.getDetail());

        viewHolder.checkbox.setChecked(item.isChecked());
        viewHolder.progressBar.setMax(item.getSyncTotal());

        viewHolder.tvOutOfSync.setVisibility(item.isOutOfSync() ? View.VISIBLE : View.GONE);
        switch (item.getDownloadingStatus()) {
            case PENDING:
                viewHolder.btnCancelSync.setVisibility(View.GONE);
                viewHolder.statusIcon.setImageResource(R.drawable.ic_access_time_black_24dp);
                viewHolder.progressBar.setIndeterminate(false);
                viewHolder.textView.setVisibility(View.GONE);
                break;
            case RUNNING:
                viewHolder.btnCancelSync.setVisibility(View.GONE);
                viewHolder.statusIcon.setImageResource(R.drawable.ic_refresh_white_2);
                viewHolder.textView.setVisibility(View.GONE);
//                if(item.getUid() != Constant.DownloadUID.ALL_FORMS){
                if (true) {
                    viewHolder.progressBar.setIndeterminate(true);
                } else {
                    viewHolder.progressBar.setMax(item.getSyncTotal());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        viewHolder.progressBar.setProgress(item.getSyncProgress(), true);
                    } else {
                        viewHolder.progressBar.setProgress(item.getSyncProgress());
                    }
                }

                break;
            case FAILED:
                viewHolder.btnCancelSync.setVisibility(View.GONE);
                viewHolder.statusIcon.setImageResource(R.drawable.exclamation);
                if (item.getUid() != Constant.DownloadUID.ODK_FORMS) {
                    viewHolder.progressBar.setIndeterminate(false);
                }

                viewHolder.textView.setVisibility(View.VISIBLE);

                String formattedMessage;

                formattedMessage = String.format("Failed %s \nReason: %s", DateTimeUtils.getRelativeTime(item.getLastSyncDateTime(), false), item.getErrorMessage());
                viewHolder.textView.setTextColor(ContextCompat.getColor(context, R.color.red));


                viewHolder.textView.setText(formattedMessage);
                break;
            case COMPLETED:
                viewHolder.btnCancelSync.setVisibility(View.GONE);
                viewHolder.statusIcon.setImageResource(R.drawable.check);
                if (item.getUid() != Constant.DownloadUID.ODK_FORMS) {
                    viewHolder.progressBar.setIndeterminate(false);
                }
                viewHolder.textView.setVisibility(View.VISIBLE);
                String message = String.format("Synced %s", DateTimeUtils.getRelativeTime(item.getLastSyncDateTime(), false));
                viewHolder.textView.setText(message);
                viewHolder.textView.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return syncableItems.size();
    }


    public ArrayList<Sync> getAll() {
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
        @BindView(R.id.btn_cancel_sync)
        Button btnCancelSync;

        @BindView(R.id.update_info)
        TextView textView;

        @BindView(R.id.tv_out_of_sync)
        TextView tvOutOfSync;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


        @OnClick(R.id.download_list_item_card)
        void onCardClick() {
            Sync sync = syncableItems.get(getAdapterPosition());
            if (onItemClickListener != null) {
                onItemClickListener.onClickPrimaryAction(sync);
            }
        }

        @OnClick(R.id.btn_cancel_sync)
        void onCardCancelBtnClick() {
            Sync sync = syncableItems.get(getAdapterPosition());
            if (onItemClickListener != null) {
                onItemClickListener.onClickSecondaryAction(sync);
            }
        }
    }


}
