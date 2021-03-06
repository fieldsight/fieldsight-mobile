package org.fieldsight.naxa.common;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PairSpinnerAdapter extends ArrayAdapter<Pair> {


    private final Context context;

    private final List<Pair> values;

    public PairSpinnerAdapter(Context context, int textViewResourceId, List<Pair> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }


    public Pair getItem(int position) {
        return values.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
//        label.setTextColor(Color.BLACK);

        label.setTextSize(16);
        label.setPadding(18, 16, 16, 16);
        label.setText((String) values.get(position).second);

        return label;
    }

    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
//        label.setTextColor(Color.BLACK);
        label.setTextSize(16);
        label.setPadding(18, 16, 16, 16);
        label.setText((String) values.get(position).second);

        return label;
    }
}
