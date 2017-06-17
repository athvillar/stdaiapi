package cn.standardai.node.organ;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.standardai.node.exec.Man;

public class Mouse extends Organ implements Runnable {

	private DataOutputStream outStream;

	public Mouse(Man owner, DataOutputStream outStream) {
		super(owner);
		this.outStream = outStream;
		new Thread(this).start();
	}

	@Override
	public void run() {
		String word = null;
		while (true) {
			try {
				word = new BufferedReader(new InputStreamReader(System.in)).readLine();
				outStream.writeUTF(word);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
