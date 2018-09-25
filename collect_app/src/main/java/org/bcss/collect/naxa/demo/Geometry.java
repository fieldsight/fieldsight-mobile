package org.bcss.collect.naxa.demo;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Geometry{

	@SerializedName("coordinates")
	private List<Double> coordinates;

	@SerializedName("type")
	private String type;

	public void setCoordinates(List<Double> coordinates){
		this.coordinates = coordinates;
	}

	public List<Double> getCoordinates(){
		return coordinates;
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
			"Geometry{" + 
			"coordinates = '" + coordinates + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}