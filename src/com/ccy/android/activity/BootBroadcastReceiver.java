package com.ccy.android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			Log.i(MainActivity.DEBUG_TAG,"Action boot completed!");
			Intent bootActivityIntent = new Intent(context, MainActivity.class);
			bootActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootActivityIntent);
		}
	}

}
