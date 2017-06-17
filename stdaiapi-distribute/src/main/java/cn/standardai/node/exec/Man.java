package cn.standardai.node.exec;

import java.util.HashMap;
import java.util.Map;

import cn.standardai.node.organ.Body;
import cn.standardai.node.organ.Mind;
import cn.standardai.node.organ.Organ;

public class Man implements Runnable {

	private Map<String, Organ> organs;

	public Man() {
		this.organs = new HashMap<String, Organ>();
		this.organs.put("mind", new Mind(this));
		this.organs.put("body", new Body(this));
		new Thread(this).start();
	}

	public void run() {
		call("mind");
	}

	public void call(String organ) {
		organs.get(organ).run();
	}

	public Organ get(String organName) {
		return this.organs.get(organName);
	}
}
