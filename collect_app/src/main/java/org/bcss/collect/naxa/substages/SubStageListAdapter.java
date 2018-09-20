package org.bcss.collect.naxa.substages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.utilities.DateTimeUtils;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.OnFormItemClickListener;
import org.bcss.collect.naxa.generalforms.GeneralFormsAdapter;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;
import org.bcss.collect.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;

import static org.bcss.collect.naxa.common.AnimationUtils.getRotationAnimation;

/**
 * Created by Susan on 1/30/2017.
 */
public class SubStageListAdapter extends
        RecyclerView.Adapter<SubStageListAdapter.ViewHolder> {


    private List<SubStage> subStages;
    private OnFormItemClickListener<SubStage> listener;
    private String stageOrder;


    public SubStageListAdapter(List<SubStage> subStages, String stageOrder, OnFormItemClickListener<SubStage> listener) {
        this.subStages = subStages;
        this.stageOrder = stageOrder;
        this.listener = listener;
    }

    public void updateList(List<SubStage> newList) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SubStageDiffCallback(newList, subStages));
        subStages.clear();
        subStages.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item_expanded, null);
        return new ViewHolder(view);
    }


    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        SubStage subStage = subStages.get(viewHolder.getAdapterPosition());

        viewHolder.tvStageName.setText(subStages.get(position).getName());
        viewHolder.tvSubTitle.setText(subStages.get(position).getName());
        viewHolder.btnViewFormHistory.setOnClickListener(view -> listener.onFormHistoryButtonClicked(subStage));

        setSubstageNumber(viewHolder);
        setSubmissionText(viewHolder, null);

    }

    private void setSubstageNumber(ViewHolder viewHolder) {

        int substageNumber = viewHolder.getAdapterPosition() + 1;
        int stageNumber = Integer.parseInt(stageOrder + 1);
        String iconText = stageNumber + "." + substageNumber;

        viewHolder.tvSubStageIconText.setText(iconText);
    }

    private void setSubmissionText(ViewHolder viewHolder, SubmissionDetail submissionDetail) {

        String submittedBy = "";
        String submissionDateTime = "";
        String submissionStatus = "";
        Context context = viewHolder.card.getContext();

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
        return subStages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvStageName, tvSubTitle, tvSubStageIconText, tvSubtext, tvLastSubmissionStatus, tvLastSubmissionDateTime;
        private Button btnViewFormHistory, btnOpenEdu;
        private CardView card;
        ImageButton btnExpandCard;
        ImageView ivCardCircle;


        public ViewHolder(View view) {
            super(view);

            tvStageName = view.findViewById(R.id.tv_form_primary);
            card = view.findViewById(R.id.card_view_form_list_item);
            tvSubTitle = view.findViewById(R.id.tv_form_secondary);
            tvSubStageIconText = view.findViewById(R.id.form_icon_text);
            btnViewFormHistory = view.findViewById(R.id.btn_form_responses);
            btnOpenEdu = view.findViewById(R.id.btn_form_edu);
            btnExpandCard = view.findViewById(R.id.btn_expand_card);
            tvLastSubmissionStatus = view.findViewById(R.id.tv_form_status);

            ivCardCircle = view.findViewById(R.id.iv_form_circle);


            btnViewFormHistory.setOnClickListener(this);
            btnOpenEdu.setOnClickListener(this);
            card.setOnClickListener(this);
            btnExpandCard.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            SubStage subStage = subStages.get(getAdapterPosition());

            switch (v.getId()) {
                case R.id.btn_form_edu:
                    listener.onGuideBookButtonClicked(subStage, getAdapterPosition());
                    break;
                case R.id.btn_form_responses:
                    listener.onFormHistoryButtonClicked(subStage);
                    break;
                case R.id.card_view_form_list_item:
                    listener.onFormItemClicked(subStage, getAdapterPosition());
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

                    tvSubtext.setVisibility(tvSubtext.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    tvLastSubmissionStatus.setVisibility(tvLastSubmissionStatus.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    tvLastSubmissionDateTime.setVisibility(tvLastSubmissionDateTime.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                    break;
            }


        }
    }
}



