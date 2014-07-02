package com.triestpa.ding;

import android.app.Application;

import com.parse.Parse;
import com.parse.PushService;

public class DingApp extends Application {

	public void onCreate() {
		super.onCreate();
		
		Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_id));
		PushService.setDefaultPushCallback(this, MainActivity.class);
		
	}

}
