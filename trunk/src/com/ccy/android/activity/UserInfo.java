package com.ccy.android.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class UserInfo {
	private int uid;
	private String RFID;
	
	UserInfo(int id, String rfid)
	{
		uid = id;
		RFID = rfid;
	}
	public int getUid()
	{
		return uid;
	}
	public String getRFID()
	{
		return RFID;
	}
	
	public void add(SQLiteDatabase db)
	{
		ContentValues cv = new ContentValues();
		cv.put("uid",uid);
		cv.put("RFID", RFID);
		db.insert("userinfo", null, cv);
	}
	
	public void update(SQLiteDatabase db)
	{
		db.execSQL("update userinfo set RFID = ? where uid = ?", new Object[]
		{ RFID,  uid });
	}
	public static void insert(String rfid) 
	{
		// TODO Auto-generated method stub
		UserInfo userInfo = new UserInfo(Integer.valueOf(MainActivity.name), rfid);
		userInfo.add(MainActivity.userinfo_db);
	}
}
