package org.bcss.collect.naxa.survey;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/29/17
 * by nishon.tan@gmail.com
 */

public class TitleDescAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SurveyForm> listOfItems;
    private OnCardClickListener onCardClickListener;

    public TitleDescAdapter() {
        listOfItems = new ArrayList<>();
    }

    public List<SurveyForm> getListOfItems() {
        return listOfItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        viewHolder = getViewHolder(parent, inflater);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return listOfItems == null ? 0 : listOfItems.size();
    }


    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_item_title_desc, parent, false);
        viewHolder = new TitleDescAdapter.TitleDescVH(v1);

        return viewHolder;
    }

    protected class TitleDescVH extends RecyclerView.ViewHolder {
        public final RelativeLayout rootLayout;
        public TextView tvTitle, tvDesc, tvIconText;

        public TitleDescVH(View itemView) {
            super(itemView);
            rootLayout = (RelativeLayout) itemView.findViewById(R.id.card_view_list_item_title_desc);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_list_item_title);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_list_item_desc);
            tvIconText = (TextView) itemView.findViewById(R.id.title_desc_tv_icon_text);
        }
    }

    public void add(SurveyForm mc) {
        listOfItems.add(mc);
        notifyItemInserted(listOfItems.size() - 1);
    }

    public void addAll(List<SurveyForm> mcList) {
        for (SurveyForm mc : mcList) {
            add(mc);
        }
    }

    public void remove(SurveyForm city) {
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

    public SurveyForm getItem(int position) {
        return listOfItems.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final SurveyForm result = listOfItems.get(position);

        TitleDescVH titleDescVH = ((TitleDescVH) holder);

        titleDescVH.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCardClickListener.onCardClicked(result);
            }
        });

        titleDescVH.tvTitle.setText(result.getName());
        titleDescVH.tvDesc.setText(result.getName());
        titleDescVH.tvIconText.setText(result.getName().substring(0, 1));
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        this.onCardClickListener = onCardClickListener;
    }

    public interface OnCardClickListener {
        void onCardClicked(SurveyForm surveyForm);
    }
}
