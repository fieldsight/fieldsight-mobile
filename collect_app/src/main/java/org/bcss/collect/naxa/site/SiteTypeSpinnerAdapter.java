package org.bcss.collect.naxa.site;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created on 8/1/17
 * by nishon.tan@gmail.com
 */

public class SiteTypeSpinnerAdapter extends ArrayAdapter<SiteType> {

    private Context context;

    private List<SiteType> values;

    public SiteTypeSpinnerAdapter(Context context, int textViewResourceId, String hint, List<SiteType> values) {
        super(context, textViewResourceId, values);
        this.context = context;

        SiteType dummy = new SiteType(hint, hint, hint, hint);
        values.add(dummy);

        this.values = values;
    }

    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }


    public SiteType getItem(int position) {
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
