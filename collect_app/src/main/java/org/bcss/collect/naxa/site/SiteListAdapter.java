package org.bcss.collect.naxa.site;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import org.bcss.collect.naxa.common.anim.FlipAnimator;
import org.bcss.collect.naxa.login.model.Site;

import java.util.ArrayList;
import java.util.List;

public class SiteListAdapter extends RecyclerView.Adapter<SiteListAdapter.MyViewHolder> {

    private final int VIEW_TYPE_SURVEY_FORM = 0, VIEW_TYPE_SITE = 1;

    private final List<Site> siteList;
    private final List<Site> filetredsitelist;
    private final SparseBooleanArray selectedItems;
    private final SparseBooleanArray animationItemsIndex;
    private final SiteListAdapter.SiteListAdapterListener listener;
    private static int currentSelectedIndex = -1;
    private boolean reverseAllAnimations = false;

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
        holder.siteName.setText(site.getName());
        holder.iconText.setText(site.getName().substring(0, 1));
        holder.subject.setText(site.getIdentifier());
        holder.message.setText(site.getAddress());
        holder.timestamp.setText(site.getRegion());
        holder.imgProfile.setImageResource(R.drawable.circle_blue);

        applyIconAnimation(holder, position);
        applyReadStatus(holder);

    }


    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(holder.iconBack.getContext(), holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }

            holder.rooTLayout.setActivated(true);
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(holder.iconBack.getContext(), holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }


            holder.rooTLayout.setActivated(false);
        }
    }

    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public TextView siteName, subject, message, iconText, timestamp;
        public ImageView iconImp, imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;
        private View rooTLayout;

        public MyViewHolder(View view) {
            super(view);
            siteName = (TextView) view.findViewById(R.id.tv_site_name);
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

            rooTLayout.setOnLongClickListener(this);
            rooTLayout.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {

            if (Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED != siteList.get(getAdapterPosition()).getIsSiteVerified()) {
                listener.onRowLongClicked(getAdapterPosition());
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            listener.onUselessLayoutClicked(siteList.get(getAdapterPosition()));
        }
    }


    private void applyReadStatus(MyViewHolder holder) {


        Site siteLocationPojo = siteList.get(holder.getAdapterPosition());

        boolean isChecked = selectedItems.get(holder.getAdapterPosition(), false);
        boolean isUnVerifiedSite = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_UNVERIFIED_SITE;
        boolean isUnsycned = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_VERIFIED_BUT_UNSYNCED;
        boolean isFinalized = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_FINALIZED;
        boolean isVerifiedSite = siteLocationPojo.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED;

        if (isChecked) {
            holder.siteName.setTypeface(null, Typeface.BOLD);
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.siteName.setTextColor(ContextCompat.getColor(holder.siteName.getContext(), R.color.from));
            holder.subject.setTextColor(ContextCompat.getColor(holder.siteName.getContext(), R.color.subject));
        }

        if (isUnVerifiedSite || isUnsycned || isFinalized) {

            holder.timestamp.setText("Offline Site");
            holder.siteName.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.siteName.setTextColor(ContextCompat.getColor(holder.siteName.getContext(), R.color.subject));
            holder.subject.setTextColor(ContextCompat.getColor(holder.siteName.getContext(), R.color.message));
        }


    }


    public void updateList(List<Site> newList) {

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new SiteListDiffCallback(newList, siteList));
        siteList.clear();
        siteList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);

    }

    public ArrayList<Site> getSelected() {
        ArrayList<Site> items = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(siteList.get(selectedItems.keyAt(i)));
        }

        return items;
    }


    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {

            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public interface SiteListAdapterListener {
        void onIconClicked(int position);

        void onRowLongClicked(int position);

        void onUselessLayoutClicked(Site site);

        void onSurveyFormClicked();
    }
}
