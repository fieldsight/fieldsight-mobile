package org.bcss.collect.naxa.submissions;

import java.util.ArrayList;

/**
 * Created on 11/29/17
 * by nishon.tan@gmail.com
 */

public class ViewModel {

    private String name;
    private String desc;
    private String id;
    private String secondaryId;
    private String pictureUrl;

    public String getPictureUrl() {
        return pictureUrl;
    }

    public ViewModel(String name, String desc, String id, String secondaryId) {
        this.name = name;
        this.desc = desc;
        this.id = id;
        this.secondaryId = secondaryId;
    }

    public String getSecondaryId() {
        return secondaryId;
    }


    public String getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public static ArrayList<ViewModel> getDummyList(int totalItems) {
        ArrayList<ViewModel> viewModels = new ArrayList<>();

        for (int i = 0; i <= totalItems; i++) {
            viewModels.add(new ViewModel("name " + i, "Description " + i, String.valueOf(i), String.valueOf(i)));
        }

        return viewModels;
    }
}
