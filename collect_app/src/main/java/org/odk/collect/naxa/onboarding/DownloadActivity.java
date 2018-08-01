package org.odk.collect.naxa.onboarding;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.anim.ScaleUpAndDownItemAnimator;
import org.odk.collect.naxa.project.ProjectListActivity;
import org.odk.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;

public class DownloadActivity extends CollectAbstractActivity implements DownloadView {


    @BindView(R.id.toggle_button)
    Button toggleButton;

    @BindView(R.id.download_button)
    Button downloadButton;

    @BindView(R.id.activity_download_recycler_view)
    public RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    private DownloadListAdapter downloadListAdapter;
    private DownloadPresenter downloadPresenter;


    public static void start(Context context) {
        Intent intent = new Intent(context, DownloadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        downloadPresenter = new DownloadPresenterImpl(this);
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_downloads));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(DownloadActivity.this, ProjectListActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDataSyncEvent(DataSyncEvent syncEvent) {
//        switch (syncEvent.getUid()) {
//            case ODK_FORMS:
//                updateODKDownloadItem(syncEvent);
//                break;
//        }
//    }
//
//    private void updateODKDownloadItem(DataSyncEvent syncEvent) {
//        switch (syncEvent.getEvent()) {
//            case EVENT_START:
//                Observable
//                        .just(downloadListAdapter.getList())
//                        .flatMapIterable((Function<ArrayList<SyncableItems>, Iterable<SyncableItems>>) downloadableItems -> downloadableItems)
//                        .map(downloadableItem -> {
//                            if (syncEvent.getUid() == downloadableItem.getUid()) {
//                            }
//                            return downloadableItem;
//                        })
//                        .toList()
//                        .subscribe(new SingleObserver<List<SyncableItems>>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//                            }
//
//                            @Override
//                            public void onSuccess(List<SyncableItems> syncableItems) {
//                                downloadListAdapter.updateList(syncableItems);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                e.printStackTrace();
//                            }
//                        });
//                break;
//            case EVENT_UPDATE:
//                break;
//            case EVENT_END:
//                break;
//            case EVENT_ERROR:
//                break;
//        }
//    }


    @Override
    public void setUpRecyclerView(List<SyncableItems> syncableItems) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new ScaleUpAndDownItemAnimator());

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        downloadListAdapter = new DownloadListAdapter((ArrayList<SyncableItems>) syncableItems);
        recyclerView.setAdapter(downloadListAdapter);
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }


    @OnClick({R.id.toggle_button, R.id.download_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toggle_button:
                downloadPresenter.onToggleButtonClick(downloadListAdapter.getList());
                break;
            case R.id.download_button:
                downloadPresenter.onDownloadButtonClick(downloadListAdapter.getList());
                break;
        }
    }

}
