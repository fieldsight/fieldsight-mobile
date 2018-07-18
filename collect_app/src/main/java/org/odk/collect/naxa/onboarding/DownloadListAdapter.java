package org.odk.collect.naxa.onboarding;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.odk.collect.android.R;
import org.odk.collect.naxa.generalforms.DisplayGeneralFormsAdapter;

import java.util.ArrayList;
import java.util.List;

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ViewHolder> {

    private ArrayList<SyncableItems> syncableItems;
    private onDownLoadItemClick listener;

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
        final DisplayGeneralFormsAdapter.ViewHolder viewHolder = new DisplayGeneralFormsAdapter.ViewHolder(rootLayout);
        return new DownloadListAdapter.ViewHolder(rootLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final DownloadListAdapter.ViewHolder viewHolder, int position) {
        SyncableItems syncableItems = this.syncableItems.get(viewHolder.getAdapterPosition());
        viewHolder.checkedItem.setText(syncableItems.getTitle(), syncableItems.getDetail());
    }

    @Override
    public int getItemCount() {
        return syncableItems.size();
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
                    listener.onItemTap(syncableItems.get(getAdapterPosition()));
                    checkedItem.toggle();
                }
            });
        }
    }


    public interface onDownLoadItemClick {
        void onItemTap(SyncableItems syncableItems);
    }
}
