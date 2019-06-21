package org.bcss.collect.naxa.notificationslist;

import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class NotificationRemoteSource {

    private static NotificationRemoteSource INSTANCE = null;

    public static NotificationRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotificationRemoteSource();
        }
        return INSTANCE;
    }

    public Single<ResponseBody> getNotifications(long epochTime) {
        return ServiceGenerator.getRxClient().create(ApiInterface.class)
                .getNotification(getHasMap(epochTime))
                .subscribeOn(Schedulers.io());
    }

    private Map<String, String> getHasMap(long epochTime) {
        HashMap<String, String> requestParams = new HashMap<>();
        requestParams.put("last_updated", epochTime+ "");
        return requestParams;
    }
}