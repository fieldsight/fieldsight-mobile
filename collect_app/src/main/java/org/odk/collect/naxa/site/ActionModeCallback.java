package org.odk.collect.naxa.site;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import org.odk.collect.android.R;

public class ActionModeCallback implements ActionMode.Callback {
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_upload_items, menu);


        return true;
    }


    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_sites:
                return true;

            case R.id.action_upload_sites:
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }


}
