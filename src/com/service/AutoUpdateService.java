package com.service;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.receiver.AutoUpdateReceriver;
import com.util.CharTools;
import com.util.HttpCallbackListener;
import com.util.HttpUtil;
import com.util.WeatherPullParase;

public class AutoUpdateService extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Tag", "自动更新了");
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d("Tag", "自动更新了");
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 1;   //这是毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Log.d("Tag triggerAtTime", triggerAtTime+"");
		Intent i = new Intent(this,AutoUpdateReceriver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 更新天气信息
	 */
	private void updateWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String cityName = prefs.getString("city_name", "");
		CharTools charTools = new CharTools();
		String appkey = "0c56b66acf6a4f39ac7ce8800c96b1de";
		String UriCityname = charTools.Utf8URLencode(cityName);
		String address = "http://api.avatardata.cn/Weather/Query?dtype=xml&key="
				+ appkey + "&cityname=" + UriCityname;
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Log.d("线程提示", "来了");
				try {
					WeatherPullParase.handleWeatherResponse(
							AutoUpdateService.this, response);
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
