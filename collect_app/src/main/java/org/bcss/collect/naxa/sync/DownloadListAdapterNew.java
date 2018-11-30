package org.bcss.collect.naxa.sync;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.android.utilities.DateTimeUtils;
import org.bcss.collect.android.utilities.ThemeUtils;
import org.bcss.collect.android.utilities.ToastUtils;

import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.onboarding.CheckedItem;
import org.bcss.collect.naxa.onboarding.DownloadListAdapter;
import org.bcss.collect.naxa.onboarding.DownloadableItemsDiffCallback;
import org.bcss.collect.naxa.onboarding.SyncableItem;
import org.bcss.collect.naxa.sync.SyncRepository;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolder.progressBar.setProgress(item.getSyncProgress(), true);
        } else {
            viewHolder.progressBar.setProgress(item.getSyncProgress());
        }
    }

    @Override
    public int getItemCount() {
        return syncableItems.size();
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
    }


}
