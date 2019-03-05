package org.bcss.collect.naxa.common;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class InternetUtils {
    public static void checkInterConnectivity(OnConnectivityListener onConnectivityListener) {
        ReactiveNetwork.checkInternetConnectivity()
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean hasInternet) {
                        if (hasInternet) {
                            onConnectivityListener.onConnectionSuccess();
                        } else {
                            onConnectivityListener.onConnectionFailure();
                        }

                        onConnectivityListener.onCheckComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onConnectivityListener.onConnectionFailure();
                        onConnectivityListener.onCheckComplete();
                    }
                });
    }

    public static DisposableObserver<Boolean> observeInternetConnectivity(OnConnectivityListener onConnectivityListener) {
        return ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean hasInternet) {
                        if (hasInternet) {
                            onConnectivityListener.onConnectionSuccess();
                        } else {
                            onConnectivityListener.onConnectionFailure();
                        }

                        onConnectivityListener.onCheckComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onConnectivityListener.onConnectionFailure();
                    }

                    @Override
                    public void onComplete() {
                        onConnectivityListener.onCheckComplete();
                    }
                });
    }

    public interface OnConnectivityListener {
        void onConnectionSuccess();

        void onConnectionFailure();

        void onCheckComplete();
    }
}
