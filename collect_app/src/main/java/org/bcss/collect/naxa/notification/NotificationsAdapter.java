package org.bcss.collect.naxa.notification;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private ArrayList<FieldSightNotification> FieldSightNotifications;
    private OnItemClickListener<FieldSightNotification> listener;


    public NotificationsAdapter(ArrayList<FieldSightNotification> totalList, OnItemClickListener<FieldSightNotification> listener) {
        this.FieldSightNotifications = totalList;
        this.listener = listener;
    }


    public void updateList(List<FieldSightNotification> newList) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new FieldSightNotificationsDiffCallback(newList, FieldSightNotifications));
        FieldSightNotifications.clear();
        FieldSightNotifications.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_title_desc, null);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FieldSightNotification FieldSightNotification = FieldSightNotifications.get(viewHolder.getAdapterPosition());
        viewHolder.tvTitle.setText(FieldSightNotification.getNotificationType());
        viewHolder.tvDesc.setText(FieldSightNotification.getId());
    }


    @Override
    public int getItemCount() {
        return FieldSightNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle, tvDesc;
        RelativeLayout rootLayout;

        public ViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tv_list_item_title);
            tvDesc = view.findViewById(R.id.tv_list_item_desc);
            rootLayout = view.findViewById(R.id.card_view_list_item_title_desc);
            rootLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FieldSightNotification FieldSightNotification = FieldSightNotifications.get(getAdapterPosition());

            switch (v.getId()) {
                case R.id.rl_form_list_item:
                    listener.onClickPrimaryAction(FieldSightNotification);
                    break;

            }
        }
    }

}
