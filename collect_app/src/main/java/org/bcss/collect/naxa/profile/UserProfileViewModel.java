package org.bcss.collect.naxa.profile;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.login.model.User;

import java.io.File;


public class UserProfileViewModel extends ViewModel {

    private UserProfileRepository userProfileRepository;
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> editProfile = new MutableLiveData<>();

    public UserProfileViewModel() {
        this.userProfileRepository = new UserProfileRepository();
    }

    public void save(User user) {
        userProfileRepository.save(user);
    }

    public User get() {
        return userProfileRepository.get();
    }

    public MutableLiveData<Boolean> getEditProfile() {
        return editProfile;
    }

    public void setEditProfile(Boolean value) {
        editProfile.setValue(value);
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setValue(user);
    }

    public File generateImageFile(String imageName) {
        String path = Collect.SITES_PATH +
                File.separator +
                imageName +
                ".jpg";

        int i = 2;
        File f = new File(path);
        while (f.exists()) {

            path = Collect.SITES_PATH +
                    File.separator +
                    imageName +
                    "_" +
                    i +
                    ".jpg";


            f = new File(path);
            i++;
        }
        return f;
    }
}
