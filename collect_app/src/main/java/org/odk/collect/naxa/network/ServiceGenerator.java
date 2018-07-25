package org.odk.collect.naxa.network;

import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.FieldSightUserSession;
import org.odk.collect.naxa.common.SharedPreferenceUtils;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {
    public final static String BASE_API_URL = "http://app.fieldsight.org";
    private static Retrofit retrofit = null;
    private static Retrofit cacheablesRetrofit = null;
    private static Gson gson = new GsonBuilder().create();
    private static Retrofit rxRetrofit;


    public static void clearInstance() {
        if (retrofit != null) {
            retrofit = null;
        }
    }

    private static Interceptor createAuthInterceptor(final String token) {

        return chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("Authorization",
                            "Token " + token)
                    .build();
            return chain.proceed(request);
        };
    }

    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        String token = FieldSightUserSession.getAuthToken();
        if (!TextUtils.isEmpty(token)) {
            okHttpClientBuilder.addInterceptor(createAuthInterceptor(token));
        }

        okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());

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

        return okHttpBuilder.build();
    }


    public static <T> T createService(Class<T> serviceClass) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(createOkHttpClient())
                    .baseUrl(BASE_API_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(serviceClass);
    }

    public static <T> T createCacheService(Class<T> serviceClass) {
        if (cacheablesRetrofit == null) {
            cacheablesRetrofit = new Retrofit.Builder()
                    .client(createCacheablesOkHttpClient())
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return cacheablesRetrofit.create(serviceClass);
    }

    public static Retrofit getRxClient() {


        if (rxRetrofit == null) {
            rxRetrofit = new Retrofit.Builder()
                    .client(createOkHttpClient())
                    .baseUrl(BASE_API_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }


        return rxRetrofit;
    }

}
