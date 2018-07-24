package org.odk.collect.naxa.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import org.odk.collect.android.R;

//https://stackoverflow.com/questions/28217436/how-to-show-an-empty-view-with-a-recyclerview
public class RecyclerViewEmptySupport extends RecyclerView {
    private View emptyView;
    private OnEmptyLayoutClickListener onEmptyLayoutClickListener;


    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {


        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    RecyclerViewEmptySupport.this.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    RecyclerViewEmptySupport.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    public RecyclerViewEmptySupport(Context context) {
        super(context);
    }

    public RecyclerViewEmptySupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewEmptySupport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView, OnEmptyLayoutClickListener onEmptyLayoutClickListener) {
        this.emptyView = emptyView;
        this.emptyView.findViewById(R.id.btn_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmptyLayoutClickListener.onRetryButtonClick();
            }
        });
    }

    public interface OnEmptyLayoutClickListener {
        void onRetryButtonClick();
    }
}