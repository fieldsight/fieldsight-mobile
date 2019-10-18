package org.fieldsight.naxa.common;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.WalledGardenInternetObservingStrategy;

import org.odk.collect.android.application.Collect;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static java.net.HttpURLConnection.HTTP_OK;

public class InternetUtils {
    private InternetUtils() {

    }

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


    public static InternetObservingSettings getReactiveNetworkSettings() {

        String host = FieldSightUserSession.getServerUrl(Collect.getInstance().getApplicationContext());
        host = host.replace("https://", "http://");
        host = "https://www.fieldsight.naxa.com.np";

        return InternetObservingSettings.builder().host(host)
                .httpResponse(HTTP_OK)
                .strategy(new SocketInternetObservingStrategy())
                .build();
    }
}
