package org.bcss.collect.naxa.login;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.exception.FirebaseTokenException;
import org.bcss.collect.naxa.common.rx.RetrofitException;
import org.bcss.collect.naxa.firebase.FCMParameter;
import org.bcss.collect.naxa.login.model.AuthResponse;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;

import javax.net.ssl.SSLException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LoginModelImpl implements LoginModel {

    @Override
    public void login(String username, String password, String token, OnLoginFinishedListener listener) {

        authenticateUser(username, password, token, listener);
    }

    @Override
    public void loginViaGoogle(String googleAccessToken, String username, String token, OnLoginFinishedListener onLoginFinishedListener) {
        ServiceGenerator.createService(ApiInterface.class)
                .getAuthTokenUsingGoogle(googleAccessToken)
                .flatMap(new Function<AuthResponse, ObservableSource<FCMParameter>>() {
                    @Override
                    public ObservableSource<FCMParameter> apply(AuthResponse authResponse) {

                        ServiceGenerator.clearInstance();

                        return ServiceGenerator
                                .createService(ApiInterface.class)
                                .postFCMUserParameter(APIEndpoint.ADD_FCM, FieldSightUserSession.getFCMParameter(username, token, true))
                                .flatMap(new Function<FCMParameter, ObservableSource<FCMParameter>>() {
                                    @Override
                                    public ObservableSource<FCMParameter> apply(FCMParameter fcmParameter) throws Exception {
                                        if ("false".equals(fcmParameter.getIs_active())) {
                                            throw new FirebaseTokenException("Failed to add token in server");
                                        }

                                        FieldSightUserSession.saveAuthToken(authResponse.getToken());
                                        return Observable.just(fcmParameter);
                                    }
                                })
                                ;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<FCMParameter>() {
                    @Override
                    public void onNext(FCMParameter fcmParameter) {
                        onLoginFinishedListener.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        String errorMessage = e.getMessage();
                        if (e instanceof RetrofitException) {
                            RetrofitException retrofitException = (RetrofitException) e;
                            boolean hasErrorBody = retrofitException.getResponse().errorBody() != null;
                            errorMessage = hasErrorBody ? retrofitException.getMessage() : retrofitException.getKind().getMessage();
                        } else if (e instanceof SSLException) {
                            errorMessage = "A SSL exception occurred";
                        } else if (e instanceof FirebaseTokenException) {
                            errorMessage = Collect.getInstance().getString(R.string.dialog_error_register);
                        }
                        Timber.e(errorMessage);

                        onLoginFinishedListener.onError(errorMessage);

                    }

                    @Override
                    public void onComplete() {

                        onLoginFinishedListener.onSuccess();
                    }
                });
    }

    private void authenticateUser(String username, String password, String token, OnLoginFinishedListener onLoginFinishedListener) {
        ServiceGenerator.createService(ApiInterface.class)
                .getAuthToken(username, password)
                .flatMap(new Function<AuthResponse, ObservableSource<FCMParameter>>() {
                    @Override
                    public ObservableSource<FCMParameter> apply(AuthResponse authResponse) {
                        ServiceGenerator.clearInstance();
                        return ServiceGenerator
                                .createService(ApiInterface.class)
                                .postFCMUserParameter(APIEndpoint.ADD_FCM, FieldSightUserSession.getFCMParameter(username, token, true))
                                .flatMap(new Function<FCMParameter, ObservableSource<FCMParameter>>() {
                                    @Override
                                    public ObservableSource<FCMParameter> apply(FCMParameter fcmParameter) throws Exception {
                                        if ("false".equals(fcmParameter.getIs_active())) {
                                            throw new FirebaseTokenException("Failed to add token in server");
                                        }

                                        FieldSightUserSession.saveAuthToken(authResponse.getToken());
                                        return Observable.just(fcmParameter);
                                    }
                                })
                                ;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<FCMParameter>() {
                    @Override
                    public void onNext(FCMParameter fcmParameter) {
                        onLoginFinishedListener.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        String errorMessage = e.getMessage();
                        if (e instanceof RetrofitException) {
                            RetrofitException retrofitException = (RetrofitException) e;
                            boolean hasErrorBody = retrofitException.getResponse().errorBody() != null;
                            errorMessage = hasErrorBody ? retrofitException.getMessage() : retrofitException.getKind().getMessage();
                        } else if (e instanceof SSLException) {
                            errorMessage = "A SSL exception occurred";
                        } else if (e instanceof FirebaseTokenException) {
                            errorMessage = Collect.getInstance().getString(R.string.dialog_error_register);
                        }

                        onLoginFinishedListener.onError(errorMessage);

                    }

                    @Override
                    public void onComplete() {
                        onLoginFinishedListener.onSuccess();
                    }
                });
    }


}
