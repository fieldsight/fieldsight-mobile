package org.bcss.collect.naxa.notificationslist;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("finstance")
    @Expose
    private Finstance finstance;
    @SerializedName("images")
    @Expose
    private List<NotificationImage> images = null;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("old_status")
    @Expose
    private String oldStatus;
    @SerializedName("new_status")
    @Expose
    private String newStatus;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Finstance getFinstance() {
        return finstance;
    }

    public void setFinstance(Finstance finstance) {
        this.finstance = finstance;
    }

    public List<NotificationImage> getImages() {
        return images;
    }

    public void setImages(List<NotificationImage> images) {
        this.images = images;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
