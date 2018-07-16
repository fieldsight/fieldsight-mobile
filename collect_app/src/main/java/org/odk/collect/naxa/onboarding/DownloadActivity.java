package org.odk.collect.naxa.onboarding;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.project.event.ErrorEvent;
import org.odk.collect.naxa.project.event.PayloadEvent;
import org.odk.collect.naxa.project.event.ProgressEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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
    private ArrayList<DownloadableItem> downloadableItems = new ArrayList<>();
    private DownloadListAdapter downloadListAdapter;
    private Button btnToggle;
    private Button btnCancle;
    private Button btnDownload;
    DownloadPresenter downloadPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        bindUI();
        populateItems();
        setupRecyclerView();

        downloadPresenter = new DownloadPresenterImpl(this);

    }

    private void populateItems() {
        DownloadableItem downloadableItem = null;

        downloadableItem = new DownloadableItem(Constant.DownloadUID.PROJECT_SITES, "0", "", "Projects", "Download Projects and sites");
        downloadableItems.add(downloadableItem);

        downloadableItem = new DownloadableItem(GENERAL_FORMS, "0", "", "General Forms", "Download General Forms");
        downloadableItems.add(downloadableItem);

        downloadableItem = new DownloadableItem(Constant.DownloadUID.ODK_FORMS, "0", "", "ODK Forms", "Download ODK Forms");
        downloadableItems.add(downloadableItem);

    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downloadListAdapter = new DownloadListAdapter(downloadableItems);
        downloadListAdapter.setOnClickListener(this);
        recyclerView.setAdapter(downloadListAdapter);
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
                        .flatMapIterable((Function<ArrayList<DownloadableItem>, Iterable<DownloadableItem>>) downloadableItems -> downloadableItems)
                        .map(downloadableItem -> {

                            if (syncEvent.getUid() == downloadableItem.getUid()) {
                                downloadableItem.setDownloadingStatus("DUCK");
                            }

                            return downloadableItem;
                        })
                        .toList()
                        .subscribe(new SingleObserver<List<DownloadableItem>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onSuccess(List<DownloadableItem> downloadableItems) {
                                downloadListAdapter.updateList(downloadableItems);
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
    public void onItemTap(DownloadableItem downloadableItem) {

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
