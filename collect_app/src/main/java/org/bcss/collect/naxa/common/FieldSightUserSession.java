package org.bcss.collect.naxa.common;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
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

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.listeners.DeleteFormsListener;
import org.bcss.collect.android.listeners.DeleteInstancesListener;
import org.bcss.collect.android.logic.PropertyManager;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.android.provider.InstanceProviderAPI;
import org.bcss.collect.naxa.common.database.FieldSightConfigDatabase;
import org.bcss.collect.naxa.common.exception.FirebaseTokenException;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;
import org.bcss.collect.naxa.firebase.FCMParameter;
import org.bcss.collect.naxa.login.LoginActivity;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.User;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.site.db.SiteLocalSource;
import org.bcss.collect.naxa.sync.SyncRepository;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.tasks.DeleteFormsTask;
import org.odk.collect.android.tasks.DeleteInstancesTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
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

    public static MutableLiveData<String[]> getLogoutMessage() {

        MutableLiveData<String[]> message = new MutableLiveData<>();
        Context context = Collect.getInstance();
        message.setValue(new String[]{
                "",
                "",
                ""
        });


        SiteLocalSource.getInstance().getAllByStatus(Constant.SiteStatus.IS_OFFLINE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Site>>() {
                    @Override
                    public void onSuccess(List<Site> sites) {
                        String msg;
                        int unsentFormCount = new InstancesDao().getUnsentInstancesCursor().getCount();
                        int offlineSitesNumber = sites.size();

                        if (offlineSitesNumber == 0 && unsentFormCount == 0) {
                            msg = context.getString(R.string.logout_message_none);
                        } else if (offlineSitesNumber == 0) {
                            msg = context.getString(R.string.logout_message_only_filled_forms, unsentFormCount);
                        } else if (unsentFormCount == 0) {
                            msg = context.getString(R.string.logout_message_only_offline_sites, offlineSitesNumber);
                        } else {
                            msg = context.getString(R.string.logout_message_all, offlineSitesNumber, unsentFormCount);
                        }


                        message.setValue(new String[]{
                                msg,
                                String.valueOf(unsentFormCount),
                                String.valueOf(offlineSitesNumber)
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        return message;
    }

    private static String getLogoutMessage(int offlineSitesNumber, int unsentFormCount) {


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


    public static void showLogoutDialog(Activity context) {

        ((CollectAbstractActivity) context).showProgress();

        SiteLocalSource.getInstance()
                .getAllByStatus(Constant.SiteStatus.IS_OFFLINE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Site>>() {
                    @Override
                    public void onSuccess(List<Site> sites) {
                        int unsentFormCount = new InstancesDao().getUnsentInstancesCursor().getCount();
                        int offlineSitesNumber = sites.size();

                        boolean isSafeToLogout = (unsentFormCount + offlineSitesNumber) == 0;
                        if (isSafeToLogout) {

                            logout(context, new OnLogoutListener() {
                                @Override
                                public void logoutTaskSuccess() {

                                    ((CollectAbstractActivity) context).hideProgress();
                                    Intent intent = new Intent(context, LoginActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                }

                                @Override
                                public void logoutTaskFailed(String message) {
                                    Timber.e(message);
                                    ((CollectAbstractActivity) context).hideProgress();
                                    FlashBarUtils.showFlashbar(context, "Logout failed ");
                                }

                                @Override
                                public void taskComplete() {
                                    ((CollectAbstractActivity) context).hideProgress();
                                }
                            });
                        } else {
                            ((CollectAbstractActivity) context).hideProgress();
                            DialogFactory.createMessageDialog(context,
                                    context.getString(R.string.msg_stop_logout),
                                    getLogoutMessage(offlineSitesNumber, unsentFormCount))
                                    .show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        ((CollectAbstractActivity) context).hideProgress();
                        DialogFactory.createMessageDialog(context,
                                context.getString(R.string.msg_stop_logout),
                                context.getString(R.string.dialog_unexpected_error_title))
                                .show();
                    }
                });


    }


    public static void stopLogoutDialog(Context context) {

        DialogFactory.createMessageDialog(context, "Can't logout", "An active internet connection required").show();
    }


    private static void removeFormsAndInstances(Context context, DeleteFormsListener listener) {
        DeleteInstancesTask deleteInstancesTask = new DeleteInstancesTask();
        deleteInstancesTask.setContentResolver(context.getContentResolver());
        deleteInstancesTask.setDeleteListener(new DeleteInstancesListener() {
            @Override
            public void deleteComplete(int deletedInstances) {
                DeleteFormsTask deleteFormsTask = new DeleteFormsTask();
                deleteFormsTask.setContentResolver(context.getContentResolver());
                deleteFormsTask.setDeleteListener(listener);
                deleteFormsTask.execute(getAllFormsIds());
            }

            @Override
            public void progressUpdate(int progress, int total) {
                Timber.i("Deleting %s out of %s instances", progress, total);
            }
        });


        deleteInstancesTask.execute(getAllInstancedsIds());

    }


    public interface OnLogoutListener {
        void logoutTaskSuccess();

        void logoutTaskFailed(String message);

        void taskComplete();
    }

    private static void logout(Context context, OnLogoutListener logoutListener) {

        Completable purgeDatabase = Completable.fromAction(() -> {
            FieldSightDatabase.getDatabase(context).clearAllTables();
            FieldSightConfigDatabase.getDatabase(context).clearAllTables();
        });


        Completable purgeSharedPref = Completable.fromAction(new Action() {
            @Override
            public void run() {
                SharedPreferenceUtils.deleteAll(context);
            }
        });


        Observable<Response<Void>> deleteFCM =
                Observable.just(1)
                        .map(new Function<Integer, User>() {
                            @Override
                            public User apply(Integer integer) throws Exception {
                                return getUser();
                            }
                        })
                        .flatMap(new Function<User, Observable<Response<Void>>>() {
                            @Override
                            public Observable<Response<Void>> apply(User user) throws Exception {
                                return ServiceGenerator
                                        .createService(ApiInterface.class)
                                        .deleteFCMUserParameter(getFCM(user.getUser_name(), false))
                                        .map(new Function<Response<Void>, Response<Void>>() {
                                            @Override
                                            public Response<Void> apply(Response<Void> voidResponse) throws Exception {
                                                if (voidResponse.code() != 200) {
                                                    throw new RuntimeException("FCM removal did not return 200");
                                                }
                                                return voidResponse;
                                            }
                                        });
                            }
                        });

        Observable.concat(deleteFCM, purgeSharedPref.toObservable(), purgeDatabase.toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(Response<Void> voidResponse) {

                        removeFormsAndInstances(context, deletedForms -> {
                            ServiceGenerator.clearInstance();
                            SyncRepository.instance = null;
                            logoutListener.logoutTaskSuccess();
                            logoutListener.taskComplete();
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        logoutListener.logoutTaskFailed(e.getMessage());
                        logoutListener.taskComplete();

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    public static void setUser(User user) {
        if (user != null) {
            String userString = GSONInstance.getInstance().toJson(user);
            SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), SharedPreferenceUtils.PREF_KEY.USER, userString);
        }
    }


    @NonNull
    public static User getUser() throws IllegalArgumentException {
        String userString = SharedPreferenceUtils.getFromPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_KEY.USER, null);
        if (userString == null || userString.length() == 0) {
            throw new IllegalArgumentException("User information is missing from cache");
        }
        return GSONInstance.getInstance().fromJson(userString, User.class);
    }

    private static void deleteAllForms(Context context) {

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


    public static void createLogoutDialogV2(Activity context, String dialogMessage) {
        String dialogTitle = context.getApplicationContext().getString(R.string.dialog_title_warning_logout);
        String dialogMsg;
        String posMsg = context.getApplicationContext().getString(R.string.dialog_warning_logout_pos);
        String negMsg = context.getApplicationContext().getString(R.string.dialog_warning_logout_neg);

        @ColorInt int color = context.getResources().getColor(R.color.primaryColor);
        Drawable drawable = context.getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        Drawable wrapped = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapped, color);


        DialogFactory.createMessageDialog(context, context.getString(R.string.msg_stop_logout), dialogMessage)

                .show();

        /**
         * 1. Show list of offline sites
         * 2. Show a list of forms
         * 3. Upload them
         * 4. If Failed show just restart the process
         */

    }

    @Deprecated
    public static void createLogoutDialog(Activity context, String dialogMsg) {

        String dialogTitle = context.getApplicationContext().getString(R.string.dialog_title_warning_logout);
        String posMsg = context.getApplicationContext().getString(R.string.dialog_warning_logout_pos);
        String negMsg = context.getApplicationContext().getString(R.string.dialog_warning_logout_neg);
        @ColorInt int color = context.getResources().getColor(R.color.primaryColor);
        Drawable drawable = context.getResources().getDrawable(android.R.drawable.ic_dialog_alert);
        Drawable wrapped = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapped, color);


        DialogFactory.createActionDialog(context, dialogTitle, dialogMsg)
                .setPositiveButton(posMsg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        logout(context, new OnLogoutListener() {
                            @Override
                            public void logoutTaskSuccess() {


                                Intent intent = new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }

                            @Override
                            public void logoutTaskFailed(String message) {


                                FlashBarUtils.showFlashbar(context, "Logout failed");
                            }

                            @Override
                            public void taskComplete() {
                                ((CollectAbstractActivity) context).hideProgress();
                            }
                        });
                    }
                }).setNegativeButton(negMsg, null).setIcon(wrapped).show();

    }

    public static boolean isLoggedIn() {
        String token = getAuthToken();
        return token != null && token.length() > 0;
    }
}
