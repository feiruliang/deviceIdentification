package fei.deviceidentification.demo;

import java.util.ArrayList;

import fei.deviceidentification.identification.DeviceInfo;
import fei.deviceidentification.identification.DeviceInfoNotify;
import fei.deviceidentification.identification.DeviceManage;
import fei.deviceidentification.identification.DeviceType;



public class DeviceInfoNotifyGUI implements DeviceInfoNotify {

	public BroadCastLibraryActivity activity;

	public DeviceInfoNotifyGUI(BroadCastLibraryActivity activity) {
		this.activity = activity;
	}

	private DeviceManage mdeviceManage;

	@Override
	public void InitialNotify(DeviceManage deviceManage) {
		// TODO Auto-generated method stub
		mdeviceManage = deviceManage;
	}

	@Override
	public void UpdateDevices() {
		// TODO Auto-generated method stub
		refresh();
	}

	private void refresh() {
		ArrayList<DeviceInfo> devices = mdeviceManage
				.getDeviceNames(DeviceType.SETTOPBOX);
		activity.updateGui(devices);
	}
}
