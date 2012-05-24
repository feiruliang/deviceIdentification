package fei.deviceidentification.identification;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

public class BroadCastCommand {
	private final String TAG = "BroadCastCommand";
	private final String DEVICETYPE_SETTOPBOX = "T";
	private final String DEVICETYPE_STBCONTROLLER = "C";
	private final String BROADCAST = "B";
	private final String BROADCAST_ANSWER = "C";
	private final String DISCONNECT = "D";
	private final String REQCONNECTENSURE = "Q";
	private final String CONNECTENSURE = "E";
	private String macAddress = "";

	// 避免反复的生成命令。
	private String mPreProcessCommand = "";
	private Boolean isChanged = true;

	public void setMacAddress(String mac) {
		this.macAddress = mac;
	}

	public BroadCastCommand() {
		isChanged = true;
		// Todo: 还没有考虑IP变化和设备名称
	}

	private static BroadCastCommand instance = new BroadCastCommand();

	public static BroadCastCommand getInstance() {
		return instance;
	}

	public String makeRequestConnectEnsureCommmand(DeviceType type, String name) {
		String command = "";
		command = command + REQCONNECTENSURE;
		command = command + ":" + makeCommonData(type, name);
		return command;
	}

	public String makeConnectEnsureCommmand(DeviceType type, String name) {
		String command = "";
		command = command + CONNECTENSURE;
		command = command + ":" + makeCommonData(type, name);
		return command;
	}

	public String makeBroadCastCommand(DeviceType type, String name) {
		// B:设备类型:设备标示符:设备名称:设备IP地址:提供服务的端口:应答端口。
		// http://172.30.4.3:8090/pages/viewpage.action?pageId=2228643
		String command = "";
		command = command + BROADCAST;
		command = command + ":" + makeCommonData(type, name);
		return command;
	}

	public String makeBroadCastAnswerCommand(DeviceType type, String name) {
		// C:设备类型:设备标示符:设备名称:设备IP地址:提供服务的端口:应答端口。
		String command = "";
		command = command + BROADCAST_ANSWER;
		command = command + ":" + makeCommonData(type, name);
		return command;
	}

	public String makeDisConnectCommand(DeviceType type, String name) {
		// D:设备类型:设备标示符:设备名称:设备IP地址:提供服务的端口:应答端口。
		String command = "";
		command = command + DISCONNECT;
		command = command + ":" + makeCommonData(type, name);
		return command;
	}

	private String makeCommonData(DeviceType type, String name) {
		if (isChanged) {
			String command = "";
			if (type == DeviceType.SETTOPBOX) {
				command = command + DEVICETYPE_SETTOPBOX;
			} else {
				command = command + DEVICETYPE_STBCONTROLLER;
			}
			command = command + ":" + getMacAddress();
			command = command + ":" + name;
			command = command + ":" + getLocalIPAddress();
			command = command
					+ ":"
					+ String.valueOf(BroadCastConfigInfo.getInstance().controlPort1);
			command = command
					+ ":"
					+ String.valueOf(BroadCastConfigInfo.getInstance().connectPort1);
			mPreProcessCommand = command;
			return command;
		} else {
			return mPreProcessCommand;
		}
	}

	public CommandType parseCommand(String command, DeviceInfo info) {
		if (command == null || command == "" || info == null)
			return CommandType.NONE;
		String[] fields = command.split(":");
		if (fields.length == 7) {
			if (fields[1].equals(DEVICETYPE_SETTOPBOX)) {
				info.type = DeviceType.SETTOPBOX;
			} else if (fields[1].equals(DEVICETYPE_STBCONTROLLER)) {
				info.type = DeviceType.STBCONTROLLER;
			} else {
				Log.e(TAG, "Device Type can not find.");
				return CommandType.NONE;
			}
			if (fields[2].equals(this.macAddress)) {
				Log.i(TAG, "This is self.");
				return CommandType.NONE;
			}
			info.id = fields[2];
			info.name = fields[3];
			info.ip = fields[4];
			try {
				info.control_port = Integer.parseInt(fields[5]);
			} catch (Exception e) {
				Log.e(TAG, "parse server port error.");
				info.control_port = 0;
			}
			try {
				info.connect_port = Integer.parseInt(fields[6]);
			} catch (Exception e) {
				Log.e(TAG, "parse server port error.");
				info.connect_port = 0;
			}
			if (fields[0].equals("B")) {
				return CommandType.BROADCAST;
			} else if (fields[0].equals("C")) {
				return CommandType.BROADCAST_ANSWER;
			} else if (fields[0].equals("D")) {
				return CommandType.DISCONNECT;
			} else if (fields[0].equals("Q")) {
				return CommandType.REQCONNECTENSURE;
			} else if (fields[0].equals("E")) {
				return CommandType.CONNECTENSURE;
			} else {
				return CommandType.NONE;
			}
		} else {
			Log.e(TAG, "this is not a command.");
			return CommandType.NONE;
		}
	}

	// 获取本地IP函数
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
						if (inetAddress.getAddress().length == 4) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}
		return null;
	}

	private String getMacAddress() {
		/*
		 * WifiManager wifi = (WifiManager)
		 * getSystemService(Context.WIFI_SERVICE); WifiInfo info =
		 * wifi.getConnectionInfo(); String mac = info.getMacAddress(); String[]
		 * macs = mac.split(":"); String macAdd = ""; for (int i = 0; i <
		 * macs.length; i++) { macAdd = macAdd + macs[i]; } return macAdd;
		 */
		return this.macAddress;
	}

}
