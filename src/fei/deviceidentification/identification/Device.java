package fei.deviceidentification.identification;

enum DeviceState {
	OFFLINE, // 为连接状态, 只有OFFLINE时设备为不可用，其他状态下设备都认为时可用的。
	CONNECTING, // 连接中
	ONLINE, // 已经连接
	TIMEOUT // 一段时间没有收到设备相应，未知设备状态。

	/***
	 * 设备经历 OFFLINE -》 ONLINE -》TIMEOUT -》 OFFLINE 的过程。
	 * 其中任何一个状态下，设备主动要求DISCONNECT时，直接进入OFFLINE。 CONNECTING
	 * 是一个中间状态，当设备主动发起连接确认时的状态。
	 * **/

}

public class Device {
	public DeviceInfo minfo;
	private SocketSend mSend;
	private DeviceManage mDeviceManage;
	private int timeoutCount = BroadCastConfigInfo.getInstance().timeoutcount;

	public Device(SocketSend send, DeviceManage deviceManage) {
		this.mSend = send;
		this.mDeviceManage = deviceManage;
	}

	private DeviceState state = DeviceState.OFFLINE;

	public Boolean isAlive() {
		if (this.state == DeviceState.OFFLINE) {
			return false;
		}
		return true;
	}

	public void startBroadCast() {
		if (this.state == DeviceState.TIMEOUT || this.state == DeviceState.ONLINE) {
			mSend.startTCPSend(minfo, CommandType.REQCONNECTENSURE);
			this.state = DeviceState.CONNECTING;
			timeoutCount = BroadCastConfigInfo.getInstance().timeoutcount;
		}
	}

	public void doDisConnect() {
		if (this.state != DeviceState.OFFLINE) {
			mSend.startTCPSend(minfo, CommandType.DISCONNECT);
		}
	}

	// 接受心跳。
	public void receivePulse() {
		if (timeoutCount < 0) {
			switch (this.state) {
			case ONLINE: {
				this.state = DeviceState.TIMEOUT;
				break;
			}
			case CONNECTING: {
				this.state = DeviceState.OFFLINE;
				this.mDeviceManage.notifyAllMonitor();
			}
			default:
				break;
			}
		} else {
			timeoutCount--;
		}

	}

	// 接受到一个命令后进行应答处理
	public void receiveACommand(DeviceInfo info, CommandType type) {
		switch (type) {
		case BROADCAST: {
			this.state = DeviceState.ONLINE;
			this.mSend.startTCPSend(info, CommandType.BROADCAST_ANSWER);
			this.mDeviceManage.notifyAllMonitor();
			break;
		}
		case BROADCAST_ANSWER: {
			this.receiveAvailable(info);
			break;
		}
		case DISCONNECT: {
			this.receiveADisConnect(info);
			break;
		}
		case REQCONNECTENSURE: {
			this.receiveARequestEnsureConnect(info);
			break;
		}
		case CONNECTENSURE: {
			this.receiveAvailable(info);
			break;
		}
		case NONE: {
			break;
		}
		default:
			break;
		}
		// 设备信息是否有更新。
		updateDeviceInfo(info);
		timeoutCount = BroadCastConfigInfo.getInstance().timeoutcount;
	}

	private void receiveAvailable(DeviceInfo info) {
		switch (this.state) {
		case OFFLINE: {
			this.mSend.startTCPSend(info, CommandType.BROADCAST_ANSWER);
			this.state = DeviceState.ONLINE;
			this.mDeviceManage.notifyAllMonitor();
			break;
		}
		default:
			this.state = DeviceState.ONLINE;
		}
	}

	private void receiveADisConnect(DeviceInfo info) {
		this.state = DeviceState.OFFLINE;
		this.mDeviceManage.notifyAllMonitor();
	}

	private void receiveARequestEnsureConnect(DeviceInfo info) {
		receiveAvailable(info);
		this.mSend.startTCPSend(info, CommandType.CONNECTENSURE);
	}

	private void updateDeviceInfo(DeviceInfo info) {
		if (!minfo.ip.equals(info.ip) || !minfo.name.equals(info.name)) {
			minfo.ip = info.ip;
			minfo.name = info.name;
			this.mDeviceManage.notifyAllMonitor();
		}
	}
}
