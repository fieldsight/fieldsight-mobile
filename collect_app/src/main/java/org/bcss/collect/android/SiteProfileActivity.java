package org.bcss.collect.android;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.GlideApp;
import org.bcss.collect.naxa.common.ViewUtils;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.site.CreateSiteActivity;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.sitedocuments.ImageViewerActivity;
import org.bcss.collect.naxa.submissions.MultiViewAdapter;
import org.bcss.collect.naxa.submissions.ViewModel;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class SiteProfileActivity extends CollectAbstractActivity implements MultiViewAdapter.OnCardClickListener {

    Gson gson;
    Site loadedSite;
    private String siteInJson;
    private MultiViewAdapter adapter;
    RecyclerView rvFormHistory;

    private TextView tvSiteName;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    int scrollRange = -1;
    private ImageView ivCircle;
    private HashMap<String, String> titles;
    private TextView tvPlaceHolder;
    String siteId;
    private Toolbar toolbar;

    @BindView(R.id.btn_edit_site)
    Button btnEditSite;

    @BindView(R.id.iv_bg_toolbar)
    ImageView ivBgToolbar;


    public static void start(Context context, String siteId) {
        Intent intent = new Intent(context, SiteProfileActivity.class);
        intent.putExtra(EXTRA_OBJECT, siteId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_profile);
        ButterKnife.bind(this);

        try {
            siteId = getIntent().getExtras().getString(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
            finish();
        }

        gson = new Gson();
        bindUI();

        titles = new HashMap<>();
        titles.put("address", "Address");
        titles.put("identifier", "Identifier");
        titles.put("phone", "Phone Number");
        titles.put("publicDesc", "Public Description");
        titles.put("region", "Region");
        titles.put("type", "Type");

        setupToolbar();

        SiteLocalSource.getInstance()
                .getBySiteId(siteId)
                .observe(this, (loadedSite) -> {
                    if (loadedSite == null) return;
                    tvSiteName.setText(loadedSite.getName());
                    setSiteImage(loadedSite.getLogo());
                    tvPlaceHolder.setText(loadedSite.getName().substring(0, 1));
                    sub(loadedSite);
                    collapsingToolbar.setTitle(loadedSite.getName());
                    SiteProfileActivity.this.loadedSite = loadedSite;
                });


        ivCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadedSite.getLogo() != null)
                    ImageViewerActivity.start(SiteProfileActivity.this, loadedSite.getLogo());
            }
        });

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (scrollRange == -1) {
                scrollRange = appBarLayout.getTotalScrollRange();
            }

            if (scrollRange + verticalOffset == 0) {
                ViewUtils.animateViewVisibility(ivCircle, View.GONE);
            } else {
                ViewUtils.animateViewVisibility(ivCircle, View.VISIBLE);
            }
        });
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

    }

    private void setSiteImage(String logo) {
        if (logo == null || logo.length() == 0) {
            tvPlaceHolder.setVisibility(View.VISIBLE);
            return;
        }
        File logoFile = new File(logo);
        if (logoFile.exists()) {
            tvPlaceHolder.setVisibility(View.GONE);
            ViewUtils.loadLocalImage(SiteProfileActivity.this, logo)
                    .circleCrop()
                    .into(ivCircle);

            ViewUtils.loadLocalImage(SiteProfileActivity.this, logo)
                    .circleCrop()
                    .into(ivBgToolbar);


        } else {
            tvPlaceHolder.setVisibility(View.VISIBLE);
        }
    }


    private void sub(Site loadedSite) {
        setup(loadedSite)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<ArrayList<ViewModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        btnEditSite.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(ArrayList<ViewModel> viewModels) {
                        adapter = new MultiViewAdapter();
                        adapter.setOnCardClickListener(SiteProfileActivity.this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SiteProfileActivity.this, LinearLayoutManager.VERTICAL, false);
                        rvFormHistory.setLayoutManager(linearLayoutManager);
                        rvFormHistory.setAdapter(adapter);
                        rvFormHistory.setItemAnimator(new DefaultItemAnimator());

                        rvFormHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                        rvFormHistory.setHasFixedSize(true);
                        rvFormHistory.setNestedScrollingEnabled(false);
                        btnEditSite.setVisibility(View.VISIBLE);

                        adapter.addAll(viewModels);

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<ArrayList<ViewModel>> setup(Site loadedSite) {
        return Observable.fromCallable(new Callable<ArrayList<ViewModel>>() {
            @Override
            public ArrayList<ViewModel> call() throws Exception {
                JSONObject json = new JSONObject(gson.toJson(loadedSite));
                Iterator<String> iter = json.keys();
                ViewModel answer;
                ArrayList<ViewModel> answers = new ArrayList<>();

                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = String.valueOf(json.get(key));

                    if (titles.containsKey(key)) {
                        answer = new ViewModel(titles.get(key), value, "id", "id");
                        answers.add(answer);
                    }

                    if ("metaAttributes".equals(key)) {
                        if (value.trim().length() == 0) continue;
                        JSONObject metaAttrsJSON = new JSONObject(value);
                        Iterator<String> metaAttrsIter = metaAttrsJSON.keys();
                        while (metaAttrsIter.hasNext()) {
                            String metaAttrsKey = metaAttrsIter.next();
                            String metaAttrsValue = metaAttrsJSON.getString(metaAttrsKey);
                            Timber.i("key: %s value: %s", metaAttrsKey, metaAttrsValue);

                            answer = new ViewModel(metaAttrsKey.replace("_", " "), metaAttrsValue, "id", "id");
                            answers.add(answer);
                        }
                    }

                }


                return answers;
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new MultiViewAdapter();
        adapter.setOnCardClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvFormHistory.setLayoutManager(linearLayoutManager);
        rvFormHistory.setAdapter(adapter);
        rvFormHistory.setItemAnimator(new DefaultItemAnimator());

        rvFormHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        rvFormHistory.setHasFixedSize(true);
        rvFormHistory.setNestedScrollingEnabled(false);
    }

    private void bindUI() {
        rvFormHistory = findViewById(R.id.recycler_view);
        tvSiteName = findViewById(R.id.tv_site_name);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar_flexible);
        ivCircle = findViewById(R.id.iv_site);
        tvPlaceHolder = findViewById(R.id.iv_placeholder_text);
    }

    @Override
    public void onCardClicked(ViewModel viewModel) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_edit_site)
    public void onViewClicked() {

        ProjectLocalSource.getInstance().getProjectById(loadedSite.getProject())
                .observe(this, project -> {
                    if (project == null) {
                        ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
                        return;
                    }

                    CreateSiteActivity.start(SiteProfileActivity.this, project, loadedSite);
                });

    }
}
