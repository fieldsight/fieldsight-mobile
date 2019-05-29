package org.bcss.collect.naxa.notificationslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.network.NetworkUtils;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.v3.network.ApiV3Interface;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

    public void pullDataFromServer(String epochTime) {
        ServiceGenerator.getRxClient().create(ApiV3Interface.class).getNotification(new HashMap<String, String>() {{
            put("last_updated", epochTime);
        }}).map(new Function<ResponseBody, ArrayList<FieldSightNotification>>() {
            @Override
            public ArrayList<FieldSightNotification> apply(ResponseBody responseBody) throws Exception {
                ArrayList<FieldSightNotification> fieldSightNotificationList = new ArrayList<>();
                try {
                    String data = responseBody.string();
                    Timber.i("NotificationListViewModel data = %s", data);

                }catch (Exception e){e.printStackTrace();
                }
                return fieldSightNotificationList;
            }
        }).subscribe(new Consumer<ArrayList<FieldSightNotification>>() {
            @Override
            public void accept(ArrayList<FieldSightNotification> fieldSightNotifications) throws Exception {
              notificationRepository.save(fieldSightNotifications);
            }
        });
    }
}
