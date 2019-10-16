package org.fieldsight.naxa.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.odk.collect.android.application.Collect;

public class NetworkUtils {

    private NetworkUtils() {

    }

    private static NetworkInfo getNetworkStat() {
        Context ctx = Collect.getInstance().getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo();
        } else {
            return null;
        }
    }

    public static boolean isMopbileType() {
        NetworkInfo networkInfo = getNetworkStat();
        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    public static boolean isWifiType(Context context) {
        NetworkInfo networkInfo = getNetworkStat();
        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    public static boolean isNetworkConnected() {
        NetworkInfo networkInfo = getNetworkStat();
        return networkInfo != null && networkInfo.isConnected();
    }
}
