package org.odk.collect.naxa.stages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.naxa.stages.data.Stage;

import java.util.ArrayList;
import java.util.List;


public class StageListAdapter extends
        RecyclerView.Adapter<StageListAdapter.ViewHolder> {

    private ArrayList<Stage> totalList;


    public StageListAdapter() {

    }

    public StageListAdapter(ArrayList<Stage> totalList) {
        this.totalList = totalList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item, null);
        return new ViewHolder(view);
    }

    public void updateList(List<Stage> newList) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new StagesDiffCallback(newList, totalList));
        totalList.clear();
        totalList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Stage stage = totalList.get(viewHolder.getAdapterPosition());
        try {

            int stageNumber = position + 1;
            viewHolder.tvStageName.setText(stage.getName());
            viewHolder.tvSubTitle.setText(stage.getDescription());
            viewHolder.tvIconText.setText(String.valueOf(stageNumber));
            viewHolder.stageBadge.setVisibility(stage.hasAllSubStageComplete() ? View.VISIBLE : View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return totalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStageName, tvSubTitle, stageIdTv, tvIconText;
        ImageView stageBadge;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvStageName = (TextView) itemLayoutView.findViewById(R.id.tv_form_primary);
            tvSubTitle = (TextView) itemLayoutView.findViewById(R.id.tv_form_secondary);
            tvIconText = (TextView) itemLayoutView.findViewById(R.id.form_icon_text);
            stageBadge = (ImageView) itemLayoutView.findViewById(R.id.iv_stage_badge);


        }

    }

}
