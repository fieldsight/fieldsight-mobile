package org.fieldsight.naxa.notificationslist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.OnItemClickListener;
import org.fieldsight.naxa.data.FieldSightNotification;
import org.fieldsight.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.odk.collect.android.utilities.DateTimeUtils;

import java.util.List;

import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.NotificationEvent.ALL_STAGE_DEPLOYED;
import static org.fieldsight.naxa.common.Constant.NotificationEvent.SINGLE_STAGED_FORM_DEPLOYED;
import static org.fieldsight.naxa.common.Constant.NotificationEvent.SINGLE_STAGE_DEPLOYED;
import static org.fieldsight.naxa.common.Constant.NotificationType.ASSIGNED_SITE;
import static org.fieldsight.naxa.common.Constant.NotificationType.DAILY_REMINDER;
import static org.fieldsight.naxa.common.Constant.NotificationType.FORM_ALTERED_PROJECT;
import static org.fieldsight.naxa.common.Constant.NotificationType.FORM_ALTERED_SITE;
import static org.fieldsight.naxa.common.Constant.NotificationType.MONTHLY_REMINDER;
import static org.fieldsight.naxa.common.Constant.NotificationType.PROJECT_FORM;
import static org.fieldsight.naxa.common.Constant.NotificationType.SITE_FORM;
import static org.fieldsight.naxa.common.Constant.NotificationType.UNASSIGNED_SITE;
import static org.fieldsight.naxa.common.Constant.NotificationType.WEEKLY_REMINDER;


public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FieldSightNotification> fieldSightNotifications;
    private OnItemClickListener<FieldSightNotification> listener;
    final int TYPE_LOADING = 1, TYPE_ITEM = 2;

    NotificationsAdapter(List<FieldSightNotification> totalList, OnItemClickListener<FieldSightNotification> listener) {
        this.fieldSightNotifications = totalList;
        this.listener = listener;
    }


    public void updateList(List<FieldSightNotification> newList) {
//        if (fieldSightNotifications.size() == 0) {
//            fieldSightNotifications.addAll(newList);
//            Timber.i("Notification Adapter, first time");
//
//        } else if (Integer.parseInt(DateTimeUtils.tsToSec8601(newList.get(0).getReceivedDateTime())) >
//                Integer.parseInt(DateTimeUtils.tsToSec8601(fieldSightNotifications.get(0).getReceivedDateTime()))) {
//            newList.addAll(fieldSightNotifications);
//            fieldSightNotifications = newList;
//            Timber.i("Notification Adapter, new notification");
//        } else {
        fieldSightNotifications = newList;
        Timber.i("Notification Adapter, older notification");

//        }
        notifyDataSetChanged();

    }

    public FieldSightNotification getMostRecentNotification() {
        if (fieldSightNotifications.size() > 0) return fieldSightNotifications.get(0);
        else return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (fieldSightNotifications.get(position) == null) {
            return TYPE_LOADING;

        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, null);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder mviewHolder = (ViewHolder) viewHolder;
            FieldSightNotification fieldSightNotification = fieldSightNotifications.get(viewHolder.getAdapterPosition());
            try {
                mviewHolder.ivNotificationIcon.setImageDrawable(getNotificationImage(fieldSightNotification.getNotificationType()));
                Pair<String, String> titleContent = FieldSightNotificationLocalSource.getInstance().generateNotificationContent(fieldSightNotification);
                String title = titleContent.first;
                String content = titleContent.second;
                mviewHolder.tvTitle.setText(title);
                mviewHolder.tvDesc.setText(content);
                mviewHolder.tvDate.setText(DateTimeUtils.getRelativeTime(fieldSightNotification.getReceivedDateTime(), true));
                mviewHolder.itemView.setOnClickListener((v) -> listener.onClickPrimaryAction(fieldSightNotification));
            } catch (NullPointerException e) {
                Timber.e("Failed loading notification onBinViewHolder() reason: %s", e.getMessage());
            }
        } else {

        }
    }

    private Drawable getNotificationImage(String notificationType) {
        Drawable icon;
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
            case ALL_STAGE_DEPLOYED:
            case SINGLE_STAGED_FORM_DEPLOYED:
                icon = ContextCompat.getDrawable(context, R.drawable.ic_form_white);
//                icon = ContextCompat.getDrawable(context, R.drawable.ic_format_list_bulleted);
                break;
            case DAILY_REMINDER:
            case WEEKLY_REMINDER:
            case MONTHLY_REMINDER:
                icon = ContextCompat.getDrawable(context, R.drawable.ic_alarms_24dp);
                break;
            default:
                icon = ContextCompat.getDrawable(context, R.drawable.ic_notification_icon);
                break;
        }

        return icon;
    }


    @Override
    public int getItemCount() {
        return fieldSightNotifications.size();
    }

    public FieldSightNotification getLastNotification() {
        return fieldSightNotifications.size() > 0 ? fieldSightNotifications.get(fieldSightNotifications.size() - 1) : null;
    }

    public void removeLoader() {
        if (fieldSightNotifications.size() > 0 && fieldSightNotifications.get(getItemCount() - 1) == null) {
            fieldSightNotifications.remove(getItemCount() - 1);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDesc;
        RelativeLayout rootLayout;
        ImageView ivNotificationIcon;
        TextView tvDate;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_list_item_title);
            tvDesc = view.findViewById(R.id.tv_list_item_desc);
            ivNotificationIcon = view.findViewById(R.id.iv_notification_icon);
            rootLayout = view.findViewById(R.id.card_view_list_item_title_desc);
            tvDate = view.findViewById(R.id.tv_notification_date);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
