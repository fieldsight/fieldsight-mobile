package org.odk.collect.naxa.login;

public interface LoginModel {

    interface OnLoginFinishedListener {
        void onCanceled();

        void onPasswordError();

        void onSuccess();
    }

    void login(String username, String password, OnLoginFinishedListener listener);
}
