package org.bcss.collect.naxa.common;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import org.bcss.collect.naxa.site.FragmentHostActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class InternetUtils {
    public static void checkInterConnectivity(OnConnectivityListener onConnectivityListener) {
        ReactiveNetwork.checkInternetConnectivity()
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        onConnectivityListener.onConnectionFailure();
                    }
                });
    }

    public  interface OnConnectivityListener {
        void onConnectionSuccess();

        void onConnectionFailure();
    }
}
