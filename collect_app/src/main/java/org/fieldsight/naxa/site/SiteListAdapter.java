package org.fieldsight.naxa.site;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.login.model.SiteBuilder;
import org.fieldsight.naxa.site.db.SiteLocalSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import timber.log.Timber;

import static org.fieldsight.naxa.common.ViewUtils.loadImageWithFallback;

public class SiteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<Site> siteList;

    private final SiteListAdapter.SiteListAdapterListener listener;
    private Context contextCompat;
    private List<Site> orginalList;
    private int offlineSiteCount = 0;

    public SiteListAdapter(Context context, List<Site> siteList, SiteListAdapter.SiteListAdapterListener listener) {
        this.listener = listener;
        this.contextCompat = context;
        this.siteList = siteList;
        this.orginalList = this.siteList;
    }

    public void setDataFromDb(List<Site> mSiteList) {
        this.orginalList = mSiteList;
    }

    public void setOfflineSiteCount(int count) {
        this.offlineSiteCount = count;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.site_list_item, parent, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Timber.i(" ======> oncreateViewHolder margin added for %d", viewType);
        view.setLayoutParams(layoutParams);
        return new SiteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SiteViewHolder siteViewHolder = (SiteViewHolder) holder;
        Site site = siteList.get(holder.getAdapterPosition());
        siteViewHolder.siteName.setText(site.getName());
        siteViewHolder.siteAddress.setVisibility(TextUtils.isEmpty(site.getAddress()) ? View.GONE : View.VISIBLE);
        siteViewHolder.siteAddress.setText(site.getAddress());
        if (site.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE) {
            siteViewHolder.viewSpace.setVisibility(View.VISIBLE);
        } else {
            siteViewHolder.viewSpace.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(site.getSite_logo())) {
            loadImageWithFallback(contextCompat, site.getSite_logo()).into(siteViewHolder.siteLogo);
        } else {
            siteViewHolder.siteLogo.setImageResource(R.drawable.fieldsight_logo);
        }
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }

    public List<Site> getAll() {
        return siteList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String queryText = constraint.toString().toLowerCase();
                FilterResults filterResults = new FilterResults();
                List<Site> filteredList = new ArrayList<>();
                if (TextUtils.isEmpty(queryText)) {
                    filteredList.addAll(orginalList);
                } else {
                    for (Site site : orginalList) {
                        if (site.getName().toLowerCase().contains(queryText) || site.getIdentifier().toLowerCase().contains(queryText) || site.getAddress().toLowerCase().contains(queryText)) {
                            filteredList.add(site);
                        }
                    }
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                updateList((List<Site>) results.values);
            }
        };
    }

    public class SiteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_site_name)
        TextView siteName;
        @BindView(R.id.iv_site_logo)
        ImageView siteLogo;
        @BindView(R.id.tv_site_address)
        TextView siteAddress;
        @BindView(R.id.view_space)
        View viewSpace;

        @OnClick(R.id.root_layout_message_list_row)
        void itemClick() {
            listener.onRowClick(siteList.get(getAdapterPosition()));
        }

        @OnLongClick(R.id.root_layout_message_list_row)
        void itemLongClick() {
            if (Constant.SiteStatus.IS_ONLINE != siteList.get(getAdapterPosition()).getIsSiteVerified()
                    && Constant.SiteStatus.IS_EDITED != siteList.get(getAdapterPosition()).getIsSiteVerified()) {
                listener.onRowLongClicked(getAdapterPosition());

            }
        }

        SiteViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public void updateList(List<Site> newList) {
        this.siteList.clear();
        if (newList == null) {
            this.siteList.addAll(this.orginalList);
        } else {
            this.siteList.addAll(newList);
        }
        Timber.i(" ========> total sites = %d", siteList.size());
        Timber.i("======> offline sites = %d", offlineSiteCount);
        int swappedLastPos = 0;
        boolean offlineSiteFound = false;
        if (offlineSiteCount > 0) {
            for (int i = 0; i < siteList.size(); i++) {
                Site mSite = siteList.get(i);
                if (mSite.getIsSiteVerified() == Constant.SiteStatus.IS_OFFLINE) {
                    if (!offlineSiteFound) offlineSiteFound = true;
                    if (swappedLastPos < offlineSiteCount) {
                        Collections.swap(siteList, i, swappedLastPos);
                        swappedLastPos++;
                    } else {
                        break;
                    }
                }
            }
            listener.hasOfflineSite(offlineSiteFound);
        } else {
            listener.hasOfflineSite(false);
        }
        Timber.i("sites total = %d", siteList.size());
        notifyDataSetChanged();

    }

    public interface SiteListAdapterListener {
        void onRowLongClicked(int position);

        void onRowClick(Site site);

        void hasOfflineSite(boolean available);
    }
}
