package org.fieldsight.naxa.site;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.common.InternetUtils;
import org.fieldsight.naxa.common.ViewModelFactory;
import org.fieldsight.naxa.forms.ui.FieldSightFormListFragment;
import org.fieldsight.naxa.generalforms.GeneralFormViewModel;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.notificationslist.NotificationListActivity;
import org.fieldsight.naxa.preferences.SettingsActivity;
import org.fieldsight.naxa.project.data.ProjectLocalSource;
import org.fieldsight.naxa.v3.forms.FormsStateFragment;
import org.fieldsight.naxa.v3.network.SyncActivity;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;

import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_MESSAGE;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.fieldsight.naxa.common.Constant.EXTRA_PROJECT;

public class FragmentHostActivity extends CollectAbstractActivity {

    Site loadedSite;
    Project project;
    Toolbar toolbar;
    boolean isParent;
    String extraMessage;
    private boolean openSubmissionScreen;

    public static void start(Context context, Site site, boolean isParent) {
        Intent intent = new Intent(context, FragmentHostActivity.class);
        intent.putExtra(EXTRA_OBJECT, site);
        intent.putExtra("isParent", isParent);
        context.startActivity(intent);
    }


    public static void startWithSurveyForm(Context context, Project project) {
        Intent intent = new Intent(context, FragmentHostActivity.class);
        intent.putExtra(EXTRA_PROJECT, project);
        intent.putExtra(EXTRA_MESSAGE, "open_survey_form");
        context.startActivity(intent);
    }

    public static void startFlaggedForm(Context context, String type, Project project) {
        Intent intent = new Intent(context, FragmentHostActivity.class);
        intent.putExtra(EXTRA_MESSAGE, type);
        intent.putExtra(EXTRA_PROJECT, project);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_dashboard);

        Bundle extras = getIntent().getExtras();

        if (extras == null) {
            ToastUtils.showShortToast(getString(R.string.dialog_unexpected_error_title));
            finish();
            return;
        }

        bindUI();
        setupToolbar();

        loadedSite = extras.getParcelable(EXTRA_OBJECT);
        extraMessage = extras.getString(EXTRA_MESSAGE);
        isParent = extras.getBoolean("isParent");


        if (extras.containsKey(EXTRA_PROJECT)) {
            project = extras.getParcelable(EXTRA_PROJECT);
        } else if (loadedSite != null) {
            project = ProjectLocalSource.getInstance().getProject(loadedSite.getProject());
            Timber.i("hasProject = %s", (project != null));
        }

        Fragment fragment;
        openSubmissionScreen = TextUtils.equals(Constant.FormStatus.FLAGGED, extraMessage)
                || TextUtils.equals(Constant.FormStatus.REJECTED, extraMessage);
        boolean openSurveyForm = TextUtils.equals("open_survey_form", extraMessage);


        if (openSubmissionScreen) {
            fragment = FormsStateFragment.newInstance(extraMessage, project);
        } else if (openSurveyForm) {

            fragment = FieldSightFormListFragment.newInstance(Constant.FormType.SURVEY, null, project);
        } else {
            fragment = SiteDashboardFragment.newInstance(loadedSite, isParent, project);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment, "frag0")
                .commit();

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindUI() {
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (openSubmissionScreen) {
            menu.findItem(R.id.action_refresh).setVisible(false);
        }
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
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_logout:
//                showProgress();
                InternetUtils.checkInterConnectivity(new InternetUtils.OnConnectivityListener() {
                    @Override
                    public void onConnectionSuccess() {
                        FieldSightUserSession.showLogoutDialog(FragmentHostActivity.this);
                    }

                    @Override
                    public void onConnectionFailure() {
                        FieldSightUserSession.stopLogoutDialog(FragmentHostActivity.this);
                    }

                    @Override
                    public void onCheckComplete() {
                        hideProgress();
                    }
                });
                break;
            case R.id.action_refresh:
                String projectId = project != null ? project.getId() : loadedSite.getProject();
                ProjectLocalSource.getInstance().getProjectById(projectId)
                        .observe(this, new Observer<Project>() {
                            @Override
                            public void onChanged(@Nullable Project project) {
                                if (project != null) {
                                    Bundle bundle = new Bundle();
                                    ArrayList<Project> projectArrayList = new ArrayList<>();
                                    projectArrayList.add(project);
                                    bundle.putParcelableArrayList("projects", projectArrayList);
                                    bundle.getBoolean("auto", true);
                                    startActivity(new Intent(FragmentHostActivity.this, SyncActivity.class)
                                            .putExtra("params", bundle));
                                }
                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static GeneralFormViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance();
        return ViewModelProviders.of(activity, factory).get(GeneralFormViewModel.class);
    }


}
