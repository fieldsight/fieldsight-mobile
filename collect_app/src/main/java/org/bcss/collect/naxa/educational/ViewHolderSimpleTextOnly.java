package org.bcss.collect.naxa.educational;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.bcss.collect.android.R;

/**
 * Created by susan on 7/5/2017.
 */

public class ViewHolderSimpleTextOnly extends RecyclerView.ViewHolder{

    private TextView sTextView;

    public ViewHolderSimpleTextOnly(View itemView) {
        super(itemView);
        sTextView = (TextView)itemView.findViewById(R.id.simple_title);
    }

    public TextView getsTextView() {
        return sTextView;
    }

    public void setsTextView(TextView sTextView) {
        this.sTextView = sTextView;
    }
}
