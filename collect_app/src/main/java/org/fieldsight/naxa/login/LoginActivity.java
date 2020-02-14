package org.fieldsight.naxa.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.android.gms.common.SignInButton;
import com.google.android.material.textfield.TextInputEditText;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.common.SettingsActivity;
import org.fieldsight.naxa.migrate.MigrateFieldSightActivity;
import org.fieldsight.naxa.migrate.MigrationHelper;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.v3.project.ProjectListActivityV3;

import timber.log.Timber;

import static org.odk.collect.android.application.Collect.allowClick;

//import org.bcss.naxa.common.Login;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseLoginActivity implements LoginView {

    // UI references.
    private EditText edt_email;
    private TextInputEditText edt_password;
    CardView email_sign_in_button;
    private LoginPresenter loginPresenter;
    private SignInButton btnGmailLogin;
    private boolean isFromGooleSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        edt_email = findViewById(R.id.email);
        edt_password = findViewById(R.id.password);
        edt_password.setHint(getResources().getString(R.string.password));
        email_sign_in_button = findViewById(R.id.email_sign_in_button);

        email_sign_in_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allowClick(getClass().getName())) {
                    hideKeyboardInActivity(LoginActivity.this);
                    attemptLogin();
                }
            }
        });

        btnGmailLogin = findViewById(R.id.btn_gmail_login);
        btnGmailLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromGooleSignin = true;
                showProgress("Logging with google, Please wait");
                gmailSignIn();
                btnGmailLogin.setEnabled(false);
            }
        });

        ImageView iv_setting = findViewById(R.id.iv_setting);
        if (!BuildConfig.BUILD_TYPE.equals("release")) {
            iv_setting.setVisibility(View.VISIBLE);
            iv_setting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allowClick(getClass().getName())) {
                        hideKeyboardInActivity(LoginActivity.this);
                        startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
                    }
                }
            });
        }
//        mLoginFormView = findViewById(R.id.logo);
//        mProgressView = findViewById(R.id.login_progress);

        findViewById(R.id.iv_back).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.tv_forgot_pwd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allowClick(getClass().getName())) {
                    String url = FieldSightUserSession.getServerUrl(LoginActivity.this) + APIEndpoint.PASSWORD_RESET;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }
            }
        });

        loginPresenter = new LoginPresenterImpl(this);


        edt_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    edt_password.setHint("");
                else
                    edt_password.setHint(getResources().getString(R.string.password));
            }
        });
    }

    @Override
    public void gmailLoginSuccess(String googleAccessToken, String username) {
        loginPresenter.googleOauthCredentials(googleAccessToken, username);
        Timber.d("gmailLoginSuccess: Access tokenId %s", googleAccessToken);

    }

    @Override
    public void gmailLoginFailed(String errorMsg) {
        showError(errorMsg);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        edt_email.setError(null);
        edt_password.setError(null);
        // Store values at the time of the login attempt.
        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();
        loginPresenter.validateCredentials(email, password);

    }

    @Override
    public void showProgress(boolean showProgress) {
        if (showProgress)
            showProgress("Signing in please wait");
        else hideProgress();
    }


    @Override
    public void showPasswordError(int resourceId) {
        edt_password.setError(getString(resourceId));
        edt_password.requestFocus();

    }

    @Override
    public void showUsernameError(int resourceId) {
        edt_email.setError(getString(resourceId));
        edt_password.requestFocus();
    }

    @Override
    public void successAction() {
        boolean hasOldAccount = new MigrationHelper(edt_email.getText().toString()).hasOldAccount();
        if (hasOldAccount) {
            MigrateFieldSightActivity.start(this, edt_email.getText().toString());
        } else {
            startActivity(new Intent(this, ProjectListActivityV3.class));
        }
        Toast.makeText(this, "Logged In!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showError(String msg) {
        showErrorDialog(msg);
    }

    private void showErrorDialog(String msg) {
        if (isFinishing()) {
            return;
        }
        Dialog dialog = DialogFactory.createActionDialog(this, "Login Failed", msg)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        retryLogin();
                    }
                })
                .setNegativeButton(R.string.dialog_action_dismiss, null)
                .create();
        dialog.show();
    }

    /**
     * Only works from an activity, using getActivity() does not work
     *
     * @param activity
     */
    public void hideKeyboardInActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window TOKEN from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window TOKEN from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    private void retryLogin() {
        if (isFromGooleSignin) {
            gmailSignIn();
        } else {
            attemptLogin();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgress();
    }
}

