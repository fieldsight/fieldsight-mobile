package org.bcss.collect.naxa.scheduled;

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

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.OnFormItemClickListener;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Susan on 1/30/2017.
 */

public class ScheduledFormsAdapter extends
        RecyclerView.Adapter<ScheduledFormsAdapter.ViewHolder> {

    private ArrayList<ScheduleForm> totalList;

    private OnFormItemClickListener<ScheduleForm> listener;

    public ScheduledFormsAdapter(ArrayList<ScheduleForm> totalList, OnFormItemClickListener<ScheduleForm> listener) {
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

        final ScheduleForm scheduleForm = totalList.get(position);


            viewHolder.tvFormName.setText(scheduleForm.getFormName());
            viewHolder.tvDesc.setText(scheduleForm.getScheduleName());
            viewHolder.tvLastFilledAt.setText(scheduleForm.getLastFilledDateTime());
            viewHolder.tvIconText.setText(scheduleForm.getScheduleName().substring(0, 1).toUpperCase());
//            viewHolder.tvScheduleLevel.setText(scheduleForm.getScheduleLevel());


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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
//             tvScheduleLevel = itemLayoutView.findViewById(R.id.tv_schedule_level);


            rootLayout.setOnClickListener(this);
            btnOpenEdu.setOnClickListener(this);
            btnOpenHistory.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ScheduleForm scheduleForm = totalList.get(getAdapterPosition());

            switch (v.getId()) {
                case R.id.rl_form_list_item:
                    listener.onFormItemClicked(scheduleForm,getAdapterPosition());
                    break;
                case R.id.btn_form_edu:
                    listener.onGuideBookButtonClicked(scheduleForm, getAdapterPosition());
                    break;
                case R.id.btn_form_responses:
                    listener.onFormHistoryButtonClicked(scheduleForm);
                    break;

            }
        }
    }


}
