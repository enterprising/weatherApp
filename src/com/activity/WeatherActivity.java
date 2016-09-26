package com.activity;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.db.CoolWeatherDB;
import com.pojo.City;
import com.pojo.Province;
import com.util.CharTools;
import com.util.HttpCallbackListener;
import com.util.HttpUtil;
import com.util.WeatherPullParase;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDesText;
	private TextView temperatureText;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDesText = (TextView) findViewById(R.id.weather_desp);
		temperatureText = (TextView) findViewById(R.id.temperature);
		currentDateText = (TextView) findViewById(R.id.current_date);
		String cityName = getIntent().getStringExtra("cityName");
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		
		CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(this);
		List<City> beijing = coolWeatherDB.loadCities(1);
		
		Log.d("这里是一个关键的地方", cityName);
		if (!TextUtils.isEmpty(cityName)) {
			publishText.setText("正在同步...");
			weatherInfoLayout.setVisibility(View.INVISIBLE); // 设置不可见
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeather(cityName);
		} else {
			// 直接去本地查
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//更换城市
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
	}

	

	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		weatherDesText.setText(prefs.getString("weatherDes", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		temperatureText.setText(prefs.getString("temperature", ""));
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	private void queryWeather(String cityName) {
		CharTools charTools = new CharTools();
		String appkey = "0c56b66acf6a4f39ac7ce8800c96b1de";
		String UriCityname = charTools.Utf8URLencode(cityName);
		String address = "http://api.avatardata.cn/Weather/Query?dtype=xml&key="
				+ appkey + "&cityname=" + UriCityname;
		
		Log.d("地址", address+"");
		queryWeatherFromServer(address);
	}

	private void queryWeatherFromServer(final String address) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				try {
					Log.d("线程提示", "来了");
					WeatherPullParase.handleWeatherResponse(
							WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeather();
						}
					});
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(final Exception e) {
				Log.d("线程提示", "来了error这里");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

}
