package org.fieldsight.naxa.site;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.login.model.SiteBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class SiteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<Site> siteList = new ArrayList<>();

    private final SiteListAdapter.SiteListAdapterListener listener;
    private Context contextCompat;
    private List<Site> orginalList;

    public SiteListAdapter(Context context, SiteListAdapter.SiteListAdapterListener listener) {
        this.listener = listener;
        this.contextCompat = context;
        this.orginalList = this.siteList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SiteViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.site_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SiteViewHolder siteViewHolder = (SiteViewHolder) holder;
        Site site = siteList.get(holder.getAdapterPosition());
        siteViewHolder.siteName.setText(site.getName());
        siteViewHolder.siteAddress.setVisibility(TextUtils.isEmpty(site.getAddress()) ? View.GONE : View.VISIBLE);
        siteViewHolder.siteAddress.setText(site.getAddress());
        if (!TextUtils.isEmpty(site.getSite_logo())) {
            Glide.with(contextCompat).load(site.getSite_logo()).apply(RequestOptions.circleCropTransform()).into(siteViewHolder.siteLogo);
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
                String queryText = constraint.toString();
                FilterResults filterResults = new FilterResults();
                List<Site> filteredList = new ArrayList<>();
                if (TextUtils.isEmpty(queryText)) {
                    filteredList.addAll(orginalList);
                } else {
                    for (Site site : orginalList) {
                        if (site.getName().contains(queryText) || site.getIdentifier().contains(queryText) || site.getAddress().contains(queryText)) {
                            filteredList.add(site);
                        }
                    }
                }
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                siteList = (List<Site>) results.values;
                notifyDataSetChanged();
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
        this.siteList.addAll(newList);
        notifyDataSetChanged();
    }




    public interface SiteListAdapterListener {
        void onRowLongClicked(int position);

        void onRowClick(Site site);
    }
}
