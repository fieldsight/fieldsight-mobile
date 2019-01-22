package org.bcss.collect.naxa.common;

import java.util.Timer;

import timber.log.Timber;

public class AppLogger {

    public static void error(Throwable e) {
        Timber.e(e);
    }

    public static void debug(String s) {
        Timber.d(s);
    }
}
