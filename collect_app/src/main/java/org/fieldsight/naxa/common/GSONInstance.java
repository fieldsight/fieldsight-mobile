package org.fieldsight.naxa.common;

import com.google.gson.Gson;

public class GSONInstance {

    private GSONInstance(){

    }

    private final static Gson gson = new Gson();


    public static Gson getInstance() {
        return gson;
    }
}
