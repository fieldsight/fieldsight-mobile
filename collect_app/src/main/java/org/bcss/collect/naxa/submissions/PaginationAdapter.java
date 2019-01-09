package org.bcss.collect.naxa.submissions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.generalforms.data.FormResponse;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final int END = -1;

    private List<FormResponse> movies;
    private Context context;

    private boolean isLoadingAdded = false;
    public OnCardClickListener listener;
    private boolean isLastPageFooterAdded = false;

    public PaginationAdapter(Context context) {
        this.context = context;
        movies = new ArrayList<>();
    }

    public List<FormResponse> getFormResponses() {
        return movies;
    }

    public void setMovies(List<FormResponse> movies) {
        this.movies = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);

                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
            case END:
                View end = inflater.inflate(R.layout.item_list_end, parent, false);
                viewHolder = new EndItemVH(end);
                break;

        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_list, parent, false);
        viewHolder = new FormVH(v1);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final FormResponse result = movies.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                FormVH formVH = (FormVH) holder;
                ((FormVH) holder).rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onFormClicked(result,view);
                    }
                });

                formVH.tvTitle.setText(formatSubmittedBy(result.getSubmittedByUsername()));
                formVH.tvSubmissionDateTime.setText(formatSubmissionDateTime(result.getDate()));
                break;
            case LOADING:
            case END:
                //Do nothing
                break;
        }

    }

    private String formatSubmittedBy(String name) {
        return context.getString(R.string.msg_form_submitted_by, name);
    }

    private String formatSubmissionDateTime(String dateTime) {


        String msg = "";
        try {
            DateTime dt = DateTime.parse(dateTime);
            msg = dt.toString(DateTimeFormat.longDateTime());


        } catch (Exception e) {
            e.printStackTrace();
            msg = "Cannot load date time";
        }


        return msg;
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    @Override
    public int getItemViewType(int position) {

        int itemType;

        if (position == movies.size() - 1 && isLoadingAdded) {
            itemType = LOADING;
        } else {
            itemType = ITEM;
        }

        return itemType;
    }


    public void add(FormResponse mc) {
        movies.add(mc);
        notifyItemInserted(movies.size() - 1);
    }

    public void addAll(List<FormResponse> mcList) {
        for (FormResponse mc : mcList) {
            add(mc);
        }
    }

    public void remove(FormResponse city) {
        int position = movies.indexOf(city);
        if (position > -1) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new FormResponse());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movies.size() - 1;
        FormResponse item = getItem(position);

        if (item != null) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public FormResponse getItem(int position) {
        return movies.get(position);
    }

    public void addLastPageFooter() {
        isLastPageFooterAdded = true;
        add(new FormResponse());
    }


    protected class FormVH extends RecyclerView.ViewHolder {
        private final CardView rootLayout;
        private TextView tvTitle, tvSubmissionDateTime;

        public FormVH(View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.card_view_item_recycler_view);
            tvTitle = itemView.findViewById(R.id.item_text);
            tvSubmissionDateTime = itemView.findViewById(R.id.tv_item_submitted_by);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    protected class EndItemVH extends RecyclerView.ViewHolder {

        public EndItemVH(View itemView) {
            super(itemView);
        }
    }


    public void setCardClickListener(OnCardClickListener listener) {
        this.listener = listener;
    }

    public interface OnCardClickListener {
        void onFormClicked(FormResponse form, View view);
    }

}