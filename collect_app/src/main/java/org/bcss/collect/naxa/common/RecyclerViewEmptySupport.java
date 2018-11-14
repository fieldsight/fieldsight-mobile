package org.bcss.collect.naxa.common;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.onboarding.DownloadActivity;

import javax.annotation.Nullable;

import timber.log.Timber;

////https://stackoverflow.com/questions/28217436/how-to-show-an-empty-view-with-a-recyclerview
public class RecyclerViewEmptySupport extends RecyclerView {
    private View emptyView;
    private View progressView;

    private long lastDispatch;


    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            dispatchViewChanges();

        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            dispatchViewChanges();

        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @android.support.annotation.Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            dispatchViewChanges();

        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            dispatchViewChanges();

        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            dispatchViewChanges();

        }

        @Override
        public void onChanged() {
            super.onChanged();
            dispatchViewChanges();
        }


    };


    public void dispatchViewChanges() {

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
    }

    public void setEmptyView(View emptyView, @Nullable String message, OnEmptyLayoutClickListener onEmptyLayoutClickListener) {
        this.emptyView = emptyView;
        TextView tvMsg = this.emptyView.findViewById(R.id.msg);
        if (message != null) {
            tvMsg.setText(message);
        }
        if (onEmptyLayoutClickListener != null) {
            this.emptyView.findViewById(R.id.btn_retry)
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadActivity.start(Collect.getInstance().getApplicationContext());
                            //onEmptyLayoutClickListener.onRetryButtonClick();
                        }
                    });
        }
    }


    public void setProgressView(View progressView) {
        this.progressView = progressView;
        progressView.setVisibility(GONE);
    }


    public void showProgressView(boolean show) {
        if (show) {
            emptyView.setVisibility(GONE);
            RecyclerViewEmptySupport.this.setVisibility(GONE);
            progressView.setVisibility(VISIBLE);
        } else {
            emptyView.setVisibility(VISIBLE);
            RecyclerViewEmptySupport.this.setVisibility(VISIBLE);
            progressView.setVisibility(GONE);
        }
    }

    public interface OnEmptyLayoutClickListener {
        void onRetryButtonClick();
    }


}