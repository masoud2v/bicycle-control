package com.ccy.android.activity;
import com.friendlyarm.AndroidSDK.HardwareControler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BicycleInfo {
	private int addr;
	private String RFID;
	private int lock_status;
	
	BicycleInfo(int a, String R, int ls)
	{
		addr = a;
		RFID = R;
		lock_status = ls;
	}
	public String getRFID()
	{
		return RFID;
	}
	public void add(SQLiteDatabase db)
	{
		ContentValues cv = new ContentValues();
		cv.put("addr",addr);
		cv.put("RFID", RFID);
		cv.put("lock_status", lock_status);
		db.insert("bicycleinfo", null, cv);
	}
	
	public void update(SQLiteDatabase db)
	{
		db.execSQL("update bicycleinfo set RFID = ?,lock_status = ? where addr = ?", new Object[]
		{ RFID, lock_status, addr });
	}
	public int getAddr()
	{
		return addr;
	}
	public int getLock_status()
	{
		return lock_status;
	}
	public static void choose(int bike_addr)
	{
		byte data[] = new byte[]{0x55,0x00,0x00,0x20,(byte) 0xAA};
		data[1] = (byte) bike_addr;
		int fd = HardwareControler.openSerialPort(MainActivity.SLAVE_UART_PORT, 115200, 8, 1);
		int count = 3;
		while(count-->0)
		{
			HardwareControler.write(fd, data);
			int count2 = 10;
			while(count2 -- > 0)
			{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(MainActivity.bicycleInfo != null && MainActivity.bicycleInfo.getAddr() == bike_addr && MainActivity.bicycleInfo.getLock_status() == MainActivity.LOCK_OPEN)
				{
					HistoryRecord.insert(MainActivity.IS_RENT_BICYCLE, MainActivity.bicycleInfo.getRFID(), Integer.valueOf(MainActivity.name));
					Cursor cur = MainActivity.userinfo_db.rawQuery("SELECT * FROM userinfo where uid = " + Integer.valueOf(MainActivity.name), null);
					if(cur.moveToNext())
					{
						UserInfo ui = new UserInfo(Integer.valueOf(MainActivity.name), MainActivity.bicycleInfo.getRFID(),cur.getInt(cur.getColumnIndex("balance")) - MainActivity.REND_PAY);
						ui.update(MainActivity.userinfo_db);
					}else{
						UserInfo.insert(MainActivity.bicycleInfo.getRFID(), MainActivity.BALANCE - MainActivity.REND_PAY);
					}				
					break;
				}
			}
			if(count2 >= 0)
				break;
		}
		HardwareControler.close(fd);
	}
	public static void choose(int bike_addr, Context context,final Activity activity)
	{
		byte data[] = new byte[]{0x55,0x00,0x00,0x20,(byte) 0xAA};
		data[1] = (byte) bike_addr;
		int fd = HardwareControler.openSerialPort(MainActivity.SLAVE_UART_PORT, 115200, 8, 1);
		int count = 3;
		while(count-->0)
		{
			HardwareControler.write(fd, data);
			int count2 = 10;
			while(count2 -- > 0)
			{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(MainActivity.bicycleInfo != null && MainActivity.bicycleInfo.getAddr() == bike_addr && MainActivity.bicycleInfo.getLock_status() == MainActivity.LOCK_OPEN)
				{
					HistoryRecord.insert(MainActivity.IS_RENT_BICYCLE, MainActivity.bicycleInfo.getRFID(), Integer.valueOf(MainActivity.name));
					Cursor cur = MainActivity.userinfo_db.rawQuery("SELECT * FROM userinfo where uid = " + Integer.valueOf(MainActivity.name), null);
					if(cur.moveToNext())
					{
						UserInfo ui = new UserInfo(Integer.valueOf(MainActivity.name), MainActivity.bicycleInfo.getRFID(), cur.getInt(cur.getColumnIndex("balance")) - MainActivity.REND_PAY);
						ui.update(MainActivity.userinfo_db);
					}else{
						UserInfo.insert(MainActivity.bicycleInfo.getRFID(), MainActivity.BALANCE - MainActivity.REND_PAY);
					}
					switch(bike_addr)
					{
					case 1:
						MainActivity.playMusic(MainActivity.MUSIC6);
						break;
					case 2:
						MainActivity.playMusic(MainActivity.MUSIC7);
						break;
					case 3:
						MainActivity.playMusic(MainActivity.MUSIC8);
						break;
					}
					new AlertDialog.Builder(context)   
					         .setTitle("自行车信息")   
					        .setMessage("您好，"+MainActivity.nameString+"，"+bike_addr+"号车位的自行车锁已经打开\n自行车号:"+MainActivity.bicycleInfo.getRFID())    
					       .setPositiveButton("确定",    
					        new DialogInterface.OnClickListener(){   
					                  public void onClick(DialogInterface dialoginterface, int i){    
					                                 //按钮事件     
					                	  		activity.finish();
					                              }    
					                      }).show();   					
					break;
				}
			}
			if(count2 >= 0)
				break;
		}
		HardwareControler.close(fd);
		if(count < 0)
		{
			MainActivity.playMusic(MainActivity.MUSIC3);
			new AlertDialog.Builder(context)   
	        .setTitle("错误")   
	       .setMessage("抱歉，操作失败！")    
	      .setPositiveButton("确定",    
	       new DialogInterface.OnClickListener(){   
	                 public void onClick(DialogInterface dialoginterface, int i){    
	                                //按钮事件     
	               	  		activity.finish();
	                             }    
	                     }).show();  
		}
	}
}
