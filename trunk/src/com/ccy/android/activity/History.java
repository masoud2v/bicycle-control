package com.ccy.android.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class History extends Activity implements OnClickListener {

	private TextView historyInfo;
	private Button print_btn;
	private Button return_btn;
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
		String text = "您好，" + MainActivity.name + "\n" + 
						"活动\t\t地点\t\t时间\n" ;
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
				text += "充电\t\t";
				break;
			case MainActivity.IS_RENT_BICYCLE:
				text += "租车\t\t";
				break;
			}
			text += cur.getString(cur.getColumnIndex("location"))+"\t\t";
			text += cur.getString(cur.getColumnIndex("time"))+"\n";
		}
		historyInfo.setText(text);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.print_btn:
			Toast.makeText(History.this, "数据正在打印，请稍候......", Toast.LENGTH_LONG).show();
			break;
		case R.id.return_btn:
			break;
		}
		finish();
	}

}
