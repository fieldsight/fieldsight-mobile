package org.fieldsight.naxa.common.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import timber.log.Timber;


public abstract class BaseRecyclerViewAdapter<L, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    private final List<L> l;
    private final int layout;

    protected BaseRecyclerViewAdapter(List<L> l, int layout) {
        this.l = l;
        this.layout = layout;
        Timber.d("listSize = %s", l.size());
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return attachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        viewBinded(holder, l.get(position));
    }

    public List<L> getData() {
        return this.l;
    }

    @Override
    public int getItemCount() {
        return l.size();
    }

    public abstract void viewBinded(H vh, L l);

    public abstract H attachViewHolder(View view);


}