package org.odk.collect.naxa.onboarding;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.sync.SyncRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

import static org.odk.collect.naxa.common.Constant.DownloadUID.GENERAL_FORMS;
import static org.odk.collect.naxa.common.Constant.DownloadUID.ODK_FORMS;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_UPDATE;

public class DownloadActivity extends CollectAbstractActivity implements DownloadListAdapter.onDownLoadItemClick, DownloadView {
    private RecyclerView recyclerView;
    private ArrayList<SyncableItems> syncableItems = new ArrayList<>();
    private DownloadListAdapter downloadListAdapter;
    private Button btnToggle;
    private Button btnCancle;
    private Button btnDownload;
    DownloadPresenter downloadPresenter;
    SyncRepository syncRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        downloadPresenter = new DownloadPresenterImpl(this);
        syncRepository = new SyncRepository(Collect.getInstance());

        bindUI();
        setupRecyclerView();

    }


    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downloadListAdapter = new DownloadListAdapter(syncableItems);
        downloadListAdapter.setOnClickListener(this);
        recyclerView.setAdapter(downloadListAdapter);


        syncRepository.getAllSyncItems().observe(this, new Observer<List<SyncableItems>>() {
            @Override
            public void onChanged(@Nullable List<SyncableItems> items) {
                downloadListAdapter.updateList(items);
            }
        });
    }


    private void bindUI() {
        recyclerView = findViewById(R.id.activity_download_recycler_view);
        btnToggle = findViewById(R.id.toggle_button);
        btnCancle = findViewById(R.id.cancel_button);
        btnDownload = findViewById(R.id.download_button);

        btnToggle.setOnClickListener(v -> downloadPresenter.onToggleButtonClick());

        btnCancle.setOnClickListener(v -> {
            //     downloadPresenter.onDownloadSelectedButtonClick();
        });

        btnDownload.setOnClickListener(v -> {
            downloadPresenter.onDownloadSelectedButtonClick();
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
    public void onItemTap(SyncableItems syncableItems) {

    }

    @Override
    public void toggleAll() {

    }

    @Override
    public void closeDownloadView() {

    }

    @Override
    public void downloadSelected() {

    }
}
