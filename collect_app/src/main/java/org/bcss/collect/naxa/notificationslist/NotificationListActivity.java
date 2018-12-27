package org.bcss.collect.naxa.notificationslist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.RecyclerViewEmptySupport;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.db.SiteLocalSource;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotificationListActivity extends CollectAbstractActivity implements OnItemClickListener<FieldSightNotification> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_toolbar_message)
    TextView tvToolbarMessage;
    @BindView(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @BindView(R.id.appbar_general)
    AppBarLayout appbarGeneral;
    @BindView(R.id.rv_notification_list)
    RecyclerViewEmptySupport rvNotificationList;

    @BindView(R.id.root_layout_empty_layout)
    RelativeLayout emptyLayout;


    private NotificationListViewModel viewModel;
    private NotificationsAdapter adapter;
    private int count;


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

                    adapter.updateList(fieldSightNotifications);
                    scrollToTop();
                });


    }


    private void runLayoutAnimation(final RecyclerView recyclerView) {

        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
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
        adapter = new NotificationsAdapter(new ArrayList<>(0), this);


        rvNotificationList.setAdapter(adapter);
        rvNotificationList.setEmptyView(emptyLayout, getString(R.string.empty_message_notification), null);

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
                FlagResposneActivity.start(this, fieldSightNotification);
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
