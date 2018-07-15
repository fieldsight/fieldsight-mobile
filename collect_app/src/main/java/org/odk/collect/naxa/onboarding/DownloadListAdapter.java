package org.odk.collect.naxa.onboarding;

import android.content.Context;
import android.graphics.Typeface;
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
import org.odk.collect.naxa.generalforms.DisplayGeneralFormsAdapter;
import org.odk.collect.naxa.generalforms.GeneralForm;
import org.odk.collect.naxa.generalforms.GeneralFormsDiffCallback;

import java.util.ArrayList;

public class DownloadListAdapter extends RecyclerView.Adapter<DisplayGeneralFormsAdapter.ViewHolder> {

    private ArrayList<GeneralForm> generalForms;
    private DisplayGeneralFormsAdapter.onGeneralFormClickListener listener;

    DownloadListAdapter(ArrayList<GeneralForm> generalForms) {
        this.generalForms = generalForms;
    }

    public void updateList(ArrayList<GeneralForm> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new GeneralFormsDiffCallback(newList, generalForms));
        generalForms.clear();
        generalForms.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public DisplayGeneralFormsAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View rootLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_item, null);
        final DisplayGeneralFormsAdapter.ViewHolder viewHolder = new DisplayGeneralFormsAdapter.ViewHolder(rootLayout);
        return new DisplayGeneralFormsAdapter.ViewHolder(rootLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final DisplayGeneralFormsAdapter.ViewHolder viewHolder, int position) {
        final GeneralForm generalForm = generalForms.get(viewHolder.getAdapterPosition());

        viewHolder.tvFormName.setText(generalForm.getFormName());
        viewHolder.tvDesc.setText(generalForm.getFormName());
        viewHolder.tvLastFilledDateTime.setText(generalForm.getLastFilledDateTime());
        viewHolder.tvIconText.setText(generalForm.getFormName().substring(0, 1));

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
                    String msg = String.format("FormID %s\nSiteID %s\nDeployedFrom %s", generalForm.getFsFormId(), generalForm.getSiteId(), generalForm.getFormDeployedFrom());
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
        return generalForms.size();
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
