package com.util;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 解析服务器返回的天气xml
 * 
 * @author PengTan
 * 
 */
public class WeatherPullParase {

	public static void handleWeatherResponse(Context context, String response)
			throws XmlPullParserException, IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser xmlPullParser = factory.newPullParser();
		xmlPullParser.setInput(new StringReader(response));
		xmlPullParser.next();
		xmlPullParser.require(XmlPullParser.START_TAG, null, "WeatherResult");
		String cityName = "";
		String publishTime = "";
		String date = "";
		String weatherDes = "";
		String temperature = "";
		while (xmlPullParser.nextTag() != XmlPullParser.END_TAG) {
			String nodeName = xmlPullParser.getName();
			Log.d("解析xml嵌套结点", nodeName + "：");
			if ("result".equals(nodeName)) {
				Log.d("提示", "来了我想要的结点里");
				xmlPullParser.require(XmlPullParser.START_TAG, null, "result");
				xmlPullParser.nextTag();
				xmlPullParser
						.require(XmlPullParser.START_TAG, null, "realtime");
				xmlPullParser.nextTag();
				xmlPullParser.require(XmlPullParser.START_TAG, null, "wind");

				xmlPullParser.nextTag();
				String nodename3 = xmlPullParser.getName();
				String nodename3_value = xmlPullParser.nextText();
				Log.d(nodename3, nodename3_value);
				xmlPullParser.nextTag();
				String nodename4 = xmlPullParser.getName();
				String nodename4_value = xmlPullParser.nextText();
				Log.d(nodename4, nodename4_value);
				xmlPullParser.nextTag();
				xmlPullParser.require(XmlPullParser.END_TAG, null, "wind");

				xmlPullParser.nextTag();

				xmlPullParser.require(XmlPullParser.START_TAG, null, "time");
				// xmlPullParser.nextTag();
				String nodename5 = xmlPullParser.getName();
				publishTime = xmlPullParser.nextText();
				Log.d(nodename5, publishTime);
				xmlPullParser.require(XmlPullParser.END_TAG, null, "time");

				while (xmlPullParser.nextTag() != XmlPullParser.END_TAG) {
					xmlPullParser.require(XmlPullParser.START_TAG, null,
							"weather");
					while (xmlPullParser.nextTag() != XmlPullParser.END_TAG) {
						String nodename6 = xmlPullParser.getName();
						String nodename6_value = xmlPullParser.nextText();
						if ("info".equals(nodename6)) {
							weatherDes = nodename6_value;
							Log.d(nodename6, nodename6_value);
						} else if ("temperature".equals(nodename6)) {
							temperature = nodename6_value;
							Log.d(nodename6, nodename6_value);
						}
					}
					Log.d("提示", "来了");
					xmlPullParser.require(XmlPullParser.END_TAG, null,
							"weather");

					xmlPullParser.nextTag();
					xmlPullParser.require(XmlPullParser.START_TAG, null,
							"dataUptime");
					xmlPullParser.getName();
					xmlPullParser.nextText();
					xmlPullParser.require(XmlPullParser.END_TAG, null,
							"dataUptime");

					xmlPullParser.nextTag();
					xmlPullParser
							.require(XmlPullParser.START_TAG, null, "date");
					String datenode = xmlPullParser.getName();
					date = xmlPullParser.nextText();
					Log.d(datenode, date);
					xmlPullParser.require(XmlPullParser.END_TAG, null, "date");

					xmlPullParser.nextTag();
					xmlPullParser.require(XmlPullParser.START_TAG, null,
							"city_code");
					xmlPullParser.getName();
					xmlPullParser.nextText();
					xmlPullParser.require(XmlPullParser.END_TAG, null,
							"city_code");

					xmlPullParser.nextTag();
					xmlPullParser.require(XmlPullParser.START_TAG, null,
							"city_name");
					String citycode2 = xmlPullParser.getName();
					cityName = xmlPullParser.nextText();
					Log.d(citycode2, cityName);
					xmlPullParser.require(XmlPullParser.END_TAG, null,
							"city_name");
					saveWeatherInfo(context, cityName, weatherDes, temperature,
							publishTime, date);
					return;
				}

			}
			xmlPullParser.nextText();
		}
		xmlPullParser.require(XmlPullParser.END_TAG, null, "WeatherResult");
	}

	/**
	 * 存储到本地
	 * 
	 * @param context
	 * @param cityName
	 * @param weatherDes
	 * @param temperature
	 * @param publishTime
	 * @param date
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherDes, String temperature, String publishTime,
			String date) {
		Log.d("这里是存储数据", "来了");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("weatherDes", weatherDes);
		editor.putString("city_name", cityName);
		editor.putString("temperature", temperature);
		editor.putString("date", date);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		Log.d("这里是存储数据cityName", cityName);
		Log.d("这里是存储数据city_selected", cityName);
		editor.commit();
	}
}
