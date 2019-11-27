package org.fieldsight.naxa.preferences;

import android.content.Context;
import android.database.Cursor;

import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.dto.Form;
import org.odk.collect.android.dto.Instance;
import org.odk.collect.android.listeners.DeleteFormsListener;
import org.odk.collect.android.listeners.DeleteInstancesListener;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.odk.collect.android.tasks.DeleteFormsTask;
import org.odk.collect.android.tasks.DeleteInstancesTask;
import org.odk.collect.android.utilities.ResetUtility;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

class FieldSightResetUtility extends ResetUtility {

    void resetAsync(Context context, List<Integer> resetActions, OnResetListener onResetListener) {
        List<Integer> failedResetActions = super.reset(context, resetActions);

        for (int action : failedResetActions) {
            if (action == FieldSightResetActions.RESET_FLAGGED_SUBMISSIONS) {
                clearFormsDownloadFromFlagged(context, new DeleteFormsListener() {
                    @Override
                    public void deleteComplete(int deletedForms) {
                        resetActions.remove(Integer.valueOf(FieldSightResetActions.RESET_FLAGGED_SUBMISSIONS));
                        onResetListener.onComplete(resetActions);
                    }
                });
            }

        }
    }

    private Long[] getFlaggedForms() {
        FormsDao dao = new FormsDao();

        String selection = FormsProviderAPI.FormsColumns.IS_TEMP_DOWNLOAD + "=? ";
        String[] selectionArgs = new String[]{"1"};
        Cursor cursor = dao.getFormsCursor(selection, selectionArgs);
        List<Form> forms = dao.getFormsFromCursor(cursor);

        ArrayList<Long> formsToRemove = new ArrayList<>();
        for (Form form : forms) {
            long id = form.getId();
            formsToRemove.add(id);
        }
        Long[] longs = new Long[formsToRemove.size()];
        return formsToRemove.toArray(longs);
    }


    private void clearFormsDownloadFromFlagged(Context context, DeleteFormsListener listener) {
        Long[] flaggedForms = getFlaggedForms();
        DeleteInstancesTask deleteInstancesTask = new DeleteInstancesTask();
        deleteInstancesTask.setContentResolver(context.getContentResolver());
        deleteInstancesTask.setDeleteListener(new DeleteInstancesListener() {
            @Override
            public void deleteComplete(int deletedInstances) {
                DeleteFormsTask deleteFormsTask = new DeleteFormsTask();
                deleteFormsTask.setContentResolver(context.getContentResolver());
                deleteFormsTask.setDeleteListener(listener);
                deleteFormsTask.execute(getFlaggedForms());
            }

            @Override
            public void progressUpdate(int progress, int total) {
                Timber.i("Deleting %s out of %s INSTANCES", progress, total);
            }
        });


        deleteInstancesTask.execute(getFlaggedFormsInstance());

    }

    private Long[] getFlaggedFormsInstance() {
        FormsDao dao = new FormsDao();

        String selection = FormsProviderAPI.FormsColumns.IS_TEMP_DOWNLOAD + "=? ";
        String[] selectionArgs = new String[]{"1"};
        Cursor cursor = dao.getFormsCursor(selection, selectionArgs);
        List<Form> forms = dao.getFormsFromCursor(cursor);


        InstancesDao instancesDao = new InstancesDao();

        ArrayList<Long> instancesToDelete = new ArrayList<Long>();
        for (Form form : forms) {
            String version = form.getJrVersion();
            String id = form.getJrFormId();

            String instanceSelection = InstanceProviderAPI.InstanceColumns.JR_VERSION + "=? AND" + InstanceProviderAPI.InstanceColumns.JR_FORM_ID + "=?";
            String[] instanceSelectionArg = new String[]{version, id};
            Cursor instancesCursor = instancesDao.getInstancesCursor(instanceSelection, instanceSelectionArg);
            List<Instance> instances = instancesDao.getInstancesFromCursor(instancesCursor);

            for (Instance instance : instances) {
                instancesToDelete.add(instance.getDatabaseId());
            }
        }

        Long[] longs = new Long[instancesToDelete.size()];
        return instancesToDelete.toArray(longs);

    }


    static class FieldSightResetActions extends ResetAction {
        static final int RESET_FLAGGED_SUBMISSIONS = 6;
    }


    public interface OnResetListener {
        void onComplete(List<Integer> resetActions);
    }
}
