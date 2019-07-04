package org.bcss.collect.naxa.notificationslist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.naxa.data.FieldSightNotification;

import java.util.ArrayList;
import java.util.List;

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

    void saveData(List<FieldSightNotification> fieldSightNotifications) {
        notificationRepository.save((ArrayList<FieldSightNotification>) fieldSightNotifications);
    }
}
