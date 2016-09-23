package com.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pojo.City;
import com.pojo.Province;

/**
 * ��������ڰ�һЩ���õ���ݿ�����װ��4 �Է����Ժ��ʹ��
 * 
 * @author pengTan
 */
public class CoolWeatherDB {
	public static final String DB_NAME = "CoolWeather8.db";
	public static final int VERSION = 2;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	// �����췽��˽�л�
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	// ��ȡCoolWeatherDB��ʵ��
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	// ��cityʵ��洢����ݿ�
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values); // ���뵽��ݿ���
		}
	}

	// ����ݿ��ж�ȡĳʡ�����еĳ�����Ϣ
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

	// ��provinceʵ��洢����ݿ�
	public void saveProvince(Province province) {
		if (province != null) {
			Log.d("������д����ݿ�ķ���", "4��");
			ContentValues values = new ContentValues();
			values.put("id", province.getId());
			values.put("province_name", province.getProvinceName());
			db.insert("Province", null, values);
		}
	}

	// ����ݿ��ж�ȡ���е�ʡ����Ϣ
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
