package org.bcss.collect.naxa.submissions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bcss.collect.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/29/17
 * by nishon.tan@gmail.com
 */

public class MultiViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TEXT_DESC = 1;
    private static final int HTTP_URL = 2;

    private ArrayList<ViewModel> listOfItems;
    private OnCardClickListener onCardClickListener;
    private Context context;

    public MultiViewAdapter() {
        listOfItems = new ArrayList<>();
    }

    public List<ViewModel> getListOfItems() {
        return listOfItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TEXT_DESC:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case HTTP_URL:
                View v2 = inflater.inflate(R.layout.item_title_url, parent, false);
                viewHolder = new URLVH(v2);
                break;
        }


        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return listOfItems == null ? 0 : listOfItems.size();
    }


    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_item_flat_title_desc, parent, false);
        viewHolder = new MultiViewAdapter.TitleDescVH(v1);

        return viewHolder;
    }

    protected class TitleDescVH extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvDesc;

        public TitleDescVH(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_list_item_title);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_list_item_desc);
            tvTitle.setTextSize(18);
            tvDesc.setTextSize(18);
        }
    }

    public void add(ViewModel mc) {
        listOfItems.add(mc);
        notifyItemInserted(listOfItems.size() - 1);
    }

    public void addAll(List<ViewModel> mcList) {
        for (ViewModel mc : mcList) {
            add(mc);
        }
    }

    public void remove(ViewModel city) {
        int position = listOfItems.indexOf(city);
        if (position > -1) {
            listOfItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {

        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public ViewModel getItem(int position) {
        return listOfItems.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ViewModel result = listOfItems.get(position);
        context = holder.itemView.getContext();

        switch (getItemViewType(position)) {
            case TEXT_DESC:

                TitleDescVH titleDescVH = ((TitleDescVH) holder);
                titleDescVH.tvTitle.setText(result.getName());
                titleDescVH.tvDesc.setText(result.getDesc());
                break;
            case HTTP_URL:
                URLVH urlvh = ((URLVH) holder);
                urlvh.tvTitle.setText(result.getName());
                urlvh.tvUrl.setText(result.getDesc());

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int itemType;
        ViewModel viewModel = listOfItems.get(position);
        boolean isURL = isValidURL(viewModel.getDesc());

        if (isURL) itemType = HTTP_URL;
        else itemType = TEXT_DESC;


        return itemType;
    }

    private boolean isValidURL(String potentialUrl) {
        return Patterns.WEB_URL.matcher(potentialUrl).matches();
    }


    protected class URLVH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUrl;


        public URLVH(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_list_item_title);
            tvUrl = (TextView) itemView.findViewById(R.id.tv_list_item_url);

        }
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public interface OnCardClickListener {
        void onCardClicked(ViewModel viewModel);
    }

    public void updateList(List<ViewModel> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new TitleDescModelDiffCallback(this.listOfItems, newList));

        this.listOfItems.clear();
        this.listOfItems.addAll(newList);

        diffResult.dispatchUpdatesTo(this);

    }

}

