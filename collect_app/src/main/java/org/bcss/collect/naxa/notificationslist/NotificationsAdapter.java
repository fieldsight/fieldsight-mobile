package org.bcss.collect.naxa.notificationslist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;

import java.util.ArrayList;
import java.util.List;

import static org.bcss.collect.naxa.common.Constant.NotificationEvent.SINGLE_STAGE_DEPLOYED;
import static org.bcss.collect.naxa.common.Constant.NotificationType.ASSIGNED_SITE;
import static org.bcss.collect.naxa.common.Constant.NotificationType.FORM_ALTERED_PROJECT;
import static org.bcss.collect.naxa.common.Constant.NotificationType.PROJECT_FORM;
import static org.bcss.collect.naxa.common.Constant.NotificationType.SITE_FORM;
import static org.bcss.collect.naxa.common.Constant.NotificationType.FORM_ALTERED_SITE;
import static org.bcss.collect.naxa.common.Constant.NotificationType.UNASSIGNED_SITE;


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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, null);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FieldSightNotification fieldSightNotification = FieldSightNotifications.get(viewHolder.getAdapterPosition());
        Context context = viewHolder.rootLayout.getContext().getApplicationContext();
        viewHolder.ivNotificationIcon.setImageDrawable(getNotificationImage(fieldSightNotification.getNotificationType()));
        Pair<String, String> titleContent = FieldSightNotificationLocalSource.getInstance().generateNotificationContent(fieldSightNotification);
        String title = titleContent.first;
        String content = titleContent.second;
        viewHolder.tvTitle.setText(title);
        viewHolder.tvDesc.setText(content);
    }

    private Drawable getNotificationImage(String notificationType) {
        Drawable icon = null;
        Context context = Collect.getInstance().getApplicationContext();
        switch (notificationType) {
            case ASSIGNED_SITE:
            case UNASSIGNED_SITE:
                icon = ContextCompat.getDrawable(context, R.drawable.ic_people);
                break;
            case SITE_FORM:
            case PROJECT_FORM:
            case FORM_ALTERED_SITE:
            case SINGLE_STAGE_DEPLOYED:
            case FORM_ALTERED_PROJECT:

                icon = ContextCompat.getDrawable(context, R.drawable.ic_form_white);
//                icon = ContextCompat.getDrawable(context, R.drawable.ic_format_list_bulleted);
                break;
            default:
                icon = ContextCompat.getDrawable(context, R.drawable.ic_notification_icon);
                break;
        }

        return icon;
    }


    @Override
    public int getItemCount() {
        return FieldSightNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle, tvDesc;
        RelativeLayout rootLayout;
        ImageView ivNotificationIcon, ivCircle;

        public ViewHolder(View view) {
            super(view);

            tvTitle = view.findViewById(R.id.tv_list_item_title);
            tvDesc = view.findViewById(R.id.tv_list_item_desc);
            ivNotificationIcon = view.findViewById(R.id.iv_notification_icon);
            rootLayout = view.findViewById(R.id.card_view_list_item_title_desc);
            rootLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FieldSightNotification FieldSightNotification = FieldSightNotifications.get(getAdapterPosition());


            switch (v.getId()) {
                case R.id.card_view_list_item_title_desc:
                    listener.onClickPrimaryAction(FieldSightNotification);
                    break;

            }
        }
    }

}
