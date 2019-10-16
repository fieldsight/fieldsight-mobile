package org.fieldsight.naxa.educational;

/**
 * Created by susan on 7/5/2017.
 */

public class EduVideoModel {

    private String videoFile;
    private String thumbnailUrl;
    private String title;
    private String desc;

    public EduVideoModel() {
    }

    public EduVideoModel(String videoFile, String thumbnailUrl, String title, String desc) {
        this.videoFile = videoFile;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.desc = desc;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public String getThumbnailUrl() {

        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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
