package org.odk.collect.naxa.login;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.SharedPreferenceUtils;
import org.odk.collect.naxa.login.model.AuthResponse;
import org.odk.collect.naxa.login.model.MeResponse;
import org.odk.collect.naxa.network.ApiInterface;
import org.odk.collect.naxa.network.ServiceGenerator;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginModelImpl implements LoginModel {

    private OnLoginFinishedListener loginFinishedListener;
    private OnFetchUserInfoListener fetchUserInfoListener;

    @Override
    public void login(String username, String password, OnLoginFinishedListener listener) {
        this.loginFinishedListener = listener;
        authenticateUser(username, password, listener);

    }

    @Override
    public void fetchUserInformation(OnFetchUserInfoListener listener) {
        this.fetchUserInfoListener = listener;
        fetchUserTask();
    }


    private void authenticateUser(String username, String password, OnLoginFinishedListener listener) {
        ServiceGenerator.createService(ApiInterface.class)
                .getAuthToken(username, password)
                .flatMap((Function<AuthResponse, ObservableSource<MeResponse>>) authResponse -> {

                    SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), Constant.PrefKey.token, authResponse.getToken());
                    return ServiceGenerator.createService(ApiInterface.class).getUserInformation();
                })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserable(listener));
    }

    private void fetchUserTask() {
        ServiceGenerator.createService(ApiInterface.class).getUserInformation()
                .subscribe(new DisposableObserver<MeResponse>() {
                    @Override
                    public void onNext(MeResponse meResponse) {
                        fetchUserInfoListener.onSucess(meResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        fetchUserInfoListener.onError();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @NonNull
    private Observer<? super Object> getObserable(OnLoginFinishedListener listener) {
        return new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                listener.onError();
            }

            @Override
            public void onComplete() {
                listener.onSuccess();
            }
        };
    }
}
