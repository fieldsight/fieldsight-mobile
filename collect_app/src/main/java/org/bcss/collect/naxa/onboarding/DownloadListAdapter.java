package org.bcss.collect.naxa.onboarding;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;
import java.util.List;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.COMPLETED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.FAILED;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;
import static org.bcss.collect.naxa.common.Constant.DownloadStatus.RUNNING;

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ViewHolder> {

    private final SyncRepository syncRepository;
    private ArrayList<SyncableItems> syncableItems;
    private int selectedItemCount = 0;

    public DownloadListAdapter(ArrayList<SyncableItems> syncableItems) {
        this.syncableItems = syncableItems;
        syncRepository = SyncRepository.getInstance();
    }

    public ArrayList<SyncableItems> getList() {
        return syncableItems;
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
        SyncableItems item = this.syncableItems.get(position);
        CheckedItem checkedItem = viewHolder.checkedItem;
        checkedItem.setText(item.getTitle(), item.getDetail());

        if (item.isChecked()) {
            checkedItem.setChecked(true);
        } else {
            checkedItem.setChecked(false);
        }

        if (item.isProgressStatus()) {
            checkedItem.showProgress();
        } else {
            checkedItem.hideProgress();
        }

        switch (item.getDownloadingStatus()) {
            case PENDING:
                checkedItem.showFailureMessage("Not synced yet");
                break;
            case COMPLETED:
                checkedItem.showSucessMessage("Last synced at " + item.getLastSyncDateTime());
                break;
            case FAILED:
                checkedItem.showFailureMessage("Last failed at " + item.getLastSyncDateTime());
                break;
            case RUNNING:
                break;
        }

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
                SyncableItems syncableItem = syncableItems.get(getAdapterPosition());
                if (checkedItem.isChecked()) {
                    syncRepository.setChecked(syncableItem.getUid(), false);
                } else {
                    syncRepository.setChecked(syncableItem.getUid(), true);
                }
                manipulateCheckedUI(syncableItem);
            });
        }

        private void manipulateCheckedUI(SyncableItems syncableItem) {
            if (syncableItem.getIsSelected()) {
                selectedItemCount++;
            } else {
                selectedItemCount--;
            }
        }
    }


}
