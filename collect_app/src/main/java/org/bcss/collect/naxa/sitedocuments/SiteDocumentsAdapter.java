package org.bcss.collect.naxa.sitedocuments;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.commons.io.FilenameUtils;
import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.GlideApp;

import java.util.List;

public class SiteDocumentsAdapter extends RecyclerView.Adapter<SiteDocumentsAdapter.ViewHolder> {

    SortedList<String> list;
    private OnSiteDocumentClickListener listener;

    public void setOnSiteDocumentClickListener(OnSiteDocumentClickListener listener) {
        this.listener = listener;
    }

    public SiteDocumentsAdapter() {
        list = new SortedList<String>(String.class, new SortedList.Callback<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(String oldItem, String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(String item1, String item2) {
                return item1.equals(item2);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    //conversation helpers
    public void addAll(List<String> countries) {
        list.beginBatchedUpdates();
        for (int i = 0; i < countries.size(); i++) {
            list.add(countries.get(i));
        }
        list.endBatchedUpdates();
    }

    public String get(int position) {
        return list.get(position);
    }

    public void clear() {
        list.beginBatchedUpdates();
        //remove items at end, to avoid unnecessary array shifting
        while (list.size() > 0) {
            list.removeItemAt(list.size() - 1);
        }
        list.endBatchedUpdates();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_site_document, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = list.get(position);
        holder.textView.setText(FilenameUtils.getName(url));
        String curUrl = list.get(holder.getAdapterPosition());
        boolean isPdf = FilenameUtils.getExtension(url).equalsIgnoreCase("pdf");

        holder.rootLayout.setOnClickListener(v -> {
            if (isPdf) {
                listener.onPDFDocumentClick(curUrl);
            } else {
                listener.onPicutureClick(v, curUrl);
            }
        });

        if(isPdf){
            return;
        }

        GlideApp.with(holder.imageView.getContext())
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        RelativeLayout rootLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_site_document_caption);
            imageView = itemView.findViewById(R.id.iv_site_document_preview);
            rootLayout = itemView.findViewById(R.id.root_layout_site_document);
        }
    }

    public interface OnSiteDocumentClickListener {
        void onPicutureClick(View v, String url);

        void onPDFDocumentClick(String url);
    }

}
