package org.bcss.collect.naxa.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.listeners.DeleteFormsListener;
import org.bcss.collect.android.listeners.DeleteInstancesListener;
import org.bcss.collect.android.logic.PropertyManager;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.naxa.common.database.FieldSightConfigDatabase;
import org.bcss.collect.naxa.common.exception.FirebaseTokenException;
import org.bcss.collect.naxa.firebase.FCMParameter;
import org.bcss.collect.naxa.login.LoginActivity;
import org.bcss.collect.naxa.login.model.User;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.sync.SyncRepository;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.tasks.DeleteFormsTask;
import org.odk.collect.android.tasks.DeleteInstancesTask;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static org.bcss.collect.naxa.network.APIEndpoint.REMOVE_FCM;

public class FieldSightUserSession {


    public static void login() {

    }

    public static String getAuthToken() {
        return SharedPreferenceUtils.getFromPrefs(Collect.getInstance(), Constant.PrefKey.token, FieldSighDebug.Token);
    }

    public static void saveAuthToken(String token) {
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), Constant.PrefKey.token, "Token " + token);
    }


    public static FCMParameter getFCM(String username, boolean deviceStatus) {
        String deviceId = new PropertyManager(Collect.getInstance()).getSingularProperty(PropertyManager.PROPMGR_DEVICE_ID);
        String fcmToken = SharedPreferenceUtils.getFromPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, null);

        if (fcmToken == null) {
            AsyncTask.execute(() -> {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }

        if (fcmToken == null) {
            throw new FirebaseTokenException("firebase token is null");
        }

        return new FCMParameter(deviceId, fcmToken, username, String.valueOf(deviceStatus));
    }

    private static String getLogoutMessage() {
        int offlineSitesNumber = 0;
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

        try {
            User user = getUser();
            ServiceGenerator
                    .createService(ApiInterface.class)
                    .postFCMUserParameter(REMOVE_FCM, getFCM(user.getUser_name(), false))
                    .subscribe(new Observer<FCMParameter>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(FCMParameter fcmParameter) {
                            Timber.i(fcmParameter.toString());
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        } catch (IllegalArgumentException e) {
//            Crashlytics.log(e.getMessage());

        }


        AsyncTask.execute(() -> {
            FieldSightDatabase.getDatabase(context).clearAllTables();
            FieldSightConfigDatabase.getDatabase(context).clearAllTables();
        });

        //bug: fcm token is not generating when issued getToken() hence we exclude it from deletion here
        String fcmToken = SharedPreferenceUtils.getFromPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, null);
        SharedPreferenceUtils.deleteAll(context);
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, fcmToken);

        ServiceGenerator.clearInstance();

        SyncRepository.instance = null; //todo: done to resolve sync screen blank bug; need to fix in future

    }

    public static void setUser(User user) {
        if (user != null) {
            String userString = GSONInstance.getInstance().toJson(user);
            SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), SharedPreferenceUtils.PREF_KEY.USER, userString);
        }
    }


    @NonNull
    public static User getUser() {
        String userString = SharedPreferenceUtils.getFromPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_KEY.USER, null);
        if (userString == null || userString.length() == 0) {
            throw new IllegalArgumentException("User information is missing from cache");
        }
        return GSONInstance.getInstance().fromJson(userString, User.class);
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
