package com.duduweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DUDUWeatherOpenHelper extends SQLiteOpenHelper{
	
	//����ʡ��
	public static final String CREATE_PROVINCE = "create table Province("
			+ "id integer primary key autoincrement,"
			+ "province_name text,"
			+"province_code text)";
	
	//�������б�
	public static final String CREATE_CITY = "create table City("
			+ "id integer primary key autoincrement,"
			+ "city_name text,"
			+ "city_code text,"
			+ "province_id integer)";
			
	
	//�����ر�
	public static final String CREATE_COUNTY = "create table County("
			+ "id integer primary key autoincrement,"
			+ "county_name text,"
			+ "county_code text,"
			+ "city_id integer)";
	
	public DUDUWeatherOpenHelper(Context context, String name, CursorFactory factory, int version)
	{
		super(context, name, factory, version);
	}
	
	@Override 
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_PROVINCE); //����ʡ��
		db.execSQL(CREATE_CITY); //�������б�
		db.execSQL(CREATE_COUNTY); //�����ر�
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oidVersion, int newVersion)
	{
		
	}
}
