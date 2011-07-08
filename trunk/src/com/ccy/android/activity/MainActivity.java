package com.ccy.android.activity;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    static final int LOCK_OPEN = 1;
    static final int LOCK_CLOSE = 0;
    static final String DATABASE_NAME = "data.db";
    static final String BICYCLE_DATABASE_NAME = "bicycle.db";
    static final String SLAVE_UART_PORT = "/dev/s3c2410_serial1";
    static final String SCANNER_UART_PORT = "/dev/s3c2410_serial3";
    static final String PRINTER_UART_PORT = "/dev/s3c2410_serial2";
    static SQLiteDatabase db;
    static SQLiteDatabase bicycle_db;
    static BicycleInfo bicycleInfo;
    private TextView title;
    private int port = 3333;
    private String ServerIP = "58.41.82.134" ;
    public static String message;
    public static String name;
    public Boolean flag;
    static Handler handler = new Handler();
    static int timeout = 20;
	static Runnable runnable =  new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(runnable, 1000);
			if(timeout-- >= 0)
			{
				HardwareControler.setLedState(1, timeout%2);
			}else{
				handler.removeCallbacks(runnable);
				HardwareControler.setLedState(0, 1);
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
			db.execSQL("CREATE TABLE record (_id INTEGER PRIMARY KEY, uid INTEGER, location TEXT, time TEXT, activity INTEGER)");
		}catch (Exception e)
		{
			
		}
		bicycle_db = this.openOrCreateDatabase(BICYCLE_DATABASE_NAME, MODE_PRIVATE, null);
		try{
			bicycle_db.execSQL("CREATE TABLE bicycleinfo (_id INTEGER PRIMARY KEY, addr INTEGER, RFID TEXT, lock_status INTEGER)");
			BicycleInfo bi= new BicycleInfo(1,"",LOCK_CLOSE);
			bi.add(bicycle_db);
			BicycleInfo bi2= new BicycleInfo(2,"",LOCK_CLOSE);
			bi2.add(bicycle_db);
		}catch (Exception e)
		{
			
		}
/*		db.close();
		this.deleteDatabase("data.db");*/
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
						String RFID = Integer.toHexString(buf[4])+Integer.toHexString(buf[5])+Integer.toHexString(buf[6])+Integer.toHexString(buf[7]);
						switch(buf[3])
						{
						case 0x20:
							bicycleInfo = new BicycleInfo(buf[2],RFID,LOCK_OPEN);
							break;
						case 0x30:
							bicycleInfo = new BicycleInfo(buf[2],RFID,LOCK_CLOSE);
							break;
						}
						bicycleInfo.update(bicycle_db);
						Cursor cur = MainActivity.bicycle_db.rawQuery("SELECT * FROM bicycleinfo where addr = "+buf[2], null);
						while (cur.moveToNext())
						{
							Log.i(DEBUG_TAG, String.valueOf(cur.getInt(cur.getColumnIndex("_id"))));
							Log.i(DEBUG_TAG, String.valueOf(cur.getInt(cur.getColumnIndex("addr"))));
							Log.i(DEBUG_TAG, cur.getString(cur.getColumnIndex("RFID")));
							Log.i(DEBUG_TAG, String.valueOf(cur.getInt(cur.getColumnIndex("lock_status"))));					
						}
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
      	        	name = String.valueOf(((int)(CardReader.Rx_Buffer[7]&0xff))*16 + (int)(CardReader.Rx_Buffer[8]&0xff));;
    	        /*	
    	        	message += " ATR Data:";
    	        	for(int i=0; i<CardReader.Rx_Buffer[7+CardReader.Rx_Buffer[6]]; i++)
    	        	{
    	        		message += Integer.toHexString(CardReader.Rx_Buffer[8+CardReader.Rx_Buffer[6]+i]);
    	        	}
    	        	*/
    	        	
    	        	Log.d(DEBUG_TAG,"message = " + message);
    	        	Log.d(DEBUG_TAG,"length = " + message.length());
//    	        	new DataToServer(message).start();
    	    		Intent intent = new Intent();
    	    		intent.setClass(MainActivity.this, SecondActivity.class);
    	    		startActivity(intent);
    	    		break;
    	        }
            }
    	}
    }
}