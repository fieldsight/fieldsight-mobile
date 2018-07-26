package org.odk.collect.naxa.onboarding;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.odk.collect.android.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ViewHolder> {

    private ArrayList<SyncableItems> syncableItems;
    private onDownLoadItemClick listener;
    private int selectedItemCount = 0;

    DownloadListAdapter(ArrayList<SyncableItems> syncableItems) {
        this.syncableItems = syncableItems;
    }


    public ArrayList<SyncableItems> getList() {
        return syncableItems;
    }

    public void setOnClickListener(onDownLoadItemClick listener) {
        this.listener = listener;
    }

    public void updateList(List<SyncableItems> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DownloadableItemsDiffCallback(newList, syncableItems));
        syncableItems.clear();
        syncableItems.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public DownloadListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View rootLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_item, null);
        return new DownloadListAdapter.ViewHolder(rootLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final DownloadListAdapter.ViewHolder viewHolder, int position) {
        SyncableItems syncableItems = this.syncableItems.get(viewHolder.getAdapterPosition());
        viewHolder.checkedItem.setText(syncableItems.getTitle(), syncableItems.getDetail());
        viewHolder.checkedItem.setChecked(syncableItems.getIsSelected());
        viewHolder.checkedItem.showSucessMessage("Last sync 2 min ago");
    }

    @Override
    public int getItemCount() {
        return syncableItems.size();
    }

    public int getSelectedItemsCount() {

        return selectedItemCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rootLayout;
        CheckedItem checkedItem;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rootLayout = itemLayoutView.findViewById(R.id.layout_download_list_item);
            checkedItem = itemLayoutView.findViewById(R.id.checked_item);

            rootLayout.setOnClickListener(v -> {
                if (listener != null) {
                    SyncableItems syncableItem = syncableItems.get(getAdapterPosition());
                    syncableItem.toggleSelected();

                    manipulateCheckedUI(syncableItem);


                }
            });
        }

        private void manipulateCheckedUI(SyncableItems syncableItem) {
            if (syncableItem.getIsSelected()) {
                selectedItemCount++;
            } else {
                selectedItemCount--;
            }

            listener.onItemTap(syncableItem);
            checkedItem.setChecked(syncableItem.getIsSelected());

        }
    }


    public interface onDownLoadItemClick {
        void onItemTap(SyncableItems syncableItems);
    }
}
