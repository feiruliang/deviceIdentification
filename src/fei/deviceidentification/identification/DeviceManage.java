package fei.deviceidentification.identification;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Build;
import android.util.Log;

public class DeviceManage {

	private final String TAG = "DeviceManage";
	private DeviceType deviceType = DeviceType.NONE;
	private String name = "";
	private SocketSend send;
	/* 自身心跳定时器 */
	private Timer timer = new Timer();

	public DeviceManage(DeviceType type) {
		this.deviceType = type;
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < devices.size(); i++) {
					devices.get(i).receivePulse();
				}
			}
		}, BroadCastConfigInfo.getInstance().pulsetime,
				BroadCastConfigInfo.getInstance().pulsetime);
	}

	public void setSocketSend(SocketSend send) {
		this.send = send;
	}

	public String getDeviceName() {
		if (name == "") {
			name = Build.MODEL;
		}
		return name;
	}
	public DeviceType getDeviceType() {
		return this.deviceType;
	}

	public void setDeviceName(String name) {
		this.name = name;
	}

	// 原始设备列表
	private ArrayList<Device> devices = new ArrayList<Device>();

	// 根据目标设备的不同返回一个临时的设备列表。
	public ArrayList<DeviceInfo> getDeviceNames(DeviceType type) {
		ArrayList<DeviceInfo> names = new ArrayList<DeviceInfo>();
		//添加自己。
		DeviceInfo self = new DeviceInfo();
		self.type = this.deviceType;
		self.name = this.name;
		names.add(self);
		
		for (int i = 0; i < devices.size(); i++) {
			if (!devices.get(i).isAlive()) {
				continue;
			}
			if (type == DeviceType.NONE) {
				names.add(devices.get(i).minfo.clone());
				continue;
			}
			if (type == devices.get(i).minfo.type) {
				names.add(devices.get(i).minfo.clone());
			}
		}

		return names;
	}

	private void addNewDevice(DeviceInfo info) {
		Device device = new Device(send, this);
		device.minfo = info.clone();
		devices.add(device);
		// 第一次通过组播添加设备时，给设备发送一个组播消息。
		device.receiveACommand(info, CommandType.BROADCAST);
	}

	public void startBroadCastSent() {
		for (int i = 0; i < devices.size(); i++) {
			devices.get(i).startBroadCast();
		}
	}

	public void receiveACommand(DeviceInfo info, CommandType type) {
		Boolean isfound = false;
		for (int index = 0; index < devices.size(); index++) {
			if (devices.get(index).minfo.id.equals(info.id)) {
				devices.get(index).receiveACommand(info, type);
				isfound = true;
			}
		}
		if (!isfound) {
			Log.i(TAG, "found a new device.");
			this.addNewDevice(info);
		}
	}

	public void doDisConnect() {
		for (int index = 0; index < devices.size(); index++) {
			devices.get(index).doDisConnect();
		}
	}

	private ArrayList<DeviceInfoNotify> notifies = new ArrayList<DeviceInfoNotify>();

	public void addNotify(DeviceInfoNotify notify) {
		if (notify != null) {
			this.notifies.add(notify);
			notify.InitialNotify(this);
		}
	}

	public void delNotify() {
		this.notifies.clear();
	}

	public void notifyAllMonitor() {
		for (int j = 0; j < notifies.size(); j++) {
			DeviceInfoNotify notify = notifies.get(j);
			notify.UpdateDevices();
		}
	}

}
