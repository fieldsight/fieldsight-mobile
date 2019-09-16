package org.fieldsight.naxa.forms;

import android.database.Cursor;

import org.odk.collect.android.provider.InstanceProviderAPI;
import org.fieldsight.naxa.helpers.FSInstancesDao;

import java.util.ArrayList;
import java.util.List;

public class FieldSightInstanceDAO extends FSInstancesDao {

    private Cursor getrecentFormCursor(String jrFormId) {
        String selection = InstanceProviderAPI.InstanceColumns.JR_FORM_ID + " =? ";
        String[] selectionArgs = {jrFormId};
        String sortOrder = InstanceProviderAPI.InstanceColumns._ID + " DESC LIMIT 1";

        return getInstancesCursor(null, selection, selectionArgs, sortOrder);

    }

    public List<Long> getRecentFormId(String jrFormId) {

        Cursor cursor = getrecentFormCursor(jrFormId);
        List<Long> ids = new ArrayList<>();
        if (cursor != null) {
            try {

                cursor.moveToFirst();

                int databaseIdIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID);
                ids.add(cursor.getLong(databaseIdIndex));
            } finally {
                cursor.close();
            }
        }
        return ids;

    }
}
