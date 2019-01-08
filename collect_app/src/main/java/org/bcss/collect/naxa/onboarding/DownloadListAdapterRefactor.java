package org.bcss.collect.naxa.onboarding;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.bcss.collect.android.R;
import org.odk.collect.android.utilities.DateTimeUtils;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;
import java.util.List;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.COMPLETED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.FAILED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.RUNNING;

public class DownloadListAdapterRefactor extends RecyclerView.Adapter<DownloadListAdapterRefactor.ViewHolder> {

    private final SyncRepository syncRepository;
    private ArrayList<SyncableItem> syncableItems;
    private int selectedItemCount = 0;

    DownloadListAdapterRefactor(ArrayList<SyncableItem> syncableItems) {
        this.syncableItems = syncableItems;
        syncRepository = SyncRepository.getInstance();
    }

    public ArrayList<SyncableItem> getList() {
        return syncableItems;
    }

    public void updateList(List<SyncableItem> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DownloadableItemsDiffCallback(newList, syncableItems));
        syncableItems.clear();
        syncableItems.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public DownloadListAdapterRefactor.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View rootLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_item, null);

        return new DownloadListAdapterRefactor.ViewHolder(rootLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final DownloadListAdapterRefactor.ViewHolder viewHolder, int position) {
        SyncableItem item = this.syncableItems.get(viewHolder.getAdapterPosition());



    }



    @Override
    public int getItemCount() {
        return syncableItems.size();
    }

    public int getSelectedItemsCount() {
        return selectedItemCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);


        }


    }


}
