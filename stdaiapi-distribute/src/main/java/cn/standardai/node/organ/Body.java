package cn.standardai.node.organ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cn.standardai.node.action.Action;
import cn.standardai.node.action.SimpleAction;
import cn.standardai.node.exec.Client;
import cn.standardai.node.exec.Man;
import cn.standardai.node.exec.Post;

public class Body extends Organ {

	private BlockingQueue<Action> actQueue;

	private BlockingQueue<String> cmdQueue;

	public Body(Man owner) {
		super(owner);
		cmdQueue = new LinkedBlockingQueue<String>();
		actQueue = new LinkedBlockingQueue<Action>();
	}

	@Override
	public void run() {
		while (true) {
			try {
				dothe(cmdQueue.take());
				//Action action = cmdQueue.take();
				//dothe(action);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void schedule(String command) {
		cmdQueue.add(command);
	}

	public void schedule(Action action) {
		actQueue.add(action);
	}

	private void dothe(String command) {
		String[] words = command.split(" ");
		if (words == null || words.length <= 1) {
			return;
		}
		switch (words[0]) {
		case "establish":
			new Post(owner, Integer.parseInt(words[1]));
			break;
		case "connect":
			break;
		case "send":
			break;
		case "calculate":
			break;
		default:
			break;
		}
	}

	private void dothe(Action action) {
		switch (action.getVerb()) {
		case establish:
			new Post(owner, Integer.parseInt(((SimpleAction)action).getTarget()));
			break;
		case connect:
			break;
		case send:
			break;
		case calculate:
			break;
		default:
			break;
		}
	}
}
