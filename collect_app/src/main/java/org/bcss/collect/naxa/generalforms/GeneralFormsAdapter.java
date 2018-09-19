package org.bcss.collect.naxa.generalforms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.utilities.DateTimeUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.OnFormItemClickListener;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;

import java.util.ArrayList;
import java.util.List;

import static org.bcss.collect.naxa.common.AnimationUtils.getRotationAnimation;

public class GeneralFormsAdapter extends RecyclerView.Adapter<GeneralFormsAdapter.ViewHolder> {

    private ArrayList<GeneralFormAndSubmission> generalForms;
    private OnFormItemClickListener<GeneralForm> listener;

    GeneralFormsAdapter(ArrayList<GeneralFormAndSubmission> totalList, OnFormItemClickListener<GeneralForm> listener) {
        this.generalForms = totalList;
        this.listener = listener;
    }

    public void updateList(List<GeneralFormAndSubmission> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GeneralFormsDiffCallback(newList, generalForms));
        generalForms.clear();
        generalForms.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item_expanded, null);
        return new ViewHolder(view);


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        GeneralForm generalForm = generalForms.get(viewHolder.getAdapterPosition()).getGeneralForm();
        SubmissionDetail submissionDetail = generalForms.get(viewHolder.getAdapterPosition()).getSubmissionDetail();

        viewHolder.tvFormName.setText(generalForm.getName());

        String relativeDateTime = DateTimeUtils.getRelativeTime(generalForm.getDateCreated(), true);
        viewHolder.tvDesc.setText(viewHolder.tvFormName.getContext().getString(R.string.msg_created_on, relativeDateTime));
        if (generalForm.getName() != null) {
            viewHolder.tvIconText.setText(generalForm.getName().substring(0, 1).toUpperCase());
        }

        Integer count = generalForm.getResponsesCount();

        setSubmissionText(viewHolder, submissionDetail);


    }

    private void setSubmissionText(ViewHolder viewHolder, SubmissionDetail submissionDetail) {

        String submittedBy = "";
        String submissionDateTime = "";
        String submissionStatus = "";
        Context context = viewHolder.cardView.getContext();

        if (submissionDetail != null) {
            submittedBy = submissionDetail.getSubmittedBy();
            submissionDateTime = DateTimeUtils.getRelativeTime(submissionDetail.getSubmissionDateTime(), true);
            submissionStatus = submissionDetail.getStatusDisplay();
            viewHolder.ivCardCircle.setImageDrawable(getCircleDrawableBackground(submissionDetail.getStatusDisplay()));
        }

        viewHolder.tvSubtext.setText(context.getString(R.string.form_last_submitted_by, submittedBy));
        viewHolder.tvLastSubmissionDateTime.setText(context.getString(R.string.form_last_submission_datetime, submissionDateTime));
        viewHolder.tvLastSubmissionStatus.setText(context.getString(R.string.form_last_submission_status, submissionStatus));


    }


    private Drawable getCircleDrawableBackground(String status) {

        Drawable drawable;
        switch (status) {
            case Constant.FormStatus.Approved:
                drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_green);
                break;
            case Constant.FormStatus.Flagged:
                drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_yellow);
                break;
            case Constant.FormStatus.Rejected:
                drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_red);
                break;
            case Constant.FormStatus.Pending:
            default:
                drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_blue);
                break;
        }

        return drawable;
    }




    @Override
    public int getItemCount() {
        return generalForms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvFormName, tvDesc, tvLastFilledDateTime, tvIconText, tvSubtext, tvLastSubmissionDateTime, tvLastSubmissionStatus;
        Button btnOpenEdu, btnOpenHistory;
        RelativeLayout rootLayout;
        ImageView badge, ivCardCircle;
        CardView cardView;
        ImageButton btnCardMenu, btnExpandCard;
        private PopupMenu popup;


        public ViewHolder(View view) {
            super(view);

            tvFormName = view.findViewById(R.id.tv_form_primary);
            tvDesc = view.findViewById(R.id.tv_form_secondary);
            btnOpenHistory = view.findViewById(R.id.btn_form_responses);
            btnOpenEdu = view.findViewById(R.id.btn_form_edu);
            rootLayout = view.findViewById(R.id.rl_form_list_item);
            tvIconText = view.findViewById(R.id.form_icon_text);
            badge = view.findViewById(R.id.iv_stage_badge);
            cardView = view.findViewById(R.id.card_view_form_list_item);
            btnCardMenu = view.findViewById(R.id.btn_card_menu);

            tvSubtext = view.findViewById(R.id.tv_form_sub_text);
            btnExpandCard = view.findViewById(R.id.btn_expand_card);

            ivCardCircle = view.findViewById(R.id.iv_form_circle);
            tvLastSubmissionDateTime = view.findViewById(R.id.tv_form_last_submitted_date);
            tvLastSubmissionStatus = view.findViewById(R.id.tv_form_status);


            cardView.setOnClickListener(this);
            btnOpenEdu.setOnClickListener(this);
            btnOpenHistory.setOnClickListener(this);
            btnCardMenu.setOnClickListener(this);

            btnExpandCard.setOnClickListener(this);

            setupPopup(cardView.getContext(), btnCardMenu);


        }


        @Override
        public void onClick(View v) {
            GeneralForm generalForm = generalForms.get(getAdapterPosition()).getGeneralForm();

            switch (v.getId()) {
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
                case R.id.btn_expand_card:

                    boolean isCollapsed = tvSubtext.getVisibility() == View.GONE;
                    if (isCollapsed) {
                        btnExpandCard.startAnimation(getRotationAnimation(180, 0));
                        btnExpandCard.setRotation(180);
                    }else {
                        btnExpandCard.startAnimation(getRotationAnimation(180, 360));
                        btnExpandCard.setRotation(360);
                    }

                    tvSubtext.setVisibility(tvSubtext.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    tvLastSubmissionStatus.setVisibility(tvLastSubmissionStatus.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    tvLastSubmissionDateTime.setVisibility(tvLastSubmissionDateTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

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
