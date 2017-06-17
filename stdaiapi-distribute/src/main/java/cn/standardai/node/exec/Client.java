package cn.standardai.node.exec;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import cn.standardai.node.organ.Ear;
import cn.standardai.node.organ.Mouse;

public class Client implements Runnable {

	public Client() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		DataInputStream inStream = null;
		DataOutputStream outStream = null;
		while (true) {
			System.out.println("Enter the ip:port of your server:");
			String str = null;
			try {
				str = new BufferedReader(new InputStreamReader(System.in)).readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] serverinfo = str.split(":");
			if (serverinfo == null || serverinfo.length != 2) {
				continue;
			}

			Socket socket = null;
			try {
				socket = new Socket(serverinfo[0], Integer.parseInt(serverinfo[1]));

				inStream = new DataInputStream(socket.getInputStream());
				outStream = new DataOutputStream(socket.getOutputStream());

				//new Mouse(outStream);
				//new Ear(inStream);

				Thread.sleep(70000);
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
}
