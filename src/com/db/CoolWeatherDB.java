package com.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.webkit.WebChromeClient.CustomViewCallback;

import com.pojo.City;
import com.pojo.Province;

/**
 * 这个类用于把一些常用的数据库操作封装起来 以方便以后的使用
 * 
 * @author pengTan
 */
public class CoolWeatherDB {
	public static final String DB_NAME = "CoolWeather8.db";
	public static final int VERSION = 2;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	// 将构造方法私有化
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	// 获取CoolWeatherDB的实例
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	// 将city实例存储到数据库
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values); // 插入到数据库中
		}
	}

	// 从数据库中读取某省下所有的城市信息
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { provinceId+"" }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setProvinceId(cursor.getInt(cursor
						.getColumnIndex("province_id")));
				list.add(city);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	// 将province实例存储到数据库
	public void saveProvince(Province province) {
		if (province != null) {
			Log.d("这里是写入数据库的方法", "来了");
			ContentValues values = new ContentValues();
			values.put("id", province.getId());
			values.put("province_name", province.getProvinceName());
			db.insert("Province", null, values);
		}
	}

	// 从数据库中读取所有的省份信息
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToNext()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
}
