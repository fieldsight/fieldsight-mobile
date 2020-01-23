package org.fieldsight.naxa.stages;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.OnFormItemClickListener;
import org.fieldsight.naxa.stages.data.Stage;

import java.util.ArrayList;
import java.util.List;


public class StageListAdapter extends
        RecyclerView.Adapter<StageListAdapter.ViewHolder> implements View.OnClickListener {

    private final ArrayList<Stage> totalList;
    public final OnFormItemClickListener<Stage> onFormItemClickListener;


    public StageListAdapter(ArrayList<Stage> totalList, OnFormItemClickListener<Stage> onFormItemClickListener) {
        this.totalList = totalList;
        this.onFormItemClickListener = onFormItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item_small2, null);
        return new ViewHolder(view);
    }



    public void updateList(List<Stage> newList) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new StagesDiffCallback(newList, totalList));
        totalList.clear();
        totalList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);

        if(newList.isEmpty()){
            //triggers observer so it display empty layout - nishon
            this.notifyDataSetChanged();
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Stage stage = totalList.get(viewHolder.getAdapterPosition());

        int stageNumber = position + 1;
        viewHolder.tvStageName.setText(stage.getName());
        if(TextUtils.isEmpty(stage.getDescription()) || TextUtils.equals(stage.getDescription(), "null")) {
            viewHolder.tvSubTitle.setVisibility(View.GONE);
        } else {
            viewHolder.tvSubTitle.setVisibility(View.VISIBLE);
            viewHolder.tvSubTitle.setText(stage.getDescription());
        }
        viewHolder.tvIconText.setText(String.valueOf(stageNumber));
    }

    @Override
    public int getItemCount() {
        return totalList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvStageName, tvSubTitle, tvIconText;
        CardView card;


        public ViewHolder(View view) {
            super(view);


            tvStageName = view.findViewById(R.id.tv_form_primary);
            tvSubTitle = view.findViewById(R.id.tv_form_secondary);
            tvIconText = view.findViewById(R.id.form_icon_text);
            card = view.findViewById(R.id.card_view_form_list_item);
            card.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            Stage stage = totalList.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.card_view_form_list_item:
                    onFormItemClickListener.onFormItemClicked(stage, getAdapterPosition());
                    break;
            }
        }
    }

}
