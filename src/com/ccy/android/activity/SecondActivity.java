package com.ccy.android.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.ccy.android.activity.R;

public class SecondActivity extends Activity implements OnClickListener {
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
		textView.setText("ÄúºÃ£¬"+MainActivity.name+"£¬ÇëÑ¡Ôñ£º");	
		TimeOutView = (TextView) this.findViewById(R.id.tv2);
		TimeOutView.setText("10");
		btn_get_bicycle = (Button) this.findViewById(R.id.btn1);
		btn_charge = (Button) this.findViewById(R.id.btn2);
		btn_history = (Button) this.findViewById(R.id.btn3);
		btn_exit = (Button) this.findViewById(R.id.btn4);
		
		btn_get_bicycle.setOnClickListener(this);
		btn_charge.setOnClickListener(this);
		btn_history.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
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

}
