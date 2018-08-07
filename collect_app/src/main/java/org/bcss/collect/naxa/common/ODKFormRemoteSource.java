package org.bcss.collect.naxa.common;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.logic.FormDetails;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.onboarding.DownloadProgress;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadReceiver;
import org.bcss.collect.naxa.onboarding.XMLFormDownloadService;
import org.bcss.collect.naxa.task.FieldSightDownloadFormListTask;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;

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
                        EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_START));
                        break;
                    case DownloadProgress.STATUS_PROGRESS_UPDATE:
                        DownloadProgress progress = (DownloadProgress) resultData.getSerializable(EXTRA_OBJECT);
                        emitter.onNext(progress);
                        EventBus.getDefault().post(new DataSyncEvent(uid, progress));
                        break;
                    case DownloadProgress.STATUS_ERROR:
                        emitter.onError(null);
                        EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_ERROR));
                        break;
                    case DownloadProgress.STATUS_FINISHED_FORM:
                        emitter.onComplete();
                        EventBus.getDefault().post(new DataSyncEvent(uid, EVENT_END));
                        break;
                }
            });
            XMLFormDownloadService.start(Collect.getInstance(), xmlFormDownloadReceiver);
        });
    }


}
