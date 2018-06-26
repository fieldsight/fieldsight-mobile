//package org.odk.collect.naxa.site;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.res.ColorStateList;
//import android.content.res.Configuration;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.CollapsingToolbarLayout;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.NavigationView;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.view.ViewPager;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.SearchView;
//import android.support.v7.widget.Toolbar;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.bumptech.glide.DrawableRequestBuilder;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//
//
//import org.odk.collect.android.R;
//import org.odk.collect.naxa.common.NonSwipeableViewPager;
//import org.odk.collect.naxa.login.model.Project;
//
//import java.util.ArrayList;
//
//
//import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
//
//public class DashboardActivity extends AppCompatActivity {
//
//    public static String MyPREFERENCES = "field_sight_data";
//    public static String MyPREFERENCES_USER = "u_p_info";
//
//    private NonSwipeableViewPager pager;
//    private Toolbar toolbar;
//    private CollapsingToolbarLayout collapsingToolbarLayout;
//    private String projectId;
//    private TabLayout tabLayout;
//    private CardView searchView;
//    private AppBarLayout appBarLayout;
//    private FloatingActionButton fabTabSitelist, fabTabContactList, fabMap;
//    private Project loadedProject;
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;
//    private ActionBarDrawerToggle drawerToggle;
//
//    private boolean mapIsVisible = false;
//    private FrameLayout navigationHeader;
//    private int mapExistReachesPosition;
//
//
//    public static void start(Context context, Project project) {
//        Intent intent = new Intent(context, DashboardActivity.class);
//        intent.putExtra(EXTRA_OBJECT, project);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dashboard);
//
//        loadedProject = getIntent().getParcelableExtra(EXTRA_OBJECT);
//        projectId = loadedProject.getId();
//
//        bindUI();
//        setViewpager();
//        setupTabLayout();
//        setupToolbar();
//        setupAppBar();
//        setupSearchView();
//        setupNavigation();
//        setupNavigationHeader();
//        setupMapMode();
//
//    }
//
//    private void setupMapMode() {
//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                        mapExistReachesPosition = position;
//                        setToolbalText("My Sites");
//                        deactivateMapMode();
//                        break;
//                    case 1:
//                        mapExistReachesPosition = position;
//                        setToolbalText("Project Contacts");
//                        deactivateMapMode();
//                        break;
//                    case 2:
//                        setToolbalText("Project Map");
//                        activateMapMode();
//                        break;
//
//                }
//
//                highlightSelectedTab(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }
//
//    private void highlightSelectedTab(int position) {
//
//        FloatingActionButton selectedFab = (FloatingActionButton) tabLayout.getTabAt(position).getCustomView();
//        setButtonTint(selectedFab, ColorStateList.valueOf(Color.parseColor("#4b8fbe")));
//
//
//        ArrayList<Integer> totalTabs = new ArrayList<>();
//        totalTabs.add(0);
//        totalTabs.add(1);
//        totalTabs.add(2);
//        totalTabs.remove(position);
//
//        FloatingActionButton unselectedFab1 = (FloatingActionButton) tabLayout.getTabAt(totalTabs.get(0)).getCustomView();
//        setButtonTint(unselectedFab1, ColorStateList.valueOf(Color.parseColor("#00628e")));
//
//        FloatingActionButton unselectedFab2 = (FloatingActionButton) tabLayout.getTabAt(totalTabs.get(1)).getCustomView();
//        setButtonTint(unselectedFab2, ColorStateList.valueOf(Color.parseColor("#00628e")));
//
//    }
//
//    public static void setButtonTint(FloatingActionButton button, ColorStateList tint) {
//        button.setBackgroundTintList(tint);
//    }
//
//    private void deactivateMapMode() {
//        appBarLayout.setExpanded(true, true);
//        drawerToggle.syncState();
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//        mapIsVisible = false;
//    }
//
//    private void activateMapMode() {
//        appBarLayout.setExpanded(false, true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        mapIsVisible = true;
//    }
//
//    @Override
//    public void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        drawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    public void setToolbalText(String title) {
//
//        collapsingToolbarLayout.setTitle(title);
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mapIsVisible) {
//            pager.setCurrentItem(mapExistReachesPosition, true);
//        } else if (pager.getCurrentItem() > 0) {
//            pager.setCurrentItem(pager.getCurrentItem() - 1, true);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    private void setupNavigationHeader() {
//        SharedPreferences prefUser = getSharedPreferences(MyPREFERENCES_USER, Context.MODE_PRIVATE);
//        SharedPreferences prefUserInfo = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        String fullName = prefUser.getString("FIRST_NAME", "") + prefUser.getString("LAST_NAME", "");
//
//        //todo investigate with this if is needed - nishon
//        if (android.text.TextUtils.isEmpty(fullName)) {
//            fullName = prefUserInfo.getString("FULL_NAME", "");
//        }
//
//        String email = prefUserInfo.getString("EMAIL", "");
//        String profilePic = prefUserInfo.getString("PROFILE_PIC", "");
//
//        loadImage(profilePic).into((ImageView) navigationHeader.findViewById(R.id.image_profile));
//        ((TextView) navigationHeader.findViewById(R.id.tv_user_name)).setText(fullName);
//        ((TextView) navigationHeader.findViewById(R.id.tv_email)).setText(email);
//
//
//        navigationHeader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggleNavDrawer();
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(DashboardActivity.this, ViewUserProfileActivity.class));
//                    }
//                }, 250);
//            }
//        });
//    }
//
//
//    private DrawableRequestBuilder<String> loadImage(@NonNull String imagePath) {
//
//        return Glide
//                .with(getApplicationContext())
//                .load(BASE_URL + imagePath)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .fitCenter()
//                .crossFade();
//    }
//
//
//    private void setViewpager() {
//        ArrayList<Fragment> fragments = new ArrayList<>();
//        SiteListFragment siteListFragment = SiteListFragment.getInstance(projectId, loadedProject);
//
//        fragments.add(siteListFragment);
//        fragments.add(new ContactFragment());
//        fragments.add(new MapFragment());
//
//        DashboardAdapter dashboardAdapter = new DashboardAdapter(getSupportFragmentManager());
//        dashboardAdapter.setFragmentList(fragments);
//        pager.setAdapter(dashboardAdapter);
//        pager.setPageMargin(ViewUtils.dp2px(getApplicationContext(), 16));
//        pager.setClipToPadding(false);
//        pager.setPadding(16, 0, 16, 0);
//
//    }
//
//
//    private void setupTabLayout() {
//        tabLayout.setupWithViewPager(pager);
//
//        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_tab_site_dashboard, null, false);
//
//        fabTabSitelist = headerView.findViewById(R.id.tab_site_list);
//        fabTabContactList = headerView.findViewById(R.id.tab_contacts);
//        fabMap = headerView.findViewById(R.id.tab_map);
//
//
//        tabLayout.getTabAt(0).setCustomView(fabTabSitelist);
//        tabLayout.getTabAt(1).setCustomView(fabTabContactList);
//        tabLayout.getTabAt(2).setCustomView(fabMap);
//
//
//        setButtonTint(fabTabSitelist, ColorStateList.valueOf(Color.parseColor("#4b8fbe")));
//
//
//    }
//
//    private void setupAppBar() {
//        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
//            @Override
//            void onStateChanged(AppBarLayout appBarLayout, int state) {
//                switch (state) {
//                    case State.EXPANDED:
//                        hideTabs();
//                        break;
//                    case State.COLLAPSED:
//                        showTabs();
//                        break;
//                    case State.IDLE:
//                        break;
//                }
//            }
//
//
//        });
//    }
//
//    private void hideTabs() {
//        fabTabSitelist.show();
//        fabTabContactList.show();
//        fabMap.show();
//    }
//
//    private void showTabs() {
//        fabTabSitelist.hide();
//        fabTabContactList.hide();
//        fabMap.hide();
//    }
//
//    private void bindUI() {
//        pager = findViewById(R.id.pager);
//        toolbar = findViewById(R.id.act_dashboard_toolbar);
//        collapsingToolbarLayout = findViewById(R.id.act_dashboard_collapsing_toolbar_layout);
//        tabLayout = findViewById(R.id.tabs);
//        searchView = findViewById(R.id.act_dashboard_search_view);
//        appBarLayout = findViewById(R.id.act_dashboard_app_bar);
//        drawerLayout = findViewById(R.id.activity_dashboard_drawer_layout);
//        navigationView = findViewById(R.id.activity_dashboard_navigation_view);
//
//        navigationHeader = (FrameLayout) navigationView.getHeaderView(0);
//
//
//    }
//
//    private void setupNavigation() {
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                toggleNavDrawer();
//                final int selectedItemId = item.getItemId();
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        handleNavDrawerClicks(selectedItemId);
//                    }
//                }, 250);
//
//
//                return false;
//            }
//        });
//
//
//        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.hello_world, R.string.hello_world);
//        drawerLayout.addDrawerListener(drawerToggle);
//
//    }
//
//
//    private void handleNavDrawerClicks(int id) {
//        switch (id) {
//            case R.id.nav_create_offline_site:
//                Intent toCollectSite = new Intent(this, CreateOfflineSiteActivity.class);
//                toCollectSite.putExtra(EXTRA_OBJECT, loadedProject);
//                startActivity(toCollectSite);
//                break;
//            case R.id.nav_delete_saved_form:
//                Intent toDelSaved = new Intent(getApplicationContext(), DataManagerList.class);
//                startActivity(toDelSaved);
//                break;
//            case R.id.nav_edit_saved_form:
//                Intent toEditSaved = new Intent(getApplicationContext(), EditSavedFormActivity.class);
//                startActivity(toEditSaved);
//                break;
//            case R.id.nav_send_final_form:
//                Intent toSendFinal = new Intent(getApplicationContext(), InstanceUploaderList.class);
//                startActivity(toSendFinal);
//                break;
//            case R.id.nav_view_finalized_offline_site:
//                FinalizedSiteListActivity.start(this, loadedProject);
//                break;
//            case R.id.nav_view_site_dashboard:
//
//                break;
//        }
//    }
//
//
//    private void setupToolbar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        setToolbalText("My Sites");
//
//
//    }
//
//    private void setupSearchView() {
//
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadToolBarSearch();
//
//            }
//        });
//
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                if (mapIsVisible) {
//                    pager.setCurrentItem(mapExistReachesPosition);
//                } else {
//                    toggleNavDrawer();
//                }
//                break;
//            case R.id.action_notificaiton:
//                startActivity(new Intent(this, NotificationListActivity.class));
//
//                break;
//            case R.id.action_app_settings:
//                startActivity(new Intent(this, SettingsActivity.class));
//
//                break;
//            case R.id.action_logout:
//                logout();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void logout() {
//        Boolean isNotConnectedToNetwork = Connectivity.isNotConnected(getApplicationContext());
//
//        if (isNotConnectedToNetwork) {
//            showSnackMsg(getString(R.string.all_msg_logout_no__internet));
//            return;
//        }
//        DialogFactoryImpl.createLogoutDialog(this);
//    }
//
//    private void showSnackMsg(String string) {
//        ToastUtils.showLong(string);
//    }
//
//
//    private void toggleNavDrawer() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            drawerLayout.openDrawer(GravityCompat.START);
//        }
//
//    }
//
//    public void loadToolBarSearch() {
//
//
//        //  ArrayList<String> sitesStored = SharedPreference.loadList(this, Utils.PREFS_NAME, Utils.KEY_SITES);
//        ArrayList<MySiteLocationPojo> sitesStored = new ArrayList<>();
//        View view = this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
//
//        LinearLayout parentToolbarSearch = view.findViewById(R.id.parent_toolbar_search);
//        ImageView btnHomeSearchToolbar = view.findViewById(R.id.img_tool_back);
//        final EditText edtToolSearch = view.findViewById(R.id.edt_tool_search);
//        ImageView imgToolMic = view.findViewById(R.id.img_tool_mic);
//        final ListView listSearch = view.findViewById(R.id.list_search);
//        final TextView txtEmpty = view.findViewById(R.id.txt_empty);
//
//        Utils.setListViewHeightBasedOnChildren(listSearch);
//        edtToolSearch.setHint(getString(R.string.search_sites));
//
//        final Dialog toolbarSearchDialog = new Dialog(this, R.style.MaterialSearch);
//
//        toolbarSearchDialog.setContentView(view);
//        toolbarSearchDialog.setCancelable(true);
//
//        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
//        toolbarSearchDialog.show();
//
//        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//
//        //sitesStored = (sitesStored != null && sitesStored.size() > 0) ? sitesStored : new ArrayList<String>();
//        final SearchAdapter searchAdapter = new SearchAdapter(this, sitesStored, false);
//
//        listSearch.setVisibility(View.VISIBLE);
//        listSearch.setAdapter(searchAdapter);
//
//        toolbarSearchDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    toolbarSearchDialog.dismiss();
//                }
//
//                return true;
//            }
//        });
//
//        btnHomeSearchToolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toolbarSearchDialog.dismiss();
//            }
//        });
//
//        imgToolMic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                edtToolSearch.setText("");
//            }
//        });
//
//        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                MySiteLocationPojo mySiteLocationPojo = searchAdapter.getMySiteLocationPojo(position);
//                String site = String.valueOf(adapterView.getItemAtPosition(position));
//                // SharedPreference.addList(DashboardActivity.this, Utils.PREFS_NAME, Utils.KEY_SITES, mySiteLocationPojo);
//                listSearch.setVisibility(View.GONE);
//                toolbarSearchDialog.dismiss();
//                //SharedPreference.
//
//
//                FragmentHostActivity.start(DashboardActivity.this, mySiteLocationPojo);
//
//                //MainHomeActivity.hackStart(ProjectListAcitivity.this,mySiteLocationPojo);
//
//            }
//        });
//        edtToolSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                listSearch.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                ArrayList<MySiteLocationPojo> filterList;
//                String userQuery = s.toString();
//
//                if (userQuery.length() <= 0) {
//                    listSearch.setVisibility(View.GONE);
//                    txtEmpty.setVisibility(View.GONE);
//                    return;
//                }
//
//                filterList = DatabaseHelper.getInstance().searchSites(userQuery.trim());
//
//                if (filterList.size() == 0) {
//                    listSearch.setVisibility(View.GONE);
//                    txtEmpty.setVisibility(View.VISIBLE);
//                    txtEmpty.setText(R.string.msg_no_data_found);
//                    return;
//                }
//
//                listSearch.setVisibility(View.VISIBLE);
//                txtEmpty.setVisibility(View.INVISIBLE);
//                searchAdapter.updateList(filterList, true);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        btnHomeSearchToolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toolbarSearchDialog.dismiss();
//            }
//        });
//    }
//
//
//    public abstract static class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
//
//        static class State {
//            static final int EXPANDED = 1;
//            static final int COLLAPSED = 0;
//            static final int IDLE = 2;
//        }
//
//
//        private int mCurrentState = State.IDLE;
//
//        @Override
//        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//            if (i == 0) {
//                if (mCurrentState != State.EXPANDED) {
//                    onStateChanged(appBarLayout, State.EXPANDED);
//                }
//                mCurrentState = State.EXPANDED;
//            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
//                if (mCurrentState != State.COLLAPSED) {
//                    onStateChanged(appBarLayout, State.COLLAPSED);
//                }
//                mCurrentState = State.COLLAPSED;
//            } else {
//                if (mCurrentState != State.IDLE) {
//                    onStateChanged(appBarLayout, State.IDLE);
//                }
//                mCurrentState = State.IDLE;
//            }
//        }
//
//        abstract void onStateChanged(AppBarLayout appBarLayout, int state);
//    }
//
//
//}
