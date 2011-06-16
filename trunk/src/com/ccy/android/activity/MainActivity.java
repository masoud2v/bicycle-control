package com.ccy.android.activity;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.friendlyarm.AndroidSDK.HardwareControler;
public class MainActivity extends Activity {
    static final String DEBUG_TAG = "DEBUG";
    private int port = 3333;
    private String ServerIP = "58.41.211.124" ;
    public static String message;
    public Boolean flag;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        HardwareControler.setLedState(0, 1);       
        
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

	class CardReaderThread extends Thread
    {
    	public void run()
    	{
    		int ret = -1;
    		Socket socket = null;
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
    	        	message = CardReader.Rx_Buffer.toString() + "\r\n"; 
    	        	Log.d(DEBUG_TAG,"message = " + message);
    	        	Log.d(DEBUG_TAG,"length = " + message.length());
    	        	try 
    	    		{	
    	    			//创建Socket
    	    			socket = new Socket(ServerIP,port); 
    	    			//向服务器发送消息
    	    			PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true);      
    	    			out.println(message); 
    	    			
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
    	    		Intent intent = new Intent();
    	    		intent.setClass(MainActivity.this, SecondActivity.class);
    	    		startActivity(intent);
    	    		break;
    	        }
            }
    	}
    }
}