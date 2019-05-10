package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SyncViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.display_subtext)
    TextView subtext;

    @BindView(R.id.display_name)
    TextView primaryText;

    @BindView(R.id.status_icon)
    ImageView icon;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.btn_cancel_sync)
    Button cancel_sync;

    @BindView(R.id.checkbox)
    CheckBox checkbox;


    public SyncViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
