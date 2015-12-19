package com.duduweather.app.util;

public interface HttpCallbackListener {
	/*
	 * Ìí¼ÓHttpCallbackListener½Ó¿Ú
	 */
	void onFinish(String response);
	
	void onError(Exception e);
		
		
		
	
}
