package fei.deviceidentification.identification;

import android.util.Log;

public class SocketReceive {
	private DeviceManage deviceManage;

	SocketReceive(DeviceManage deviceManage) {
		this.deviceManage = deviceManage;
	}

	private BroadCastReceiveHandle bcHandle;
	private ConnectReceiveHandle coHandle;
	private Thread bcThread;
	private Thread coThread;
	//private Boolean isBroadcastStart = false;

	public void startBroadCastReceive() {
		if (bcHandle == null || (bcHandle != null && bcHandle.isStop())) {
			bcHandle = new BroadCastReceiveHandle(this);
			bcThread = new Thread(null, bcHandle, "BroadCastReceiveThread");
			bcThread.start();
		} 
	}

	public void startConnectReceive() {
		if (coHandle == null || (coHandle != null && coHandle.isStop())) {
			coHandle = new ConnectReceiveHandle(this);
			coThread = new Thread(null, coHandle, "ConnectReceiveThread");
			coThread.start();
		}
	}

	public void stopBroadCastReceive() {
		try {
			if (bcHandle != null) {
				bcHandle.stop();
			}
			bcHandle = null;
			bcThread = null;
		} catch (Exception e) {
			Log.e("SocketReceive.stopBroadCastReceive", e.toString());
		}
	}

	public void stopConnectReceive() {
		try {
			if (coHandle != null) {
				coHandle.stop();
			}
			coHandle = null;
			coThread = null;
		} catch (Exception e) {
			Log.e("SocketReceive.stopConnectReceive", e.toString());
		}
	}

	public void receiveACommand(DeviceInfo info, CommandType type) {
		deviceManage.receiveACommand(info, type);
	}

}
