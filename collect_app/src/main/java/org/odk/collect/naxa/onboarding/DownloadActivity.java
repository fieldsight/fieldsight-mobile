package org.odk.collect.naxa.onboarding;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.odk.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.naxa.generalforms.DisplayGeneralFormsAdapter;
import org.odk.collect.naxa.generalforms.GeneralForm;

import java.util.ArrayList;

public class DownloadActivity extends CollectAbstractActivity {
    private RecyclerView recyclerView;
    private ArrayList<GeneralForm> generalFormList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        bindUI();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DownloadListAdapter downloadListAdapter = new DownloadListAdapter(generalFormList);
        downloadListAdapter.setGeneralFormClickListener(this);
        recyclerView.setAdapter(downloadListAdapter);

    }

    private void bindUI() {
        recyclerView = findViewById(R.id.activity_download_recycler_view);

    }


}
