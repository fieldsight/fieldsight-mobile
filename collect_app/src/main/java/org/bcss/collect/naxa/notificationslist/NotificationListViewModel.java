package org.bcss.collect.naxa.notificationslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.login.model.SiteMetaAttribute;
import org.bcss.collect.naxa.network.NetworkUtils;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.v3.network.ApiV3Interface;
import org.bcss.collect.naxa.v3.network.NotificationRemoteSource;
import org.bcss.collect.naxa.v3.network.ProjectBuilder;
import org.bcss.collect.naxa.v3.network.Region;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class NotificationListViewModel extends ViewModel {

    private MutableLiveData<Boolean> hasListData = new MutableLiveData<Boolean>();

    public MutableLiveData<List<FieldSightNotification>> fieldSightNoticationLiveData = new MutableLiveData<>();

    private final FieldSightNotificationRepository notificationRepository;

    public NotificationListViewModel(FieldSightNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void showList() {
        hasListData.setValue(true);
    }

    public void showEmpty() {
        hasListData.setValue(false);
    }

    public LiveData<List<FieldSightNotification>> getAll() {
        return notificationRepository.getAll(false);
    }

    void pullDataFromServer(String epochTime) {
        NotificationRemoteSource.getInstance().getNotifications(0)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<ResponseBody, ObservableSource<JSONObject>>) responseBody -> {
                    Timber.i("NotificationListViewModel value = %s", responseBody.string());
                    JSONArray jsonArray = new JSONObject(responseBody.string()).optJSONArray("notifications");
                    Timber.i("NotificationListViewModel, val length:: %d", jsonArray.length());
                    return Observable.range(0, jsonArray.length())
                            .map(jsonArray::getJSONObject);
                })
                .map(new Function<JSONObject, FieldSightNotification>() {
                    @Override
                    public FieldSightNotification apply(JSONObject json) throws Exception {
                        FieldSightNotification fieldSightNotification = new FieldSightNotification();
                        fieldSightNotification.setNotifiedDate(json.optString("data"));
                        fieldSightNotification.setProjectId(json.optJSONObject("message").optJSONObject("project").optString("id"));
                        fieldSightNotification.setProjectName(json.optJSONObject("message").optJSONObject("project").optString("name"));
                        fieldSightNotification.setNotificationType(json.optJSONObject("message").optString("notify_type"));
                        fieldSightNotification.setSiteId(json.optJSONObject("message").optJSONObject("site").optString("id"));
                        fieldSightNotification.setSiteName(json.optJSONObject("message").optJSONObject("site").optString("name"));
                        return fieldSightNotification;
                    }
                })
                .toList()
                .subscribe(new DisposableSingleObserver<List<FieldSightNotification>>() {
                    @Override
                    public void onSuccess(List<FieldSightNotification> list) {
                        notificationRepository.save((ArrayList<FieldSightNotification>) list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }
}
