package org.bcss.collect.naxa.sync;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

class ContentDownloadAdapter extends RecyclerView.Adapter<DownloadContentViewHolder> {
    private final ArrayList<DownloadableItem> syncableItems;

    ContentDownloadAdapter(ArrayList<DownloadableItem> syncableItems) {
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
    public DownloadContentViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View rootLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_item_new, parent, false);
        return new DownloadContentViewHolder(rootLayout) {
            @Override
            void onCancelled(int pos) {
                super.onCancelled(pos);
                onItemClickListener.onClickPrimaryAction(syncableItems.get(pos));
            }

            @Override
            void viewItemClicked(int pos) {
                super.viewItemClicked(pos);
                onItemClickListener.onClickSecondaryAction(syncableItems.get(pos));
            }
        };
    }




    @Override
    public void onBindViewHolder(@NonNull DownloadContentViewHolder viewHolder, int pos) {
        DownloadableItem item = syncableItems.get(pos);
        viewHolder.bindView(item);
        viewHolder.enableOrDisableCard(true);
        viewHolder.setStatus(item);
    }

    @Override
    public int getItemCount() {
        return this.syncableItems.size();
    }


    public ArrayList<DownloadableItem> getAll() {
        return this.syncableItems;
    }
}
