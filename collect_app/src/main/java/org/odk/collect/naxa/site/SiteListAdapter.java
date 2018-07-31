package org.odk.collect.naxa.site;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.anim.FlipAnimator;
import org.odk.collect.naxa.login.model.Site;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class SiteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


    private Context mContext;
    private List<Site> filetredsitelist;
    private List<Site> siteList;
    private SiteListAdapterListener listener;
    private SparseBooleanArray selectedItems;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;
    private static final int VIEW_TYPE_SURVEY_FORM = 0, VIEW_TYPE_SITE = 1;

    public SiteListAdapter(Context mContext, List<Site> sitelist, SiteListAdapterListener listener) {
        this.mContext = mContext;

        this.siteList = sitelist;
        this.filetredsitelist = sitelist;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        // setHasStableIds(true);
    }

    public List<Site> getAllItems() {
        return siteList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView siteName, siteAddress, sitePhone, siteIdentifier, sitePendingFormsNumber, site, iconText, timestamp, tvTagOfflineSite;
        public ImageView iconImp, imgProfile;
        public RelativeLayout iconContainer, iconBack, iconFront;
        public RelativeLayout rootLayout;
        public CardView card;

        public MyViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.card_site_lst_row);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            rootLayout = (RelativeLayout) view.findViewById(R.id.root_layout_message_list_row);

            siteName = (TextView) view.findViewById(R.id.site_list_row_site_name);
            siteAddress = (TextView) view.findViewById(R.id.site_list_row_site_address);
            sitePhone = (TextView) view.findViewById(R.id.site_list_row_site_phone);
            siteIdentifier = (TextView) view.findViewById(R.id.tv_site_identifier);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            tvTagOfflineSite = (TextView) view.findViewById(R.id.tv_tag_offline_site);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
        }
    }

    public class SurveyViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rootLayout;

        public SurveyViewHolder(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.root_layout_survey_form_list_item);
            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onSurveyFormClicked();
                }
            });

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder = null;

        if (viewType == VIEW_TYPE_SURVEY_FORM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.open_survey_from, parent, false);
            holder = new SurveyViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_SITE) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.site_list_row, parent, false);
            holder = new MyViewHolder(itemView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case VIEW_TYPE_SURVEY_FORM:
                SurveyViewHolder surveyViewHolder = (SurveyViewHolder) holder;


                break;
            case VIEW_TYPE_SITE:

                final MyViewHolder siteViewHolder = (MyViewHolder) holder;
                final Site site = siteList.get(position);

                siteViewHolder.siteName.setText(site.getName());
                siteViewHolder.sitePhone.setText(site.getPhone());
                siteViewHolder.siteAddress.setText(site.getAddress());
                siteViewHolder.iconText.setText(site.getName().substring(0, 1));
                siteViewHolder.siteIdentifier.setText(site.getIdentifier());
                siteViewHolder.tvTagOfflineSite.setText(mContext.getString(R.string.msg_offline_site, "Finalized"));
                siteViewHolder.imgProfile.setImageResource(R.drawable.circle_blue);
                siteViewHolder.iconText.setVisibility(View.VISIBLE);
                hidePhoneNumber(siteViewHolder);


                siteViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        toSiteOptionFragment(site);
                    }
                });

                setCardBackground(position, siteViewHolder.rootLayout);
                setOfflineSiteTag(siteViewHolder.tvTagOfflineSite, site);
                allowOfflineSitesActions(site, position, siteViewHolder);
                break;
        }

    }


    private void setOfflineSiteTag(TextView tvTagOfflineSite, Site site) {
        tvTagOfflineSite.setVisibility(site.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE_SITE_SYNCED ? View.GONE : View.VISIBLE);
        switch (site.getIsSiteVerified()) {
            case Constant.SiteStatus.IS_FINALIZED:
                tvTagOfflineSite.setText(mContext.getString(R.string.msg_offline_site, "Finalized"));
                break;
            case Constant.SiteStatus.IS_UNVERIFIED_SITE:
                tvTagOfflineSite.setText(mContext.getString(R.string.msg_offline_site, ""));
                break;

        }


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

    //https://stackoverflow.com/questions/26724964/how-to-animate-recyclerview-items-when-they-appear
    private void setScaleAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

    private void hidePhoneNumber(MyViewHolder holder) {
        String phoneNumber = holder.sitePhone.getText().toString();
        boolean isPhoneEmpty = android.text.TextUtils.isEmpty(phoneNumber);
        holder.sitePhone.setVisibility(isPhoneEmpty ? View.GONE : View.VISIBLE);
    }

    private void allowOfflineSitesActions(Site site, final int position, MyViewHolder holder) {
        switch (site.getIsSiteVerified()) {
            case Constant.SiteStatus.IS_UNVERIFIED_SITE:
            case Constant.SiteStatus.IS_VERIFIED_BUT_UNSYNCED:
            case Constant.SiteStatus.IS_FINALIZED:

                holder.iconContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onIconClicked(position);
                    }
                });

                holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        listener.onIconClicked(position);
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        return true;
                    }
                });

                applyIconAnimation(holder, position);
                break;

        }

    }

    private void toSiteOptionFragment(Site site) {
        if (getSelectedItemCount() > 0) return;

        listener.onUselessLayoutClicked(site);
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(filetredsitelist.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return filetredsitelist.size();
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

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public ArrayList<Site> getSelected() {
        ArrayList<Site> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(siteList.get(i));
        }

        return items;
    }

    public void removeData(int position) {
        filetredsitelist.remove(position);
        resetCurrentIndex();
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {


        //todo use survey form object in future
        boolean isSurveyForm = siteList.get(position).getName().equalsIgnoreCase("survey");

        if (isSurveyForm) {
            return VIEW_TYPE_SURVEY_FORM;
        } else {
            return VIEW_TYPE_SITE;
        }


    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterSites(siteList, charString);
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filetredsitelist = (ArrayList<Site>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private List<Site> filterSites(List<Site> models, String query) {
        if (query.isEmpty()) {
            return siteList;
        }
        final String lowerCaseQuery = query.toLowerCase();
        final List<Site> filteredModelList = new ArrayList<>();
        for (Site model : models) {
            final String text = model.getName().toLowerCase();
            final String address = model.getAddress().toLowerCase();
            final String siteId = model.getIdentifier();
            final String phoneNumber = model.getPhone();
            if (text.contains(lowerCaseQuery) || address.contains(lowerCaseQuery) || siteId.contains(lowerCaseQuery) || phoneNumber.contains(query)) {
                filteredModelList.add(model);
            }
        }
        Timber.d("Found %s sites for search query %q", filteredModelList.size(), query);
        return filteredModelList;
    }

    public void add(Site mc) {
        siteList.add(mc);
        notifyItemInserted(siteList.size() - 1);
    }

    public void addAll(LinkedList<Site> mcList) {
        for (Site mc : mcList) {
            add(mc);
        }
    }

    public void remove(Site city) {
        int position = siteList.indexOf(city);
        if (position > -1) {
            siteList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void swapItems(List<Site> siteList) {

        final SiteListDiffCallback diffCallback = new SiteListDiffCallback(this.siteList, siteList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);


        this.siteList.clear();
        this.siteList.addAll(siteList);

        diffResult.dispatchUpdatesTo(this);
    }

    public Site getItem(int position) {
        return siteList.get(position);
    }

    public interface SiteListAdapterListener {
        void onIconClicked(int position);

        void onRowLongClicked(int position);

        void onUselessLayoutClicked(Site site);

        void onSurveyFormClicked();
    }
}