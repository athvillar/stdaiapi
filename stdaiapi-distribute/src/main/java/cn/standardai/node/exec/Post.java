package cn.standardai.node.exec;

import java.net.ServerSocket;
import java.net.Socket;

import cn.standardai.node.organ.Organ;

public class Post extends Organ implements Runnable {

	private int port;

	public Post(Man owner, int port) {
		super(owner);
		this.port = port;
		new Thread(this).start();
	}

	@SuppressWarnings("resource")
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket client = serverSocket.accept();
				new Sentry(owner, client);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
