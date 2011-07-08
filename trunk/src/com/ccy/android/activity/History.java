package com.ccy.android.activity;

import java.io.UnsupportedEncodingException;

import com.friendlyarm.AndroidSDK.HardwareControler;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class History extends Activity implements OnClickListener {

	private TextView historyInfo;
	private Button print_btn;
	private Button return_btn;
	private String text;
	private String RecordItemList = "";
	private int i = 0;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.history);
		historyInfo = (TextView) this.findViewById(R.id.historyInfo);
		print_btn = (Button) this.findViewById(R.id.print_btn);
		return_btn = (Button) this.findViewById(R.id.return_btn);
		print_btn.setOnClickListener(this);
		return_btn.setOnClickListener(this);
		text = "您好，" + MainActivity.name + "\n" + 
						"活动  \t\t地点  \t\t时间\n" ;
		Cursor cur = MainActivity.db.rawQuery("SELECT * FROM record where uid = "+MainActivity.name, null);
		while (cur.moveToNext())
		{
			Log.i("DEBUG", String.valueOf(cur.getInt(cur.getColumnIndex("_id"))));
			Log.i("DEBUG", String.valueOf(cur.getInt(cur.getColumnIndex("uid"))));
			Log.i("DEBUG", cur.getString(cur.getColumnIndex("location")));
			Log.i("DEBUG", cur.getString(cur.getColumnIndex("time")));
			Log.i("DEBUG", String.valueOf(cur.getInt(cur.getColumnIndex("activity"))));
			switch(cur.getInt(cur.getColumnIndex("activity")))
			{
			case MainActivity.IS_EV_CHARGE:
				RecordItemList += "充电  \t\t";
				break;
			case MainActivity.IS_RENT_BICYCLE:
				RecordItemList += "租车  \t\t";
				break;
			}
			RecordItemList += cur.getString(cur.getColumnIndex("location"))+"  \t\t";
			RecordItemList += cur.getString(cur.getColumnIndex("time"))+"\n";
		}
		text += RecordItemList;
		historyInfo.setText(text);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.print_btn:
			Toast.makeText(History.this, "数据正在打印，请稍候......", Toast.LENGTH_LONG).show();
			int fd = HardwareControler.openSerialPort(MainActivity.PRINTER_UART_PORT, 9600, 8, 1);
	        
	        try {
				HardwareControler.write(fd, RecordItemList.getBytes("GB2312"));
				String txt = "活动  地点  时间\n您好，" + MainActivity.name + "\n" + 
				"    \n    \n" ;
				HardwareControler.write(fd, txt.getBytes("GB2312"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        HardwareControler.close(fd);
			break;
		case R.id.return_btn:
			break;
		}
		finish();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
	//	return super.onKeyDown(keyCode, event);
		return true;
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_DOWN:
			i++;
			if(i==2)i=1;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			i--;
			if(i==-1)i=0;
			break;
		}
		this.findViewById(R.id.print_btn+i).requestFocus();
		return super.onKeyUp(keyCode, event);
		
	}
}
