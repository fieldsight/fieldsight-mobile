package org.bcss.collect.naxa.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bcss.collect.android.application.Collect;

import java.util.List;


public abstract class BaseRecyclerViewAdapter<L, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<L> l;
    private int layout;

    protected BaseRecyclerViewAdapter(List<L> l, int layout) {
        this.l = l;
        this.layout = layout;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(Collect.getInstance()).inflate(layout, parent, false);
        return attachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        viewBinded(holder, l.get(position));
        holder.itemView.setOnClickListener(v -> {
            onSyncButtonTap(l.get(position));
        });
    }

    public void onSyncButtonTap(L l) {

    }

    public void showProgress() {

    }

    public List<L> getData() {
        return this.l;
    }

    @Override
    public int getItemCount() {
        return l.size();
    }

    public abstract void viewBinded(VH vh, L l);

    public abstract VH attachViewHolder(View view);


}