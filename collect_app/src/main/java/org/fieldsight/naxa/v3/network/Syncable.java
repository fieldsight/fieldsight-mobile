package org.fieldsight.naxa.v3.network;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Yubaraj Poudel
 * @Since: 14/05/2019
 * this Model will handle the sync process of all the content
 */

public class Syncable implements Serializable {

    private final String title;
    private final Set<String> lastFailedUrl = new HashSet<>();
    private int total;
    private int progress;
    public int status;

    /**
     * @param title  - title that is show in the list
     * @param status - status of the download
     */

    public Syncable(String title, int status) {
        this.title = title;
        this.status = status;
    }

    Syncable(String title, int status, int total, int progress) {
        this.title = title;
        this.status = status;
        this.total = total;
        this.progress = progress;
    }

    public int getTotal() {
        return total;
    }

    public int getProgress() {
        return progress;
    }

    public String getTitle() {
        return this.title;
    }

    public Set<String> getFailedUrl() {
        return this.lastFailedUrl;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void addFailedUrl(String[] urlList) {
        this.lastFailedUrl.addAll(Arrays.asList(urlList));
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isProgressBarEnabled() {
        return getTotal() > 0;
    }
}