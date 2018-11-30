package org.bcss.collect.naxa.previoussubmission;

import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.rx.RetrofitException;
import org.bcss.collect.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.previoussubmission.model.LastSubmissionResponse;
import org.bcss.collect.naxa.previoussubmission.model.SubmissionDetail;
import org.bcss.collect.naxa.sync.DisposableManager;
import org.bcss.collect.naxa.sync.SyncLocalSource;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.PREV_SUBMISSION;
import static org.bcss.collect.naxa.common.Constant.DownloadUID.SITE_TYPES;

public class LastSubmissionRemoteSource implements BaseRemoteDataSource<LastSubmissionResponse> {

    private static LastSubmissionRemoteSource INSTANCE;

    public static LastSubmissionRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LastSubmissionRemoteSource();
        }
        return INSTANCE;
    }

    private LastSubmissionRemoteSource() {

    }


    @Override
    public void getAll() {


        getPageAndNext(APIEndpoint.GET_ALL_SUBMISSION)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<LastSubmissionResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        LastSubmissionLocalSource.getInstance().deleteAll();
                        SyncRepository.getInstance().showProgress(PREV_SUBMISSION);

                        SyncLocalSource.getINSTANCE().markAsRunning(PREV_SUBMISSION);
                    }

                    @Override
                    public void onSuccess(List<LastSubmissionResponse> lastSubmissionResponses) {
                        FieldSightNotificationLocalSource.getInstance().markFormStatusChangeAsRead();

                        SyncLocalSource.getINSTANCE().markAsCompleted(PREV_SUBMISSION);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncRepository.getInstance().setError(PREV_SUBMISSION);
                        SyncLocalSource.getINSTANCE().markAsFailed(PREV_SUBMISSION);
                        e.printStackTrace();


                        if (e instanceof RetrofitException) {
                            String message = ((RetrofitException) e).getMessage();
                            SyncLocalSource.getINSTANCE().addErrorMessage(PREV_SUBMISSION, message);
                        }
                    }
                });


    }

    private Observable<LastSubmissionResponse> getPageAndNext(String url) {
        return ServiceGenerator.getRxClient().create(ApiInterface.class)
                .getAllFormResponses(url)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                if (throwable instanceof SocketTimeoutException) {
                                    return throwableObservable.delay(10, TimeUnit.SECONDS);
                                }

                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .doOnNext(new Consumer<LastSubmissionResponse>() {
                    @Override
                    public void accept(LastSubmissionResponse lastSubmissionResponse) throws Exception {
                        LastSubmissionLocalSource.getInstance().save((ArrayList<SubmissionDetail>) lastSubmissionResponse.getSubmissionDetails());
                    }
                })
                .concatMap(new Function<LastSubmissionResponse, ObservableSource<LastSubmissionResponse>>() {
                    @Override
                    public ObservableSource<LastSubmissionResponse> apply(LastSubmissionResponse lastSubmissionResponse) throws Exception {
                        if (lastSubmissionResponse.getNext() == null) {
                            return Observable.just(lastSubmissionResponse);
                        }

                        return Observable.just(lastSubmissionResponse)
                                .delay(5, TimeUnit.SECONDS)
                                .concatWith(getPageAndNext(lastSubmissionResponse.getNext()));
                    }
                });

    }
}

