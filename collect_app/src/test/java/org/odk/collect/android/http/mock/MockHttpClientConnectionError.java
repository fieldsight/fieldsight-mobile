package org.odk.collect.android.http.mock;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.bcss.collect.android.http.HttpCredentialsInterface;
import org.bcss.collect.android.http.HttpGetResult;

import java.net.URI;

public class MockHttpClientConnectionError extends MockHttpClientConnection {

    @Override
    @NonNull
    public HttpGetResult get(@NonNull URI uri, @Nullable String contentType, @Nullable HttpCredentialsInterface credentials) {
        return null;
    }
}
