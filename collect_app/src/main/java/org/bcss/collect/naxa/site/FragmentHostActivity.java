package org.bcss.collect.naxa.site;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.generalforms.GeneralFormViewModel;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.notificationslist.NotificationListActivity;
import org.bcss.collect.naxa.onboarding.DownloadActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentEnterAnimation;
import static org.bcss.collect.naxa.common.Constant.ANIM.fragmentExitAnimation;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.ALL_FORMS;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class FragmentHostActivity extends CollectAbstractActivity {

    Site loadedSite = null;
    Toolbar toolbar;

    public static void start(Context context, Site site) {
        Intent intent = new Intent(context, FragmentHostActivity.class);
        intent.putExtra(EXTRA_OBJECT, site);
        context.startActivity(intent);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_dashboard);
        loadedSite = getIntent().getExtras().getParcelable(EXTRA_OBJECT);
        bindUI();
        setupToolbar();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, SiteDashboardFragment.newInstance(loadedSite), "frag0")
                .commit();



        FieldSightNotificationLocalSource.getInstance()
                .isSiteNotSynced(loadedSite.getId(), loadedSite.getProject())
                .observe(this, new Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer integer) {
                        if (integer != null && integer > 0) {
                            FlashBarUtils.showOutOfSyncMsg(ALL_FORMS, FragmentHostActivity.this, "Form(s) information is out of sync");
                        }
                    }
                });


        FlashBarUtils.showOutOfSyncMsg(ALL_FORMS, FragmentHostActivity.this, "Form(s) information is out of sync");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;

            case R.id.action_notificaiton:
                startActivity(new Intent(this, NotificationListActivity.class));

                break;
            case R.id.action_app_settings:
                //startActivity(new Intent(this, SettingsActivity.class));

                break;
            case R.id.action_logout:
                ReactiveNetwork.checkInternetConnectivity()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableSingleObserver<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                if (aBoolean) {
                                    FieldSightUserSession.createLogoutDialog(FragmentHostActivity.this);
                                } else {
                                    FieldSightUserSession.stopLogoutDialog(FragmentHostActivity.this);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                break;
            case R.id.action_refresh:
                DownloadActivity.start(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public static GeneralFormViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel

        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        GeneralFormViewModel viewModel =
                ViewModelProviders.of(activity, factory).get(GeneralFormViewModel.class);

        return viewModel;
    }


}
