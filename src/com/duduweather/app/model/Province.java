package com.duduweather.app.model;

public class Province {
	//省对应的实体类
	private int id;
	private String provinceName;
	private String provinceCode;
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	
	public String getProvinceName(){
		return provinceName;
	}
	
	public void setProvinceName(String provinceName){
		this.provinceName = provinceName;
	}
	
	
	public String getProvinceCode(){
		return provinceCode;
	}
	
	public void setProvinceCode(String provinceCode){
		this.provinceCode = provinceCode;
	}
	
}
