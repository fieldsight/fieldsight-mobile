package org.bcss.collect.naxa.educational;

/**
 * Created by susan on 7/5/2017.
 */

public class Edu_Video_Model {

    private String videoFile;
    private String thumbnail_url;
    private String title;
    private String desc;

    public Edu_Video_Model() {
    }

    public Edu_Video_Model(String videoFile, String thumbnail_url, String title, String desc) {
        this.videoFile = videoFile;
        this.thumbnail_url = thumbnail_url;
        this.title = title;
        this.desc = desc;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public String getThumbnail_url() {

        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
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
