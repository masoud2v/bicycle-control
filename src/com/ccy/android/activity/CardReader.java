package com.ccy.android.activity;
import android.util.Log;

import com.friendlyarm.AndroidSDK.HardwareControler;

public class CardReader {

	public static byte[] Tx_Buffer = new byte [255];
	public static byte[] Rx_Buffer = new byte [255];
	public static int rx_length;
	public static int tx_length;
	
	public static int led_control(byte led_bit){
		tx_length = 3;
		rx_length = 0;
		Tx_Buffer[0] = 0x02;
		Tx_Buffer[1] = (byte) (tx_length>>8 & 0xff);
		Tx_Buffer[2] = (byte) (tx_length & 0xff);
		Tx_Buffer[3] = 0x31;
		Tx_Buffer[4] = 0x14;
		Tx_Buffer[5] = (byte) (led_bit<<6);
		Tx_Buffer[6] = calcEcc();
		Tx_Buffer[7] = 0x03;
		process_cmd();
		if(rx_length == 7 && Rx_Buffer[3] == 0x00 && Rx_Buffer[4] == 0x00)
		{
		  return 0;
		}
		else return -1;
	}
	public static int bell_control(){
		tx_length = 5;
		rx_length = 0;
		Tx_Buffer[0] = 0x02;
		Tx_Buffer[1] = (byte) (tx_length>>8 & 0xff);
		Tx_Buffer[2] = (byte) (tx_length & 0xff);
		Tx_Buffer[3] = 0x31;
		Tx_Buffer[4] = 0x13;
		Tx_Buffer[5] = 0x01;
		Tx_Buffer[6] = 0x00;
		Tx_Buffer[7] = 0x01;
		Tx_Buffer[8] = calcEcc();
		Tx_Buffer[9] = 0x03;
		process_cmd();
		if(rx_length == 7 && Rx_Buffer[3] == 0x00 && Rx_Buffer[4] == 0x00)
		{
		  return 0;
		}
		else return -1;
	}
	public static int power_on_card(){
		tx_length = 5;
		rx_length = 0;
		Tx_Buffer[0] = 0x02;
		Tx_Buffer[1] = (byte) (tx_length>>8 & 0xff);
		Tx_Buffer[2] = (byte) (tx_length & 0xff);
		Tx_Buffer[3] = 0x32;
		Tx_Buffer[4] = 0x22;
		Tx_Buffer[5] = 0x00;
		Tx_Buffer[6] = 0x00;
		Tx_Buffer[7] = 0x11;
		Tx_Buffer[8] = calcEcc();
		Tx_Buffer[9] = 0x03;
		process_cmd();
		if(rx_length > 0 && Rx_Buffer[3] == 0x00 && Rx_Buffer[4] == 0x00)
		{
		  return 0;
		}
		else return -1;
	}
	public static int active_card(){
		tx_length = 4;
		rx_length = 0;
		Tx_Buffer[0] = 0x02;
		Tx_Buffer[1] = (byte) (tx_length>>8 & 0xff);
		Tx_Buffer[2] = (byte) (tx_length & 0xff);
		Tx_Buffer[3] = 0x32;
		Tx_Buffer[4] = 0x24;
		Tx_Buffer[5] = 0x00;
		Tx_Buffer[6] = 0x00;
//		Tx_Buffer[5] = (byte) 0xff;
//		Tx_Buffer[6] = (byte) 0xff;
		Tx_Buffer[7] = calcEcc();
		Tx_Buffer[8] = 0x03;
		process_cmd();
		if(rx_length > 0 && Rx_Buffer[3] == 0x00 && Rx_Buffer[4] == 0x00)
		{
			bell_control();
			return 0;
		}
		else return -1;
	}
	private static byte calcEcc() {
		// TODO Auto-generated method stub
		byte count,EccValue=0;
		for(count = 3; count < 3+tx_length; count ++)
		    EccValue ^= Tx_Buffer[count];
		return EccValue;
	}
	private static void process_cmd() {
		// TODO Auto-generated method stub
		int fd = HardwareControler.openSerialPort("/dev/s3c2410_serial2", 57600, 8, 1);
		HardwareControler.write(fd, Tx_Buffer);
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rx_length=HardwareControler.read(fd, Rx_Buffer, 100);
		HardwareControler.close(fd);
		Log.d(MainActivity.DEBUG_TAG,Integer.toString(rx_length));
	}
}
