package org.odk.collect.naxa.site;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.naxa.common.ViewUtils;
import org.odk.collect.naxa.login.model.Site;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private Context mContext;
    private List<Site> siteList;
    private LayoutInflater mLayoutInflater;
    private boolean mIsFilterList;

    public SearchAdapter(Context context, List<Site> siteList, boolean isFilterList) {
        this.mContext = context;
        this.siteList = siteList;
        this.mIsFilterList = isFilterList;
    }


    public void updateList(List<Site> filterList, boolean isFilterList) {
        this.siteList = filterList;
        this.mIsFilterList = isFilterList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return siteList != null ? siteList.size() : 0;
    }

    @Override
    public String getItem(int position) {
        return siteList.get(position).getName();
    }

    public Site getMySiteLocationPojo(int position) {
        return siteList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {

            holder = new ViewHolder();

            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = mLayoutInflater.inflate(R.layout.list_item_search, parent, false);
            holder.tvSiteName = (TextView) v.findViewById(R.id.search_item_site_name);
            holder.tvSiteId = (TextView) v.findViewById(R.id.search_item_site_identifier);
            holder.tvSiteAddress = (TextView) v.findViewById(R.id.search_item_site_address);
            holder.tvPhoneNumber = (TextView) v.findViewById(R.id.search_item_site_phone_number);
            holder.tvIconText = v.findViewById(R.id.title_desc_tv_icon_text);

            v.setTag(holder);
        } else {

            holder = (ViewHolder) v.getTag();
        }

        Site site = siteList.get(position);
        ViewUtils.showOrHide(holder.tvIconText, site.getName().substring(0, 1));
        ViewUtils.showOrHide(holder.tvSiteName, site.getName());
        ViewUtils.showOrHide(holder.tvSiteId, site.getIdentifier());
        ViewUtils.showOrHide(holder.tvPhoneNumber, site.getPhone());
        ViewUtils.showOrHide(holder.tvSiteAddress, site.getAddress());


        return v;
    }


}

class ViewHolder {
    TextView tvSiteName, tvIconText, tvSiteId, tvSiteAddress, tvPhoneNumber;

}





