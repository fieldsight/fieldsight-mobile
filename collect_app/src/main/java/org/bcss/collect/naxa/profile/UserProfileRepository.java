package org.bcss.collect.naxa.profile;

import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.login.model.User;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ApiInterface;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static org.bcss.collect.naxa.network.ServiceGenerator.getRxClient;

public class UserProfileRepository {

    public void save(User user) {
        FieldSightUserSession.setUser(user);
    }



    public Observable<User> upload(User user) {

        Observable<User> userObservable = null;

        RequestBody fIn = checkAndReturnStringBody(user.getFirstName());
        RequestBody lIn = checkAndReturnStringBody(user.getLastName());
        RequestBody addIn = checkAndReturnStringBody(user.getAddress());
        RequestBody genderIn = checkAndReturnStringBody(user.getGender());
        RequestBody phoneIn = checkAndReturnStringBody(user.getPhone());
        RequestBody skypeIn = checkAndReturnStringBody(user.getSkype());
        RequestBody primaryIn = checkAndReturnStringBody(user.getPrimaryNumber());
        RequestBody secondIn = checkAndReturnStringBody(user.getSecondaryNumber());
        RequestBody officeIn = checkAndReturnStringBody(user.getOfficeNumber());
        RequestBody viberIn = checkAndReturnStringBody(user.getViber());
        RequestBody whatsAppIn = checkAndReturnStringBody(user.getWhatsApp());
        RequestBody wechatIn = checkAndReturnStringBody(user.getWechat());
        RequestBody lineIn = checkAndReturnStringBody(user.getLine());
        RequestBody tangoIn = checkAndReturnStringBody(user.getTango());
        RequestBody hikeIn = checkAndReturnStringBody(user.getHike());
        RequestBody qqIn = checkAndReturnStringBody(user.getQq());
        RequestBody googleTalkIn = checkAndReturnStringBody(user.getGoogleTalk());
        RequestBody twitterIn = checkAndReturnStringBody(user.getTwitter());
        RequestBody organizationIn = checkAndReturnStringBody(user.getOrganization());

//        if (user.getProfilepic().isEmpty()) {
        userObservable = getRxClient()
                .create(ApiInterface.class)
                .updateUserProfileNoImage(APIEndpoint.BASE_URL + "/users/api/profile/" + "303" + "/", fIn, lIn, addIn, genderIn, phoneIn, skypeIn, primaryIn, secondIn,
                        officeIn, viberIn, whatsAppIn, wechatIn, lineIn, tangoIn, hikeIn, qqIn, googleTalkIn, twitterIn, organizationIn);
//        } else {
//            File image = new File(user.getProfilepic());
//            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), image);
//            MultipartBody.Part imageIn = MultipartBody.Part.createFormData(image.getName(), image.getName(), imageRequestBody);
//            userObservable = getRxClient()
//                    .create(ApiInterface.class)
//                    .updateUserProfile(APIEndpoint.BASE_URL + "/users/api/profile/" + user.getUser_name() + "/", fIn, lIn, addIn, genderIn, phoneIn, skypeIn, primaryIn, secondIn,
//                            officeIn, viberIn, whatsAppIn, wechatIn, lineIn, tangoIn, hikeIn, qqIn, googleTalkIn, twitterIn, organizationIn, imageIn);
//        }

        return userObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private RequestBody checkAndReturnStringBody(String value) {
        String tempValue = value;
        if (tempValue == null) tempValue = "";
        return RequestBody.create(MediaType.parse("text/plain"), tempValue);
    }

}
