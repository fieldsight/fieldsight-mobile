package org.bcss.collect.naxa.previoussubmission;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.educational.EducationalMaterialsDao;
import org.bcss.collect.naxa.educational.EducationalMaterialsRemoteSource;
import org.bcss.collect.naxa.generalforms.data.FormResponse;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.project.data.MySiteResponse;
import org.bcss.collect.naxa.submissions.FormHistoryResponse;
import org.bcss.collect.naxa.sync.SyncRepository;
import org.reactivestreams.Publisher;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.PREV_SUBMISSION;

public class LastSubmissionRemoteSource implements BaseRemoteDataSource<LastSubmissionResponse> {

    private static LastSubmissionRemoteSource INSTANCE;

    public static LastSubmissionRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LastSubmissionRemoteSource();
        }
        return INSTANCE;
    }

    private LastSubmissionRemoteSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
//        this.dao = database.getEducationalMaterialDAO();
    }


    @Override
    public void getAll() {
//        getPageAndNext(APIEndpoint.GET_ALL_SUBMISSION)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<FormHistoryResponse>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        SyncRepository.getInstance().showProgress(PREV_SUBMISSION);
//                    }
//
//                    @Override
//                    public void onNext(FormHistoryResponse formHistoryResponse) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        SyncRepository.getInstance().setError(PREV_SUBMISSION);
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        SyncRepository.getInstance().setSuccess(PREV_SUBMISSION);
//                    }
//                });
    }

//    private Observable<FormHistoryResponse> getPageAndNext(String url) {
//        return ServiceGenerator.getRxClient().create(ApiInterface.class)
//                .getFormResponse(url)
//                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) throws Exception {
//                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
//                            @Override
//                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
//                                if (throwable instanceof SocketTimeoutException) {
//                                    return throwableObservable.delay(10, TimeUnit.SECONDS);
//                                }
//
//                                return Observable.error(throwable);
//                            }
//                        });
//                    }
//                })
////                .
////                .map(new Function<FormHistoryResponse, List<FormResponse>>() {
////                    @Override
////                    public List<FormResponse> apply(FormHistoryResponse formHistoryResponse) throws Exception {
////                        return formHistoryResponse.getResults();
////                    }
////                })
//                .flatMapIterable(new Function<List<FormResponse>, Iterable<FormResponse>>() {
//                    @Override
//                    public Iterable<FormResponse> apply(List<FormResponse> formResponses) throws Exception {
//                        Timber.i("Updating %s responses",formResponses.size());
//                        uploadFormWithSubmission(formResponses);
//                        return formResponses;
//                    }
//                })
//
//                .doOnNext(new Consumer<FormHistoryResponse>() {
//                    @Override
//                    public void accept(FormHistoryResponse formHistoryResponse) throws Exception {
//                        Timber.i("Updating %s responses", formHistoryResponse.getResults().size());
//                        uploadFormWithSubmission(formHistoryResponse.getResults());
//                    }
//                })
//                .concatMap(new Function<FormHistoryResponse, ObservableSource<FormHistoryResponse>>() {
//                    @Override
//                    public ObservableSource<FormHistoryResponse> apply(FormHistoryResponse formHistoryResponse) throws Exception {
//                        if (formHistoryResponse.getNext() == null) {
//                            return Observable.just(formHistoryResponse);
//                        }
//
//                        return Observable.just(formHistoryResponse)
//                                .delay(5, TimeUnit.SECONDS)
//                                .concatWith(getPageAndNext(formHistoryResponse.getNext()));
//                    }
//                })
//                ;
//    }

    private void uploadFormWithSubmission(List<FormResponse> results) {
        for (FormResponse formResponse : results) {
            formResponse.getFormType();
        }
    }
}


