package org.bcss.collect.naxa.login;

import android.text.TextUtils;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.firebase.iid.FirebaseInstanceId;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;

import java.util.logging.Handler;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenterImpl implements LoginPresenter, LoginModel.OnLoginFinishedListener {

    private LoginView loginView;
    private LoginModel loginModel;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginModel = new LoginModelImpl();
    }

    @Override
    public void validateCredentials(String username, String password) {
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            loginView.showPasswordError(R.string.error_incorrect_password);
            return;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            loginView.showUsernameError(R.string.error_invalid_email);
            return;
        }

        loginView.showProgress(true);

        ReactiveNetwork.checkInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isConnected) {
                        if (isConnected) {
                            FirebaseInstanceId.getInstance().getToken();
                            loginModel.login(username, password, LoginPresenterImpl.this);
                        } else {
                            loginView.showError("No Network Connectivity");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loginView.showError("No Network Connectivity");
                    }
                });

    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public void onError() {
        loginView.showProgress(false);
        loginView.showError("Invalid email/username or password");

    }

    @Override
    public void fcmTokenError() {
        loginView.showProgress(false);
        loginView.showError(Collect.getInstance().getString(R.string.dialog_error_register));
    }


    @Override
    public void onSuccess() {
        loginView.showProgress(false);
        loginView.successAction();
    }
}
