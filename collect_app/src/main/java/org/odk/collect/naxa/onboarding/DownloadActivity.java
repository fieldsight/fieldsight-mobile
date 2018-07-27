package org.odk.collect.naxa.onboarding;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.anim.ScaleUpAndDownItemAnimator;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.project.ProjectListActivity;
import org.odk.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;

import static org.odk.collect.naxa.common.Constant.DownloadUID.ODK_FORMS;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_UPDATE;

public class DownloadActivity extends CollectAbstractActivity implements DownloadView {


    @BindView(R.id.toggle_button)
    Button toggleButton;
    @BindView(R.id.download_button)
    Button downloadButton;
    private ArrayList<SyncableItems> syncableItems = new ArrayList<>();
    private DownloadListAdapter downloadListAdapter;
    private Button btnToggle;
    private Button btnClose;
    private Button btnDownload;
    DownloadPresenter downloadPresenter;
    SyncRepository syncRepository;

    @BindView(R.id.activity_download_recycler_view)
    public RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;


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
        syncRepository = new SyncRepository(Collect.getInstance());
        setupRecyclerView();
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

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new ScaleUpAndDownItemAnimator());

        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downloadListAdapter = new DownloadListAdapter(syncableItems);
        recyclerView.setAdapter(downloadListAdapter);


        syncRepository.getAllSyncItems()
                .observe(this, new Observer<List<SyncableItems>>() {
                    @Override
                    public void onChanged(@Nullable List<SyncableItems> items) {
                        downloadListAdapter.updateList(items);
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSyncEvent(DataSyncEvent syncEvent) {
        switch (syncEvent.getUid()) {
            case ODK_FORMS:
                updateODKDownloadItem(syncEvent);
                break;
        }
    }

    private void updateODKDownloadItem(DataSyncEvent syncEvent) {
        switch (syncEvent.getEvent()) {
            case EVENT_START:
                Observable
                        .just(downloadListAdapter.getList())
                        .flatMapIterable((Function<ArrayList<SyncableItems>, Iterable<SyncableItems>>) downloadableItems -> downloadableItems)
                        .map(downloadableItem -> {

                            if (syncEvent.getUid() == downloadableItem.getUid()) {

                            }

                            return downloadableItem;
                        })
                        .toList()
                        .subscribe(new SingleObserver<List<SyncableItems>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<SyncableItems> syncableItems) {
                                downloadListAdapter.updateList(syncableItems);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                break;
            case EVENT_UPDATE:
                break;
            case EVENT_END:
                break;
            case EVENT_ERROR:
                break;
        }
    }


    @Override
    public void toggleAll() {
        Observable.just(downloadListAdapter.getList())
                .flatMapIterable((Function<ArrayList<SyncableItems>, Iterable<SyncableItems>>) syncableItems -> syncableItems)
                .flatMap((Function<SyncableItems, ObservableSource<SyncableItems>>) syncableItems -> {
                    syncableItems.toggleSelected();
                    return Observable.just(syncableItems);
                })
                .toList()
                .subscribe(new DisposableSingleObserver<List<SyncableItems>>() {
                    @Override
                    public void onSuccess(List<SyncableItems> items) {
                        downloadListAdapter.updateList(items);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    public void closeDownloadView() {

    }

    @Override
    public void downloadSelected() {
        downloadPresenter.onDownloadSelectedButtonClick(downloadListAdapter.getList());
    }


    @OnClick({R.id.toggle_button, R.id.download_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toggle_button:
                downloadPresenter.onToggleButtonClick();

                break;
            case R.id.download_button:
                downloadPresenter.onDownloadSelectedButtonClick(downloadListAdapter.getList());

                break;
        }
    }

}
