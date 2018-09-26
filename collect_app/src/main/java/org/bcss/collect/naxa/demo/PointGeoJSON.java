package org.bcss.collect.naxa.demo;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PointGeoJSON{

	@SerializedName("features")
	private List<FeaturesItem> features;

	@SerializedName("crs")
	private Crs crs;

	@SerializedName("type")
	private String type;

	public void setFeatures(List<FeaturesItem> features){
		this.features = features;
	}

	public List<FeaturesItem> getFeatures(){
		return features;
	}

	public void setCrs(Crs crs){
		this.crs = crs;
	}

	public Crs getCrs(){
		return crs;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"PointGeoJSON{" + 
			"features = '" + features + '\'' + 
			",crs = '" + crs + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}