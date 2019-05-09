package org.bcss.collect.naxa.network;

import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.common.rx.RxErrorHandlingCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {
    private static Retrofit retrofit = null;
    private static Retrofit cacheablesRetrofit = null;
    private static Gson gson = new GsonBuilder().create();
    private static Retrofit rxRetrofit;
    private static OkHttpClient okHttp;


    public static void clearInstance() {
        retrofit = null;
        cacheablesRetrofit = null;
        rxRetrofit = null;
        okHttp = null;
    }

    private static Interceptor createAuthInterceptor(final String token) {

        return chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("Authorization",
                            token)
                    .build();
            return chain.proceed(request);
        };
    }

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        String token = FieldSightUserSession.getAuthToken();

        boolean isTokenEmpty = token == null || token.trim().length() == 0;

        if (!isTokenEmpty) {
            okHttpClientBuilder.addInterceptor(createAuthInterceptor(token));
        }

        okHttpClientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(3600, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(3600, TimeUnit.SECONDS);


        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        }

        return okHttpClientBuilder
                .build();
    }

    private static OkHttpClient createCacheablesOkHttpClient() {
        String token = SharedPreferenceUtils.getFromPrefs(Collect.getInstance(), Constant.PrefKey.token, "");
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(Collect.getInstance().getCacheDir(), cacheSize);

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.cache(cache);
        okHttpBuilder.addInterceptor(chain -> {
            try {
                Request authorization = chain.request().newBuilder()
                        .addHeader("Authorization", "Token " + token).build();

                return chain.proceed(authorization);
            } catch (Exception e) {
                Request offlineRequest = chain.request().newBuilder()
                        .header("Cache-Control", "public, only-if-cached," +
                                "max-stale=" + 60 * 60 * 24)
                        .build();
                return chain.proceed(offlineRequest);
            }
        });

        if (BuildConfig.DEBUG) {
            okHttpBuilder.addNetworkInterceptor(new StethoInterceptor());
        }

        return okHttpBuilder.build();
    }


    public static <T> T createService(Class<T> serviceClass) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(createOkHttpClient())
                    .baseUrl(FieldSightUserSession.getServerUrl(Collect.getInstance()))
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }


        return retrofit.create(serviceClass);
    }

    public static <T> T createCacheService(Class<T> serviceClass) {
        if (cacheablesRetrofit == null) {
            cacheablesRetrofit = new Retrofit.Builder()
                    .client(createCacheablesOkHttpClient())
                    .baseUrl(FieldSightUserSession.getServerUrl(Collect.getInstance()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return cacheablesRetrofit.create(serviceClass);
    }

    public static Retrofit getRxClient() {


        if (okHttp == null) {
            okHttp = createOkHttpClient();
        }

        if (rxRetrofit == null) {
            rxRetrofit = new Retrofit.Builder()
                    .client(okHttp)
                    .baseUrl(FieldSightUserSession.getServerUrl(Collect.getInstance()))
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }


        return rxRetrofit;
    }


    public static int getRunningAPICount() {
        if (okHttp == null) return 0;

        return okHttp.dispatcher().runningCallsCount();
    }

    public static int getQueuedAPICount() {
        if (okHttp == null) return 0;

        return okHttp.dispatcher().queuedCallsCount();
    }

}
