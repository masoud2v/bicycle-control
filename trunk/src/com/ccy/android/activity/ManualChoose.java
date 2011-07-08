package com.ccy.android.activity;



import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class ManualChoose extends Activity implements OnClickListener, OnCheckedChangeListener {

	private TextView info;
	private Button ok_btn;
	private Button cancel_btn;
	private RadioGroup RG;
	private int checkedId = 0;
	private int i = 0;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.manual);
		info = (TextView) this.findViewById(R.id.Info);
		info.setText("您好，"+MainActivity.name+"，请选择：");
		ok_btn = (Button) this.findViewById(R.id.ok_btn);
		cancel_btn = (Button) this.findViewById(R.id.cancel_btn);
		RG = (RadioGroup) this.findViewById(R.id.RadioGroup2);				
		ok_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		RG.setOnCheckedChangeListener(this);
		

		Cursor cur = MainActivity.bicycle_db.rawQuery("SELECT * FROM bicycleinfo where lock_status = "+MainActivity.LOCK_OPEN, null);
		while (cur.moveToNext())
		{
			switch(cur.getInt(cur.getColumnIndex("addr")))
			{
			case 0x01:
				this.findViewById(R.id.r_btn_bike1).setEnabled(false);
				break;
			case 0x02:
				this.findViewById(R.id.r_btn_bike2).setEnabled(false);
				break;
			}				
		}
		if(cur.getCount() == 2)
		{
			Toast.makeText(ManualChoose.this, "您好，不好意思，已经没有自行车可供选择，欢迎下次光临！", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.ok_btn:
			if(this.checkedId >0)
			{
				BicycleInfo.choose(this.checkedId, ManualChoose.this, this);
			}else{
				Toast.makeText(ManualChoose.this, "您好，请选择自行车，谢谢！", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.cancel_btn:
			finish();
			break;
		}
	}
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId)
		{
		case R.id.r_btn_bike1:
			this.checkedId = 1;
			break;
		case R.id.r_btn_bike2:
			this.checkedId = 2;
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
			if(i==4)i=3;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			i--;
			if(i==-1)i=0;
			break;
		}
		this.findViewById(R.id.r_btn_bike1+i).requestFocus();
		return super.onKeyUp(keyCode, event);
		
	}
}
