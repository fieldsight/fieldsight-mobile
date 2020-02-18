package org.fieldsight.naxa.profile;

import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.contact.ContactRemoteSource;
import org.fieldsight.naxa.login.model.User;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ApiInterface;

import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.application.Collect;

import org.fieldsight.naxa.v3.project.Users;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.concurrent.TimeUnit;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;

import static org.fieldsight.naxa.network.ServiceGenerator.getRxClient;

public class UserProfileRepository {


    private static UserProfileRepository userProfileRepository;


    public synchronized static UserProfileRepository getInstance() {
        if (userProfileRepository == null) {
            userProfileRepository = new UserProfileRepository();
        }
        return userProfileRepository;
    }


    public void upload(Users contactDetail) {

        getRxClient()
                .create(ApiInterface.class)
                .updateUserProfileNoImage(FieldSightUserSession.getServerUrl(Collect.getInstance().getApplicationContext()) + "/users/api/profile/" + contactDetail.id + "/",
                        checkAndReturnStringBody(contactDetail.fullName),
                        checkAndReturnStringBody(contactDetail.firstName),
                        checkAndReturnStringBody(contactDetail.lastName),
                        checkAndReturnStringBody(contactDetail.email),
                        checkAndReturnStringBody(contactDetail.address),
                        checkAndReturnStringBody(contactDetail.gender),
                        checkAndReturnStringBody(contactDetail.phone),
                        checkAndReturnStringBody(contactDetail.skype),
                        checkAndReturnStringBody(contactDetail.primaryNumber),
                        checkAndReturnStringBody(contactDetail.secondaryNumber),
                        checkAndReturnStringBody(contactDetail.officeNumber),
                        checkAndReturnStringBody(contactDetail.viber),
                        checkAndReturnStringBody(contactDetail.whatsApp),
                        checkAndReturnStringBody(contactDetail.weChat),
                        checkAndReturnStringBody(contactDetail.line),
                        checkAndReturnStringBody(contactDetail.tango),
                        checkAndReturnStringBody(contactDetail.hike),
                        checkAndReturnStringBody(contactDetail.qq),
                        checkAndReturnStringBody(contactDetail.googleTalk),
                        checkAndReturnStringBody(contactDetail.twitter))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<User>() {
            @Override
            public void onNext(User user) {
                ToastUtils.showLongToast("Profile updated");
                try{
                    FieldSightUserSession.getUserV2(true);
                }catch (IllegalArgumentException e){
                    //ignore
                }
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
                ToastUtils.showLongToast("Profile update failed");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private RequestBody checkAndReturnStringBody(String value) {
        String tempValue = value;
        if (tempValue == null) {
            tempValue = "";
        }
        return RequestBody.create(MediaType.parse("text/plain"), tempValue);
    }

}
