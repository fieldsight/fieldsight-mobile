package org.bcss.collect.naxa.notificationslist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.network.NetworkUtils;
import org.bcss.collect.naxa.v3.network.NotificationRemoteSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import timber.log.Timber;

public class NotificationListActivity extends CollectAbstractActivity implements OnItemClickListener<FieldSightNotification> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar_general)
    AppBarLayout appbarGeneral;

    @BindView(R.id.rv_notification)
    RecyclerView rvNotificationList;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.tv_nodata)
    TextView tv_nodata;

    boolean isNewerLoading = false;
    boolean isOlderLoading = false;
    private NotificationListViewModel viewModel;
    private NotificationsAdapter adapter;
    private int count;
    final String latest_notification = "1"; // notification type when the user scroll down
    final String older_notification = "2"; // notification type when the user scrolls up
    List<FieldSightNotification> mNotificationList = new ArrayList<>();

    public static void start(Context context) {
        Intent intent = new Intent(context, NotificationListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaiton_list);
        ButterKnife.bind(this);

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(NotificationListViewModel.class);

        setupToolbar();
        setupRecyclerView();

        viewModel.getAll()
                .observe(this, fieldSightNotifications -> {
                    rvNotificationList.post(new Runnable() {
                        @Override
                        public void run() {
                            if (fieldSightNotifications.size() > 0) {
                                adapter.updateList(fieldSightNotifications);
                            }
                        }
                    });
                });

        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtils.isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    swipeLayout.setRefreshing(false);
                    return;
                }
                if (isNewerLoading) {
                    Toast.makeText(getApplicationContext(), "New notification is loading please wait", Toast.LENGTH_SHORT).show();
                    swipeLayout.setRefreshing(false);
                    return;
                }
                Timber.i("NotificationListActivity is loading");
                pullNotificationByDate(latest_notification);
                isNewerLoading = true;
                swipeLayout.setRefreshing(false);

            }
        });

        // Scheme colors for animation
        swipeLayout.setColorSchemeColors(
                getResources().getColor(R.color.primaryColor),
                getResources().getColor(R.color.primaryColor),
                getResources().getColor(R.color.primaryColor),
                getResources().getColor(R.color.primaryColor)
        );

        rvNotificationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleThreshold = 15;
                int totalItemCount = rvNotificationList.getLayoutManager().getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) rvNotificationList.getLayoutManager()).findLastVisibleItemPosition();
                Timber.i("NotificationList, lastVisible item = %d", lastVisibleItem);
                if (NetworkUtils.isNetworkConnected() && !isOlderLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    pullNotificationByDate(older_notification);
                    mNotificationList.add(null);
                    adapter.notifyDataSetChanged();
                    isOlderLoading = true;

                }
            }
        });

        if (adapter.getItemCount() == 0) {
            getDataFromServer("" + (System.currentTimeMillis() / 1000), older_notification);
        } else {
            pullNotificationByDate(older_notification);
        }
    }

    private void pullNotificationByDate(String type) {
        FieldSightNotification lastUpdatedDate;
        if (type == older_notification) {
            lastUpdatedDate = adapter.getLastNotification();
        } else {
            lastUpdatedDate = adapter.getMostRecentNotification();
        }
        if (lastUpdatedDate != null) {
            String date = lastUpdatedDate.getReceivedDateTime();

            String epochTime = DateTimeUtils.tsToSec8601(date);
            Timber.i("NotificationListActivity, date = %s, epochTime = %s", date, epochTime);
            if (epochTime != null) {
                getDataFromServer(epochTime, type);
            } else {
                if (swipeLayout.isRefreshing())
                    swipeLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get latest notification", Toast.LENGTH_SHORT).show();
        }
    }

    void getDataFromServer(String mEpcoh, String mType) {
        NotificationRemoteSource.getInstance().getNotifications(mEpcoh, mType)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<ResponseBody, ObservableSource<JSONObject>>) responseBody -> {
                    String jsonString = responseBody.string();
                    JSONArray notificationJsonArray = new JSONObject(jsonString).optJSONArray("notifications");
                    return Observable.range(0, notificationJsonArray.length())
                            .map(notificationJsonArray::getJSONObject);
                })
                .map(new Function<JSONObject, FieldSightNotification>() {
                    @Override
                    public FieldSightNotification apply(JSONObject json) throws Exception {

                        return FieldSightNotificationLocalSource.getInstance().parseNotificationData(json);
                    }
                })
                .toList()
                .subscribe(new DisposableSingleObserver<List<FieldSightNotification>>() {
                    @Override
                    public void onSuccess(List<FieldSightNotification> list) {
                        viewModel.saveData(list);
                        if (mType.equals(older_notification)) {
                            isOlderLoading = false;
                            adapter.removeLoader();
                        } else if (mType.equals(latest_notification)) {
                            isNewerLoading = false;
                            swipeLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        if (mType.equals(older_notification)) {
                            isOlderLoading = false;
                            adapter.removeLoader();
                        } else if (mType.equals(latest_notification)) {
                            isNewerLoading = false;
                            swipeLayout.setRefreshing(false);
                        }
                    }
                });
    }


    private void setupRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvNotificationList.getContext(),
                layoutManager.getOrientation());

        rvNotificationList.addItemDecoration(dividerItemDecoration);
        rvNotificationList.setLayoutManager(layoutManager);
        rvNotificationList.setItemAnimator(new DefaultItemAnimator());
        adapter = new NotificationsAdapter(mNotificationList, this);
        rvNotificationList.setAdapter(adapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_notification));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    public void onClickPrimaryAction(FieldSightNotification fieldSightNotification) {
        switch (fieldSightNotification.getNotificationType()) {
            case Constant.NotificationType.FORM_FLAG:
                FlaggedInstanceActivity.start(this, fieldSightNotification);
                break;
        }
    }

    @Override
    public void onClickSecondaryAction(FieldSightNotification fieldSightNotification) {

    }

    @OnClick(R.id.fab_scroll_to_top)
    public void scrollToTop() {
        new Handler().post(() -> {
            rvNotificationList.smoothScrollToPosition(0);
        });

    }
}
