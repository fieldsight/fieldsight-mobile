package org.bcss.collect.naxa.login;

import android.support.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.auth.api.Auth;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.logic.PropertyManager;
import org.bcss.collect.naxa.FirebaseTokenException;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.firebase.FCMParameter;
import org.bcss.collect.naxa.login.model.AuthResponse;
import org.bcss.collect.naxa.login.model.MeResponse;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
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
                    public ObservableSource<FCMParameter> apply(AuthResponse authResponse) throws Exception {
                        return ServiceGenerator
                                .createService(ApiInterface.class)
                                .postFCMUserParameter(APIEndpoint.ADD_FCM, FieldSightUserSession.getFCM(username, true))
                                .flatMap(new Function<FCMParameter, ObservableSource<FCMParameter>>() {
                                    @Override
                                    public ObservableSource<FCMParameter> apply(FCMParameter fcmParameter) throws Exception {
                                        if ("false".equals(fcmParameter.getIs_active())) {
                                            throw new FirebaseTokenException("FieldSight failed to add token");
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
                            onLoginFinishedListener.onError();
                        } else if (e instanceof FirebaseTokenException) {
                            onLoginFinishedListener.fcmTokenError();
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
