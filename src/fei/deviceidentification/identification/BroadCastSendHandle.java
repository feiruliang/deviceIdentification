package fei.deviceidentification.identification;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Semaphore;

import android.util.Log;

//这部分的代码参考自: 
//http://blog.lujian.org/javabroadcast/

public class BroadCastSendHandle implements Runnable {

	private final String TAG = "BroadCastSendHandle";
	private InetAddress group = null;
	private MulticastSocket socket = null;
	private Boolean isStop = false;
	private DeviceType type = DeviceType.NONE;
	private String name = "";
	private Semaphore start_sem = new Semaphore(0);

	public void stop() {
		this.isStop = true;
		start_sem.release();
	}
	
	public Boolean isStop(){
		if(isStop)
			return true;
		return false;
	}

	public void restartSend() {
		this.start_sem.release();
	}

	public BroadCastSendHandle(DeviceType type, String name) {
		try {
			this.type = type;
			this.name = name;
			group = InetAddress
					.getByName(BroadCastConfigInfo.getInstance().broadCastIp1);
			socket = new MulticastSocket(
					BroadCastConfigInfo.getInstance().broadCastPort1);
			// Set TTL to 255;
			socket.setTimeToLive(1);
			socket.setLoopbackMode(false);
			socket.joinGroup(group);
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	@Override
	public void run() {
		int count = 0;
		while (!isStop) {
			try {
				DatagramPacket packet = null;
				String command = BroadCastCommand.getInstance()
						.makeBroadCastCommand(type, name);
				byte data[] = command.getBytes();
				packet = new DatagramPacket(data, data.length, group,
						BroadCastConfigInfo.getInstance().broadCastPort1);
				Log.i("BroadCastSendHandle", new String(data));
				socket.send(packet);
				count++;
				if (count >= (BroadCastConfigInfo.getInstance().timeoutcount)) {
					count = 0;
					start_sem.acquire();
				} else {
					Thread.sleep(BroadCastConfigInfo.getInstance().pulsetime);
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				break;
			}
		}
		try {
			socket.leaveGroup(group);
			socket.close();
			group = null;
			socket = null;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}finally{
			isStop = true;
		}
	}

}
