package org.bcss.collect.naxa.profile;

import org.bcss.collect.naxa.common.FieldSightUserSession;
import org.bcss.collect.naxa.login.model.User;

public class UserProfileRepository {

    public void save(User user) {
        FieldSightUserSession.setUser(user);
    }

    public User get() {
        return FieldSightUserSession.getUser();
    }

}
