package org.bcss.collect.naxa.site;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.bcss.collect.naxa.site.data.SiteRegion;

import java.util.List;

/**
 * Created on 8/1/17
 * by nishon.tan@gmail.com
 */

public class SiteClusterSpinnerAdapter extends ArrayAdapter<SiteRegion> {

    private Context context;

    private List<SiteRegion> values;

    public SiteClusterSpinnerAdapter(Context context, int textViewResourceId, String hint, List<SiteRegion> values) {
        super(context, textViewResourceId, values);
        this.context = context;

        SiteRegion dummy = new SiteRegion(null, hint, null);
        values.add(dummy);

        this.values = values;
    }

    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }


    public SiteRegion getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);


        label.setPadding(18, 16, 16, 16);
        label.setText(values.get(position).getName());

        return label;
    }

    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setPadding(18, 16, 16, 16);
        label.setText(values.get(position).getName());

        return label;
    }

}
