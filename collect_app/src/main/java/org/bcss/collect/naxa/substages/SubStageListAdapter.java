package org.bcss.collect.naxa.substages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Susan on 1/30/2017.
 */
public class SubStageListAdapter extends
        RecyclerView.Adapter<SubStageListAdapter.ViewHolder> {


    private List<SubStage> subStages;
    private OnSubStageClickListener listener;

    private final String TAG = "SubStageAdapter";
  
    private String stageOrder;
    public String idFormsTable;
    private int stagePosition;


    public SubStageListAdapter(List<SubStage> subStages) {
        this.subStages = subStages;
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_list_item, null);
        return new ViewHolder(view);
    }


    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        SubStage subStage = subStages.get(viewHolder.getAdapterPosition());

        viewHolder.tvStageName.setText(subStages.get(position).getName());
        viewHolder.tvSubTitle.setText(subStages.get(position).getName());
//        viewHolder.tvLastFilledDateTime.setText(subStage.getLastFilledDateTime());
//        viewHolder.substageBadge.setVisibility(subStage.isSubStageComplete() ? View.VISIBLE : View.GONE);
        viewHolder.btnViewFormHistory.setOnClickListener(view -> listener.onPreviousSubmissionButtonClicked(subStage));

        int substageNumber = position + 1;
        String iconText = stageOrder + "." + substageNumber;

        viewHolder.tvSubStageIconText.setText(iconText);

        viewHolder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFormItemClicked(subStage);
            }
        });

        viewHolder.rootlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (BuildConfig.DEBUG) {
//                    Context context = viewHolder.layoutFormStatus.getContext();
//                    String msg = String.format("FormID %s\nSiteID %s\nDeployedFrom %s\nFormType %s", subStage.getFsFormId(),
//                            subStage.getSiteId(),
//                            subStage.getFormDeployedFrom());
//                    DialogFactory.createGenericErrorDialog(context, msg).show();
//
//                    ToastUtils.showShortToast("fsFormId %s siteId %s", subStage.getFsFormId(), subStage.getSiteId());
                }
                return true;
            }
        });

        viewHolder.btnOpenEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onGuideBookButtonClicked(subStage, viewHolder.getAdapterPosition());
            }
        });

    }


    @Override
    public int getItemCount() {
        return subStages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvStageName, tvSubTitle, tvLastFilledDateTime, tvSubStageIconText;
        private RelativeLayout rootlayout, layoutFormStatus;
        Button btnViewFormHistory, btnOpenEdu;
        ImageView substageBadge;

        public ViewHolder(View view) {
            super(view);

            rootlayout = (RelativeLayout) view.findViewById(R.id.rl_form_list_item);
            tvStageName = (TextView) view.findViewById(R.id.tv_form_primary);
            tvSubTitle = (TextView) view.findViewById(R.id.tv_form_secondary);
            tvSubStageIconText = (TextView) view.findViewById(R.id.form_icon_text);
            tvLastFilledDateTime = (TextView) view.findViewById(R.id.tv_form_status);
            btnViewFormHistory = (Button) view.findViewById(R.id.btn_form_responses);
            btnOpenEdu = (Button) view.findViewById(R.id.btn_form_edu);
            substageBadge = view.findViewById(R.id.iv_stage_badge);

            btnViewFormHistory.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_form_edu:
//                    Intent intent = new Intent(v.getContext(), EducationMaterialSliderActivity.class);
//                    // Code for button 2 click
//                    intent.putExtra("SUB_SITE_ID", subStages.get(getPosition()).getSiteId());
//                    intent.putExtra("SUB_STAGE_ID", subStages.get(getPosition()).getSubStageId());
//                    intent.putExtra("StagePosition", stagePosition);
//                    Log.d(TAG, "onClick: " + getAdapterPosition() + "  " + getItemId());
//                    intent.putExtra("SUB_STAGE_POSITION", getAdapterPosition());
//                    v.getContext().startActivity(intent);
                    break;


            }
        }
    }

    public interface OnSubStageClickListener {

        void onGuideBookButtonClicked(SubStage subStage, int position);

        void onFormItemClicked(SubStage subStage);

        void onFormStatusClicked();

        void onPreviousSubmissionButtonClicked(SubStage subStage);
    }
}
