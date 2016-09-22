package com.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.db.CoolWeatherDB;
import com.db.CoolWeatherOpenHelper;
import com.pojo.City;
import com.pojo.Province;
import com.util.CityPullParse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseAreaActivity extends Activity {

	public static final int LEAVEL_PROVINCE = 0;
	public static final int LEAVEL_CITY = 1;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	// 省列表
	private List<Province> provinceList;
	// 城市列表
	private List<City> cityList;
	// 选中的省份
	private Province selectedProvince;
	// 选中的城市
	private City selectedCity;
	// 当前选中的级别
	private int currentLevel;

	private boolean flag;
	private TextView textview;
	private String fileName = "city.xml";
	private XmlResourceParser provinceandcityParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
					queryCities();
				} else if (currentLevel == LEAVEL_CITY) {
					selectedCity = cityList.get(index);
				}
			}
		});
		queryProvince();
	}

	/**
	 * 查询全国所有的省份，优先从数据库查，没有再去XML文件里面读取
	 */
	private void queryProvince() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			// 有时候我们需要修改已经生成的列表，添加或者修改数据，
			// notifyDataSetChanged()可以在修改适配器绑定的数组后，不用重新刷新Activity，通知Activity更新ListView
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

	/**
	 * 查询选中省份内所有的市。肯定已经存在数据库里面了，所以不用重新解析xml文件
	 */
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
		// cityArray = CityPullParse.Parse(getInputStreamFromAssets(fileName));
		/*
		 * for (City city : cityArray) { String provinceName = ""; int
		 * provinceid = city.getProvinceId(); cityStr += "省份ID[" +
		 * city.getProvinceId() + "],省份name[" + city.getProvinceName() +
		 * "],城市ID[" + city.getId() + "], " + city.getCityName() + "\n"; }
		 */
		textview.setText(flag + "");
		Log.d("这里是getcity", "获取信息成功！");
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
	 * 从assets中读取文件到InputStream中
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

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在拼了命的加载..");
			progressDialog.setCanceledOnTouchOutside(false);

		}
		progressDialog.show();
	}

	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 获取back键，根据当前级别来判断，此时应该返回到市列表、省列表还是直接退出
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEAVEL_CITY) {
			queryProvince();
		} else {
			finish();
		}
	}

}
