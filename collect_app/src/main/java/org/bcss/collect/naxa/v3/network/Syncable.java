package org.bcss.collect.naxa.v3.network;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Yubaraj Poudel
 * @Since: 14/05/2019
 * this Model will handle the sync process of all the content
 */

public class Syncable implements Serializable {
        String title;
//        this flag is used to enable it to sync or not
        boolean sync = true;
        Set<String> lastFailedUrl = new HashSet<>();

        public String getTitle(){
            return this.title;
        }

        public void setSync(boolean sync) {
            this.sync = sync;
        }
        public boolean getSync() {
            return this.sync;
        }
        public int status;
        public void toggleSync() {
            this.sync = !this.sync;
        }

        /**
         * @param title - title that is show in the list
         * @param sync  - selector to include in the downlod or not. if no need to download {@code sync = false }
         * @param status - status of the download
         */

        public Syncable(String title, boolean sync, int status) {
            this.title = title;
            this.sync = sync;
            this.status = status;
        }

        public void addFailedUrl(String url) {
            this.lastFailedUrl.add(url);
        }

        public Set<String> getFailedUrl() {
            return this.lastFailedUrl;
        }

        public Syncable(String title) {
            this.title = title;
        }
        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return this.status;
        }

    }