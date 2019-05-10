package org.bcss.collect.naxa.v3.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    @BindView(R.id.chkbx_sync)
    CheckBox chkbx_sync;

    @BindView(R.id.tv_project_address)
    TextView project_address;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener((v) ->  itemClicked(getLayoutPosition()));
        chkbx_sync.setOnClickListener((v -> checkBoxChanged(getLayoutPosition(), ((CheckBox)v).isChecked())));
    }
    void bindView(Project project) {
        primary_text.setText(project.getName());
        sub_text.setText(String.format("A project by %s", project.getOrganizationName()));
        project_address.setText(String.format("%s", project.getAddress()));
        chkbx_sync.setChecked(project.isChecked());
        chkbx_sync.setVisibility(project.isChecked() ? View.VISIBLE : View.GONE);
    }

    void checkBoxChanged(int index, boolean isChecked) {

    }

    void itemClicked(int index) {

    }

}
