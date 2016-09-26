package com.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.db.CoolWeatherDB;
import com.db.CoolWeatherOpenHelper;
import com.pojo.City;
import com.pojo.Province;
import com.util.CityPullParse;

public class ChooseAreaActivity extends Activity {

	public static final int LEAVEL_PROVINCE = 0;
	public static final int LEAVEL_CITY = 1;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	private List<Province> provinceList;
	private List<City> cityList;
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;

	private boolean flag;
	private String fileName = "city.xml";
	private XmlResourceParser provinceandcityParser;

	/**
	 * 是否从WeatherActivity中跳转过来。
	 */
	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Log.d("isFromWeatherActivity", isFromWeatherActivity + "");
		Log.d("city_selected", prefs.getBoolean("city_selected", false) + "");
		if (prefs.getBoolean("city_selected", false) && isFromWeatherActivity) {
			Log.d("切换城市", WeatherActivity.class + "");
			Log.d("切换城市", this + "");
			Intent intent = new Intent(ChooseAreaActivity.this,
					ChooseAreaActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEAVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					Log.d("省份", "来了" + selectedProvince.getProvinceName());
					if ("北京".equals(selectedProvince.getProvinceName())
							|| "上海".equals(selectedProvince.getProvinceName())
							|| "天津".equals(selectedProvince.getProvinceName())
							|| "重庆".equals(selectedProvince.getProvinceName())) {
						Log.d("直辖市", "来了" + selectedProvince.getProvinceName());
						Intent intent = new Intent(ChooseAreaActivity.this,
								WeatherActivity.class);
						intent.putExtra("cityName",
								selectedProvince.getProvinceName());
						startActivity(intent);
						finish();
					} else {
						queryCities();
					}
				} else if (currentLevel == LEAVEL_CITY) {
					selectedCity = cityList.get(index);
					String cityName = selectedCity.getCityName();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("cityName", cityName);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvince();
	}

	private void queryProvince() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			// notifyDataSetChanged()
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEAVEL_PROVINCE;
		} else {
			queryFromXml();
		}
	}

	private void queryFromXml() {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(this,
				"CoolWeather8.db", null, 2);
		dbHelper.getWritableDatabase();
		getcity();
	}

	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEAVEL_CITY;
		}
	}

	private void getcity() {
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		provinceandcityParser = getXMLFromResXml(fileName);
		flag = CityPullParse.ParseXml(provinceandcityParser, coolWeatherDB);
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

	public InputStream getInputStreamFromAssets(String fileName) {
		try {
			InputStream inputStream = getResources().getAssets().open(fileName);
			return inputStream;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载..");
			progressDialog.setCanceledOnTouchOutside(false);

		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (currentLevel == LEAVEL_CITY) {
			queryProvince();
		} else {
			finish();
		}
	}

}
