package org.bcss.collect.naxa.site;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.login.model.Site;

import java.util.List;

public class SiteListAdapter extends RecyclerView.Adapter<SiteListAdapter.MyViewHolder> {


    private final List<Site> siteList;
    private final List<Site> filetredsitelist;
    private final SparseBooleanArray selectedItems;
    private final SparseBooleanArray animationItemsIndex;
    private final SiteListAdapter.SiteListAdapterListener listener;

    SiteListAdapter(List<Site> sitelist, SiteListAdapter.SiteListAdapterListener listener) {
        this.siteList = sitelist;
        this.filetredsitelist = sitelist;
        this.listener = listener;
        this.selectedItems = new SparseBooleanArray();
        this.animationItemsIndex = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Site site = siteList.get(holder.getAdapterPosition());
        holder.from.setText(site.getName());
        holder.iconText.setText(site.getName().substring(0, 1));
        setCardBackground(holder.getAdapterPosition(),holder.rooTLayout);

    }

    private void setCardBackground(int position, View view) {
        Site siteLocationPojo = siteList.get(position);

        boolean isChecked = selectedItems.get(position, false);
        boolean isUnVerifiedSite = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_UNVERIFIED_SITE;
        boolean isUnsycned = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_VERIFIED_BUT_UNSYNCED;
        boolean isFinalized = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_FINALIZED;
        boolean isVerifiedSite = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED;

        view.setActivated(true);

        if (isChecked) {
            view.setActivated(true);
        }

        if (isUnVerifiedSite || isUnsycned || isFinalized) {
            view.setActivated(false);
        }

    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, subject, message, iconText, timestamp;
        public ImageView iconImp, imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;
        private View rooTLayout;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            subject = (TextView) view.findViewById(R.id.txt_primary);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            iconImp = (ImageView) view.findViewById(R.id.icon_star);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            messageContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            rooTLayout = view.findViewById(R.id.root_layout_message_list_row);

            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public void updateList(List<Site> newList) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SiteListDiffCallback(newList, siteList));
        siteList.clear();
        siteList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);

    }


    public interface SiteListAdapterListener {
        void onIconClicked(int position);

        void onRowLongClicked(int position);

        void onUselessLayoutClicked(Site site);

        void onSurveyFormClicked();
    }
}
