package org.odk.collect.naxa.scheduled;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.odk.collect.android.BuildConfig;
import org.odk.collect.android.R;
import org.odk.collect.naxa.common.DialogFactory;
import org.odk.collect.naxa.scheduled.data.ScheduleForm;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Susan on 1/30/2017.
 */

public class ScheduledFormsAdapter extends
        RecyclerView.Adapter<ScheduledFormsAdapter.ViewHolder> {

    private ArrayList<ScheduleForm> totalList;

    private OnFormClickListener onClickListener;

    public ScheduledFormsAdapter(ArrayList<ScheduleForm> totalList) {
        this.totalList = totalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        final ScheduleForm scheduleForm = totalList.get(position);

        try {
            viewHolder.tvFormName.setText(scheduleForm.getScheduleName());
            viewHolder.tvDesc.setText(scheduleForm.getFormName());
            viewHolder.tvLastFilledAt.setText(scheduleForm.getLastFilledDateTime());
            viewHolder.tvIconText.setText(scheduleForm.getScheduleName().substring(0, 1));
            viewHolder.tvScheduleLevel.setText(scheduleForm.getScheduleLevel());

        } catch (NullPointerException e) {

        }


        viewHolder.btnOpenHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onPreviousSubmissionButtonClicked(scheduleForm);
            }
        });

        viewHolder.btnOpenEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onGuideBookButtonClicked(scheduleForm, viewHolder.getAdapterPosition());
            }
        });

        viewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onFormItemClicked(scheduleForm);
            }
        });

        viewHolder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (BuildConfig.DEBUG) {

                    Context context = viewHolder.rootLayout.getContext();
                    String msg = String.format("FormID %s\nSiteID %s\nDeployedFrom %s", scheduleForm.getFsFormId(), scheduleForm.getSiteId(), scheduleForm.getFormDeployedFrom());
                    DialogFactory.createGenericErrorDialog(context, msg).show();


                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return totalList.size();
    }

    public void updateList(List<ScheduleForm> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ScheduleFormsDiffCallback(totalList, newList));
        totalList.clear();
        totalList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFormName, tvDesc, tvLastFilledAt, tvIconText, tvScheduleLevel;
        Button btnOpenEdu, btnOpenHistory;
        RelativeLayout rootLayout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvFormName = itemLayoutView.findViewById(R.id.tv_form_primary);
            tvDesc = itemLayoutView.findViewById(R.id.tv_form_secondary);
            tvLastFilledAt = itemLayoutView.findViewById(R.id.tv_form_status);
            btnOpenHistory = itemLayoutView.findViewById(R.id.btn_form_responses);
            btnOpenEdu = itemLayoutView.findViewById(R.id.btn_form_edu);
            rootLayout = itemLayoutView.findViewById(R.id.rl_form_list_item);
            tvIconText = itemLayoutView.findViewById(R.id.form_icon_text);
            // tvScheduleLevel = itemLayoutView.findViewById(R.id.tv_schedule_level);
        }
    }

    public void setOnClickListener(OnFormClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnFormClickListener {
        void onPreviousSubmissionButtonClicked(ScheduleForm scheduleForm);

        void onGuideBookButtonClicked(ScheduleForm scheduleForm, int position);

        void onFormItemClicked(ScheduleForm scheduleForm);

        void onFormStatusClicked();
    }


}
