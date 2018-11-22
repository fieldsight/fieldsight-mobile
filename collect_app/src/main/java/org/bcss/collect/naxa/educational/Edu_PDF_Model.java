package org.bcss.collect.naxa.educational;

/**
 * Created by susan on 7/5/2017.
 */

public class Edu_PDF_Model {

    private String pdfUrlOn;
    private String pdfUrlOff;
    private String title;
    private String desc;

    public Edu_PDF_Model() {
    }

    public Edu_PDF_Model(String pdfUrlOn, String pdfUrlOff, String title) {
        this.pdfUrlOn = pdfUrlOn;
        this.pdfUrlOff = pdfUrlOff;
        this.title = title;
    }

    public String getPdfUrlOn() {
        return pdfUrlOn;
    }

    public void setPdfUrlOn(String pdfUrlOn) {
        this.pdfUrlOn = pdfUrlOn;
    }

    public String getPdfUrlOff() {
        return pdfUrlOff;
    }

    public void setPdfUrlOff(String pdfUrlOff) {
        this.pdfUrlOff = pdfUrlOff;
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
