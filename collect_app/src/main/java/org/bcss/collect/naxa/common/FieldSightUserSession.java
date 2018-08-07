package org.bcss.collect.naxa.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.DrawableCompat;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.dao.FormsDao;
import org.bcss.collect.android.dao.InstancesDao;
import org.bcss.collect.android.database.helpers.FormsDatabaseHelper;
import org.bcss.collect.android.listeners.DeleteFormsListener;
import org.bcss.collect.android.listeners.DeleteInstancesListener;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.android.tasks.DeleteFormsTask;
import org.bcss.collect.android.tasks.DeleteInstancesTask;
import org.bcss.collect.android.tasks.DownloadFormsTask;
import org.bcss.collect.naxa.common.database.FieldSightConfigDatabase;
import org.bcss.collect.naxa.login.LoginActivity;
import org.bcss.collect.naxa.site.db.SiteDao;

import java.util.ArrayList;

import timber.log.Timber;

public class FieldSightUserSession {


    public static void login() {

    }

    public static String getAuthToken() {
        return SharedPreferenceUtils.getFromPrefs(Collect.getInstance(), Constant.PrefKey.token, "");
    }

    public static void saveAuthToken(String token) {
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), Constant.PrefKey.token, "Token " + token);
    }

    private static String getLogoutMessage() {
        int offlineSitesNumber = 12;
        int unsentFormCount = new InstancesDao().getUnsentInstancesCursor().getCount();

        String msg;
        Context context = Collect.getInstance();

        if (offlineSitesNumber == 0 && unsentFormCount == 0) {
            msg = context.getString(R.string.logout_message_none);
        } else if (offlineSitesNumber == 0) {
            msg = context.getString(R.string.logout_message_only_filled_forms, unsentFormCount);
        } else if (unsentFormCount == 0) {
            msg = context.getString(R.string.logout_message_only_offline_sites, offlineSitesNumber);
        } else {
            msg = context.getString(R.string.logout_message_all, offlineSitesNumber, unsentFormCount);
        }

        return msg;
    }


    public static void stopLogoutDialog(Context context) {
        DialogFactory.createMessageDialog(context, "Can't logout", "An active internet connection required").show();
    }

    private static void logout(Context context) {

        AsyncTask.execute(() -> {
            FieldSightDatabase.getDatabase(context).clearAllTables();
            FieldSightConfigDatabase.getDatabase(context).clearAllTables();
        });
        SharedPreferenceUtils.deleteAll(context);


        DeleteInstancesTask deleteInstancesTask = new DeleteInstancesTask();
        deleteInstancesTask.setContentResolver(context.getContentResolver());
        deleteInstancesTask.setDeleteListener(new DeleteInstancesListener() {
            @Override
            public void deleteComplete(int deletedInstances) {
                deleteAllForms(context);
            }

            @Override
            public void progressUpdate(int progress, int total) {
                Timber.i("Deleting %s out of %s instances", progress, total);
            }
        });

        deleteInstancesTask.execute(getAllInstancedsIds());
    }

    private static void deleteAllForms(Context context) {
        DeleteFormsTask deleteFormsTask = new DeleteFormsTask();
        deleteFormsTask.setContentResolver(context.getContentResolver());
        deleteFormsTask.setDeleteListener(new DeleteFormsListener() {
            @Override
            public void deleteComplete(int deletedForms) {
                Timber.i("%s forms has been deleted", deletedForms);
                Intent intent = new Intent(context, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

            }
        });
        deleteFormsTask.execute(getAllFormsIds());
    }

    private static Long[] getAllFormsIds() {
        Cursor results = null;
        ArrayList<Long> ids = new ArrayList<>();
        try {
            results = new FormsDao().getFormsCursor();
            if (results.getCount() > 0) {
                results.moveToPosition(-1);
                while (results.moveToNext()) {
                    String id = results.getString(results
                            .getColumnIndex(FormsProviderAPI.FormsColumns._ID));

                    ids.add(Long.valueOf(id));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();//should never happen
        } finally {
            if (results != null) {
                results.close();
            }
        }

        Long[] longs = new Long[ids.size()];
        return ids.toArray(longs);
    }

    private static Long[] getAllInstancedsIds() {
        Cursor results = null;
        ArrayList<Long> ids = new ArrayList<>();
        try {
            results = new InstancesDao().getInstanceCursor();
            if (results.getCount() > 0) {
                results.moveToPosition(-1);
                while (results.moveToNext()) {
                    String id = results.getString(results
                            .getColumnIndex(InstanceProviderAPI.InstanceColumns._ID));

                    ids.add(Long.valueOf(id));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();//should never happen
        } finally {
            if (results != null) {
                results.close();
            }
        }

        Long[] longs = new Long[ids.size()];
        return ids.toArray(longs);
    }

    public static void createLogoutDialog(Context context) {

        String dialogTitle = context.getApplicationContext().getString(R.string.dialog_title_warning_logout);
        String dialogMsg;
        String posMsg = context.getApplicationContext().getString(R.string.dialog_warning_logout_pos);
        String negMsg = context.getApplicationContext().getString(R.string.dialog_warning_logout_neg);
        @ColorInt int color = context.getResources().getColor(R.color.primaryColor);
        Drawable drawable = context.getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        Drawable wrapped = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapped, color);

        dialogMsg = FieldSightUserSession.getLogoutMessage();


        DialogFactory.createActionDialog(context, dialogTitle, dialogMsg).setPositiveButton(posMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logout(context);
            }
        }).setNegativeButton(negMsg, null).setIcon(wrapped).show();

    }

    public static boolean isLoggedIn() {
        String token = getAuthToken();
        return token != null && token.length() > 0;
    }
}
