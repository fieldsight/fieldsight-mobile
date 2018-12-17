package org.bcss.collect.naxa.sync;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.onboarding.SyncableItem;
import org.bcss.collect.naxa.site.db.SiteLocalSource;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadStatus.PENDING;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDITED_SITES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.OFFLINE_SITES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.PROJECT_SITES;
import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;
import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_EDITED;
import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_OFFLINE;

public class DownloadActivityRefresh extends CollectAbstractActivity implements OnItemClickListener<Sync> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_download_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toggle_button)
    Button toggleButton;
    @BindView(R.id.download_button)
    Button downloadButton;

    private DownloadListAdapterNew adapter;
    private DownloadViewModel viewModel;


    public static void start(Context context) {
        Intent intent = new Intent(context, DownloadActivityRefresh.class);
        context.startActivity(intent);
    }

    public static void start(Activity context, int outOfSyncUid) {
        Intent intent = new Intent(context, DownloadActivityRefresh.class);
        intent.putExtra(EXTRA_MESSAGE, outOfSyncUid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        setupToolbar();
        setupRecyclerView();
        setupViewModel();



        int count = (ServiceGenerator.getQueuedAPICount() + ServiceGenerator.getRunningAPICount());
        if (count == 0) {
            viewModel.setAllRunningTaskAsFailed();
        }

        SyncLocalSource.getINSTANCE()
                .init()
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Timber.i("Insert complete on sync table");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Insert failed on sync table reason: %s", e.getMessage());
                        e.printStackTrace();
                    }
                });

        SiteLocalSource.getInstance()
                .getAllByStatus(IS_EDITED)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Site>>() {
                    @Override
                    public void onSuccess(List<Site> sites) {
                        String msg = String.format("Upload %s Edited Site(s)", sites.size());
                        if (sites.size() > 0) {
                            SyncLocalSource.getINSTANCE().saveAsAsync(new Sync(EDITED_SITES, PENDING, "Edited Site(s)", msg));
                        } else {
                            SyncLocalSource.getINSTANCE().deleteById(EDITED_SITES);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        SiteLocalSource.getInstance()
                .getAllByStatus(IS_OFFLINE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Site>>() {
                    @Override
                    public void onSuccess(List<Site> sites) {
                        if (sites.size() > 0) {
                            String msg = String.format("Upload %s Offline Site(s)", sites.size());
                            SyncLocalSource.getINSTANCE().saveAsAsync(new Sync(OFFLINE_SITES, PENDING, "Offline Site(s)", msg));
                        } else {
                            SyncLocalSource.getINSTANCE().deleteById(OFFLINE_SITES);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


        SyncLocalSource.getINSTANCE().getAll()
                .observe(this, new Observer<List<Sync>>() {
                    @Override
                    public void onChanged(@Nullable List<Sync> syncs) {

                        addOutOfSyncMessage(syncs);
                    }
                });


        SyncLocalSource.getINSTANCE()
                .selectedItemCountLive()
                .observe(this, integer -> {
                    if (integer == null) return;
                    if (integer > 0) {
                        toggleButton.setText(getString(R.string.clear_all));
                        downloadButton.setEnabled(true);
                    } else {
                        downloadButton.setEnabled(false);
                        toggleButton.setText(getString(R.string.select_all));
                    }
                });

        SyncLocalSource.getINSTANCE()
                .runningItemCountLive()
                .observe(this, integer -> {
                    if (integer == null) return;
                    if (integer > 0) {
                        downloadButton.setText(R.string.stop_download);
                    } else {
                        downloadButton.setText(R.string.download);
                    }
                });

        try {
            int outOfSyncId = getIntent().getExtras().getInt(EXTRA_MESSAGE);
            viewModel.downloadOneItem(outOfSyncId);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void addOutOfSyncMessage(List<Sync> syncs) {


        Observable<Integer> notificaitonCountForm = FieldSightNotificationLocalSource.getInstance().anyFormsOutOfSync().toObservable();
        Observable<Integer> notificaitonCountSites = FieldSightNotificationLocalSource.getInstance().anyProjectSitesOutOfSync().toObservable();
        Observable<Integer> notificaitonCountPreviousSubmission = FieldSightNotificationLocalSource.getInstance().anyFormStatusChangeOutOfSync().toObservable();


        Observable.just(syncs)
                .flatMapIterable((Function<List<Sync>, Iterable<Sync>>) syncableItems1 -> syncableItems1)
                .flatMap(new Function<Sync, ObservableSource<Sync>>() {
                    @Override
                    public ObservableSource<Sync> apply(Sync syncableItem) throws Exception {
                        return notificaitonCountForm
                                .map(new Function<Integer, Sync>() {
                                    @Override
                                    public Sync apply(Integer integer) throws Exception {
                                        switch (syncableItem.getUid()) {
                                            case Constant.DownloadUID.ALL_FORMS:
                                                syncableItem.setOutOfSync(integer > 0);
                                                break;
                                        }

                                        return syncableItem;
                                    }
                                });
                    }
                })
                .flatMap(new Function<Sync, ObservableSource<Sync>>() {
                    @Override
                    public ObservableSource<Sync> apply(Sync syncableItem) throws Exception {
                        return notificaitonCountSites
                                .map(new Function<Integer, Sync>() {
                                    @Override
                                    public Sync apply(Integer integer) throws Exception {
                                        switch (syncableItem.getUid()) {
                                            case Constant.DownloadUID.PROJECT_SITES:
                                                syncableItem.setOutOfSync(integer > 0);
                                                break;
                                        }

                                        return syncableItem;
                                    }
                                });
                    }
                })
                .flatMap(new Function<Sync, ObservableSource<Sync>>() {
                    @Override
                    public ObservableSource<Sync> apply(Sync syncableItem) throws Exception {
                        return notificaitonCountPreviousSubmission
                                .map(new Function<Integer, Sync>() {
                                    @Override
                                    public Sync apply(Integer integer) throws Exception {
                                        switch (syncableItem.getUid()) {
                                            case Constant.DownloadUID.PREV_SUBMISSION:
                                                syncableItem.setOutOfSync(integer > 0);
                                                break;
                                        }

                                        return syncableItem;
                                    }
                                });
                    }
                })
//                .flatMap(new Function<SyncableItem, ObservableSource<SyncableItem>>() {
//                    @Override
//                    public ObservableSource<SyncableItem> apply(SyncableItem syncableItem) throws Exception {
//                        boolean hasAPIRunning = ServiceGenerator.getRunningAPICount() > 0;
//                        if(!hasAPIRunning){
//                            SyncRepository.getInstance().setError(syncableItem.getUid());
//                        }
//                        return Observable.just(syncableItem);
//                    }
//                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Sync>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Sync> syncableItems) {
                        adapter.updateList(syncableItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setupViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(DownloadViewModel.class);
    }

    @OnClick({R.id.toggle_button, R.id.download_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toggle_button:
                SyncLocalSource.getINSTANCE()
                        .toggleAllChecked()
                        .subscribeOn(Schedulers.io())
                        .subscribe(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Timber.i("Update completed on sync table");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e("Update failed on sync table reason: %s", e.getMessage());
                                e.printStackTrace();
                            }
                        });
                break;
            case R.id.download_button:

                if (getString(R.string.download).contentEquals(downloadButton.getText())) {
                    runDownload();
                } else if (getString(R.string.stop_download).contentEquals(downloadButton.getText())) {
                    stopDownload();
                }


                break;
        }
    }

    private void stopDownload() {
        viewModel.cancelAllTask();
    }

    private void runDownload() {
        SyncLocalSource.getINSTANCE().getAllChecked()
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<List<Sync>>() {
                    @Override
                    public void onSuccess(List<Sync> syncs) {
                        viewModel.queueSyncTask(syncs);
                        Timber.i("Select completed on sync table");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Select failed on sync table reason: %s", e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.setOnItemClickListener(null);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_downloads));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new DownloadListAdapterNew(new ArrayList<>(0));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickPrimaryAction(Sync sync) {
        SyncLocalSource.getINSTANCE()
                .toggleSingleItem(sync)
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Timber.i("Update completed on sync table");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Update failed on sync table reason: %s", e.getMessage());
                        e.printStackTrace();
                    }
                })
        ;
    }

    @Override
    public void onClickSecondaryAction(Sync sync) {
//        SyncLocalSource.getINSTANCE().toggleSingleItem(sync);
        SyncLocalSource.getINSTANCE().markAsPending(sync.getUid());
    }
}
