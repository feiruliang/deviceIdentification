package fei.deviceidentification.identification;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Semaphore;

import android.util.Log;

//这部分的代码参考自: 
//http://blog.lujian.org/javabroadcast/

public class BroadCastReceiveHandle implements Runnable {

	private SocketReceive socketreceive;
	private final String TAG = "BroadCastReceiveHandle";
	private int port;
	private InetAddress group = null;
	private MulticastSocket socket = null;
	private Boolean isStop = false;
	private ReceiveDataHandle datahandle = new ReceiveDataHandle();
	private Semaphore semaphore_receive = new Semaphore(1);
	private Semaphore semaphore_parse = new Semaphore(0);
	//private ReentrantLock receiveLock = new ReentrantLock();
	//private Condition condition;

	public void stop() {
		this.isStop = true;
	}
	public Boolean isStop(){
		return isStop;
	}

	// Todo: 尚未考虑IP冲突问题。
	public BroadCastReceiveHandle(SocketReceive socketreceive) {
		try {
			this.socketreceive = socketreceive;
			String ip = BroadCastConfigInfo.getInstance().broadCastIp1;
			group = InetAddress.getByName(ip);
			port = BroadCastConfigInfo.getInstance().broadCastPort1;
			socket = new MulticastSocket(port);
			socket.setTimeToLive(1);
			socket.setLoopbackMode(false);
			socket.joinGroup(group);
		} catch (Exception e) {
			Log.e(TAG, "Create Socket Error.");
		}
	}

	public void run() {
		Thread thread = new Thread(null, datahandle, "ReceiveDataHandle");
		thread.start();
		try {
			Thread.sleep(50); // wait for thread start.
		} catch (Exception e) {
			Log.e(TAG, "sleep Error.");
			// thread.stop();
		}
		byte data[] = new byte[1024];
		DatagramPacket packet = null;
		packet = new DatagramPacket(data, data.length, group, port);
		while (!isStop) {
			try {
				socket.receive(packet);
				String message = new String(packet.getData(), 0,
						packet.getLength());
				semaphore_receive.acquire();
				datahandle.setReceiveData(message);
				semaphore_parse.release();
				Log.i(TAG, message);
			} catch (Exception e) {
				Log.e(TAG, "Error");
			}
		}
		try {
			this.socket.leaveGroup(group);
			this.socket.close();
			this.socket = null;
			this.group = null;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}finally{
			isStop = true;
		}
	}

	public class ReceiveDataHandle implements Runnable {
		private String mdata = "";

		public void setReceiveData(String data) {
			mdata = data;
		}

		@Override
		public void run() {
			try {
				DeviceInfo info = new DeviceInfo();
				while (!isStop) {
					semaphore_parse.acquire();
					if (BroadCastCommand.getInstance()
							.parseCommand(mdata, info) == CommandType.BROADCAST) {
						socketreceive.receiveACommand(info,CommandType.BROADCAST);
						Log.i(TAG, "receive a broadcast.");
					} else {
						Log.e(TAG, "receive a command is not broadcast or self.");
					}
					semaphore_receive.release();
				}
			} catch (Exception e) {
				Log.e(TAG, "Can not waite the condition.");
			}

		}
	}

}
