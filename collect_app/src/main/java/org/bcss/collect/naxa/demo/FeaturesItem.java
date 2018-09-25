package org.bcss.collect.naxa.demo;

import com.google.gson.annotations.SerializedName;


public class FeaturesItem{

	@SerializedName("geometry")
	private Geometry geometry;

	@SerializedName("id")
	private int id;

	@SerializedName("type")
	private String type;

	@SerializedName("properties")
	private Properties properties;

	public void setGeometry(Geometry geometry){
		this.geometry = geometry;
	}

	public Geometry getGeometry(){
		return geometry;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setProperties(Properties properties){
		this.properties = properties;
	}

	public Properties getProperties(){
		return properties;
	}

	@Override
 	public String toString(){
		return 
			"FeaturesItem{" + 
			"geometry = '" + geometry + '\'' + 
			",id = '" + id + '\'' + 
			",type = '" + type + '\'' + 
			",properties = '" + properties + '\'' + 
			"}";
		}
}