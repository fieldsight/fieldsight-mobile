package org.bcss.collect.naxa.notificationslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;

import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.data.FieldSightNotification;

import java.util.ArrayList;
import java.util.List;

import static org.bcss.collect.naxa.common.Truss.makeSectionOfTextBold;
import static org.bcss.collect.naxa.firebase.FieldSightFirebaseMessagingService.APPROVED_FORM;
import static org.bcss.collect.naxa.firebase.FieldSightFirebaseMessagingService.FLAGGED_FORM;
import static org.bcss.collect.naxa.firebase.FieldSightFirebaseMessagingService.REJECTED_FORM;

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
        String desc;
        SpannableStringBuilder formattedDesc = null;

        switch (fieldSightNotification.getNotificationType()) {
            case (FLAGGED_FORM):
                viewHolder.tvTitle.setText(context.getResources().getString(R.string.notify_title_submission_result));
                desc = context.getResources().getString(R.string.notify_submission_result,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_flagged));

                formattedDesc = makeSectionOfTextBold(desc,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_flagged));
                break;

            case APPROVED_FORM:
                viewHolder.tvTitle.setText(context.getResources().getString(R.string.notify_title_submission_result));
                desc = context.getResources().getString(R.string.notify_submission_result,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_approved) + ".");

                formattedDesc = makeSectionOfTextBold(desc,
                        fieldSightNotification.getSiteName(),
                        fieldSightNotification.getFormName(), context.getResources().getString(R.string.notify_form_approved));
                break;
            case REJECTED_FORM:

                viewHolder.tvTitle.setText(context.getResources().getString(R.string.notify_title_submission_result));
                String form_rejected_response = context.getResources().getString(R.string.notify_submission_result,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_rejected) + ".");

                formattedDesc = makeSectionOfTextBold(form_rejected_response,
                        fieldSightNotification.getFormName(),
                        fieldSightNotification.getSiteName(),
                        context.getResources().getString(R.string.notify_form_rejected));
                break;
        }


        if (formattedDesc != null) {
            viewHolder.tvDesc.setText(formattedDesc.toString());
        }

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
