package org.bcss.collect.naxa.educational;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;

/**
 * Created by susan on 7/5/2017.
 */

public class ViewHolderPDF extends RecyclerView.ViewHolder{

    public static TextView label1, label2;
    public static LinearLayout linearClick;

    public ViewHolderPDF(View itemView) {
        super(itemView);
        label1 = itemView.findViewById(R.id.tv_title);
        linearClick = itemView.findViewById(R.id.linear_layout_click);
//        label2 = (TextView)itemView.findViewById(R.id.tv_desc);
    }

    public TextView getLabel1() {
        return label1;
    }

    public void setLabel1(TextView label1) {
        ViewHolderPDF.label1 = label1;
    }

//    public TextView getLabel2() {
//        return label2;
//    }
//
//    public void setLabel2(TextView label2) {
//        this.label2 = label2;
//    }
}
