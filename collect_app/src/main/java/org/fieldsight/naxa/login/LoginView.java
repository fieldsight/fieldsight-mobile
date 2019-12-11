package org.fieldsight.naxa.login;

public interface LoginView {
    void showProgress(boolean showProgress);

    void showPasswordError(int resourceId);

    void showUsernameError(int resourceId);

    void successAction();

    void showError(String errorMessage);
}
