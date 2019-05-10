package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bcss.collect.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
/**
 * @author: yubaraj poudel
 * @Since 2019/05/10
 *
 * Manages the sync of all the project related contents. It is the controller that allows the user to select
 * which content of the projects to download
 **/

public class SyncAdapterv3 extends RecyclerView.Adapter<SyncViewHolder> {
    List<Syncable> list;
    // this class will manage the sync list to determine which should be synced
    class Syncable{
        String title;
        boolean sync;
        /**
         *
         * @param title - title that is show in the list
         * @param sync - selector to include in the downlod or not. if no need to download {@code sync = false }
         */
        public Syncable(String title, boolean sync) {
            this.title = title;
            this.sync = sync;
        }
    }
    void createSyncableList(boolean auto) {
        list = new ArrayList<Syncable>() {{
            add(new Syncable("Regions and sites", auto));
            add(new Syncable("Forms", auto));
            add(new Syncable("Materials", auto));
        }};
    }

   public SyncAdapterv3(boolean auto) {
      createSyncableList(auto);
   }
    @NonNull
    @Override
    public SyncViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SyncViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_list_item_new, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SyncViewHolder syncViewHolder, int i) {
        Syncable syncable = list.get(i);
        syncViewHolder.primaryText.setText(syncable.title);
        syncViewHolder.checkbox.setChecked(syncable.sync);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


