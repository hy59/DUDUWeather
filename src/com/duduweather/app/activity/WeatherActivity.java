package com.duduweather.app.activity;

import java.util.HashMap;
import java.util.Map;

import com.duduweather.app.R;
import com.duduweather.app.service.AutoUpdateService;
import com.duduweather.app.util.HttpUtil;
import com.duduweather.app.util.Utility;
import com.duduweather.app.util.HttpCallbackListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	
	private LinearLayout weatherInfoLayout;
		/*
		 * 显示城市名
		 */
	private TextView cityNameText;
	/*
	 * 显示发布时间
	 */
	private TextView publishText;
	/*
	 * 显示天气描述信息
	 */
	private TextView weatherDespText;
	/*
	 * 显示气温一
	 */
	private TextView temp1Text;
	/*
	 * 显示气温二
	 */
	private TextView temp2Text;
	/*
	 * 显示当前日期
	 */
	private TextView currentDateText;
	
	/*
	 *切换城市按钮 
	 */
	private Button switchCity;
	/*
	 * 更新天气按钮
	 * 
	 */
	private Button refreshWeather;
	
	/*
	 * 天气状况
	 * 
	 */
	private enum WeatherKind{
		cloudy,fog,hail,heavyrain,heavysnow,rain,sandstorm,sleet,snow,sunny,thunder;
	}
	
	private static Map<String,WeatherKind> weatherKind = new HashMap<String,WeatherKind>(); 
			static{
				weatherKind.put("多云", WeatherKind.cloudy);
				weatherKind.put("雾", WeatherKind.fog);
				weatherKind.put("冰雹", WeatherKind.hail);
				weatherKind.put("暴雨", WeatherKind.heavyrain);
				weatherKind.put("大雪", WeatherKind.heavysnow);
				weatherKind.put("小雨", WeatherKind.rain);
				weatherKind.put("沙尘暴", WeatherKind.sandstorm);
				weatherKind.put("雨夹雪", WeatherKind.sleet);
				weatherKind.put("小雪", WeatherKind.snow);
				weatherKind.put("晴", WeatherKind.sunny);
				weatherKind.put("雷阵雨", WeatherKind.thunder);
			}
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode)){
			//有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			//没有县级代号就直接显示天气
			showWeather();
		}
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v){
		switch (v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weatherCode", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	
	/*
	 * 查询县级代号所对应的天气代号
	 */
	private void queryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
	}
	
	/*
	 * 查询天气代号所对应的天气
	 */
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
	}
	
	/*
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 */
	private void queryFromServer(final String address, final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			@Override
			public void onFinish(final String response){
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if(array !=null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						publishText.setText("同步失败...");
					}
				});
			}
		});
	}
	
	
	/*
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		
		String weatherDesp = prefs.getString("weather_desp", "");  
        weatherDespText.setText(weatherDesp);  
        WeatherKind myWeather = weatherKind.get(weatherDesp);  
        if (myWeather != null) {  
            changeBackground(myWeather);  
        }  
		
		publishText.setText(prefs.getString("publish_time", "")+"发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		/*
		 * 激活后台自动更新
		 */
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}

	/*
	 * 更换背景
	 */
	
	private void changeBackground(WeatherKind weather) {
		// TODO Auto-generated method stub
		View view = findViewById(R.id.weather_background);
		switch(weather){
		case cloudy:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.cloudy));
			break;
		case fog:
			view.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.fog));
			break;
		case hail:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.hail));
			break;
		case heavyrain:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.heavyrain));
			break;
		case heavysnow:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.heavysnow));
			break;
		case rain:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.rain));
			break;
		case sandstorm:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.sandstorm));
			break;
		case sleet:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.sleet));
			break;
		case snow:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.snow));
			break;
		case sunny:
			view.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.sunny));
			break;
		case thunder:
			view.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.thunder));
			break;
		
		default:
			break;
		}
	}
}
