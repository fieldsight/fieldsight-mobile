package org.bcss.collect.naxa.migrate;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.login.LoginActivity;
import org.bcss.collect.naxa.onboarding.DownloadActivity;
import org.bcss.collect.naxa.project.ProjectListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
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

    @BindView(R.id.fieldsight_migrate_act_btn_proceed_anyway)
    Button btnProceedAnyway;

    @BindView(R.id.fieldsight_migrate_act_tv_error_message)
    TextView tvMigrateErrorMessage;

    @BindView(R.id.fieldsight_migrate_act_error_card)
    CardView cardViewError;

    final Integer errorOccured = -1;
    private final Integer max = 3;
    private Observable<Integer> migration;

    private final int MAX_PROGRESS = 8;

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

        String usernameOrEmail = getIntent().getExtras().getString(EXTRA_MESSAGE);
        setupViewModel(usernameOrEmail);
        subtitle.setText(usernameOrEmail);


        migration = viewModel.copyFromOldAccount();

        migration
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        progressBar.setMax(MAX_PROGRESS);
                        progressBar.setInterpolator(new DecelerateInterpolator());
                    }

                    @Override
                    public void onNext(Integer progress) {
                        switch (progress) {
                            case -1:
                                showErrorUI("");
                                break;
                            case MAX_PROGRESS:
                                openProjectList();
                                break;
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
        tvMigrateErrorMessage.setText(s);
        cardViewError.setVisibility(View.VISIBLE);
    }


    private void setupViewModel(String userNameOrEmail) {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(MigrateFieldSightViewModel.class);
        viewModel.setUserNameEmail(userNameOrEmail);
    }


    @OnClick(R.id.fieldsight_migrate_act_btn_proceed_anyway)
    public void openProjectList() {
        DownloadActivity.runAll(this);
        finish();

    }

}
