package org.bcss.collect.naxa.educational;

/**
 * Created by susan on 7/5/2017.
 */

public class Edu_Image_Model {

    private String thumbImageOn;
    private String thumbImageOff;
    private String title;
    private String desc;

    public Edu_Image_Model() {
    }

    public Edu_Image_Model(String thumbImageOn, String thumbImageOff, String title) {
        this.thumbImageOn = thumbImageOn;
        this.thumbImageOff = thumbImageOff;
        this.title = title;
    }

    public String getThumbImageOn() {
        return thumbImageOn;
    }

    public void setThumbImageOn(String thumbImageOn) {
        this.thumbImageOn = thumbImageOn;
    }

    public String getThumbImageOff() {
        return thumbImageOff;
    }

    public void setThumbImageOff(String thumbImageOff) {
        this.thumbImageOff = thumbImageOff;
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
