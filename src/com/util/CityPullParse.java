package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.db.CoolWeatherDB;
import com.pojo.City;
import com.pojo.Province;

import android.R.bool;
import android.content.res.XmlResourceParser;
import android.util.Log;

/**
 * 解析XML，获取城市
 * 
 */
public class CityPullParse {

	public static boolean Parse(String CityString, CoolWeatherDB coolWeatherDB) {
		// ArrayList<City> CityArray = new ArrayList<City>();
		boolean x = false;
		try {
			// 定义工厂 XmlPullParserFactory
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

			// 定义解析器 XmlPullParser
			XmlPullParser parser = factory.newPullParser();

			// 获取xml输入数据
			parser.setInput(new StringReader(CityString));

			x = ParseXml(parser, coolWeatherDB);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return x;
	}

	public static boolean Parse(InputStream cityIS, CoolWeatherDB coolWeatherDB) {
		// ArrayList<City> cityArray = new ArrayList<City>();
		boolean x = false;
		try {
			// 定义工厂 XmlPullParserFactory
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

			// 定义解析器 XmlPullParser(要转换为XmlResourceParser,否则不能正常解析)
			XmlResourceParser parser = (XmlResourceParser) factory
					.newPullParser();

			// 获取xml输入数据
			parser.setInput(cityIS, "utf-8");

			x = ParseXml(parser, coolWeatherDB);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return x;
	}

	/**
	 * @param parser
	 * @return
	 */
	public static boolean ParseXml(XmlPullParser parser,
			CoolWeatherDB coolWeatherDB) {
		// ArrayList<City> cityArray = new ArrayList<City>();
		City CityTemp = null;
		Province province = null;
		String cityName;
		String provinceName = "";
		int provinceId = 1;
		boolean x = false;
		boolean y = false;
		try {
			// 开始解析事件
			int eventType = parser.getEventType();

			// 处理事件，不碰到文档结束就一直处理
			while (eventType != XmlPullParser.END_DOCUMENT) {
				// 因为定义了一堆静态常量，所以这里可以用switch
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG:
					// 给当前标签起个名字
					String tagName = parser.getName();
					if (tagName.equals("province")) {
						// provinceId =
						// Integer.parseInt(parser.getAttributeValue(null,
						// "id"));// 获取province节点属性为id的值
						// Integer.parseInt(parser.getAttributeValue(0));//第二种方式（获取province节点属性为id的值
						provinceName = String.valueOf(parser.getAttributeValue(
								null, "name"));// 获取province节点属性为name的值
						Log.d("这里是xml读取出来的省份的名字", provinceName);
						Log.d("这里是省份的id", provinceId+"");
						province = new Province();
						province.setId(provinceId);
						province.setProvinceName(provinceName);
						provinceId++;
						coolWeatherDB.saveProvince(province);
						x = true;
					} else if (tagName.equals("item")) {
						CityTemp = new City();
						cityName = parser.nextText(); // 读取的城市名
						CityTemp.setCityName(cityName);
						CityTemp.setProvinceId(provinceId-1);
						coolWeatherDB.saveCity(CityTemp);
						// cityArray.add(CityTemp);
						y = true;
					}
					break;

				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				}

				// 别忘了用next方法处理下一个事件，忘了的结果就成死循环#_#
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (x && y) {
			return true;
		}
		return false;
	}
}