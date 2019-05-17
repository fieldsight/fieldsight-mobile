package org.bcss.collect.naxa.login;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.network.APIEndpoint;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
                            String fcmToken = SharedPreferenceUtils.getFromPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, "");
                            if(!TextUtils.isEmpty(fcmToken)) {
                                    Timber.i("token generated: " + fcmToken);
                                    loginModel.login(username, password, fcmToken, LoginPresenterImpl.this);
                            } else {
                                loginView.showError("Failed to get token");
                            }
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

    @Override
    public void googleOauthCredentials(String googleAccessToken, String username) {
        loginView.showProgress(true);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                loginModel.loginViaGoogle(googleAccessToken, username, token, LoginPresenterImpl.this);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginView.showError(Collect.getInstance().getString(R.string.dialog_unexpected_error_title));
            }
        });


    }


    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onError(String message) {
        loginView.showProgress(false);
        loginView.showError(message);
    }


    @Override
    public void onSuccess() {
        loginView.showProgress(false);
        loginView.successAction();
    }
}
