package org.bcss.collect.naxa.v3.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.sync.ContentDownloadAdapter;
import org.bcss.collect.naxa.sync.DownloadViewModel;
import org.odk.collect.android.activities.CollectAbstractActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;

public class SyncActivity extends CollectAbstractActivity  {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_download_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toggle_button)
    Button toggleButton;
    @BindView(R.id.download_button)
    Button downloadButton;
    @BindView(R.id.layout_network_connectivity)
    RelativeLayout layoutNetworkConnectivity;

    private ContentDownloadAdapter adapter;
    private DownloadViewModel viewModel;
    private DisposableObserver<Boolean> connectivityDisposable;
    boolean isNetworkConnected = true;

    public static void start(Context context) {
        Intent intent = new Intent(context, SyncActivity.class);
        context.startActivity(intent);
    }

    public static void start(Activity context, int outOfSyncUid) {
        Intent intent = new Intent(context, SyncActivity.class);
        intent.putExtra(EXTRA_MESSAGE, outOfSyncUid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);







    }

}
