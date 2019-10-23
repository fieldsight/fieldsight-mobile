package org.fieldsight.naxa.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.map.ProjectMapFragment;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;


public class ProjectMapActivity extends CollectAbstractActivity {


    private Toolbar toolbar;

    public static void start(Context context, Site loadedSite) {
        Intent intent = new Intent(context, ProjectMapActivity.class);
        intent.putExtra(EXTRA_OBJECT, loadedSite);
        context.startActivity(intent);
    }


    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_map);
        setupToolbar();
        Site loadedSite = getIntent().getExtras().getParcelable(EXTRA_OBJECT);
        if (loadedSite == null) {
            ToastUtils.showLongToast(R.string.dialog_unexpected_error_title);
            finish();
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, ProjectMapFragment.newInstance(loadedSite), "frag1")
                .commit();

        toolbar.setTitle(loadedSite.getName());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
