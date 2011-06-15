package com.ccy.android.activity;

import android.app.Activity;
import android.os.Bundle;
import com.friendlyarm.AndroidSDK.HardwareControler;
public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        HardwareControler.setLedState(0, 1);
    }
}