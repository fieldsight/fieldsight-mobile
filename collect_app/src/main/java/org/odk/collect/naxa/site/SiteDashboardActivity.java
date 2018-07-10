package org.odk.collect.naxa.site;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import org.odk.collect.android.R;
import org.odk.collect.naxa.common.NonSwipeableViewPager;
import org.odk.collect.naxa.login.model.Project;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;

public class SiteDashboardActivity extends AppCompatActivity {


    public static String MyPREFERENCES = "field_sight_data";
    public static String MyPREFERENCES_USER = "u_p_info";

    private NonSwipeableViewPager pager;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String projectId;
    private TabLayout tabLayout;
    private CardView searchView;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fabTabSitelist, fabTabContactList, fabMap;
    private Project loadedProject;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;

    private boolean mapIsVisible = false;
    private FrameLayout navigationHeader;
    private int mapExistReachesPosition;

    public static void start(Context context, Project project) {
        Intent intent = new Intent(context, SiteDashboardActivity.class);
        intent.putExtra(EXTRA_OBJECT, project);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bindUI();
        setupTabLayout();
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
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(pager);

        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_tab_site_dashboard, null, false);

        fabTabSitelist = headerView.findViewById(R.id.tab_site_list);
        fabTabContactList = headerView.findViewById(R.id.tab_contacts);
        fabMap = headerView.findViewById(R.id.tab_map);


//        tabLayout.getTabAt(0).setCustomView(fabTabSitelist);
//        tabLayout.getTabAt(1).setCustomView(fabTabContactList);
//        tabLayout.getTabAt(2).setCustomView(fabMap);
//
//        fabTabSitelist.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4b8fbe")));

    }

}
