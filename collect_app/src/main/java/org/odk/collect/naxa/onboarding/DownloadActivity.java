package org.odk.collect.naxa.onboarding;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.naxa.generalforms.GeneralForm;

import java.sql.Date;
import java.util.ArrayList;

public class DownloadActivity extends CollectAbstractActivity implements DownloadListAdapter.onDownLoadItemClick, DownloadView {
    private RecyclerView recyclerView;
    private ArrayList<DownloadableItem> downloadableItems = new ArrayList<>();
    private DownloadListAdapter downloadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        bindUI();
        DownloadableItem downloadableItem = new DownloadableItem("1", "0", "", "Projects", "Projects");
        downloadableItems.add(downloadableItem);

        setupRecyclerView();

    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downloadListAdapter = new DownloadListAdapter(downloadableItems);
        downloadListAdapter.setGeneralFormClickListener(this);
        recyclerView.setAdapter(downloadListAdapter);
    }

    private void bindUI() {
        recyclerView = findViewById(R.id.activity_download_recycler_view);
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
