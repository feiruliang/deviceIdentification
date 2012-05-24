package fei.deviceidentification.identification;

import android.util.Log;

public class SocketSend {
	private DeviceManage deviceManage;

	SocketSend(DeviceManage deviceManage) {
		this.deviceManage = deviceManage;
	}

	private BroadCastSendHandle bcHandle;
	private ConnectSendHandle coHandle;
	private Thread bcThread;
	private Thread coThread;

	public void startBroadCastSend() {
		if(bcHandle == null || (bcHandle != null && bcHandle.isStop())){
			bcHandle = new BroadCastSendHandle(deviceManage.getDeviceType(),
					deviceManage.getDeviceName());
			bcThread = new Thread(null, bcHandle, "BroadCastSendThread");
			bcThread.start();
		} else if (bcThread != null && bcThread.isAlive()) {
			this.deviceManage.startBroadCastSent();
			bcHandle.restartSend();
		}
	}

	public void startTCPSend(DeviceInfo info, CommandType type) {
		coHandle = new ConnectSendHandle(deviceManage.getDeviceType(),
				deviceManage.getDeviceName(), info.ip, info.connect_port, type);
		coThread = new Thread(null, coHandle, "ConnectSendThread");
		coThread.start();
	}

	public void stopBroadCastSend() {
		try {
			if (bcHandle != null) {
				bcHandle.stop();
			}
			bcHandle = null;
			bcThread = null;
		} catch (Exception e) {
			Log.e("SocketSend.stopBroadCastSend", e.toString());
		}
	}

	public void stopConnectAnswerSend() {
		try {
			coHandle = null;
			coThread = null;
		} catch (Exception e) {
			Log.e("SocketSend.stopConnectAnswerSend", e.toString());
		}
	}

	public void doDisConnectSend() {
		deviceManage.doDisConnect();
	}

}
