package org.bcss.collect.naxa.sync;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.common.AnimationUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.InternetUtils;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDITED_SITES;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.OFFLINE_SITES;
import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;
import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_EDITED;
import static org.bcss.collect.naxa.common.Constant.SiteStatus.IS_OFFLINE;

public class ContentDownloadActivity extends CollectAbstractActivity implements OnItemClickListener<DownloadableItem> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_download_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toggle_button)
    Button toggleButton;
    @BindView(R.id.download_button)
    Button downloadButton;
    @BindView(R.id.layout_network_connectivity)
    RelativeLayout layoutNetworkConnectivity;


    private ContentDownloadAdapter adapter;
    private DownloadViewModel viewModel;
    private DisposableObserver<Boolean> connectivityDisposable;
    boolean isNetworkConnected = true;

    public static void start(Context context) {
        Intent intent = new Intent(context, ContentDownloadActivity.class);
        context.startActivity(intent);
    }

    public static void start(Activity context, int outOfSyncUid) {
        Intent intent = new Intent(context, ContentDownloadActivity.class);
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


//        int count = (ServiceGenerator.getQueuedAPICount() + ServiceGenerator.getRunningAPICount());
//        int count = 0;
//        if (count == 0) {
//            viewModel.setAllRunningTaskAsFailed();
//        }

        DownloadableItemLocalSource.getINSTANCE()
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

                        if (sites.size() > 0) {
                            String msg = String.format("Upload %s Edited Site(s)", sites.size());
                            DownloadableItemLocalSource.getINSTANCE().markAsPending(EDITED_SITES, msg);
                        } else {
                            DownloadableItemLocalSource.getINSTANCE().markAsDisabled(EDITED_SITES, "No, edited sites present");
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
                            DownloadableItemLocalSource.getINSTANCE().markAsPending(OFFLINE_SITES, msg);
                        } else {
                            DownloadableItemLocalSource.getINSTANCE().markAsDisabled(OFFLINE_SITES, "No, offline sites present");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


        DownloadableItemLocalSource.getINSTANCE().getAll()
                .observe(this, new Observer<List<DownloadableItem>>() {
                    @Override
                    public void onChanged(@Nullable List<DownloadableItem> downloadableItems) {

                        addOutOfSyncMessage(downloadableItems);
                    }
                });


        DownloadableItemLocalSource.getINSTANCE()
                .selectedItemCountLive()
                .observe(this, integer -> {
                    if (integer == null) return;
                    if (integer > 0) {
                        toolbar.setTitle(String.format(Locale.US, "Sync (%d)", integer));
                        toggleButton.setText(getString(R.string.clear_all));
                        downloadButton.setEnabled(true);
                    } else {
                        toolbar.setTitle("Sync");
                        downloadButton.setEnabled(false);
                        toggleButton.setText(getString(R.string.select_all));
                    }
                });

        DownloadableItemLocalSource.getINSTANCE()
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
        adapter.setOnItemClickListener(this);
    }

    private void addOutOfSyncMessage(List<DownloadableItem> downloadableItems) {


        Observable<Integer> notificaitonCountForm = FieldSightNotificationLocalSource.getInstance().anyFormsOutOfSync().toObservable();
        Observable<Integer> notificaitonCountSites = FieldSightNotificationLocalSource.getInstance().anyProjectSitesOutOfSync().toObservable();
        Observable<Integer> notificaitonCountPreviousSubmission = FieldSightNotificationLocalSource.getInstance().anyFormStatusChangeOutOfSync().toObservable();


        Observable.just(downloadableItems)
                .flatMapIterable((Function<List<DownloadableItem>, Iterable<DownloadableItem>>) syncableItems1 -> syncableItems1)
                .flatMap(new Function<DownloadableItem, ObservableSource<DownloadableItem>>() {
                    @Override
                    public ObservableSource<DownloadableItem> apply(DownloadableItem syncableItem) {
                        return notificaitonCountForm
                                .map(new Function<Integer, DownloadableItem>() {
                                    @Override
                                    public DownloadableItem apply(Integer integer) {
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
                .flatMap(new Function<DownloadableItem, ObservableSource<DownloadableItem>>() {
                    @Override
                    public ObservableSource<DownloadableItem> apply(DownloadableItem syncableItem) {
                        return notificaitonCountSites
                                .map(new Function<Integer, DownloadableItem>() {
                                    @Override
                                    public DownloadableItem apply(Integer integer) {
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
                .flatMap(new Function<DownloadableItem, ObservableSource<DownloadableItem>>() {
                    @Override
                    public ObservableSource<DownloadableItem> apply(DownloadableItem syncableItem) {
                        return notificaitonCountPreviousSubmission
                                .map(new Function<Integer, DownloadableItem>() {
                                    @Override
                                    public DownloadableItem apply(Integer integer) {
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
                .subscribe(new SingleObserver<List<DownloadableItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<DownloadableItem> syncableItems) {
                        if (adapter.getAll().size() == 0) {
                            adapter.updateList(syncableItems);
                            AnimationUtils.runLayoutAnimation(recyclerView);
                        } else {
                            adapter.updateList(syncableItems);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    private void setupViewModel() {
//        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        viewModel = ViewModelProviders.of(this).get(DownloadViewModel.class);
    }

    @OnClick({R.id.toggle_button, R.id.download_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toggle_button:
                DownloadableItemLocalSource.getINSTANCE()
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
                if (!isNetworkConnected) {
                    stopDownload();
                    return;
                }

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
        DownloadableItemLocalSource.getINSTANCE().getAllChecked()
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableSingleObserver<List<DownloadableItem>>() {
                    @Override
                    public void onSuccess(List<DownloadableItem> downloadableItems) {
                        viewModel.queueSyncTask(downloadableItems);
                        Timber.i("Select completed on sync table");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Select failed on sync table reason: %s", e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private void setupInternetLayout(boolean show) {
        layoutNetworkConnectivity.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        adapter.setOnItemClickListener(this);
        connectivityDisposable = InternetUtils.observeInternetConnectivity(new InternetUtils.OnConnectivityListener() {
            @Override
            public void onConnectionSuccess() {
                setupInternetLayout(false);
                Timber.d("We have internet");
                isNetworkConnected = true;
            }

            @Override
            public void onConnectionFailure() {
                setupInternetLayout(true);
                Timber.d("We don't have internet");
                isNetworkConnected = false;
            }

            @Override
            public void onCheckComplete() {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
//        adapter.setOnItemClickListener(null);
        connectivityDisposable.dispose();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_downloads));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                layoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new ContentDownloadAdapter(new ArrayList<>(0));
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
//    this is triggered when itemview in recycler is clicked
    @Override
    public void onClickPrimaryAction(DownloadableItem downloadableItem) {
        DownloadableItemLocalSource.getINSTANCE()
                .toggleSingleItem(downloadableItem)
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

//cancel button if clicked trigger this callback
    @Override
    public void onClickSecondaryAction(DownloadableItem downloadableItem) {
//        DownloadableItemLocalSource.getINSTANCE().toggleSingleItem(downloadableItem);
        DownloadableItemLocalSource.getINSTANCE().markAsPending(downloadableItem.getUid());
    }
}
