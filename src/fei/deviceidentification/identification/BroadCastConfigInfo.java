package fei.deviceidentification.identification;

public class BroadCastConfigInfo {

	public BroadCastConfigInfo() {
	}
	
	public static BroadCastConfigInfo instance = new BroadCastConfigInfo();
	
	public static BroadCastConfigInfo getInstance(){
		return instance;
	}

	// 这里ip和端口已1号配置为主，2号主要是用户1号被占用时的备用。
	public final String broadCastIp1 = "239.255.8.0";
	public final String broadCastIp2 = "239.234.9.0";

	public final int broadCastPort1 = 19991;
	public final int braodCastPort2 = 39991;

	public final int connectPort1 = 19992;
	public final int connectPort2 = 20002;
	public final int connectPort3 = 39992;

	public final int controlPort1 = 19993;
	public final int controlPort2 = 20003;
	public final int controlPort3 = 39993;
	public final int controlPort4 = 65003;
	
	// 心跳脉冲时间 默认为1秒。
	public final int pulsetime = 1000;
	public final int timeoutcount = 3;
	//public final int deadline = 3;
	
	
	//需要后期设定
	public String connectIp = "";
	public String controlIp = "";

}
