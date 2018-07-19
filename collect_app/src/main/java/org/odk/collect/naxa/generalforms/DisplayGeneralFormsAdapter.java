package org.odk.collect.naxa.generalforms;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
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

import java.util.ArrayList;
import java.util.List;

public class DisplayGeneralFormsAdapter extends RecyclerView.Adapter<DisplayGeneralFormsAdapter.ViewHolder> {

    private ArrayList<GeneralForm> totalList;

    View itemLayoutView;


    public static String idFormsTable;
    private onGeneralFormClickListener listener;


    public DisplayGeneralFormsAdapter(ArrayList<GeneralForm> totalList) {
        this.totalList = totalList;
    }


    public void updateList(List<GeneralForm> newList) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GeneralFormsDiffCallback(newList, totalList));
        totalList.clear();
        totalList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fieldsigh_general_list_row, null);
        final ViewHolder viewHolder = new ViewHolder(itemLayoutView);


        return new ViewHolder(itemLayoutView);


    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final GeneralForm generalForm = totalList.get(position);

        viewHolder.tvFormName.setText(generalForm.getName());
        viewHolder.tvDesc.setText(generalForm.getName());
        viewHolder.tvLastFilledDateTime.setText(generalForm.getName());
        viewHolder.tvIconText.setText(generalForm.getName().substring(0, 1));

        viewHolder.btnOpenEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onGuideBookButtonClicked(generalForm, position);
            }
        });

        viewHolder.btnOpenHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onFormHistoryButtonClicked(generalForm);
            }
        });

        viewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                listener.onFormItemClicked(generalForm);
            }
        });

        viewHolder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (BuildConfig.DEBUG) {

                    Context context = viewHolder.rootLayout.getContext();
                    String msg = String.format("FormID %s\nSiteID %s\nDeployedFrom %s", generalForm.getFsFormId(), generalForm.getIdString(), generalForm.getDownloadUrl());
                    DialogFactory.createGenericErrorDialog(context, msg).show();

                }
                return false;
            }
        });

        viewHolder.btnOpenEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onGuideBookButtonClicked(generalForm, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return totalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFormName, tvDesc, tvLastFilledDateTime, tvIconText;
        Button btnOpenEdu, btnOpenHistory;
        RelativeLayout rootLayout;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvFormName = (TextView) itemLayoutView.findViewById(R.id.tv_name);
            tvDesc = (TextView) itemLayoutView.findViewById(R.id.tv_desc);
            tvLastFilledDateTime = (TextView) itemLayoutView.findViewById(R.id.tv_last_filled_dt);
            btnOpenHistory = (Button) itemLayoutView.findViewById(R.id.btn_general_history);
            btnOpenEdu = (Button) itemLayoutView.findViewById(R.id.btn_open_edu);
            rootLayout = (RelativeLayout) itemLayoutView.findViewById(R.id.rl_form_list_item);
            tvIconText = (TextView) itemLayoutView.findViewById(R.id.general_icon_text);


        }
    }


    public void setGeneralFormClickListener(DisplayGeneralFormsAdapter.onGeneralFormClickListener listener) {
        this.listener = listener;
    }

    public interface onGeneralFormClickListener {


        void onGuideBookButtonClicked(GeneralForm generalForm, int position);

        void onFormItemClicked(GeneralForm generalForm);

        void onFormStatusClicked();

        void onFormItemLongClicked(String deployedFrom);

        void onFormHistoryButtonClicked(GeneralForm generalForm);
    }

}
