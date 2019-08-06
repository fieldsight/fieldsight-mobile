package org.bcss.collect.naxa.report;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.listeners.PermissionListener;
import org.bcss.collect.android.logic.PropertyManager;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.PermissionUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ReportActivity extends CollectAbstractActivity {
    @BindView(R.id.tv_device_id)
    TextView tv_device_id;

    @BindView(R.id.tv_fcm_token)
    TextView tv_fcm_token;

    @BindView(R.id.tv_app_version)
    TextView tv_app_version;

    @BindView(R.id.tv_os_version)
    TextView tv_os_version;

    @BindView(R.id.tv_lat)
    TextView tv_lat;

    @BindView(R.id.tv_lng)
    TextView tv_lng;

    @BindView(R.id.spnr_type)
    Spinner spnr_type;

    @BindView(R.id.tv_device_name)
    TextView tv_device_name;

    @BindView(R.id.edt_message)
    EditText edt_message;

    @BindView(R.id.chkbx_agree)
    CheckBox chkbx_agree;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FusedLocationProviderClient fusedLocationClient;
    DisposableObserver<ResponseBody> observer = null;
    boolean isSubmitting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.report_bug));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hideKeyboardInActivity();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        tv_device_id.setText(new PropertyManager(this).getSingularProperty(PropertyManager.PROPMGR_DEVICE_ID));
        tv_fcm_token.setText(SharedPreferenceUtils.getFromPrefs(this, SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, ""));
        tv_app_version.setText(BuildConfig.VERSION_NAME);
        tv_os_version.setText(Build.VERSION.RELEASE);
        tv_device_name.setText(Build.MANUFACTURER);
       new PermissionUtils().requestLocationPermissions(this, new PermissionListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void granted() {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(ReportActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    tv_lat.setText(location.getLatitude() + "");
                                    tv_lng.setText(location.getLongitude() + "");
                                }
                            }
                        });
            }

            @Override
            public void denied() {

            }
        });



    }

    private String checkEmptyWithFallback(View v, String fallback) {
        CharSequence data = "";
        if(v instanceof TextView) {
            data = ((TextView)v).getText();
        }else if(v instanceof EditText){
            data = ((EditText)v).getText();
        }else if(v instanceof Spinner){
            Spinner spnr = (Spinner)v;
            data = spnr.getSelectedItemPosition() == 0 ? fallback : spnr.getSelectedItem().toString();
        }
        return TextUtils.isEmpty(data)? fallback : data.toString();
    }
    @OnClick(R.id.btn_report)
    void submitReport() {
        if(isSubmitting) {
            Toast.makeText(getApplicationContext(), "Report form is submitting please wait", Toast.LENGTH_SHORT).show();
            return;
        }
        if(chkbx_agree.isChecked()) {
            isSubmitting = true;
            String deviceId = checkEmptyWithFallback(tv_device_id, "0");
            String deviceName = checkEmptyWithFallback(tv_device_name, "");
            String fcmToken = checkEmptyWithFallback(tv_fcm_token, "");
            String app_os_version = checkEmptyWithFallback(tv_os_version, "");
            String lat = checkEmptyWithFallback(tv_lat, "0");
            String lng = checkEmptyWithFallback(tv_lng, "0");
            String message_type = checkEmptyWithFallback(spnr_type, "");
            String app_version = checkEmptyWithFallback(tv_app_version, "");
            String message = checkEmptyWithFallback(edt_message, "I have an issues using the app");
            observer = ServiceGenerator.getRxClient().create(ApiInterface.class)
                    .submitReport(deviceId, fcmToken, app_version, app_os_version, message_type, message, deviceName, lat, lng)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ResponseBody>() {

                        @Override
                        public void onNext(ResponseBody response) {
                            try {
                                if(response != null) {
                                    String reply = response.string();
                                    JSONObject replyJSON = new JSONObject(reply);
                                    Toast.makeText(getApplicationContext(), replyJSON.optString("message"), Toast.LENGTH_SHORT).show();
                                    isSubmitting = false;
                                    hideProgressDialog();
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                                isSubmitting = false;
                                hideProgressDialog();
                                Toast.makeText(getApplicationContext(), "Unknown error in sending report, Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            isSubmitting = false;
                            hideProgressDialog();
                        }

                        @Override
                        public void onComplete() {
                            isSubmitting = false;
                            hideProgressDialog();
                        }
                    });
            showPrgressDialog();
        }else {
            Toast.makeText(this, "Please select I agree", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(observer != null && !observer.isDisposed()) {
            observer.dispose();
        }
    }

    ProgressDialog pd = null;
    void showPrgressDialog() {
       if(pd == null) {
           pd = new ProgressDialog(this);
       }
       pd.setMessage("Submitting your report please wait");
       pd.show();
    }

    void hideProgressDialog() {
        if(pd != null) {
            pd.hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void hideKeyboardInActivity() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

}
