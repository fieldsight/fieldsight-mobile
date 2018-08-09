package org.bcss.collect.naxa.notification;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

public class NotificationListViewModel extends ViewModel {

    private MutableLiveData<Boolean> hasListData = new MutableLiveData<Boolean>();

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

    public LiveData<List<FieldSightNotification>> getAll(){
        return notificationRepository.getAll(false);
    }
}
