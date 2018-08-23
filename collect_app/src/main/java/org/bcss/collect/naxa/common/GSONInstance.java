package org.bcss.collect.naxa.common;

import com.google.gson.Gson;

public class GSONInstance {

    private static Gson gson;


    public static Gson getInstance() {
        if (gson == null) {
            gson = new Gson();
        }

        return gson;
    }
}
