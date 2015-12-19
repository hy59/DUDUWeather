package com.duduweather.app.model;

public class City {
	//创建城市实例
	private int id;
	private String cityName;
	private String cityCode;
	private int provinceId;
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	
	public String getCityName(){
		return cityName;
	}
	public void setCityName(String cityName){
		this.cityName = cityName;
	}
	
	
	public String getCityCode(){
		return cityCode;
	}
	public void setCityCode(String cityCode){
		this.cityCode = cityCode;
	}
	
	
	public int getProvinceId(){
		return provinceId;
	}
	public void setProvinceId(int provinceId){
		this.provinceId = provinceId;
	}
}
