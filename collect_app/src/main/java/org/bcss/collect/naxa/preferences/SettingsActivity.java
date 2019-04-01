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


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ScheduledNotificationSettingsFragment())
                .addToBackStack(null)
                .commit();

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
