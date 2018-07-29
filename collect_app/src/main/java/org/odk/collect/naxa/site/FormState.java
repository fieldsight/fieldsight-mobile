package org.odk.collect.naxa.site;

class FormState {
    private boolean isProgressIndicatorShown;
    private boolean isSiteTypeShown;
    private boolean isSiteClusterShown;

    public boolean isProgressIndicatorShown() {
        return isProgressIndicatorShown;
    }

    public void setProgressIndicatorShown(boolean progressIndicatorShown) {
        isProgressIndicatorShown = progressIndicatorShown;
    }


    public boolean isSiteTypeShown() {
        return isSiteTypeShown;
    }

    public void setSiteTypeShown(boolean isSiteTypeShown) {
        isSiteTypeShown = isSiteTypeShown;
    }


    public boolean isSiteClusterShown() {
        return isSiteClusterShown;
    }

    public void setSiteClusterShown(boolean isSiteClusterShown) {
        isSiteClusterShown = isSiteClusterShown;
    }


}
