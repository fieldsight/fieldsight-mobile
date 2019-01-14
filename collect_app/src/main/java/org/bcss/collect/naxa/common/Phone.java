package org.bcss.collect.naxa.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import org.odk.collect.android.utilities.ToastUtils;


/**
 * Created by nishon on 4/25/17.
 */

public class Phone {


    private Context context;

    public Phone(Context context) {
        this.context = context;
    }

    public void sendSMS() {

    }

    public void sendSMS(String msg) {

    }



    public void ringNumber(String ContactName, String phoneNumber) {


        if (!containsNumber(phoneNumber)) {
            if (android.text.TextUtils.isEmpty(phoneNumber)) {

                String msg = "%s does not have a phone number";
                showMessage(String.format(msg, ContactName));

            } else {
                String msg = "Phone number ( %s ) provided by %s is invalid";
                showMessage(String.format(msg, phoneNumber, ContactName));
            }

            return;
        }

        phoneNumber = "tel:" + phoneNumber;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse(phoneNumber));
        if (canDeviceHandleCall(callIntent) && hasDeviceCallPermission()) {
            context.startActivity(callIntent);
        }

    }


    private boolean containsNumber(String str) {

        return str != null && str.matches(".*\\d+.*");
    }


    private boolean hasDeviceCallPermission() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }


        showMessage("Allow, phone call permission.");


        return false;
    }


    private void showMessage(String message) {
        ToastUtils.showShortToast(message);
    }


    private boolean canDeviceHandleCall(Intent callIntent) {
        if (callIntent.resolveActivity(context.getPackageManager()) != null) {
            return true;
        }

        showMessage("Device does not support phone calls");
        return false;
    }
}
