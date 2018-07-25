package org.odk.collect.naxa.generalforms;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.OnFormItemClickListener;
import org.odk.collect.naxa.generalforms.data.GeneralForm;

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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item, null);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        GeneralForm generalForm = generalForms.get(viewHolder.getAdapterPosition());
        viewHolder.tvFormName.setText(generalForm.getName());
        viewHolder.tvDesc.setText(generalForm.getName());
        viewHolder.tvLastFilledDateTime.setText(generalForm.getName());

        if (generalForm.getName() != null) {
            viewHolder.tvIconText.setText(generalForm.getName().substring(0, 1));
        }

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

            rootLayout.setOnClickListener(this);
            btnOpenEdu.setOnClickListener(this);
            btnOpenHistory.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            GeneralForm generalForm = generalForms.get(getAdapterPosition());

            switch (v.getId()) {
                case R.id.rl_form_list_item:
                    listener.onFormItemClicked(generalForm);
                    break;
                case R.id.btn_form_edu:
                    listener.onGuideBookButtonClicked(generalForm, getAdapterPosition());
                    break;
                case R.id.btn_form_responses:
                    listener.onFormHistoryButtonClicked(generalForm);
                    break;

            }
        }
    }

}
