package org.bcss.collect.naxa.login;

import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.exception.FirebaseTokenException;
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
import retrofit2.HttpException;

public class LoginModelImpl implements LoginModel {

    @Override
    public void login(String username, String password, OnLoginFinishedListener listener) {

        authenticateUser(username, password, listener);
    }

    private void authenticateUser(String username, String password, OnLoginFinishedListener onLoginFinishedListener) {


        ServiceGenerator.createService(ApiInterface.class)
                .getAuthToken(username, password)
                .flatMap(new Function<AuthResponse, ObservableSource<FCMParameter>>() {
                    @Override
                    public ObservableSource<FCMParameter> apply(AuthResponse authResponse) {

                        ServiceGenerator.clearInstance();

                        return ServiceGenerator
                                .createService(ApiInterface.class)
                                .postFCMUserParameter(APIEndpoint.ADD_FCM, FieldSightUserSession.getFCM(username, true))
                                .flatMap(new Function<FCMParameter, ObservableSource<FCMParameter>>() {
                                    @Override
                                    public ObservableSource<FCMParameter> apply(FCMParameter fcmParameter) {
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

                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            int statusCode = httpException.response().code();
                            switch (statusCode) {
                                case 400:
                                    String parsedErrorMessage = APIErrorUtils.getNonFieldError(httpException);
                                    onLoginFinishedListener.onError(parsedErrorMessage);
                                    break;
                                default:
                                    onLoginFinishedListener.onError("Server returned " + statusCode);
                            }
                        } else if (e instanceof FirebaseTokenException) {
                            onLoginFinishedListener.fcmTokenError();
                        } else if (e instanceof SSLException) {
                            onLoginFinishedListener.onError("An SSL exception occurred");
                        } else {
                            onLoginFinishedListener.onError("Generic error occurred: " + e.getMessage());

                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
