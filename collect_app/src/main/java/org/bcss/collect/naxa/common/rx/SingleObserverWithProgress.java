package org.bcss.collect.naxa.common.rx;

import android.content.Context;
import android.support.annotation.NonNull;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.firebase.NotificationUtils;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class SingleObserverWithProgress<T> implements SingleObserver<T> {
    private String progressMessage, completedMessage, failedMessage;
    private int uid;
    private Context context;

    protected SingleObserverWithProgress(@NonNull String progressMessage, @NonNull String completedMessage, @NonNull String failedMessage) {
        this.progressMessage = progressMessage;
        this.completedMessage = completedMessage;
        this.failedMessage = failedMessage;
        this.context = Collect.getInstance().getApplicationContext();
    }

    @Override
    public void onSubscribe(Disposable d) {
        uid = NotificationUtils.createProgressNotification(Collect.getInstance().getApplicationContext(), progressMessage);
    }

    @Override
    public void onSuccess(T t) {
        NotificationUtils.cancelNotification(uid);
        NotificationUtils.notifyNormal(context, completedMessage, completedMessage);
    }

    @Override
    public void onError(Throwable e) {
        NotificationUtils.notifyNormal(context, failedMessage, failedMessage);
    }
}
