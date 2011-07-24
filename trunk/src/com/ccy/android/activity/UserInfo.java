package com.ccy.android.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class UserInfo {
	private int uid;
	private String RFID;
	private int balance;
	
	UserInfo(int id, String rfid, int balan)
	{
		uid = id;
		RFID = rfid;
		balance = balan;
	}
	public int getUid()
	{
		return uid;
	}
	public String getRFID()
	{
		return RFID;
	}
	
	public int getBalance()
	{
		return balance;
	}
	public void add(SQLiteDatabase db)
	{
		ContentValues cv = new ContentValues();
		cv.put("uid",uid);
		cv.put("RFID", RFID);
		cv.put("balance", balance);
		db.insert("userinfo", null, cv);
	}
	
	public void update(SQLiteDatabase db)
	{
		db.execSQL("update userinfo set RFID = ?,balance = ? where uid = ?", new Object[]
		{ RFID, balance, uid });
	}
	public static void insert(String rfid, int bal) 
	{
		// TODO Auto-generated method stub
		UserInfo userInfo = new UserInfo(Integer.valueOf(MainActivity.name), rfid, bal);
		userInfo.add(MainActivity.userinfo_db);
	}
}
