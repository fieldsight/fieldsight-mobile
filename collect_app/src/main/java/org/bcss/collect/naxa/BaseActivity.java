package org.bcss.collect.naxa;


import android.view.MenuItem;
import android.widget.Toast;

import org.odk.collect.android.activities.CollectAbstractActivity;

public abstract class BaseActivity extends CollectAbstractActivity {


    public void initBack() {
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackClicked(true);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        onBackClicked(false);
    }

    public void toast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public abstract void onBackClicked(boolean isHome);


}
