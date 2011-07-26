package com.ccy.android.activity;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import com.ccy.android.activity.R;

public class SecondActivity extends Activity implements OnClickListener, OnFocusChangeListener, OnTouchListener {
	private TextView textView;
	private TextView TimeOutView;
	private int timeOut = 10;
	private Button btn_get_bicycle,btn_charge,btn_history,btn_exit;
	private int i = 0;
	Handler handler = new Handler();
	Runnable runnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(runnable, 1000);
			TimeOutView.setText(Integer.toString(--timeOut));
			if(timeOut == 0)
				finish();
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.second);
		textView = (TextView) this.findViewById(R.id.tv1);
		textView.setText("ÄúºÃ£¬"+MainActivity.nameString+"£¬ÇëÑ¡Ôñ£º");	
		TimeOutView = (TextView) this.findViewById(R.id.tv2);
		TimeOutView.setText("10");
		
		btn_get_bicycle = (Button) this.findViewById(R.id.btn1);
		btn_charge = (Button) this.findViewById(R.id.btn2);
		btn_history = (Button) this.findViewById(R.id.btn3);
		btn_exit = (Button) this.findViewById(R.id.btn4);
		btn_get_bicycle.setOnClickListener(this);
		btn_get_bicycle.setOnFocusChangeListener(this);
		btn_get_bicycle.setOnTouchListener(this);
		btn_charge.setOnClickListener(this);
		btn_charge.setOnFocusChangeListener(this);
		btn_charge.setOnTouchListener(this);
		btn_history.setOnClickListener(this);
		btn_history.setOnFocusChangeListener(this);
		btn_history.setOnTouchListener(this);
		btn_exit.setOnClickListener(this);
		btn_exit.setOnFocusChangeListener(this);
		btn_exit.setOnTouchListener(this);
		Cursor cur = MainActivity.userinfo_db.rawQuery("SELECT * FROM userinfo where uid = " + Integer.valueOf(MainActivity.name), null);
		if(!cur.moveToNext())
		{
			UserInfo.insert("", MainActivity.BALANCE);
		}
		handler.postDelayed(runnable, 2000);
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
			if(i==4)i=3;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			i--;
			if(i==-1)i=0;
			break;
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		}
		this.findViewById(R.id.btn1+i).requestFocus();		
		return super.onKeyUp(keyCode, event);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn1:
			Intent intent1 = new Intent(SecondActivity.this, RentBicycle.class);
			this.startActivity(intent1);			
			break;
		case R.id.btn2:
			Intent intent2 = new Intent(SecondActivity.this, EvCharge.class);
			this.startActivity(intent2);
			break;
		case R.id.btn3:
			Intent intent3 = new Intent(SecondActivity.this, History.class);
			this.startActivity(intent3);
			break;
		case R.id.btn4:
			break;
		}
		finish();
	}

	@Override
	public void onFocusChange(View v, boolean arg1) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn1:
			if(arg1)
			{				
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.rent_f);
			}
			else
			{
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.rent);
			}
			break;
		case R.id.btn2:
			if(arg1)
			{				
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.charge_f);
			}
			else
			{
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.charge);
			}
			break;
		case R.id.btn3:
			if(arg1)
			{				
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.history_f);
			}
			else
			{
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.history);
			}
			break;
		case R.id.btn4:
			if(arg1)
			{				
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.return1_f);
			}
			else
			{
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.return1);
			}
			break;
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent me) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn1:
			if(me.getAction() == MotionEvent.ACTION_DOWN)
			{				
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.rent_f);
			}
			else
			{
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.rent);
			}
			break;
		case R.id.btn2:
			if(me.getAction() == MotionEvent.ACTION_DOWN)
			{				
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.charge_f);
			}
			else
			{
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.charge);
			}
			break;
		case R.id.btn3:
			if(me.getAction() == MotionEvent.ACTION_DOWN)
			{				
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.history_f);
			}
			else
			{
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.history);
			}
			break;
		case R.id.btn4:
			if(me.getAction() == MotionEvent.ACTION_DOWN)
			{				
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.return1_f);
			}
			else
			{
				this.findViewById(v.getId()).setBackgroundResource(R.drawable.return1);
			}
			break;
		}
		return false;
	}

}
