package fei.deviceidentification.demo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import fei.deviceidentification.R;
import fei.deviceidentification.identification.BroadCastCommand;
import fei.deviceidentification.identification.BroadCastController;
import fei.deviceidentification.identification.DeviceInfo;
import fei.deviceidentification.identification.DeviceType;

public class BroadCastLibraryActivity extends Activity {
	/** Called when the activity is first created. */
	private TextView receiveMessage;
	private BroadCastController controller;
	private DeviceInfoNotifyGUI notify;
	private MulticastLock multicastLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		controller = new BroadCastController(DeviceType.SETTOPBOX);
		notify = new DeviceInfoNotifyGUI(this);
		BroadCastCommand.getInstance().setMacAddress(this.getMacAddress());

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		multicastLock = wifiManager.createMulticastLock("multicast.test");
		
		TextView textView = (TextView) findViewById(R.id.textview1);
		textView.setText(getMacAddress());

		TextView textView2 = (TextView) findViewById(R.id.textview2);
		textView2.setText(getLocalIPAddress());

		receiveMessage = (TextView) findViewById(R.id.textView_receivemessage);

		Button buttonstart = (Button) findViewById(R.id.button1);



		buttonstart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				multicastLock.acquire();
				controller.addNotify(notify);
				controller.connectNetwork();
			}
		});

		Button buttonstop = (Button) findViewById(R.id.Button2);
		buttonstop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				multicastLock.release();
				controller.disconnectNetwork();
			}
		});

		Button buttonclear = (Button) findViewById(R.id.button3);
		buttonclear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				receiveMessage.setText("");
			}
		});
		
		Button buttonrefresh = (Button) findViewById(R.id.button4);
		buttonrefresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				controller.refreshDevices();
			}
		});
		
	}

	public void updateGui(ArrayList<DeviceInfo> devices) {
		this.devices = devices;
		handler.post(this.doUpdateList);
	}

	private Handler handler = new Handler();
	private ArrayList<DeviceInfo> devices;
	private Runnable doUpdateList = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			receiveMessage.setText("");
			for (int i = 0; i < devices.size(); i++) {
				receiveMessage.append("Device -" + devices.get(i).name);
				receiveMessage.append("\n");;
			}
		}
	};

	
	private String getMacAddress() {
		String macAdd = "";
		try {
			WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String mac = info.getMacAddress();
			String[] macs = mac.split(":");
			for (int i = 0; i < macs.length; i++) {
				macAdd = macAdd + macs[i];
			}
		} catch (Exception e) {
			macAdd = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
		}
		return macAdd;
	}
	
	private String getLocalIPAddress() {
		try {
			for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
					.getNetworkInterfaces(); mEnumeration.hasMoreElements();) {
				NetworkInterface intf = mEnumeration.nextElement();
				for (Enumeration<InetAddress> enumIPAddr = intf
						.getInetAddresses(); enumIPAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIPAddr.nextElement();
					// 如果不是回环地址
					if (!inetAddress.isLoopbackAddress()) {
						// 直接返回本地IP地址
						if(inetAddress.getAddress().length == 4)
						{
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("", ex.toString());
		}
		return null;
	}
}