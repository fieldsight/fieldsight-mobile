package org.bcss.collect.naxa.demo;

import com.google.gson.annotations.SerializedName;


public class Properties {

    @SerializedName("name")
    private String name;

    @SerializedName("identifier")
    private String identifier;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return
                "Properties{" +
                        "name = '" + name + '\'' +
                        ",identifier = '" + identifier + '\'' +
                        "}";
    }
}