package org.bcss.collect.naxa.common;

import android.os.Handler;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.onboarding.DownloadProgress;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadReceiver;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadService;
import org.bcss.collect.naxa.sync.SyncLocalSource;
import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.bcss.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class ODKFormRemoteSource {


    private static ODKFormRemoteSource INSTANCE;

    public static ODKFormRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ODKFormRemoteSource();
        }
        return INSTANCE;
    }

    public Observable<DownloadProgress> fetchODKForms() {
        int uid = Constant.DownloadUID.ODK_FORMS;
        return Observable.create(emitter -> {
            XMLFormDownloadReceiver xmlFormDownloadReceiver = new XMLFormDownloadReceiver(new Handler());

            xmlFormDownloadReceiver.setReceiver((resultCode, resultData) -> {
                switch (resultCode) {
                    case DownloadProgress.STATUS_RUNNING:
                        break;
                    case DownloadProgress.STATUS_PROGRESS_UPDATE:
                        DownloadProgress progress = (DownloadProgress) resultData.getSerializable(EXTRA_OBJECT);
                        emitter.onNext(progress);
                        SyncLocalSource.getINSTANCE().updateProgress(Constant.DownloadUID.ALL_FORMS,progress.getTotal(),progress.getProgress());
                        break;
                    case DownloadProgress.STATUS_ERROR:
                        emitter.onError(null);
                        break;
                    case DownloadProgress.STATUS_FINISHED_FORM:
                        emitter.onComplete();
                        break;
                }
            });

            XMLFormDownloadService.start(Collect.getInstance(), xmlFormDownloadReceiver);
        });
    }


}
