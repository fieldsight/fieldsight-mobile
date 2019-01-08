package org.bcss.collect.naxa.onboarding;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import org.bcss.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class DownloadActivityRefactor extends CollectAbstractActivity {

    @BindView(R.id.toggle_button)
    Button toggleButton;

    @BindView(R.id.download_button)
    Button downloadButton;

    @BindView(R.id.activity_download_recycler_view)
    public RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;


    private DownloadListAdapterRefactor downloadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_OBJECT)) {
                int uid = bundle.getInt(EXTRA_OBJECT);

            } else if (bundle.containsKey("run_all")) {

            }
        }

        setupToolbar();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        downloadListAdapter = new DownloadListAdapterRefactor(new ArrayList<>(0));
        recyclerView.setAdapter(downloadListAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_downloads));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
