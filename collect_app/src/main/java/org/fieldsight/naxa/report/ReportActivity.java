package org.fieldsight.naxa.report;

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
import org.bcss.collect.android.R;;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.logic.PropertyManager;
import org.fieldsight.naxa.common.SharedPreferenceUtils;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
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
import timber.log.Timber;

public class ReportActivity extends CollectAbstractActivity {
    @BindView(R.id.tv_device_id)
    TextView tvDeviceId;

    @BindView(R.id.tv_fcm_token)
    TextView tvFcmToken;

    @BindView(R.id.tv_app_version)
    TextView tvAppVersion;

    @BindView(R.id.tv_os_version)
    TextView tvOsVersion;

    @BindView(R.id.tv_lat)
    TextView tvLat;

    @BindView(R.id.tv_lng)
    TextView tvLng;

    @BindView(R.id.spnr_type)
    Spinner spnrType;

    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;

    @BindView(R.id.edt_message)
    EditText edtMessage;

    @BindView(R.id.chkbx_agree)
    CheckBox chkbxAgree;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FusedLocationProviderClient fusedLocationClient;
    DisposableObserver<ResponseBody> observer;
    boolean isSubmitting;

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
        tvDeviceId.setText(new PropertyManager(this).getSingularProperty(PropertyManager.PROPMGR_DEVICE_ID));
        tvFcmToken.setText(SharedPreferenceUtils.getFromPrefs(this, SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, ""));
        tvAppVersion.setText(BuildConfig.VERSION_NAME);
        tvOsVersion.setText(Build.VERSION.RELEASE);
        tvDeviceName.setText(Build.MANUFACTURER);
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
                                    tvLat.setText(String.valueOf(location.getLatitude()));
                                    tvLng.setText(String.valueOf(location.getLongitude()));
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
        if (v instanceof TextView) {
            data = ((TextView) v).getText();
        } else if (v instanceof EditText) {
            data = ((EditText) v).getText();
        } else if (v instanceof Spinner) {
            Spinner spnr = (Spinner) v;
            data = spnr.getSelectedItemPosition() == 0 ? fallback : spnr.getSelectedItem().toString();
        }
        return TextUtils.isEmpty(data) ? fallback : data.toString();
    }

    @OnClick(R.id.btn_report)
    void submitReport() {
        if (isSubmitting) {
            Toast.makeText(getApplicationContext(), "Report form is submitting please wait", Toast.LENGTH_SHORT).show();
            return;
        }
        if (chkbxAgree.isChecked()) {
            isSubmitting = true;
            String deviceId = checkEmptyWithFallback(tvDeviceId, "0");
            String deviceName = checkEmptyWithFallback(tvDeviceName, "");
            String fcmToken = checkEmptyWithFallback(tvFcmToken, "");
            String appOsVersion = checkEmptyWithFallback(tvOsVersion, "");
            String lat = checkEmptyWithFallback(tvLat, "0");
            String lng = checkEmptyWithFallback(tvLng, "0");
            String messageType = checkEmptyWithFallback(spnrType, "");
            String appVersion = checkEmptyWithFallback(tvAppVersion, "");
            String message = checkEmptyWithFallback(edtMessage, "I have an issues using the app");
            observer = ServiceGenerator.getRxClient().create(ApiInterface.class)
                    .submitReport(deviceId, fcmToken, appVersion, appOsVersion, messageType, message, deviceName, lat, lng)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ResponseBody>() {

                        @Override
                        public void onNext(ResponseBody response) {
                            try {
                                if (response != null) {
                                    String reply = response.string();
                                    JSONObject replyJSON = new JSONObject(reply);
                                    Toast.makeText(getApplicationContext(), replyJSON.optString("message"), Toast.LENGTH_SHORT).show();
                                    isSubmitting = false;
                                    hideProgressDialog();
                                }
                            } catch (IOException | JSONException e) {
                                Timber.e(e);
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
        } else {
            Toast.makeText(this, "Please select I agree", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (observer != null && !observer.isDisposed()) {
            observer.dispose();
        }
    }

    ProgressDialog pd;

    void showPrgressDialog() {
        if (pd == null) {
            pd = new ProgressDialog(this);
        }
        pd.setMessage("Submitting your report please wait");
        pd.show();
    }

    void hideProgressDialog() {
        if (pd != null) {
            pd.hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void hideKeyboardInActivity() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window TOKEN from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window TOKEN from it
        if (view == null) {
            view = new View(this);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

}
