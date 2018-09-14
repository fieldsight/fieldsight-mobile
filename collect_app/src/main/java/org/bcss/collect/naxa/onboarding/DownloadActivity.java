package org.bcss.collect.naxa.onboarding;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.anim.ScaleUpAndDownItemAnimator;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.project.ProjectListActivity;
import org.bcss.collect.naxa.sync.SyncRepository;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class DownloadActivity extends CollectAbstractActivity implements DownloadView {


    @BindView(R.id.toggle_button)
    Button toggleButton;

    @BindView(R.id.download_button)
    Button downloadButton;

    @BindView(R.id.activity_download_recycler_view)
    public RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    private DownloadListAdapter downloadListAdapter;
    private DownloadPresenter downloadPresenter;

    public static void start(Activity context, int outOfSyncUid) {
        Intent intent = new Intent(context, DownloadActivity.class);
        intent.putExtra(EXTRA_OBJECT, outOfSyncUid);
        context.startActivity(intent);
    }


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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int uid = bundle.getInt(EXTRA_OBJECT);
            downloadPresenter.startDownload(uid);
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
        recyclerView.setLayoutManager(layoutManager);
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


    @Override
    public void addAdapter(List<SyncableItems> syncableItems) {


        Observable<Integer> notificaitonCountForm = FieldSightNotificationLocalSource.getInstance().anyFormsOutOfSync().toObservable();
        Observable<Integer> notificaitonCountSites = FieldSightNotificationLocalSource.getInstance().anyProjectSitesOutOfSync().toObservable();


        Observable.just(syncableItems)
                .flatMapIterable((Function<List<SyncableItems>, Iterable<SyncableItems>>) syncableItems1 -> syncableItems1)
                .flatMap(new Function<SyncableItems, ObservableSource<SyncableItems>>() {
                    @Override
                    public ObservableSource<SyncableItems> apply(SyncableItems syncableItems) throws Exception {
                        return notificaitonCountForm
                                .map(new Function<Integer, SyncableItems>() {
                                    @Override
                                    public SyncableItems apply(Integer integer) throws Exception {
                                        switch (syncableItems.getUid()) {
                                            case Constant.DownloadUID.ALL_FORMS:
                                                syncableItems.setOutOfSync(integer > 0);
                                                break;
                                        }

                                        return syncableItems;
                                    }
                                });
                    }
                })
                .flatMap(new Function<SyncableItems, ObservableSource<SyncableItems>>() {
                    @Override
                    public ObservableSource<SyncableItems> apply(SyncableItems syncableItems) throws Exception {
                        return notificaitonCountSites
                                .map(new Function<Integer, SyncableItems>() {
                                    @Override
                                    public SyncableItems apply(Integer integer) throws Exception {
                                        switch (syncableItems.getUid()) {
                                            case Constant.DownloadUID.PROJECT_SITES:
                                                syncableItems.setOutOfSync(integer > 0);
                                                break;
                                        }

                                        return syncableItems;
                                    }
                                });
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<SyncableItems>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Timber.i("OnSubscribe");
                    }

                    @Override
                    public void onSuccess(List<SyncableItems> syncableItems) {
                        downloadListAdapter = new DownloadListAdapter((ArrayList<SyncableItems>) syncableItems);
                        recyclerView.setAdapter(downloadListAdapter);
                        Timber.i("onSuccess");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Timber.i("onError");
                    }
                });


//        Observable.fromPublisher(notificaitonCountForm)
//                .flatMapIterable(new Function<Integer, Iterable<SyncableItems>>() {
//                    @Override
//                    public Iterable<SyncableItems> apply(Integer integer) throws Exception {
//
//                        return null;
//                    }
//                });
//
//


    }


    private void runLayoutAnimation(final RecyclerView recyclerView) {

        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }


    @OnClick({R.id.toggle_button, R.id.download_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toggle_button:
                downloadPresenter.onToggleButtonClick(downloadListAdapter.getList());
                break;
            case R.id.download_button:
                downloadPresenter.onDownloadButtonClick(downloadListAdapter.getList());
                break;
        }
    }


}
