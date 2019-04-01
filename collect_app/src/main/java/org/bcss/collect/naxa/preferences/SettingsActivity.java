package org.bcss.collect.naxa.preferences;

import android.os.Bundle;
import android.view.MenuItem;

import org.bcss.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;

public class SettingsActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fieldsight_settings);
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
