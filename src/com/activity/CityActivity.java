package com.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.db.CoolWeatherDB;
import com.db.CoolWeatherOpenHelper;
import com.pojo.City;
import com.pojo.Province;
import com.util.CityPullParse;

public class CityActivity extends Activity implements OnClickListener {

	private ArrayList<City> cityArray;
	private boolean flag;
	private ArrayList<Province> provinceArray;
	private String cityStr;
	private TextView textview;
	private String fileName = "city.xml";
	private XmlResourceParser provinceandcityParser;
	public CoolWeatherDB coolWeatherDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		((Button) findViewById(R.id.btn_getcity)).setOnClickListener(this);
		textview = (TextView) findViewById(R.id.textview);
	}

	private void getcity() {
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		provinceandcityParser = getXMLFromResXml(fileName);
		flag = CityPullParse.ParseXml(provinceandcityParser, coolWeatherDB);
		// cityArray = CityPullParse.Parse(getInputStreamFromAssets(fileName));
		/*
		 * for (City city : cityArray) { String provinceName = ""; int
		 * provinceid = city.getProvinceId(); cityStr += "ʡ��ID[" +
		 * city.getProvinceId() + "],ʡ��name[" + city.getProvinceName() +
		 * "],����ID[" + city.getId() + "], " + city.getCityName() + "\n"; }
		 */
		textview.setText(flag + "");
	}

	public XmlResourceParser getXMLFromResXml(String fileName) {
		XmlResourceParser xmlParser = null;
		try {
			xmlParser = this.getResources().getXml(R.xml.city);
			return xmlParser;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlParser;
	}

	/**
	 * ��assets�ж�ȡ�ļ���InputStream��
	 */
	public InputStream getInputStreamFromAssets(String fileName) {
		try {
			InputStream inputStream = getResources().getAssets().open(fileName);
			return inputStream;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(this,
				"CoolWeather8.db", null, 2);
		if (v.getId() == R.id.btn_getcity) {
			System.out.println(this);
			dbHelper.getWritableDatabase();
			getcity();
		}
	}
}