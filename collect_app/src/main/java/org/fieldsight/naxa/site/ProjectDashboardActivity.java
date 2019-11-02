package org.fieldsight.naxa.site;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;;
import org.fieldsight.naxa.BackupActivity;
import org.fieldsight.naxa.BaseActivity;
import org.fieldsight.naxa.FSInstanceChooserList;
import org.fieldsight.naxa.FSInstanceUploaderListActivity;
import org.fieldsight.naxa.common.AppBarStateChangeListener;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.common.InternetUtils;
import org.fieldsight.naxa.common.NonSwipeableViewPager;
import org.fieldsight.naxa.common.RxSearchObservable;
import org.fieldsight.naxa.common.SettingsActivity;
import org.fieldsight.naxa.common.ViewUtils;
import org.fieldsight.naxa.contact.ProjectContactsFragment;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.login.model.User;
import org.fieldsight.naxa.notificationslist.NotificationListActivity;
import org.fieldsight.naxa.profile.UserActivity;
import org.fieldsight.naxa.project.TermsLabels;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.fieldsight.naxa.site.map.ProjectMapFragment;
import org.fieldsight.naxa.v3.network.SyncActivity;
import org.json.JSONObject;
import org.odk.collect.android.activities.FileManagerTabs;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.android.application.Collect.allowClick;

public class ProjectDashboardActivity extends BaseActivity {

    private Project loadedProject;


    private NonSwipeableViewPager pager;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TabLayout tabLayout;
    private CardView searchView;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fabTabSitelist, fabTabContactList, fabMap;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    private boolean mapIsVisible;
    private FrameLayout navigationHeader;
    private int mapExistReachesPosition;
    TermsLabels tl;

    public static void start(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDashboardActivity.class);
        intent.putExtra(EXTRA_OBJECT, project);
        context.startActivity(intent);
    }


    @SafeVarargs
    public static void start(Activity context, Project project, Pair<View, String>... pairs) {
        Intent intent = new Intent(context, ProjectDashboardActivity.class);
        intent.putExtra(EXTRA_OBJECT, project);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_dashboard);

        try {
            loadedProject = getIntent().getParcelableExtra(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
            finish();
        }

        bindUI();
        setViewpager();
        setupTabLayout();
        setupMapMode();

        setupToolbar();
        setupAppBar();
        setupSearchView();
        setupNavigation();
        setupAnimation();
        tl = getTermsAndLabels();
        setupNavigationHeader();
        if (tl != null) {
            if (!TextUtils.isEmpty(tl.site)) {
                navigationView.getMenu().findItem(R.id.nav_create_offline_site).setTitle(String.format("Create New %s", tl.site));
                navigationView.getMenu().findItem(R.id.nav_view_site_dashboard).setTitle(String.format("My %s", tl.site));
            }
        }
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

    private void setupAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && false) {
            Transition sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();

            sharedElementEnterTransition
                    .addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {
                            pager.setVisibility(View.INVISIBLE);
                            hideTabs();
                            pager.animate().alpha(0.0f).setDuration(500).start();
                        }

                        @Override
                        public void onTransitionEnd(Transition transition) {
                            pager.setVisibility(View.VISIBLE);
                            pager.animate().alpha(1.0f).setDuration(500).start();
                            showTabs();
                        }

                        @Override
                        public void onTransitionCancel(Transition transition) {
                        }

                        @Override
                        public void onTransitionPause(Transition transition) {
                        }

                        @Override
                        public void onTransitionResume(Transition transition) {
                        }
                    });
        } else {
            pager.setVisibility(View.VISIBLE);
            showTabs();
        }
    }


    private void setupSearchView() {

        searchView.setOnClickListener(view -> {
            if (allowClick(getClass().getName())) {
                loadToolBarSearch();
            }
        });

    }

    private void setupNavigationHeader() {
        try {
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
                new Handler()
                        .postDelayed(() -> {
                            UserActivity.start(this);
                        }, 250);
            });
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }


    }

    private void setupMapMode() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mapExistReachesPosition = position;
                        setToolbarText(loadedProject.getName());
//                        setToolbarText(SharedPreferenceUtils.getSiteLisTitle(ProjectDashboardActivity.this, loadedProject.getId()));
                        deactivateMapMode();
                        break;
                    case 1:
                        mapExistReachesPosition = position;
                        setToolbarText("Project Contacts");
                        deactivateMapMode();
                        break;
                    case 2:
                        setToolbarText("Project Map");
                        activateMapMode();
                        break;

                }

                highlightSelectedTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void highlightSelectedTab(int position) {

        FloatingActionButton selectedFab = (FloatingActionButton) tabLayout.getTabAt(position).getCustomView();
        ViewUtils.setButtonTint(selectedFab, ColorStateList.valueOf(Color.parseColor("#4b8fbe")));


        ArrayList<Integer> totalTabs = new ArrayList<>();
        totalTabs.add(0);
        totalTabs.add(1);
        totalTabs.add(2);
        totalTabs.remove(position);

        FloatingActionButton unselectedFab1 = (FloatingActionButton) tabLayout.getTabAt(totalTabs.get(0)).getCustomView();
        ViewUtils.setButtonTint(unselectedFab1, ColorStateList.valueOf(Color.parseColor("#00628e")));

        FloatingActionButton unselectedFab2 = (FloatingActionButton) tabLayout.getTabAt(totalTabs.get(1)).getCustomView();
        ViewUtils.setButtonTint(unselectedFab2, ColorStateList.valueOf(Color.parseColor("#00628e")));

    }


    private void deactivateMapMode() {
        appBarLayout.setExpanded(true, true);
        drawerToggle.syncState();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mapIsVisible = false;
    }

    private void activateMapMode() {
        appBarLayout.setExpanded(false, true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mapIsVisible = true;
    }


    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                toggleNavDrawer();
                final int selectedItemId = item.getItemId();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleNavDrawerClicks(selectedItemId);
                    }
                }, 250);


                return false;
            }
        });


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void handleNavDrawerClicks(int id) {
        switch (id) {
            case R.id.nav_create_offline_site:
                String siteLabel = "Site";
                String regionLabel = "Region";
                if (tl != null) {
                    siteLabel = tl.site;
                    regionLabel = tl.region;
                }
                CreateSiteActivity.start(this, loadedProject, null, siteLabel, regionLabel);
                break;
            case R.id.nav_delete_saved_form:

                startActivity(new Intent(getApplicationContext(), FileManagerTabs.class));
                break;
            case R.id.nav_edit_saved_form:

                Intent i = new Intent(getApplicationContext(), FSInstanceChooserList.class);
                i.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
                startActivity(i);
                break;
            case R.id.nav_send_final_form:

                startActivity(new Intent(getApplicationContext(), FSInstanceUploaderListActivity.class));

                break;
            case R.id.nav_view_finalized_offline_site:

                break;
            case R.id.nav_view_site_dashboard:

                break;
            case R.id.nav_backup:
                startActivity(new Intent(this, BackupActivity.class));
                return;
            case R.id.nav_setting:
                startActivity(new Intent(this, org.fieldsight.naxa.preferences.SettingsActivity.class));
                break;
            case R.id.nav_flagged_form:
                FragmentHostActivity.startFlaggedForm(this, "Flagged");
                break;
            case R.id.nav_rejected_form:
                FragmentHostActivity.startFlaggedForm(this, "Rejected");
                break;
        }
    }

    private void toggleNavDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }

    }

    private void setupAppBar() {

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, int state) {
                switch (state) {
                    case State.EXPANDED:
                        showTabs();
                        break;
                    case State.COLLAPSED:
                        hideTabs();
                        break;
                    case State.IDLE:
                        break;
                }
            }
        });
    }

    private void hideTabs() {
        fabTabSitelist.hide();
        fabTabContactList.hide();
        fabMap.hide();
    }

    private void showTabs() {
        fabTabSitelist.show();
        fabTabContactList.show();
        fabMap.show();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setToolbarText(loadedProject.getName());
    }

    public void setToolbarText(String title) {
        collapsingToolbarLayout.setTitle(title);
    }


    private void bindUI() {
        pager = findViewById(R.id.pager);
        toolbar = findViewById(R.id.act_dashboard_toolbar);
        collapsingToolbarLayout = findViewById(R.id.act_dashboard_collapsing_toolbar_layout);
        tabLayout = findViewById(R.id.tabs);
        searchView = findViewById(R.id.act_dashboard_search_view);
        appBarLayout = findViewById(R.id.act_dashboard_app_bar);
        drawerLayout = findViewById(R.id.activity_dashboard_drawer_layout);
        navigationView = findViewById(R.id.activity_dashboard_navigation_view);
        navigationHeader = (FrameLayout) navigationView.getHeaderView(0);
//        pager.setVisibility(View.GONE);
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(pager);

        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_tab_site_dashboard, null, false);

        fabTabSitelist = headerView.findViewById(R.id.tab_site_list);
        fabTabContactList = headerView.findViewById(R.id.tab_contacts);
        fabMap = headerView.findViewById(R.id.tab_map);

        tabLayout.getTabAt(0).setCustomView(fabTabSitelist);
        tabLayout.getTabAt(1).setCustomView(fabTabContactList);
        tabLayout.getTabAt(2).setCustomView(fabMap);
//
        ViewUtils.setButtonTint(fabTabSitelist, ColorStateList.valueOf(Color.parseColor("#4b8fbe")));
    }


    private void setViewpager() {


        ArrayList<Fragment> fragments = new ArrayList<>();
        SiteListFragment siteListFragment = SiteListFragment.newInstance(loadedProject);
        ProjectContactsFragment projectContactsFragment = ProjectContactsFragment.newInstance();

        ProjectMapFragment mapFragment = ProjectMapFragment.newInstance(loadedProject);
        fragments.add(siteListFragment);
        fragments.add(projectContactsFragment);
        fragments.add(mapFragment);


        DashboardAdapter dashboardAdapter = new DashboardAdapter(getSupportFragmentManager());
        dashboardAdapter.setFragmentList(fragments);
        pager.setAdapter(dashboardAdapter);
        pager.setPageMargin(ViewUtils.dp2px(getApplicationContext(), 16));
        pager.setClipToPadding(false);
        pager.setPadding(16, 0, 16, 0);
    }

    @Override
    public void onBackClicked(boolean isHome) {
        Timber.d("onBackClicked");
        if (mapIsVisible) {
            pager.setCurrentItem(mapExistReachesPosition, true);
        } else if (pager.getCurrentItem() > 0) {
            pager.setCurrentItem(pager.getCurrentItem() - 1, true);
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (isHome) {
                toggleNavDrawer();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_app_settings);
        menuItem.setVisible((BuildConfig.BUILD_TYPE.equals("internal")));
        // change the title of the filter with terms and labels case
        if (tl != null) {
            menu.findItem(R.id.action_filter).setTitle("Filter " + tl.site);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
//                ContentDownloadActivity.start(this);
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

    public void loadToolBarSearch() {

        //  ArrayList<String> sitesStored = SharedPreference.loadList(this, Utils.PREFS_NAME, Utils.KEY_SITES);
        ArrayList<Site> sitesStored = new ArrayList<>();
        View view = this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);

        ImageView btnHomeSearchToolbar = view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = view.findViewById(R.id.edt_tool_search);
        ImageView imgToolMic = view.findViewById(R.id.img_tool_mic);
        final ListView listSearch = view.findViewById(R.id.list_search);
        final TextView txtEmpty = view.findViewById(R.id.txt_empty);

        ViewUtils.setListViewHeightBasedOnChildren(listSearch);
        edtToolSearch.setHint(getString(R.string.search_sites));

        final Dialog toolbarSearchDialog = new Dialog(this, R.style.MaterialSearch);

        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(true);

        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();

        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        //sitesStored = (sitesStored != null && sitesStored.size() > 0) ? sitesStored : new ArrayList<String>();
        final SearchAdapter searchAdapter = new SearchAdapter(this, sitesStored);

        listSearch.setVisibility(View.VISIBLE);
        listSearch.setAdapter(searchAdapter);

        toolbarSearchDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    toolbarSearchDialog.dismiss();
                }

                return true;
            }
        });

        btnHomeSearchToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
            }
        });

        imgToolMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtToolSearch.setText("");
            }
        });

        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Site mySiteLocationPojo = searchAdapter.getMySiteLocationPojo(position);
                listSearch.setVisibility(View.GONE);
                toolbarSearchDialog.dismiss();
                FragmentHostActivity.start(ProjectDashboardActivity.this, mySiteLocationPojo, false);
            }
        });

        RxSearchObservable.fromView(edtToolSearch)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listSearch.setVisibility(s.isEmpty() ? View.GONE : View.VISIBLE);
                                txtEmpty.setVisibility(s.isEmpty() ? View.VISIBLE : View.GONE);
                            }
                        });

                        return s;
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(final String s) {
                        return !s.isEmpty();
                    }
                })
                .distinctUntilChanged()
                .switchMap(new Function<String, ObservableSource<List<Site>>>() {
                    @Override
                    public ObservableSource<List<Site>> apply(String userQuery) {
                        List<Site> filteredSites = SiteLocalSource.getInstance().searchSites(userQuery.trim(), loadedProject.getId());
                        return Observable.just(filteredSites);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<Site>>() {
                    @Override
                    public void onNext(List<Site> mySiteLocationPojos) {


                        listSearch.setVisibility(mySiteLocationPojos.isEmpty() ? View.GONE : View.VISIBLE);
                        txtEmpty.setVisibility(mySiteLocationPojos.isEmpty() ? View.VISIBLE : View.GONE);
                        searchAdapter.updateList(mySiteLocationPojos, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
                        toolbarSearchDialog.dismiss();
                        Crashlytics.logException(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        btnHomeSearchToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
            }
        });
    }


}
