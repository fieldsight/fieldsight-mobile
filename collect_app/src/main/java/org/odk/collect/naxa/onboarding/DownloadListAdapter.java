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

    private ArrayList<DownloadableItem> downloadableItems;
    private onDownLoadItemClick listener;

    DownloadListAdapter(ArrayList<DownloadableItem> downloadableItems) {
        this.downloadableItems = downloadableItems;
    }

    public void updateList(ArrayList<DownloadableItem> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DownloadableItemsDiffCallback(newList, downloadableItems));
        downloadableItems.clear();
        downloadableItems.addAll(newList);
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

    }


    @Override
    public int getItemCount() {
        return downloadableItems.size();
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


    public void setGeneralFormClickListener(DownloadListAdapter.onDownLoadItemClick listener) {
        this.listener = listener;
    }


    public interface onDownLoadItemClick {
        void onItemTap(DownloadableItem downloadableItem);
    }
}
