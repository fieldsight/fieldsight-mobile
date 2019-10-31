package org.fieldsight.naxa.v3.forms;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.v3.FormState;

import java.util.Locale;

class FieldSightFormStateVH extends RecyclerView.ViewHolder {
    private final TextView tvTitle, tvSubtitle, tvDate;

    private final ImageView ivNotificationBg;

    FieldSightFormStateVH(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tv_list_item_title);
        tvSubtitle = itemView.findViewById(R.id.tv_list_item_desc);
        ivNotificationBg = itemView.findViewById(R.id.iv_notification_bg);
        tvDate = itemView.findViewById(R.id.tv_notification_date);

    }

    public void onItemTapped(FormState form) {

    }

    public void bindView(FormState form) {

        tvTitle.setText(form.getStatusDisplay());
        String message = itemView.getContext().getString(R.string.msg_form_flagged, form.getFormName()
                , form.getStatusDisplay().toLowerCase(Locale.getDefault()),
                form.getSiteName());
        tvSubtitle.setText(message);

        if (TextUtils.equals(form.getStatusDisplay().toLowerCase(Locale.getDefault()), "flagged")) {
            ivNotificationBg.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.circle_yellow));
        } else if (TextUtils.equals(form.getStatusDisplay().toLowerCase(Locale.getDefault()), "rejected")) {
            ivNotificationBg.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.circle_red));
        }

        tvDate.setVisibility(View.GONE);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemTapped(form);
            }
        });

    }


}

