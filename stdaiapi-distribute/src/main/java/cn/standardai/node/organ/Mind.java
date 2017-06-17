package cn.standardai.node.organ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.standardai.node.action.Action;
import cn.standardai.node.action.Action.Verb;
import cn.standardai.node.action.SimpleAction;
import cn.standardai.node.exec.Man;

public class Mind extends Organ {

	public Mind(Man man) {
		super(man);
	}

	public void run() {
		while (true) {
			try {
				String command = new BufferedReader(new InputStreamReader(System.in)).readLine();
				//((Body)owner.get("body")).schedule(explain(command));
				((Body)owner.get("body")).schedule(command);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Action explain(String command) {
		String[] words = command.split(" ");
		if (words == null || words.length == 0) {
			return null;
		}
		switch (Action.parse(words[0])) {
		case establish:
			SimpleAction establish = new SimpleAction(Verb.establish, words[1]); 
			return establish;
		case connect:
			SimpleAction connect = new SimpleAction(Verb.connect, words[1]); 
			return connect;
		case send:
			SimpleAction send = new SimpleAction(Verb.send, words[1]); 
			return send;
		case calculate:
			SimpleAction calculate = new SimpleAction(Verb.send, words[1]); 
			return calculate;
		default:
			return null;
		}
	}
}
