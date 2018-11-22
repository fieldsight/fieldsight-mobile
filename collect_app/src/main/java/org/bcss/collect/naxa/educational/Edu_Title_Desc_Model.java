package org.bcss.collect.naxa.educational;

/**
 * Created by susan on 7/5/2017.
 */

public class Edu_Title_Desc_Model {

    private String title;
    private String desc;

    public Edu_Title_Desc_Model() {
    }

    public Edu_Title_Desc_Model(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
