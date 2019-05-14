package org.bcss.collect.naxa.v3.network;

public class Syncable {
        String title;
        boolean sync;

        public String getTitle(){
            return this.title;
        }

        public void setSync(boolean sync) {
            this.sync = sync;
        }
        public boolean getSync() {
            return this.sync;
        }

        public void toggleSync() {
            this.sync = !this.sync;
        }

        /**
         * @param title - title that is show in the list
         * @param sync  - selector to include in the downlod or not. if no need to download {@code sync = false }
         */
        public Syncable(String title, boolean sync) {
            this.title = title;
            this.sync = sync;
        }
    }