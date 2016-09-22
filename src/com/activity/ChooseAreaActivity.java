package com.activity;

import java.util.ArrayList;
import java.util.List;

import com.db.CoolWeatherDB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseAreaActivity extends Activity{
	
	public static final int LEAVEL_PROVINCE = 0;
	public static final int LEAVEL_CITY = 1;
	
	private ProgressDialog progressDialog;
	private TextView tltleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	//Ê¡ÁÐ±í

}
