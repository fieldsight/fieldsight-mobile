package org.bcss.collect.naxa.sitedocuments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.GridItemDecoration;
import org.bcss.collect.naxa.common.RecyclerViewEmptySupport;
import org.bcss.collect.naxa.login.model.Site;
import org.odk.collect.android.activities.CollectAbstractActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class SiteDocumentsListActivity extends CollectAbstractActivity implements SiteDocumentsAdapter.OnSiteDocumentClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_site_documents)
    RecyclerViewEmptySupport rvSiteDocuments;
    @BindView(R.id.root_layout_empty_layout)
    RelativeLayout emptyLayout;
    @BindView(R.id.fab_scroll_to_top)
    FloatingActionButton fabScrollToTop;
    private SiteDocumentsAdapter adapter;
    private Site loadedSite;

    public static void start(Context context, Site loadedSite) {
        Intent intent = new Intent(context, SiteDocumentsListActivity.class);
        intent.putExtra(EXTRA_OBJECT, loadedSite);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_documents_list);
        ButterKnife.bind(this);

        loadedSite = getIntent().getExtras().getParcelable(EXTRA_OBJECT);

        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_site_documents));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new GridLayoutManager(this, 2);

        rvSiteDocuments.setLayoutManager(manager);
        adapter = new SiteDocumentsAdapter();
        adapter.setOnSiteDocumentClickListener(this);
        rvSiteDocuments.setAdapter(adapter);
        rvSiteDocuments.setEmptyView(emptyLayout, getString(R.string.empty_message_site_documents), null);
        rvSiteDocuments.addItemDecoration(new GridItemDecoration(16, 2));
        if(loadedSite.getSiteDocuments() != null){
            adapter.addAll(loadedSite.getSiteDocuments());
        }

    }

    @Override
    public void onPicutureClick(View v, String url) {
        ImageViewerActivity.start(this, url);
    }

    @Override
    public void onPDFDocumentClick(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
