package org.bcss.collect.naxa.login;

import org.bcss.collect.naxa.login.model.MeResponse;

public interface LoginModel {

    interface OnLoginFinishedListener {
        void onError(String message);

        void fcmTokenError();

        void onSuccess();
    }




    void login(String username, String password, OnLoginFinishedListener listener);


}
