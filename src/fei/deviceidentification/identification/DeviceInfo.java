package fei.deviceidentification.identification;



// 设备类型:设备标示符:设备名称:设备IP地址:提供服务的端口:应答端口
public class DeviceInfo {
	public DeviceType type;
	public String id;
	public String name;
	public String ip;
	public int control_port;
	public int connect_port;
	
	public DeviceInfo()
	{
		type = DeviceType.NONE;
		id = "";
		name = "";
		ip = "";
		control_port = 0;
		connect_port = 0;
	}
	
	public DeviceInfo clone()
	{
		DeviceInfo info = new DeviceInfo();
		info.type = this.type;
		info.id = this.id;
		info.name = this.name;
		info.ip = this.ip;
		info.control_port = this.control_port;
		info.connect_port = this.connect_port;
		
		return info;
	}
}