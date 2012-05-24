package fei.deviceidentification.identification;

public class BroadCastController {

	private DeviceManage deviceManage;
	private SocketReceive receive;
	private SocketSend send;
	private Boolean isStart = false;

	public BroadCastController(DeviceType type) {
		this.deviceManage = new DeviceManage(type);
		this.send = new SocketSend(this.deviceManage);
		this.deviceManage.setSocketSend(this.send);
		this.receive = new SocketReceive(this.deviceManage);
	}

	public void addNotify(DeviceInfoNotify notify)
	{
		deviceManage.addNotify(notify);
	}
	
	public void disconnectNetwork() {
		if (isStart) {
			send.doDisConnectSend();
			stopReceiveBroadCast();
			stopSendBroadCast();
			deviceManage.delNotify();
		}

	}

	public void connectNetwork() {
		if (!isStart) {
			startReceiveBroadCast();
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
			startSendBroadCast();
			isStart = true;
		}

	}

	public void refreshDevices() {
		if (isStart) {
			startReceiveBroadCast();
			startSendBroadCast();
			isStart = true;
		}
	}
	
	public void setDeviceName(String name){
		this.deviceManage.setDeviceName(name);
		this.refreshDevices();
	}

	private void startReceiveBroadCast() {
		if (this.receive == null) {
			this.receive = new SocketReceive(this.deviceManage);
		}
		this.receive.startBroadCastReceive();
		this.receive.startConnectReceive();
	}

	private void stopReceiveBroadCast() {
		if (receive != null) {
			receive.stopBroadCastReceive();
			receive.stopConnectReceive();
		}
	}

	private void startSendBroadCast() {
		if (this.send == null) {
			this.send = new SocketSend(this.deviceManage);
		}
		this.send.startBroadCastSend();
	}

	private void stopSendBroadCast() {
		if (send != null) {
			send.stopBroadCastSend();
		}
	}

}
