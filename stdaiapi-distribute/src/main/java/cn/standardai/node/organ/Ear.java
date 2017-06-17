package cn.standardai.node.organ;

import java.io.DataInputStream;
import java.io.IOException;

import cn.standardai.node.exec.Man;

public class Ear extends Organ implements Runnable {

	private DataInputStream inStream;

	public Ear(Man owner, DataInputStream inStream) {
		super(owner);
		this.inStream = inStream;
		new Thread(this).start();
	}

	public void run() {
		while (true) {
			try {
				String remotestr = inStream.readUTF();
				System.out.println("Remote says: " + remotestr);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
