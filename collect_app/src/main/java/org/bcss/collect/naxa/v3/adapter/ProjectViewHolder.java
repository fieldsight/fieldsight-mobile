package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.login.model.Project;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

class ProjectViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.primary_text)
    TextView primary_text;
    @BindView(R.id.sub_text)
    TextView sub_text;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
    void bindView(Project project) {
        primary_text.setText(project.getName());
        sub_text.setText(project.getAddress());
    }
}
