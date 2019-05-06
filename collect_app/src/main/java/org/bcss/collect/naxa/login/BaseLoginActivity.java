package org.bcss.collect.naxa.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import org.odk.collect.android.activities.CollectAbstractActivity;
import org.opendatakit.httpclientandroidlib.HttpResponse;
import org.opendatakit.httpclientandroidlib.NameValuePair;
import org.opendatakit.httpclientandroidlib.client.ClientProtocolException;
import org.opendatakit.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import org.opendatakit.httpclientandroidlib.client.methods.HttpPost;
import org.opendatakit.httpclientandroidlib.message.BasicNameValuePair;
import org.opendatakit.httpclientandroidlib.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseLoginActivity extends CollectAbstractActivity {
    protected static final String TAG = "BaseLoginActivity";

    public static final String SCOPES = "https://www.googleapis.com/auth/plus.login "
            + "https://www.googleapis.com/auth/drive.file";


    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGmailLogin();
    }

    // gmail Login Start
    protected void setupGmailLogin() {

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        String serverClientId = "408539660464-n0dqb40ul44vmavdmopnua3rig6alnqs.apps.googleusercontent.com";
        String serverClientId = "408539660464-m18d6hs1ok8bcqdifb6da0baaum98i2o.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    protected void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    protected void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String authCode = account.getServerAuthCode();

            Log.d(TAG, "handleSignInResult: suthCode "+authCode);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }



    public void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            return;
        }

        gmailLoginSuccess(account);

    }

//    protected void sendAuthCodeToAppsBackend (String authCode){
//        HttpPost httpPost = new HttpPost("https://yourbackend.example.com/authcode");
//
//        try {
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//            nameValuePairs.add(new BasicNameValuePair("authCode", authCode));
//            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//            HttpResponse response = httpClient.execute(httpPost);
//            int statusCode = response.getStatusLine().getStatusCode();
//            final String responseBody = EntityUtils.toString(response.getEntity());
//        } catch (ClientProtocolException e) {
//            Log.e(TAG, "Error sending auth code to backend.", e);
//        } catch (IOException e) {
//            Log.e(TAG, "Error sending auth code to backend.", e);
//        }
//
//    }

    public abstract void gmailLoginSuccess (GoogleSignInAccount googleSignInAccount);

}


