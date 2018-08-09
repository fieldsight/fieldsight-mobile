package org.bcss.collect.naxa.notificationslist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.naxa.OnItemClickListener;
import org.bcss.collect.naxa.common.RecyclerViewEmptySupport;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.data.FieldSightNotificationBuilder;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private NotificationListViewModel viewModel;
    private NotificationsAdapter adapter;
    private int count;


    public static void start(Context context){
        Intent intent = new Intent(context,NotificationListActivity.class);
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


      // FieldSightNotificationLocalSource.getInstance().clear();
        count = 0;
        //addItem();


        viewModel.getAll()
                .observe(this, fieldSightNotifications -> {
                    if(adapter.getItemCount() == 0){
                        adapter.updateList(fieldSightNotifications);
                        runLayoutAnimation(rvNotificationList);
                    }
                    adapter.updateList(fieldSightNotifications);

                });



    }

    private void addItem() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                count++;
                FieldSightNotificationBuilder obj = new FieldSightNotificationBuilder().setId(String.valueOf(count)).setNotificationType(count + ": notification");

                FieldSightNotificationLocalSource.getInstance().save(obj.createFieldSightNotification());
                addItem();


            }
        }, 3000);
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
        rvNotificationList.setLayoutManager(new LinearLayoutManager(this));
        rvNotificationList.setItemAnimator(new DefaultItemAnimator());
        adapter = new NotificationsAdapter(new ArrayList<>(0), this);

        rvNotificationList.setAdapter(adapter);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_notification));
        getSupportActionBar().setSubtitle("Site Supervisor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public void onClickPrimaryAction(FieldSightNotification fieldSightNotification) {

    }

    @Override
    public void onClickSecondaryAction(FieldSightNotification fieldSightNotification) {

    }
}
