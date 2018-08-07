package org.bcss.collect.naxa.login;

import android.support.annotation.NonNull;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.auth.api.Auth;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.login.model.AuthResponse;
import org.bcss.collect.naxa.login.model.MeResponse;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginModelImpl implements LoginModel {

    @Override
    public void login(String username, String password, OnLoginFinishedListener listener) {

        authenticateUser(username, password, listener);
    }

    private void authenticateUser(String username, String password, OnLoginFinishedListener onLoginFinishedListener) {


        ServiceGenerator.createService(ApiInterface.class)
                .getAuthToken(username, password)
                .map((Function<AuthResponse, ObservableSource<MeResponse>>) authResponse -> {
                    FieldSightUserSession.saveAuthToken(authResponse.getToken());
                    return Observable.empty();
                })
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ObservableSource<MeResponse>>() {
                    @Override
                    public void onSuccess(ObservableSource<MeResponse> meResponseObservableSource) {
                        onLoginFinishedListener.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onLoginFinishedListener.onError();
                        e.printStackTrace();
                    }
                });
    }

}
