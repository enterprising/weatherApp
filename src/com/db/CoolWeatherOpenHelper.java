package com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * 关于SQLite的增删改查
 * 
 * @author pengTan
 * 
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	private Context mcontext;

	/**
	 * City表建表语句
	 */
	public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement," 
			+ "city_name text,"
			+ "province_id int)";
	/**
	 * Province表建表语句
	 */
	public static final String CREATE_PROVINCE = "create table Province (" 
			+ "id integer," 
			+ "province_name text)";

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		mcontext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CITY);  //创建表
		db.execSQL(CREATE_PROVINCE);
		Toast.makeText(mcontext, "创建表成功！", Toast.LENGTH_LONG).show();
		Log.d("这是来自数据库工具类的提示信息", "创建表成功");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}
