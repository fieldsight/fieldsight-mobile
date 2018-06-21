package org.odk.collect.naxa.login;

import android.os.AsyncTask;

public class LoginModelImpl implements LoginModel {

    private static final String[] DUMMY_CREDENTIALS = new String[] {
            "test@gmail.com:12345678", "test2@gmail.edu:asdfasdf"
    };
    private UserLoginTask mAuthTask = null;

    private OnLoginFinishedListener loginFinishedListener;

    @Override
    public void login(String username, String password, OnLoginFinishedListener listener) {
        this.loginFinishedListener = listener;
        new UserLoginTask(username,password).execute();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                loginFinishedListener.onSuccess();
            } else {
                loginFinishedListener.onPasswordError();
            }
        }

        @Override
        protected void onCancelled() {
            loginFinishedListener.onCanceled();
        }
    }
}
