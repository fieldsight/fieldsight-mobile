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
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.v3.network.ApiV3Interface;
import org.json.JSONArray;
import org.json.JSONObject;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import timber.log.Timber;

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
        getDataFromRemote();
    }


    void getDataFromRemote() {
        HashMap<String, String> params = new HashMap<>();
        params.put(APIEndpoint.PARAMS.SITE_ID, loadedSite.getId());

        ServiceGenerator.createCacheService(ApiV3Interface.class)
                .getSiteDocuments(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseBody, SingleSource<List<String>>>() {
                    @Override
                    public SingleSource<List<String>> apply(ResponseBody responseBody) throws Exception {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray result = jsonObject.getJSONArray("blueprints");
                        return io.reactivex.Observable.range(0, result.length())
                                .map(new Function<Integer, String>() {
                                    @Override
                                    public String apply(Integer index) throws Exception {
                                        return result.getString(index);
                                    }
                                }).toList();
                    }
                })
                .subscribe(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(List<String> urls) {
                        adapter.addAll(urls);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Failed to add documents to list due to %s", e.getMessage());
                    }
                });
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
