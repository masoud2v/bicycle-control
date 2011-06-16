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

public class SecondActivity extends Activity implements OnClickListener {
	private TextView textView;
	private Button btn_get_bicycle,btn_print,btn_exit;
	Handler handler = new Handler();
	Runnable runnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			finish();
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.second);
		textView = (TextView) this.findViewById(R.id.tv1);
		textView.setText(MainActivity.message);	
		btn_get_bicycle = (Button) this.findViewById(R.id.btn1);
		btn_print = (Button) this.findViewById(R.id.btn2);
		btn_exit = (Button) this.findViewById(R.id.btn3);
		
		btn_get_bicycle.setOnClickListener(this);
		btn_print.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		handler.postDelayed(runnable, 5000);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn1:
			break;
		case R.id.btn2:
			break;
		case R.id.btn3:
			finish();
			break;
		}
	}

}
