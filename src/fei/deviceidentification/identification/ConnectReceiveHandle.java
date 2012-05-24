package fei.deviceidentification.identification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.util.Log;

public class ConnectReceiveHandle implements Runnable {

	private final String TAG = "ConnectAnswerReceiveHandle";
	private List<Socket> mListClient = new ArrayList<Socket>();
	private ServerSocket connectServerSocket;
	private ExecutorService mExecutorService = null;
	private Boolean isStop = false;
	private SocketReceive socketreceive;

	public ConnectReceiveHandle(SocketReceive socketreceive) {
		this.socketreceive = socketreceive;
	}

	public void stop() {
		this.isStop = true;
		try {
			connectServerSocket.close();
			connectServerSocket = null;
			mExecutorService = null;
			mListClient.clear();
		} catch (IOException ex) {
			Log.i(TAG, ex.toString());
		}
	}
	public Boolean isStop(){
		return isStop;
	}

	public void run() {
		try {
			connectServerSocket = new ServerSocket(
					BroadCastConfigInfo.getInstance().connectPort1);
			mExecutorService = Executors.newCachedThreadPool();
			Socket client = null;
			while (!isStop) {
				client = connectServerSocket.accept();
				mListClient.add(client);
				mExecutorService.execute(new ReceiveDataHandle(client));
			}
		} catch (IOException ex) {
			Log.i(TAG, ex.toString());
		}finally{
			isStop = true;
		}
	}

	public class ReceiveDataHandle implements Runnable {
		private Socket client = null;
		private BufferedReader in = null;

		ReceiveDataHandle(Socket socket) {
			client = socket;
			try {
				in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}

		@Override
		public void run() {
			try {
				String msg = "";
				Boolean isStop = false;
				while (!isStop) {
					if ((msg = in.readLine()) != null) {
						DeviceInfo info = new DeviceInfo();
						CommandType type = BroadCastCommand.getInstance()
								.parseCommand(msg, info);
						socketreceive.receiveACommand(info, type);
						//作为一种约定，用户发送设备消息的每一次TCP连接只接受一行数据，之后便各自断开。
						isStop = true;
						in.close();
						client.close();
					}
				}
			} catch (Exception ex) {
				Log.e(TAG, ex.toString());
			}finally{
				
			}

		}
	}
}
