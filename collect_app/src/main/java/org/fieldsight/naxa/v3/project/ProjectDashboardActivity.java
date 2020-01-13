package org.fieldsight.naxa.v3.project;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.fieldsight.naxa.BackupActivity;
import org.fieldsight.naxa.FSInstanceChooserList;
import org.fieldsight.naxa.FSInstanceUploaderListActivity;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.common.InternetUtils;
import org.fieldsight.naxa.common.SettingsActivity;
import org.fieldsight.naxa.common.ViewUtils;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.User;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.notificationslist.NotificationListActivity;
import org.fieldsight.naxa.profile.UserActivity;
import org.fieldsight.naxa.project.TermsLabels;
import org.fieldsight.naxa.site.CreateSiteActivity;
import org.fieldsight.naxa.site.FragmentHostActivity;
import org.fieldsight.naxa.site.OldProjectDashboardActivity;
import org.fieldsight.naxa.site.map.ProjectMapFragment;
import org.fieldsight.naxa.v3.network.ApiV3Interface;
import org.fieldsight.naxa.v3.network.SyncActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.activities.FileManagerTabs;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.fieldsight.naxa.network.ServiceGenerator.getRxClient;
import static org.odk.collect.android.application.Collect.allowClick;

public class ProjectDashboardActivity extends CollectAbstractActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Project loadedProject;
    private CardView searchView;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fabTabSitelist, fabTabContactList, fabMap;
    private ActionBarDrawerToggle drawerToggle;

    private boolean mapIsVisible;
    private View navigationHeader;
    private int mapExistReachesPosition;
    TermsLabels tl;

    @BindView(R.id.cl_main)
    CoordinatorLayout clMain;

    @BindView(R.id.act_dashboard_toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.pager)
    ViewPager pager;

    @BindView(R.id.activity_dashboard_navigation_view)
    NavigationView navigationView;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.activity_dashboard_drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.tv_project_organization)
    TextView organizationName;

    @BindView(R.id.iv_project)
    ImageView projectImage;

    @BindView(R.id.tv_project_name)
    TextView projectName;

    @BindView(R.id.tv_desc)
    TextView tvDescription;

    @BindView(R.id.ll_desc)
    LinearLayout llDesc;

    @BindView(R.id.tv_regions)
    TextView tvRegions;

    @BindView(R.id.tv_sites)
    TextView tvSites;

    @BindView(R.id.tv_users)
    TextView tvUsers;

    @BindView(R.id.tv_submissions)
    TextView tvSubmissions;

    public class ProjectFragment {
        public String title;
        public Fragment fragment;

        public ProjectFragment(String title, Fragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }
    }

    List<ProjectFragment> projectFragmentList = new ArrayList<>();

    public static void start(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDashboardActivity.class);
        intent.putExtra(EXTRA_OBJECT, project);
        context.startActivity(intent);
    }

    ProjectViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_dashboard);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");

        // this is for demonstration how can we manage the code with git with multiple developers


        try {
            loadedProject = getIntent().getParcelableExtra(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
            finish();
        }
        addDrawerToggle();
        tl = getTermsAndLabels();
        setupNavigationHeader();
        if (tl != null) {
            if (!TextUtils.isEmpty(tl.site)) {
                navigationView.getMenu().findItem(R.id.nav_create_offline_site).setTitle(String.format("Create New %s", tl.site));
                navigationView.getMenu().findItem(R.id.nav_view_site_dashboard).setTitle(String.format("My %s", tl.site));
            }
        }
        addProjectInfoInView();
        navigationView.setNavigationItemSelectedListener(this);
        // get the projectdashboard stat
        ServiceGenerator.createCacheService(ApiV3Interface.class).getProjectDashboardStat(loadedProject.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                initPager(responseBody);
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showShortToast(e.getMessage());
                initPager(null);
                hideProgress();
            }

            @Override
            public void onComplete() {
                hideProgress();
            }
        });
        showProgress("Loading project info, Please wait ");
    }

    void updateCountUI(int totalRegions, int sitesCount, int usersCount, int submissonCount) {
        tvRegions.setText(totalRegions+"");
        tvSites.setText(sitesCount+"");
        tvUsers.setText(usersCount+"");
        tvSubmissions.setText(submissonCount+"");
    }

    void initPager(ResponseBody responseBody) {
        String users = "", forms = "";
        if(responseBody != null) {
            try {
                String data = responseBody.string();
                JSONObject dataJSON = new JSONObject(data);
                forms = dataJSON.optString("forms");
                users = dataJSON.optString("users");
                int totalRegions = dataJSON.optInt("total_regions");
                int sitesCount = dataJSON.optInt("total_sites");
                int usersCount = dataJSON.optJSONArray("users").length();
                int submissonCount = dataJSON.optInt("total_submissions");
                updateCountUI(totalRegions, sitesCount, usersCount, submissonCount);
            } catch (Exception e) {

            }
        }
        projectFragmentList.add(new ProjectFragment("Sites", SiteListFragment.newInstance(loadedProject)));
        projectFragmentList.add(new ProjectFragment("Users", UsersFragment.getInstance(users)));
        projectFragmentList.add(new ProjectFragment("Submissions", SubmissionFragment.getInstance(forms)));
        projectFragmentList.add(new ProjectFragment("Map", ProjectMapFragment.newInstance(loadedProject)));
        adapter = new ProjectViewPagerAdapter(getSupportFragmentManager(), projectFragmentList);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }

    void addProjectInfoInView() {
        projectName.setText(loadedProject.getName());
        organizationName.setText(loadedProject.getOrganizationName());
        if(!TextUtils.isEmpty(loadedProject.getUrl())) {
            Glide.with(this).load(loadedProject.getUrl()).apply(RequestOptions.circleCropTransform()).into(projectImage);
        }
        if(!TextUtils.isEmpty(loadedProject.getDescription())) {
            llDesc.setVisibility(View.VISIBLE);
            tvDescription.setText(loadedProject.getDescription());
        }
    }

    void addDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private TermsLabels getTermsAndLabels() {
        if (!TextUtils.isEmpty(loadedProject.getTerms_and_labels())) {
            try {
                Timber.i("ProjectDashBoardActivity:: terms and labels = %s", loadedProject.getTerms_and_labels());
                JSONObject tlJson = new JSONObject(loadedProject.getTerms_and_labels());
                return TermsLabels.fromJSON(tlJson);
            } catch (Exception e) {
                Timber.e("Failed to load terms and labels; Reason: %s", e.getMessage());
                return null;
            }
        } else {
            return null;
        }

    }

    @OnClick(R.id.tv_more)
    void moreDesc() {
       //TODO : what to do either open in new page or expand the text ?
       //TODO: what to do if text are long ?
    }

    @OnClick(R.id.tv_project_forms)
    void openProjectSurveyForm() {
        FragmentHostActivity.startWithSurveyForm(this, loadedProject);
    }


    private void toggleNavDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }

    private void setupNavigationHeader() {
        try {
            navigationHeader = navigationView.getHeaderView(0);
            User user = FieldSightUserSession.getUser();
            ((TextView) navigationHeader.findViewById(R.id.tv_user_name)).setText(user.getFullName());
            ((TextView) navigationHeader.findViewById(R.id.tv_email)).setText(user.getEmail());
            if (tl != null && !TextUtils.isEmpty(tl.siteSupervisor)) {
                Timber.i("ProjectDashboardActivity, data:: sitesv = %s", tl.siteSupervisor);
                ((TextView) navigationHeader.findViewById(R.id.tv_user_post)).setText(tl.siteSupervisor);
            }

            ImageView ivProfilePicture = navigationHeader.findViewById(R.id.image_profile);
            ViewUtils.loadRemoteImage(this, user.getProfilepic())
                    .circleCrop()
                    .into(ivProfilePicture);

            navigationHeader.setOnClickListener(v -> {
                toggleNavDrawer();
                UserActivity.start(this);
            });
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notificaiton:
                NotificationListActivity.start(this);
                break;

            case R.id.action_logout:
                showProgress();
                InternetUtils.checkInterConnectivity(new InternetUtils.OnConnectivityListener() {
                    @Override
                    public void onConnectionSuccess() {
                        FieldSightUserSession.showLogoutDialog(ProjectDashboardActivity.this);
                    }

                    @Override
                    public void onConnectionFailure() {
                        FieldSightUserSession.stopLogoutDialog(ProjectDashboardActivity.this);
                    }

                    @Override
                    public void onCheckComplete() {
                        hideProgress();
                    }
                });
                break;
            case R.id.action_refresh:
                Bundle bundle = new Bundle();
                ArrayList<Project> projectArrayList = new ArrayList<>();
                projectArrayList.add(loadedProject);
                bundle.putParcelableArrayList("projects", projectArrayList);
                bundle.getBoolean("auto", true);
                startActivity(new Intent(this, SyncActivity.class)
                        .putExtra("params", bundle));
                break;
            case R.id.action_app_settings:
                if (allowClick(getClass().getName())) {
                    startActivity(new Intent(this, SettingsActivity.class));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_create_offline_site:
                String siteLabel = "Site";
                String regionLabel = "Region";
                if (tl != null) {
                    siteLabel = tl.site;
                    regionLabel = tl.region;
                }
                CreateSiteActivity.start(this, loadedProject, null, siteLabel, regionLabel);
                return true;
            case R.id.nav_delete_saved_form:

                startActivity(new Intent(getApplicationContext(), FileManagerTabs.class));
                return true;
            case R.id.nav_edit_saved_form:

                Intent i = new Intent(getApplicationContext(), FSInstanceChooserList.class);
                i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
                startActivity(i);
                return true;
            case R.id.nav_send_final_form:
                startActivity(new Intent(getApplicationContext(), FSInstanceUploaderListActivity.class));
                return true;
            case R.id.nav_view_finalized_offline_site:

                return true;
            case R.id.nav_view_site_dashboard:
                return true;
            case R.id.nav_backup:
                startActivity(new Intent(this, BackupActivity.class));
                return true;
            case R.id.nav_setting:
                startActivity(new Intent(this, org.fieldsight.naxa.preferences.SettingsActivity.class));
                return true;
            case R.id.nav_flagged_form:
                FragmentHostActivity.startFlaggedForm(this, "Flagged", loadedProject);
                return true;
            case R.id.nav_rejected_form:
                FragmentHostActivity.startFlaggedForm(this, "Rejected", loadedProject);
                return true;
        }
        return false;
    }

}
