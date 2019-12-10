package org.fieldsight.naxa.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.bcss.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import timber.log.Timber;

public abstract class BaseLoginActivity extends AppCompatActivity {
    protected static final String TAG = "BaseLoginActivity";


    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private String username = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGmailLogin();
    }

    protected void setupGmailLogin() {
        String serverClientId = "1035621646272-qqp0bibmbrhaehd4dhbg98heuurfb1jv.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    protected void gmailSignIn() {
//   The account selection is cached, so you have to call signOut first to show account chooser every time with GoogleSignIn
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                handleSignInResult(task);
            } catch (Exception e) {

                Timber.e(e);
            }
        }

    }

    protected void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String authCode = account.getServerAuthCode();
            Timber.d("handleSignInResult: suthCode " + authCode);
            updateUI(account);
        } catch (ApiException e) {
            Timber.e(e);
            gmailLoginFailed(e.getMessage());
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }


    public void updateUI(GoogleSignInAccount account)  {
        if (account == null) {
            return;
        }
        username = account.getEmail();
        new GetAccessTokenTask().execute(account.getServerAuthCode());
    }

    public abstract void gmailLoginSuccess(String googleAccessToken, String username);

    public abstract void gmailLoginFailed(String errorMsg);


    private class GetAccessTokenTask extends AsyncTask<String, Void, GoogleTokenResponse> {


        @Override
        protected GoogleTokenResponse doInBackground(String... urls) {

            try {
// Exchange auth code for access TOKEN
                GoogleClientSecrets clientSecrets =
                        GoogleClientSecrets.load(
                                JacksonFactory.getDefaultInstance(), new BufferedReader(
                                        new InputStreamReader(getResources().openRawResource(R.raw.client_secret))));

                GoogleTokenResponse tokenResponse =
                        new GoogleAuthorizationCodeTokenRequest(
                                new NetHttpTransport(),
                                JacksonFactory.getDefaultInstance(),
                                "https://www.googleapis.com/oauth2/v4/token",
                                clientSecrets.getDetails().getClientId(),
                                clientSecrets.getDetails().getClientSecret(),
                                urls[0],
                                "")  // Specify the same redirect URI that you use with your web
                                // app. If you don't have a web version of your app, you can
                                // specify an empty string.
                                .execute();
                return tokenResponse;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(GoogleTokenResponse tokenResponse) {
   
            if (tokenResponse == null) {
                gmailLoginFailed("Unable to get Gmail auth TOKEN");
            }else {
                gmailLoginSuccess(tokenResponse.getAccessToken(), username);
                Timber.d("onPostExecute: accessToken " + tokenResponse.getAccessToken());

            }


        }
    }
    
}


