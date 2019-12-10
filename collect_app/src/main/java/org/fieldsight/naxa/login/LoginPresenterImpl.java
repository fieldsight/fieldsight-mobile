package org.fieldsight.naxa.login;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.bcss.collect.android.R;;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.SharedPreferenceUtils;
import timber.log.Timber;

public class LoginPresenterImpl implements LoginPresenter, LoginModel.OnLoginFinishedListener {

    private final LoginView loginView;
    private final LoginModel loginModel;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginModel = new LoginModelImpl();
    }

    @Override
    public void validateCredentials(String username, String password) {
        // Check for a valid email address.
        boolean isValid = true;
        if (TextUtils.isEmpty(username)) {
            loginView.showUsernameError(R.string.error_invalid_email);
            isValid = false;
        }
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            loginView.showPasswordError(R.string.error_incorrect_password);
            isValid = false;
        }
        if (!isValid) return;
        loginView.showProgress(true);
        String fcmToken = SharedPreferenceUtils.getFromPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, "");
        if (!TextUtils.isEmpty(fcmToken)) {
            Timber.i("TOKEN generated: %s", fcmToken);
            loginModel.login(username, password, fcmToken, LoginPresenterImpl.this);
        } else {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                String fcmToken1 = instanceIdResult.getToken();
                Timber.i("RegeneratedToken: " + fcmToken1);
                SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, fcmToken1);
                loginModel.login(username, password, fcmToken1, LoginPresenterImpl.this);
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Timber.i("Error exception, %s ", e.getMessage());
                    loginView.showError("Failed to get TOKEN");
                }
            });
        }

    }

    @Override
    public void googleOauthCredentials(String googleAccessToken, String username) {
        loginView.showProgress(true);
        String fcmToken = SharedPreferenceUtils.getFromPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, "");
        if (!TextUtils.isEmpty(fcmToken)) {
            Timber.i("TOKEN generated: %s", fcmToken);
            loginModel.loginViaGoogle(googleAccessToken, username, fcmToken, this);
        } else {
            loginView.showError("Failed to get TOKEN");
        }
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
