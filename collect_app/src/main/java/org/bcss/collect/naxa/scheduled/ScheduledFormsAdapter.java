package org.bcss.collect.naxa.scheduled;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.utilities.DateTimeUtils;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.OnFormItemClickListener;
import org.bcss.collect.naxa.previoussubmission.model.ScheduledFormAndSubmission;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static org.bcss.collect.naxa.common.AnimationUtils.getRotationAnimation;

/**
 * Created by Susan on 1/30/2017.
 */

public class ScheduledFormsAdapter extends
        RecyclerView.Adapter<ScheduledFormsAdapter.ViewHolder> {

    private ArrayList<ScheduledFormAndSubmission> totalList;

    private OnFormItemClickListener<ScheduleForm> listener;

    public ScheduledFormsAdapter(ArrayList<ScheduledFormAndSubmission> totalList, OnFormItemClickListener<ScheduleForm> listener) {
        this.totalList = totalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item_small, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        ScheduleForm scheduleForm = totalList.get(position).getScheduleForm();
        SubmissionDetail submissionDetail = totalList.get(position).getSubmissionDetail();


        viewHolder.tvFormName.setText(scheduleForm.getFormName());
        viewHolder.tvDesc.setText(scheduleForm.getScheduleName());
        viewHolder.tvIconText.setText(scheduleForm.getScheduleName().substring(0, 1).toUpperCase());

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
        return totalList.size();
    }

    public void updateList(List<ScheduledFormAndSubmission> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ScheduleFormsDiffCallback(totalList, newList));
        totalList.clear();
        totalList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvFormName, tvDesc, tvLastFilledAt, tvIconText, tvScheduleLevel, tvSubtext, tvLastSubmissionDateTime, tvLastSubmissionStatus;
        Button btnOpenEdu, btnOpenHistory;
        RelativeLayout rootLayout;
        CardView cardView;
        ImageButton btnCardMenu, btnExpandCard;
        ImageView ivCardCircle;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvFormName = itemLayoutView.findViewById(R.id.tv_form_primary);
            tvDesc = itemLayoutView.findViewById(R.id.tv_form_secondary);
            btnOpenHistory = itemLayoutView.findViewById(R.id.btn_form_responses);
            btnOpenEdu = itemLayoutView.findViewById(R.id.btn_form_edu);
            rootLayout = itemLayoutView.findViewById(R.id.rl_form_list_item);
            tvIconText = itemLayoutView.findViewById(R.id.form_icon_text);
            cardView = itemLayoutView.findViewById(R.id.card_view_form_list_item);
//             tvScheduleLevel = itemLayoutView.findViewById(R.id.tv_schedule_level);

            tvSubtext = itemLayoutView.findViewById(R.id.tv_form_sub_text);
            btnExpandCard = itemLayoutView.findViewById(R.id.btn_expand);

            ivCardCircle = itemLayoutView.findViewById(R.id.iv_form_circle);
            tvLastSubmissionDateTime = itemLayoutView.findViewById(R.id.tv_form_last_submitted_date);
            tvLastSubmissionStatus = itemLayoutView.findViewById(R.id.tv_form_status);


            rootLayout.setOnClickListener(this);
            btnOpenEdu.setOnClickListener(this);
            btnOpenHistory.setOnClickListener(this);
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
                case R.id.btn_expand:

                    boolean isCollapsed = tvSubtext.getVisibility() == View.GONE;
                    if (isCollapsed) {
                        btnExpandCard.startAnimation(getRotationAnimation(180, 0));
                        btnExpandCard.setRotation(180);
                    }else {
                        btnExpandCard.startAnimation(getRotationAnimation(180, 360));
                        btnExpandCard.setRotation(360);

                    }


                    int newVisiblity = tvSubtext.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;

                    tvSubtext.setVisibility(newVisiblity);
                    tvLastSubmissionStatus.setVisibility(newVisiblity);
                    tvLastSubmissionDateTime.setVisibility(newVisiblity);

                    break;
            }
        }
    }
}



