package fei.deviceidentification.identification;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


import android.util.Log;

public class ConnectSendHandle implements Runnable {

	private final String TAG = "ConnectSendHandle";
	private DeviceType mType = DeviceType.NONE;
	private String mName = "";
	private String mServerIp;
	private int mServerPort;
	private CommandType mCType = CommandType.NONE;
	private PrintWriter out = null;

	public ConnectSendHandle(DeviceType type, String name, String serverIp,
			int serverPort, CommandType cType) {
		mType = type;
		mName = name;
		mServerIp = serverIp;
		mServerPort = serverPort;
		mCType = cType;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Socket socket = new Socket(mServerIp, mServerPort);
			socket.setSoTimeout(6 * 1000);
			socket.setKeepAlive(true);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())), true);
			String command = "";
			switch (mCType) {
			case BROADCAST_ANSWER:
				command = BroadCastCommand.getInstance()
						.makeBroadCastAnswerCommand(mType, mName);
				break;
			case DISCONNECT:
				command = BroadCastCommand.getInstance().makeDisConnectCommand(
						mType, mName);
				break;
			case REQCONNECTENSURE:
				command = BroadCastCommand.getInstance()
						.makeRequestConnectEnsureCommmand(mType, mName);
				break;
			case CONNECTENSURE:
				command = BroadCastCommand.getInstance()
						.makeConnectEnsureCommmand(mType, mName);
				break;
			default:
				break;
			}
			out.println(command);
			out.close();
			if (socket.isOutputShutdown()) {
				socket.shutdownOutput();
				out = null;
			}
			socket.close();
			socket = null;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
}
