package org.bcss.collect.naxa.demo;

import com.google.gson.annotations.SerializedName;

public class Crs{

	@SerializedName("type")
	private String type;

	@SerializedName("properties")
	private Properties properties;

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
			"Crs{" + 
			"type = '" + type + '\'' + 
			",properties = '" + properties + '\'' + 
			"}";
		}
}