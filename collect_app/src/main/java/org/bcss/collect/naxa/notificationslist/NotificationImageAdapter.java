package org.bcss.collect.naxa.notificationslist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.bcss.collect.android.R;

import java.util.List;


public class NotificationImageAdapter extends RecyclerView.Adapter<NotificationImageAdapter.ViewHolder> implements View.OnClickListener {


    private List<NotificationImage> items;

    private OnItemClickListener onItemClickListener;

    public NotificationImageAdapter(List<NotificationImage> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutRes = R.layout.list_item_image_notification;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        v.setOnClickListener(this);


        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationImage item = items.get(position);

        Context context = holder.itemView.getContext();

        Glide.with(context).load(item.getImage()).into(holder.image);

        holder.itemView.setTag(position);


    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(final View v) {
        onItemClickListener.onItemClick(v, (int) v.getTag(), items);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.list_item_notificaiton_iv);

        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position, List<NotificationImage> urls);

    }
}
