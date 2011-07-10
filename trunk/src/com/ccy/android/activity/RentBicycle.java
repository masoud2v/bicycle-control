package com.ccy.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RentBicycle extends Activity implements OnClickListener {

	private Button auto_btn;
	private Button manual_btn;
	private TextView RentInfo;
	private int i = 0;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.rentbicycle);
		Cursor cur = MainActivity.userinfo_db.rawQuery("SELECT * FROM userinfo where uid = " + Integer.valueOf(MainActivity.name) + " AND RFID != ''", null);
		if(cur.moveToNext())
		{
			new AlertDialog.Builder(this)   
	         .setTitle("租赁信息")   
	        .setMessage("您好，"+MainActivity.name+"，系统显示您上次租借的"+cur.getString(cur.getColumnIndex("RFID"))+"号自行车尚未归还，请归还后再重新租赁，谢谢！")    
	       .setPositiveButton("确定",    
	        new DialogInterface.OnClickListener(){   
	                  public void onClick(DialogInterface dialoginterface, int i){    
	                                 //按钮事件     
	                	  		finish();
	                              }    
	                      }).show(); 
		}
		RentInfo = (TextView)this.findViewById(R.id.rentInfo);
		RentInfo.setText("您好，"+MainActivity.name+"，请选择：");
		auto_btn = (Button) this.findViewById(R.id.auto);
		manual_btn = (Button) this.findViewById(R.id.manual);
		
		auto_btn.setOnClickListener(this);
		manual_btn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.auto:
			Cursor cur = MainActivity.bicycle_db.rawQuery("SELECT * FROM bicycleinfo where lock_status = "+MainActivity.LOCK_CLOSE, null);
			if (cur.moveToNext())
			{
				BicycleInfo.choose(cur.getInt(cur.getColumnIndex("addr")), RentBicycle.this, this);				
			}else{
				Toast.makeText(RentBicycle.this, "您好，不好意思，已经没有自行车可供选择，欢迎下次光临！", Toast.LENGTH_LONG).show();
				finish();
			}
			break;
		case R.id.manual:
			Intent intent = new Intent(RentBicycle.this, ManualChoose.class);
			this.startActivity(intent);
			finish();
			break;
		}
		
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
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		}
		this.findViewById(R.id.auto+i).requestFocus();
		return super.onKeyUp(keyCode, event);
		
	}
	
}
