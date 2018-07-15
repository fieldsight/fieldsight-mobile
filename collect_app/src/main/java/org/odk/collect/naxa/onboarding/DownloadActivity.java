package org.odk.collect.naxa.onboarding;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;

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
        DownloadableItem downloadableItem = null;
        downloadableItem = new DownloadableItem("1", "0", "", "Projects", "Download Projects and sites");
        downloadableItems.add(downloadableItem);

        downloadableItem = new DownloadableItem("2", "0", "", "General Forms", "Download General Forms");
        downloadableItems.add(downloadableItem);

        setupRecyclerView();

        downloadPresenter = new DownloadPresenterImpl(this);

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
