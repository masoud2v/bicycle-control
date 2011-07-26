package com.ccy.android.activity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.friendlyarm.AndroidSDK.HardwareControler;
public class MainActivity extends Activity {
    static final String DEBUG_TAG = "DEBUG";
    static final int IS_RENT_BICYCLE = 1;
    static final int IS_EV_CHARGE = 2;
    static final int IS_RETURN_BICYCLE = 3;
    static final int LOCK_OPEN = 1;
    static final int LOCK_CLOSE = 0;
    static final int BALANCE = 10000;
    static final int REND_PAY = 1;
    static final int CHARGE_PAY_UNIT = 1;
    static final int BICYCLE_NUMBER = 3;
    static final String DATABASE_NAME = "data.db";
    static final String BICYCLE_DATABASE_NAME = "bicycle.db";
    static final String USERINFO_DATABASE_NAME = "userinfo.db";
    static final String SLAVE_UART_PORT = "/dev/s3c2410_serial2";
    static final String SCANNER_UART_PORT = "/dev/s3c2410_serial3";
    static final String PRINTER_UART_PORT = "/dev/s3c2410_serial1";
    static final String MUSIC1 = "/mnt/sdcard/一号电动车充电完成，请取出充电把手，放回原位.wav";
    static final String MUSIC2 = "/mnt/sdcard/充电即将完成，请注意.wav";
    static final String MUSIC3 = "/mnt/sdcard/操作失败，请重试.wav";
    static final String MUSIC4 = "/mnt/sdcard/欢迎进入智能公共自行车租赁系统及电动车充电系统.wav";
    static final String MUSIC5 = "/mnt/sdcard/正在打印，请稍候，完成后请取凭条.wav";
    static final String MUSIC6 = "/mnt/sdcard/请去1号锁位取车.wav";
    static final String MUSIC7 = "/mnt/sdcard/请去2号锁位取车.wav";
    static final String MUSIC8 = "/mnt/sdcard/请去3号锁位取车.wav";
    static final String MUSIC9 = "/mnt/sdcard/请选择“自动选车或者手动选车”.wav";
    static final String MUSIC10 = "/mnt/sdcard/请选择充电时间.wav";
    
    static final String MUSIC11 = "/mnt/sdcard/一号电动车正在充电，请稍候.wav";
    static final String MUSIC12 = "/mnt/sdcard/刷卡失败，请重刷.wav";
    static final String MUSIC13 = "/mnt/sdcard/您以前的租车未还，请先还车.wav";
    
    static final String USER1 = "陈先生";
    static final String USER2 = "唐先生";
    static final String USER3 = "周先生";
    static SQLiteDatabase db;
    static SQLiteDatabase bicycle_db;
    static SQLiteDatabase userinfo_db;
    static BicycleInfo bicycleInfo;
    private TextView title;
    private int port = 3333;
    private String ServerIP = "124.76.33.110" ;
    public static String message;
    public static String name;//number
    public static String nameString;
    public Boolean flag;
    static Handler handler = new Handler();
    static int timeout = 20;
	static Runnable runnable =  new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(runnable, 1000);
			if(timeout == 20)MainActivity.playMusic(MUSIC2);
			if(timeout-- >= 0)
			{
				HardwareControler.setLedState(1, timeout%2);
			}else{				
				handler.removeCallbacks(runnable);
				HardwareControler.setLedState(0, 1);
				MainActivity.playMusic(MUSIC1);
			}
			
		}		
	};
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        title = (TextView)this.findViewById(R.id.Title);
        title.setGravity(Gravity.CENTER); 
        HardwareControler.setLedState(0, 1);
        HardwareControler.setLedState(1, 0);
        db = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        try{
			db.execSQL("CREATE TABLE record (_id INTEGER PRIMARY KEY, uid INTEGER, location TEXT, time TEXT, activity INTEGER, RFID TEXT)");
		}catch (Exception e)
		{
			
		}
		bicycle_db = this.openOrCreateDatabase(BICYCLE_DATABASE_NAME, MODE_PRIVATE, null);
		try{
			bicycle_db.execSQL("CREATE TABLE bicycleinfo (_id INTEGER PRIMARY KEY, addr INTEGER, RFID TEXT, lock_status INTEGER)");
			BicycleInfo bi = new BicycleInfo(1,"",LOCK_CLOSE);
			bi.add(bicycle_db);
			BicycleInfo bi2 = new BicycleInfo(2,"",LOCK_CLOSE);
			bi2.add(bicycle_db);
			BicycleInfo bi3 = new BicycleInfo(3,"",LOCK_CLOSE);
			bi3.add(bicycle_db);
		}catch (Exception e)
		{
			
		}
		userinfo_db = this.openOrCreateDatabase(USERINFO_DATABASE_NAME, MODE_PRIVATE, null);
		try{
			userinfo_db.execSQL("CREATE TABLE userinfo (_id INTEGER PRIMARY KEY, uid INTEGER, RFID TEXT, balance INTEGER)");
		}catch (Exception e)
		{
			
		}
/*		db.close();
		this.deleteDatabase("data.db");
		playMusic(MUSIC1);*/
    }
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		flag = true;
		new CardReaderThread().start();
		new SlaveData().start();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		flag = false;
		super.onPause();
	}
	class SlaveData extends Thread{

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int fd = HardwareControler.openSerialPort(SLAVE_UART_PORT, 115200, 8, 1);
			byte buf[] = new byte[30];
			int len = 0;
			while(true)
			{
				len = HardwareControler.read(fd, buf, 30);
				if(len > 0)
				{
//					Log.i(MainActivity.DEBUG_TAG,"len = "+len);
//					for(int i = 0;i<len;i++)
//						Log.i(MainActivity.DEBUG_TAG,"buf = "+buf[i]);
					if(buf[0] == 0x55 && (int)(buf[8]&0xff) == 0xAA && buf[1] == 0x00)
					{
						Cursor cur;
						String RFID = Integer.toHexString((int)buf[4]&0xff)+Integer.toHexString((int)buf[5]&0xff)+Integer.toHexString((int)buf[6]&0xff)+Integer.toHexString((int)buf[7]&0xff);
						switch(buf[3])
						{
						case 0x20:
							bicycleInfo = new BicycleInfo(buf[2],RFID,LOCK_OPEN);
							bicycleInfo.update(bicycle_db);
							break;
						case 0x30:
							bicycleInfo = new BicycleInfo(buf[2],RFID,LOCK_CLOSE);	
							cur = MainActivity.userinfo_db.rawQuery("SELECT * FROM userinfo where RFID = '" + RFID +"'", null);
							if(cur.moveToNext())
							{								
								UserInfo ui = new UserInfo(cur.getInt(cur.getColumnIndex("uid")), "", cur.getInt(cur.getColumnIndex("balance")));
								ui.update(MainActivity.userinfo_db);
								HistoryRecord.insert( MainActivity.IS_RETURN_BICYCLE, RFID, cur.getInt(cur.getColumnIndex("uid")));								
							}
							bicycleInfo.update(bicycle_db);
//							cur = MainActivity.userinfo_db.rawQuery("SELECT * FROM userinfo" , null);
//							while(cur.moveToNext())
//							{
//								if(RFID.equals(cur.getString(cur.getColumnIndex("RFID"))))
//								{
//									UserInfo ui = new UserInfo(cur.getInt(cur.getColumnIndex("uid")), "");
//									ui.update(MainActivity.userinfo_db);
//									HistoryRecord.insert( MainActivity.IS_RETURN_BICYCLE, RFID, cur.getInt(cur.getColumnIndex("uid")));
//									break;
//								}
//							}							
							break;
						case 0x40:
							name = String.valueOf(((int)(buf[10]&0xff))*16 + (int)(buf[11]&0xff));
							cur = MainActivity.userinfo_db.rawQuery("SELECT * FROM userinfo where uid = " + Integer.valueOf(MainActivity.name) + " AND RFID != ''", null);
							if(cur.moveToNext())
							{
								byte data[] = new byte[]{0x55,0x00,0x00,0x50,(byte) 0xAA};//failed
								data[1] = (byte) buf[2];
								int fd2 = HardwareControler.openSerialPort(MainActivity.SLAVE_UART_PORT, 115200, 8, 1);
								HardwareControler.write(fd2, data);
								HardwareControler.close(fd2);
							}else{
								BicycleInfo.choose(buf[2]);
							}
							break;
						}
						
											
//						cur = MainActivity.bicycle_db.rawQuery("SELECT * FROM bicycleinfo where addr = " + buf[2], null);
//						while (cur.moveToNext())
//						{
//							Log.i(DEBUG_TAG, String.valueOf(cur.getInt(cur.getColumnIndex("_id"))));
//							Log.i(DEBUG_TAG, String.valueOf(cur.getInt(cur.getColumnIndex("addr"))));
//							Log.i(DEBUG_TAG, cur.getString(cur.getColumnIndex("RFID")));
//							Log.i(DEBUG_TAG, String.valueOf(cur.getInt(cur.getColumnIndex("lock_status"))));					
//						}
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	class DataToServer extends Thread
	{
		private String Data;
		DataToServer(String data)
		{
			Data = data;
		}
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
    		Socket socket = null;
			try 
    		{	
    			//创建Socket
    			socket = new Socket(ServerIP,port); 
    			//向服务器发送消息
    			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);  
    			Data += "\n"; 
    			out.println(Data); 
    			
    			//接收来自服务器的消息
    			/*
    			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
    			String msg = br.readLine(); 
    			
    			if ( msg != null )
    			{
    				mTextView.setText(msg);
    			}
    			else
    			{
    				mTextView.setText("数据错误!");
    			}*/
    			//关闭流
    			out.close();
    			//br.close();
    			//关闭Socket
    			socket.close(); 
    		}
    		catch (Exception e) 
    		{
    			// TODO: handle exception
    			Log.e(DEBUG_TAG, e.toString());
    		}
		}
		
	}
	static void playMusic(String FilePath)
	{
		MediaPlayer mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(FilePath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	static void setNameString(String str)
	{
		switch(Integer.valueOf(str))
      	{
      	case 3835:
      		nameString = USER1;
      		break;
      	case 1901:
      		nameString = USER2;
      		break;
      	case 3299:
      		nameString = USER3;
      		break;	
      	default:
      		nameString = str;
      		break;     		
      	}
	}
	class CardReaderThread extends Thread
    {
    	public void run()
    	{
    		int ret = -1;

    		CardReader.led_control((byte)0x01);
            CardReader.bell_control();
            CardReader.power_on_card();
            while(flag)
            {
            	try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	        ret = CardReader.active_card();
    	        if(ret == 0)
    	        {
    	        	switch(CardReader.Rx_Buffer[5])
    				{
    				case 0x0A:
    					message = "TYPE A ";
    					break;
    				case 0x1A:
    					message = "TYPE M1 ";
    					break;
    				case 0x0B:
    					message = "TYPE B ";
    					break;
    				default:
    					message = "TYPE UNKNOWN ";
    					break;
    				}
    	        	message += "CARD UID:";
    	        	for(int i=0; i<CardReader.Rx_Buffer[6]; i++)
    	        	{
    	        		message += Integer.toHexString(CardReader.Rx_Buffer[7+i]);    	        		
    	        	}
    	        	if(CardReader.Rx_Buffer[7] == 0 && CardReader.Rx_Buffer[8] == 0)
    	        	{
    	        		MainActivity.playMusic(MainActivity.MUSIC12);
    	        		return;//请重刷
    	        	}
      	        	name = String.valueOf(((int)(CardReader.Rx_Buffer[7]&0xff))*16 + (int)(CardReader.Rx_Buffer[8]&0xff));      	        	
      	        	setNameString(name);
    	        /*	
    	        	message += " ATR Data:";
    	        	for(int i=0; i<CardReader.Rx_Buffer[7+CardReader.Rx_Buffer[6]]; i++)
    	        	{
    	        		message += Integer.toHexString(CardReader.Rx_Buffer[8+CardReader.Rx_Buffer[6]+i]);
    	        	}
    	        	*/
    	        	
    	        	Log.d(DEBUG_TAG,"message = " + message);
    	        	Log.d(DEBUG_TAG,"length = " + message.length());
    	        	new DataToServer(message).start();
    	        	MainActivity.playMusic(MainActivity.MUSIC4);
    	    		Intent intent = new Intent();
    	    		intent.setClass(MainActivity.this, SecondActivity.class);
    	    		startActivity(intent);
    	    		break;
    	        }
            }
    	}
    }
}