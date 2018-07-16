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

    private ArrayList<DownloadableItem> downloadableItems;
    private onDownLoadItemClick listener;

    DownloadListAdapter(ArrayList<DownloadableItem> downloadableItems) {
        this.downloadableItems = downloadableItems;
    }


    public ArrayList<DownloadableItem> getList() {
        return downloadableItems;
    }

    public void setOnClickListener(onDownLoadItemClick listener) {
        this.listener = listener;
    }

    public void updateList(List<DownloadableItem> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DownloadableItemsDiffCallback(newList, downloadableItems));
        downloadableItems.clear();
        downloadableItems.addAll(newList);
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
        DownloadableItem downloadableItem = downloadableItems.get(viewHolder.getAdapterPosition());
        viewHolder.checkedItem.setText(downloadableItem.getTitle(), downloadableItem.getDetail());
    }

    @Override
    public int getItemCount() {
        return downloadableItems.size();
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
                    listener.onItemTap(downloadableItems.get(getAdapterPosition()));
                    checkedItem.toggle();
                }
            });
        }
    }


    public interface onDownLoadItemClick {
        void onItemTap(DownloadableItem downloadableItem);
    }
}
