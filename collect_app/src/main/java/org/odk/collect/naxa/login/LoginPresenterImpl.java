package org.odk.collect.naxa.login;

import android.text.TextUtils;

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.SnackbarUtils;
import org.odk.collect.naxa.login.model.MeResponse;

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
        loginModel.login(username, password, this);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public void onError() {
        loginView.showProgress(false);
        loginView.showError();

    }


    @Override
    public void onPasswordError() {
        loginView.showProgress(false);
        loginView.showPasswordError(R.string.error_incorrect_password);
    }

    @Override
    public void onSuccess() {
        loginView.showProgress(false);
        loginView.successAction();
    }
}
