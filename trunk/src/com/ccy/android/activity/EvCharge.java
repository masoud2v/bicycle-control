package com.ccy.android.activity;


import com.friendlyarm.AndroidSDK.HardwareControler;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class EvCharge extends Activity implements OnClickListener, OnCheckedChangeListener {
	
	private TextView chargeInfo;
	private Button ok_btn;
	private Button cancel_btn;
	private RadioGroup RG;
	private String checkedTime = null;
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.evcharge);
		chargeInfo = (TextView) this.findViewById(R.id.chargeInfo);
		chargeInfo.setText("您好，"+ MainActivity.name +"，请选择电动车充电时间：");
		
		ok_btn = (Button) this.findViewById(R.id.ok_btn);
		cancel_btn = (Button) this.findViewById(R.id.cancel_btn);
		RG = (RadioGroup) this.findViewById(R.id.RadioGroup);
		
		ok_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		RG.setOnCheckedChangeListener(this);
			
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.ok_btn:
			if(this.checkedTime != null)
			{
				Toast.makeText(EvCharge.this, "您好，您的电动车正在充电，充电时间为"+this.checkedTime+"，请稍候......", Toast.LENGTH_LONG).show();
				HardwareControler.setLedState(0, 1);
				HardwareControler.setLedState(1, 1);
				MainActivity.handler.postDelayed(MainActivity.runnable, 20000);
				HistoryRecord.insert(MainActivity.IS_EV_CHARGE);
				finish();
			}else{
				Toast.makeText(EvCharge.this, "您好，请选择充电时间，谢谢！", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.cancel_btn:
			
			finish();
			break;
		}
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId)
		{
		case R.id.r_btn1:
			this.checkedTime = "10分钟";
			break;
		case R.id.r_btn2:
			this.checkedTime = "20分钟";
			break;
		case R.id.r_btn3:
			this.checkedTime = "30分钟";
			break;
		}
	}
}
