package org.bcss.collect.naxa.migrate;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.project.ProjectListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;

public class MigrateFieldSightActivity extends CollectAbstractActivity {


    private MigrateFieldSightViewModel viewModel;

    @BindView(R.id.fieldsight_migrate_act_progress)
    ProgressBar progressBar;

    @BindView(R.id.subtitle)
    TextView subtitle;

    final Integer errorOccured = -1;
    private final Integer max = 3;

    public static void start(Context context, String usernameOrEmail) {
        Intent intent = new Intent(context, MigrateFieldSightActivity.class);
        intent.putExtra(EXTRA_MESSAGE, usernameOrEmail);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fieldsight_migrate_activity);
        ButterKnife.bind(this);
        setupViewModel();
        String usernameOrEmail = getIntent().getExtras().getString(EXTRA_MESSAGE);

        subtitle.setText(usernameOrEmail);


        viewModel.copyFromOldAccount(usernameOrEmail)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        progressBar.setMax(6);
                        progressBar.setInterpolator(new DecelerateInterpolator());
                    }

                    @Override
                    public void onNext(Integer progress) {
                        switch (progress) {
                            case -1:
                                showErrorUI("");
                                break;
                            case 6:
                                new Handler().postDelayed(() -> {

                                    startActivity(new Intent(MigrateFieldSightActivity.this, ProjectListActivity.class));
                                    finish();
                                }, 2000);

                            default:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    progressBar.setProgress(progress, true);
                                } else {
                                    progressBar.setProgress(progress);
                                }
                                break;
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        showErrorUI(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void showErrorUI(String s) {
        ToastUtils.showShortToastInMiddle("ERROR: "+s);
    }


    private void setupViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(MigrateFieldSightViewModel.class);
    }


}
