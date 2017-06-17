package cn.standardai.node.exec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cn.standardai.node.organ.Ear;
import cn.standardai.node.organ.Mouse;

public class Sentry implements Runnable {

	private Man owner;

	private Socket socket;

	public Sentry(Man owner, Socket client) {
		this.owner = owner;
		this.socket = client;
		new Thread(this).start();
	}

	public void run() {
		DataInputStream inStream = null;
		DataOutputStream outStream = null;
		try {
			inStream = new DataInputStream(socket.getInputStream());
			outStream = new DataOutputStream(socket.getOutputStream());

			new Mouse(owner, outStream);
			new Ear(owner, inStream);

			Thread.sleep(80000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
				if (inStream != null) {
					inStream.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
