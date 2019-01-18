package org.bcss.collect.naxa.common;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.support.v4.app.Fragment;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.dao.InstancesDao;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class FieldSightFormListFragment extends Fragment {

    protected void fillODKForm(String idString) {
        try {
            long formId = getFormId(idString);
            Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, formId);
            String action = getActivity().getIntent().getAction();


            if (Intent.ACTION_PICK.equals(action)) {
                // caller is waiting on a picked form
                getActivity().setResult(RESULT_OK, new Intent().setData(formUri));
            } else {
                // caller wants to view/edit a form, so launch formentryactivity
                Intent toFormEntry = new Intent(Intent.ACTION_EDIT, formUri);
                toFormEntry.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(toFormEntry);

            }
        } catch (CursorIndexOutOfBoundsException e) {
            DialogFactory.createGenericErrorDialog(getActivity(), getString(R.string.form_not_present)).show();
            Timber.e("Failed to load xml form  %s", e.getMessage());
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            DialogFactory.createGenericErrorDialog(getActivity(), e.getMessage()).show();
            Timber.e("Failed to load xml form %s", e.getMessage());
        }


    }

    protected String generateSubmissionUrl(String formDeployedFrom, String creatorsId, String fsFormId) {
        return InstancesDao.generateSubmissionUrl(formDeployedFrom, creatorsId, fsFormId);
    }

    protected long getFormId(String jrFormId) throws CursorIndexOutOfBoundsException, NullPointerException, NumberFormatException {

        String[] projection = new String[]{FormsProviderAPI.FormsColumns._ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH};
        String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
        String[] selectionArgs = new String[]{jrFormId};
        String sortOrder = FormsProviderAPI.FormsColumns._ID + " DESC LIMIT 1";

        Cursor cursor = requireActivity().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                projection,
                selection, selectionArgs, sortOrder);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID);
        long formId = Long.parseLong(cursor.getString(columnIndex));

        cursor.close();

        return formId;
    }


}
