package org.bcss.collect.naxa.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.bcss.collect.android.R;

/**
 * https://gist.github.com/sheharyarn/5602930ad84fa64c30a29ab18eb69c6e
 * Custom implementation of AdapterDataObserver to show empty layouts
 * for RecyclerView when there's no data
 * <p>
 * Usage:
 * <p>
 * adapter.registerAdapterDataObserver(new RVEmptyObserver(recyclerView, emptyView));
 */
public class RVEmptyObserver extends RecyclerView.AdapterDataObserver {
    private View emptyView;
    private View progressView;
    private RecyclerView recyclerView;
    private OnRetryTapListener onRetryTapListene;



    public interface OnRetryTapListener {
        void onRetryButtonTap();
    }


    /**
     * Constructor to set an Empty View for the RV
     */
    public RVEmptyObserver(RecyclerView rv, View emptyView , OnRetryTapListener onRetryTapListener) {
        this.recyclerView = rv;
        this.emptyView = emptyView;

        this.onRetryTapListene = onRetryTapListener;


        bindUI();
        checkIfEmpty();
    }

    private void bindUI() {
        emptyView.findViewById(R.id.btn_retry).setOnClickListener(v -> onRetryTapListene.onRetryButtonTap());
    }


    /**
     * Check if Layout is empty and show the appropriate view
     */
    private void checkIfEmpty() {
        if (emptyView != null && recyclerView.getAdapter() != null) {
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Abstract method implementations
     */
    @Override
    public void onChanged() {
        checkIfEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        checkIfEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        checkIfEmpty();
    }

}

