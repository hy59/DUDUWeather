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
		 * ��ʾ������
		 */
	private TextView cityNameText;
	/*
	 * ��ʾ����ʱ��
	 */
	private TextView publishText;
	/*
	 * ��ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	/*
	 * ��ʾ����һ
	 */
	private TextView temp1Text;
	/*
	 * ��ʾ���¶�
	 */
	private TextView temp2Text;
	/*
	 * ��ʾ��ǰ����
	 */
	private TextView currentDateText;
	
	/*
	 *�л����а�ť 
	 */
	private Button switchCity;
	/*
	 * ����������ť
	 * 
	 */
	private Button refreshWeather;
	
	/*
	 * ����״��
	 * 
	 */
	private enum WeatherKind{
		cloudy,fog,hail,heavyrain,heavysnow,rain,sandstorm,sleet,snow,sunny,thunder;
	}
	
	private static Map<String,WeatherKind> weatherKind = new HashMap<String,WeatherKind>(); 
			static{
				weatherKind.put("����", WeatherKind.cloudy);
				weatherKind.put("��", WeatherKind.fog);
				weatherKind.put("����", WeatherKind.hail);
				weatherKind.put("����", WeatherKind.heavyrain);
				weatherKind.put("��ѩ", WeatherKind.heavysnow);
				weatherKind.put("С��", WeatherKind.rain);
				weatherKind.put("ɳ����", WeatherKind.sandstorm);
				weatherKind.put("���ѩ", WeatherKind.sleet);
				weatherKind.put("Сѩ", WeatherKind.snow);
				weatherKind.put("��", WeatherKind.sunny);
				weatherKind.put("������", WeatherKind.thunder);
			}
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//��ʼ�����ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode)){
			//���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			//û���ؼ����ž�ֱ����ʾ����
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
			publishText.setText("ͬ����...");
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
	 * ��ѯ�ؼ���������Ӧ����������
	 */
	private void queryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
	}
	
	/*
	 * ��ѯ������������Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
	}
	
	/*
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 */
	private void queryFromServer(final String address, final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			@Override
			public void onFinish(final String response){
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//�ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if(array !=null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//������������ص�������Ϣ
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
						publishText.setText("ͬ��ʧ��...");
					}
				});
			}
		});
	}
	
	
	/*
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
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
		
		publishText.setText(prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		/*
		 * �����̨�Զ�����
		 */
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}

	/*
	 * ��������
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
