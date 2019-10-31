package org.fieldsight.naxa.network;

import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.fieldsight.collect.android.BuildConfig;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.common.rx.RxErrorHandlingCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {
    private static Retrofit retrofit;
    private static Retrofit cacheablesRetrofit;
    private static Gson gson = new GsonBuilder().create();
    private static Retrofit rxRetrofit;
    private static OkHttpClient okHttp;


    private ServiceGenerator() {

    }

    public static void clearInstance() {
        retrofit = null;
        cacheablesRetrofit = null;
        rxRetrofit = null;
        okHttp = null;
    }

    private static Interceptor createAuthInterceptor(final String token) {
        return chain -> {
            try {
                Request authorization = chain.request().newBuilder()
                        .addHeader("Authorization", token).build();

                return chain.proceed(authorization);
            } catch (Exception e) {
                Request offlineRequest = chain.request().newBuilder()
                        .header("Cache-Control", "public, only-if-cached," +
                                "max-stale=" + 60 * 60 * 24)
                        .build();
                return chain.proceed(offlineRequest);
            }
        };
    }

    private static OkHttpClient createOkHttpClient(boolean cacheRequest) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        String token = FieldSightUserSession.getAuthToken();
        if (!TextUtils.isEmpty(token)) {
            okHttpClientBuilder.addInterceptor(createAuthInterceptor(token));
        }

        okHttpClientBuilder.connectTimeout(60, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(60, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS);

        if (cacheRequest) {
            int cacheSize = 10 * 1024 * 1024;
            Cache cache = new Cache(Collect.getInstance().getCacheDir(), cacheSize);
            okHttpClientBuilder.cache(cache);

        }
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        }

        return okHttpClientBuilder
                .build();
    }

    public synchronized static <T> T createService(Class<T> serviceClass) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(createOkHttpClient(false))
                    .baseUrl(FieldSightUserSession.getServerUrl(Collect.getInstance()))
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(serviceClass);
    }

    public synchronized static <T> T createCacheService(Class<T> serviceClass) {
        if (cacheablesRetrofit == null) {
            cacheablesRetrofit = new Retrofit.Builder()
                    .client(createOkHttpClient(true))
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .baseUrl(FieldSightUserSession.getServerUrl(Collect.getInstance()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return cacheablesRetrofit.create(serviceClass);
    }


    public synchronized static Retrofit getRxClient() {


        if (okHttp == null) {
            okHttp = createOkHttpClient(false);
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
        if (okHttp == null) {
            return 0;
        }

        return okHttp.dispatcher().runningCallsCount();
    }

    public static int getQueuedAPICount() {
        if (okHttp == null) {
            return 0;
        }

        return okHttp.dispatcher().queuedCallsCount();
    }

}
