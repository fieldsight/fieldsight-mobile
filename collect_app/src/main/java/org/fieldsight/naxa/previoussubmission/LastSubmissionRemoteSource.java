package org.fieldsight.naxa.previoussubmission;

import org.fieldsight.naxa.common.BaseRemoteDataSource;
import org.fieldsight.naxa.common.rx.RetrofitException;
import org.fieldsight.naxa.data.source.local.FieldSightNotificationLocalSource;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.previoussubmission.model.LastSubmissionResponse;
import org.fieldsight.naxa.previoussubmission.model.SubmissionDetail;
import org.fieldsight.naxa.common.DisposableManager;
import org.fieldsight.naxa.sync.DownloadableItemLocalSource;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.DownloadUID.PREV_SUBMISSION;

public class LastSubmissionRemoteSource implements BaseRemoteDataSource<LastSubmissionResponse> {

    private static LastSubmissionRemoteSource lastSubmissionRemoteSource;

    public static LastSubmissionRemoteSource getInstance() {
        if (lastSubmissionRemoteSource == null) {
            lastSubmissionRemoteSource = new LastSubmissionRemoteSource();
        }
        return lastSubmissionRemoteSource;
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
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsRunning(PREV_SUBMISSION);
                    }

                    @Override
                    public void onSuccess(List<LastSubmissionResponse> lastSubmissionResponses) {
                        FieldSightNotificationLocalSource.getInstance().markFormStatusChangeAsRead();
                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsCompleted(PREV_SUBMISSION);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String message;
                        if (e instanceof RetrofitException) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }

                        DownloadableItemLocalSource.getDownloadableItemLocalSource().markAsFailed(PREV_SUBMISSION,message);
                    }
                });


    }

    private Observable<LastSubmissionResponse> getPageAndNext(String url) {
        return ServiceGenerator.getRxClient().create(ApiInterface.class)
                .getAllFormResponses(url)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) {
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
                    public void accept(LastSubmissionResponse lastSubmissionResponse) {
                        LastSubmissionLocalSource.getInstance().save((ArrayList<SubmissionDetail>) lastSubmissionResponse.getSubmissionDetails());
                    }
                })
                .concatMap(new Function<LastSubmissionResponse, ObservableSource<LastSubmissionResponse>>() {
                    @Override
                    public ObservableSource<LastSubmissionResponse> apply(LastSubmissionResponse lastSubmissionResponse) {
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

