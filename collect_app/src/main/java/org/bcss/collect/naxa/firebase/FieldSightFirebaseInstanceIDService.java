package org.bcss.collect.naxa.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;

public class FieldSightFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_FCM, fcmToken);

    }
}
