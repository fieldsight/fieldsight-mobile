package org.odk.collect.naxa.project.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.odk.collect.android.R;
import org.odk.collect.naxa.login.model.Project;

import java.util.List;

import static org.odk.collect.naxa.network.APIEndpoint.BASE_URL;


public class MyProjectsAdapter extends RecyclerView.Adapter<MyProjectsAdapter.MyViewHolder> {

    private List<Project> myProjectList;

    private Context context;
    private View itemView;
    private Typeface face;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, organizationName;
        private ImageView ivLogo;

        private RelativeLayout rootLayout;


        private MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_project_list_name);
            organizationName = view.findViewById(R.id.tv_organization_name);
            ivLogo = view.findViewById(R.id.iv_org_logo);
            rootLayout = view.findViewById(R.id.project_list_item_root_layout);

            title.setTypeface(face);

        }
    }


    public MyProjectsAdapter(final List<Project> myProjectList) {
        this.myProjectList = myProjectList;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Project project = myProjectList.get(holder.getAdapterPosition());

        this.context = holder.title.getContext();

        holder.title.setText(project.getName());

        loadImage(project.getOrganizationlogourl()).into(holder.ivLogo);


        holder.rootLayout.setOnClickListener(view -> {

        });

        holder.organizationName.setText(formatOrganizationName(project.getOrganizationName()));


    }

    @Override
    public int getItemCount() {
        return myProjectList.size();
    }

    private String formatOrganizationName(String name) {
        return "A Project by " + name;
    }

    private DrawableRequestBuilder<String> loadImage(@NonNull String imagePath) {

        return Glide
                .with(context)
                .load(BASE_URL + imagePath)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .fitCenter()
                .crossFade();
    }


}
