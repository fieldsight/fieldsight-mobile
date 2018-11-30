package org.bcss.collect.naxa.sync;

import org.bcss.collect.naxa.common.Constant;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DownloadReportingObserver implements Observer {
    int downloadUID;

    public DownloadReportingObserver(int downloadUID) {
        this.downloadUID = downloadUID;
    }

    @Override
    public void onSubscribe(Disposable d) {
        SyncLocalSource.getINSTANCE()
                .markAsRunning(downloadUID);
    }

    @Override
    public void onNext(Object o) {
        SyncLocalSource.getINSTANCE()
                .markAsCompleted(downloadUID);
    }

    @Override
    public void onError(Throwable e) {
        SyncLocalSource.getINSTANCE()
                .markAsFailed(downloadUID);
    }

    @Override
    public void onComplete() {

    }
}
