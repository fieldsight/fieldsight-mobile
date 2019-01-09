package org.bcss.collect.naxa.generalforms.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bcss.collect.naxa.generalforms.EmImageTypeConverter;

import java.util.List;

@Entity(tableName = "educational_materials")
public class Em {


    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("em_images")
    @Expose
    @TypeConverters(EmImageTypeConverter.class)
    private List<EmImage> emImages = null;

    @SerializedName("is_pdf")
    @Expose
    @Ignore
    private Boolean isPdf;

    @SerializedName("pdf")
    @Expose
    private String pdf;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("text")
    @Expose
    private String text;

    @PrimaryKey
    @NonNull
    @SerializedName("fsxf")
    private String fsFormId;

    @NonNull
    public String getFsFormId() {
        return fsFormId;
    }

    public void setFsFormId(@NonNull String fsFormId) {
        this.fsFormId = fsFormId;
    }

    public void setPdf(Boolean pdf) {
        isPdf = pdf;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<EmImage> getEmImages() {
        return emImages;
    }

    public void setEmImages(List<EmImage> emImages) {
        this.emImages = emImages;
    }


    public void setIsPdf(Boolean isPdf) {
        this.isPdf = isPdf;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
