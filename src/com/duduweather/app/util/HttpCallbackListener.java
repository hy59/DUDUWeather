package com.duduweather.app.util;

public interface HttpCallbackListener {
	/*
	 * ���HttpCallbackListener�ӿ�
	 */
	void onFinish(String response);
	
	void onError(Exception e);
		
		
		
	
}
