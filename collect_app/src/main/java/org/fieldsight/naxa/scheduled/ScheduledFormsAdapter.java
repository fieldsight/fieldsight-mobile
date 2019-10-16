package org.fieldsight.naxa.scheduled;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.fieldsight.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.OnFormItemClickListener;
import org.fieldsight.naxa.previoussubmission.model.ScheduledFormAndSubmission;
import org.fieldsight.naxa.previoussubmission.model.SubmissionDetail;
import org.fieldsight.naxa.scheduled.data.ScheduleForm;
import org.odk.collect.android.utilities.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.fieldsight.naxa.common.AnimationUtils.getRotationAnimation;

/**
 * Created by Susan on 1/30/2017.
 */

public class ScheduledFormsAdapter extends
        RecyclerView.Adapter<ScheduledFormsAdapter.ViewHolder> {

    private final ArrayList<ScheduledFormAndSubmission> totalList;

    private final OnFormItemClickListener<ScheduleForm> listener;

    public ScheduledFormsAdapter(ArrayList<ScheduledFormAndSubmission> totalList, OnFormItemClickListener<ScheduleForm> listener) {
        this.totalList = totalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item_expanded, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        ScheduleForm scheduleForm = totalList.get(position).getScheduleForm();
        SubmissionDetail submissionDetail = totalList.get(position).getSubmissionDetail();


        viewHolder.tvFormName.setText(scheduleForm.getFormName());
        viewHolder.tvDesc.setText(scheduleForm.getScheduleName());
        if(!TextUtils.isEmpty(scheduleForm.getScheduleName())){
            viewHolder.tvIconText.setText(scheduleForm.getScheduleName().substring(0, 1).toUpperCase(Locale.getDefault()));
        }

        setSubmissionText(viewHolder, submissionDetail, scheduleForm);
    }


    private void setSubmissionText(ScheduledFormsAdapter.ViewHolder viewHolder, SubmissionDetail submissionDetail, ScheduleForm scheduleForm) {

        String submissionDateTime = "";
        String submittedBy = "";
        String submissionStatus = "";
        String scheduleType = "";
        Context context = viewHolder.cardView.getContext();


        if (submissionDetail != null) {
            submittedBy = submissionDetail.getSubmittedBy();
            submissionStatus = submissionDetail.getStatusDisplay();
            submissionDateTime = DateTimeUtils.getRelativeTime(submissionDetail.getSubmissionDateTime(), true);
        }

        if (submissionDetail != null && submissionDetail.getSubmissionDateTime() == null) {
            submissionDateTime = context.getString(R.string.form_pending_submission);
        } else {
            submissionDateTime = context.getString(R.string.form_last_submission_datetime, submissionDateTime);
        }

        if (scheduleForm.getScheduleLevel() != null) {
            scheduleType = scheduleForm.getScheduleLevel();
        }

        String formSubtext = generateSubtext(context, submittedBy, submissionStatus, scheduleType);


        viewHolder.ivCardCircle.setImageDrawable(getCircleDrawableBackground(submissionStatus));
        viewHolder.tvDesc.setText(submissionDateTime);
        viewHolder.tvSubtext.setText(formSubtext);
    }

    private String generateSubtext(Context context, String submittedBy, String submissionStatus, String scheduleType) {

        return context.getString(R.string.form_last_submitted_by, submittedBy == null ? "" : submittedBy)
                + "\n" +
                context.getString(R.string.form_last_submission_status, submissionStatus == null ? "" : submissionStatus)
                + "\n" +
                context.getString(R.string.schedule_type, scheduleType == null ? "" : scheduleType);
    }

    private Drawable getCircleDrawableBackground(String status) {

        Drawable drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_blue);

        if (status == null) {
            return drawable;
        }

        switch (status) {
            case Constant.FormStatus.APPROVED:
                drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_green);
                break;
            case Constant.FormStatus.FLAGGED:
                drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_yellow);
                break;
            case Constant.FormStatus.REJECTED:
                drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_red);
                break;
            case Constant.FormStatus.PENDING:
            default:
                drawable = ContextCompat.getDrawable(Collect.getInstance().getApplicationContext(), R.drawable.circle_blue);
                break;
        }

        return drawable;
    }

    @Override
    public int getItemCount() {
        return totalList.size();
    }

    public void updateList(List<ScheduledFormAndSubmission> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ScheduleFormsDiffCallback(totalList, newList));
        totalList.clear();
        totalList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);

        if(newList.isEmpty()){
            //triggers observer so it display empty layout - nishon
            this.notifyDataSetChanged();
        }
    }

    public ArrayList<ScheduledFormAndSubmission> getAll() {
        return totalList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvFormName, tvDesc, tvIconText, tvSubtext;
        private final Button btnOpenEdu, btnOpenHistory;
        private final ImageView ivCardCircle;
        private final View cardView;
        private final ImageButton btnExpandCard;

        public ViewHolder(View view) {
            super(view);

            cardView = view.findViewById(R.id.card_view_form_list_item);
            tvFormName = view.findViewById(R.id.tv_form_primary);
            tvDesc = view.findViewById(R.id.tv_form_secondary);
            tvIconText = view.findViewById(R.id.form_icon_text);
            tvSubtext = view.findViewById(R.id.tv_form_subtext);

            ivCardCircle = view.findViewById(R.id.iv_form_circle);

            btnOpenHistory = view.findViewById(R.id.btn_form_responses);
            btnOpenEdu = view.findViewById(R.id.btn_form_edu);
            btnExpandCard = view.findViewById(R.id.btn_expand_card);

            cardView.setOnClickListener(this);
            btnOpenEdu.setOnClickListener(this);
            btnOpenHistory.setOnClickListener(this);
            btnExpandCard.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            ScheduleForm scheduleForm = totalList.get(getAdapterPosition()).getScheduleForm();

            switch (v.getId()) {
                case R.id.btn_form_edu:
                    listener.onGuideBookButtonClicked(scheduleForm, getAdapterPosition());
                    break;
                case R.id.btn_form_responses:
                    listener.onFormHistoryButtonClicked(scheduleForm);
                    break;
                case R.id.card_view_form_list_item:
                    listener.onFormItemClicked(scheduleForm, getAdapterPosition());
                    break;
                case R.id.btn_expand_card:

                    boolean isCollapsed = tvSubtext.getVisibility() == View.GONE;
                    if (isCollapsed) {
                        btnExpandCard.startAnimation(getRotationAnimation(180, 0));
                        btnExpandCard.setRotation(180);
                    } else {
                        btnExpandCard.startAnimation(getRotationAnimation(180, 360));
                        btnExpandCard.setRotation(360);

                    }

                    int newVisiblity = tvSubtext.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    tvSubtext.setVisibility(newVisiblity);

                    break;
            }
        }
    }
}



