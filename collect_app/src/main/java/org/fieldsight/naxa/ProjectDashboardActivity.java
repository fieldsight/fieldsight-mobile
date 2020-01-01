package org.fieldsight.naxa;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.common.NonSwipeableViewPager;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.project.TermsLabels;
import org.fieldsight.naxa.site.OldProjectDashboardActivity;
import org.odk.collect.android.utilities.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class ProjectDashboardActivity extends BaseActivity {

    private Project loadedProject;
    private CardView searchView;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fabTabSitelist, fabTabContactList, fabMap;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private boolean mapIsVisible;
    private FrameLayout navigationHeader;
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

    public static void start(Context context, Project project) {
        Intent intent = new Intent(context, OldProjectDashboardActivity.class);
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
        ButterKnife.bind(this);

        try {
            loadedProject = getIntent().getParcelableExtra(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
            finish();
        }

    }
    @Override
    public void onBackClicked(boolean isHome) {

    }
}
