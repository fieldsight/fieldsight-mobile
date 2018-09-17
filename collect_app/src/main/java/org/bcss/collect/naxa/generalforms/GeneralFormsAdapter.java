package org.bcss.collect.naxa.generalforms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.utilities.DateTimeUtils;
import org.bcss.collect.naxa.common.OnFormItemClickListener;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;

import java.util.ArrayList;
import java.util.List;

public class GeneralFormsAdapter extends RecyclerView.Adapter<GeneralFormsAdapter.ViewHolder> {

    private ArrayList<GeneralForm> generalForms;
    private OnFormItemClickListener<GeneralForm> listener;


    public GeneralFormsAdapter(ArrayList<GeneralForm> totalList, OnFormItemClickListener<GeneralForm> listener) {
        this.generalForms = totalList;
        this.listener = listener;
    }


    public void updateList(List<GeneralForm> newList) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GeneralFormsDiffCallback(newList, generalForms));
        generalForms.clear();
        generalForms.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item_small, null);
        return new ViewHolder(view);


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        GeneralForm generalForm = generalForms.get(viewHolder.getAdapterPosition());
        viewHolder.tvFormName.setText(generalForm.getName());
        viewHolder.tvLastFilledDateTime.setText(generalForm.getName());

        String relativeDateTime = DateTimeUtils.getRelativeTime(generalForm.getDateCreated(), true);
        viewHolder.tvDesc.setText(viewHolder.tvFormName.getContext().getString(R.string.msg_created_on,relativeDateTime));


        if (generalForm.getName() != null) {
            viewHolder.tvIconText.setText(generalForm.getName().substring(0, 1).toUpperCase());
        }


        Integer count = generalForm.getResponsesCount();
        viewHolder.badge.setVisibility(count != null && count > 0 ? View.VISIBLE : View.VISIBLE);

    }

    private void showOrHide(TextView textView, String text) {
        textView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        textView.setText(text);
    }


    @Override
    public int getItemCount() {
        return generalForms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvFormName, tvDesc, tvLastFilledDateTime, tvIconText;
        Button btnOpenEdu, btnOpenHistory;
        RelativeLayout rootLayout;
        View badge;
        CardView cardView;
        ImageButton btnCardMenu;
        private PopupMenu popup;


        public ViewHolder(View view) {
            super(view);

            tvFormName = view.findViewById(R.id.tv_form_primary);
            tvDesc = view.findViewById(R.id.tv_form_secondary);
            tvLastFilledDateTime = view.findViewById(R.id.tv_form_status);
            btnOpenHistory = view.findViewById(R.id.btn_form_responses);
            btnOpenEdu = view.findViewById(R.id.btn_form_edu);
            rootLayout = view.findViewById(R.id.rl_form_list_item);
            tvIconText = view.findViewById(R.id.form_icon_text);
            badge = view.findViewById(R.id.iv_stage_badge);
            cardView = view.findViewById(R.id.card_view_form_list_item);
            btnCardMenu = view.findViewById(R.id.btn_card_menu);


            cardView.setOnClickListener(this);
//            rootLayout.setOnClickListener(this);
            btnOpenEdu.setOnClickListener(this);
            btnOpenHistory.setOnClickListener(this);
            btnCardMenu.setOnClickListener(this);

            setupPopup(cardView.getContext(), btnCardMenu);

        }


        @Override
        public void onClick(View v) {
            GeneralForm generalForm = generalForms.get(getAdapterPosition());

            switch (v.getId()) {
                case R.id.rl_form_list_item:
                    break;
                case R.id.btn_form_edu:
                    listener.onGuideBookButtonClicked(generalForm, getAdapterPosition());
                    break;
                case R.id.btn_form_responses:
                    listener.onFormHistoryButtonClicked(generalForm);
                    break;
                case R.id.card_view_form_list_item:
                    listener.onFormItemClicked(generalForm, getAdapterPosition());
                    break;
                case R.id.btn_card_menu:
                    popup.show();
                    break;
            }
        }

        private void setupPopup(Context context, ImageButton button) {

            popup = new PopupMenu(context, button);
            popup.getMenuInflater().inflate(R.menu.popup_menu_form, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    Bundle bundle = new Bundle();

                    return true;
                }
            });
        }
    }

}
