package cn.standardai.api.distribute.verb;

import java.util.Map;

import cn.standardai.api.distribute.noun.Expression;
import cn.standardai.api.distribute.noun.Node;
import cn.standardai.api.distribute.noun.NodeStatus;
import cn.standardai.api.distribute.noun.Task;

public class Dispatcher {

	private Map<Node, NodeStatus> availableNodes;

	private Map<Node, Task> nodeChain;

	private Task task;

	public void dispatch() {
		if (task != null) {
			Expression[] expressions = task.split();
			for (int i = 0; i < expressions.length; i++) {
				Node node1 = findNode();
				node1.mount(expressions[i]);
				node1.run();
			}
		}
	}

	private Node findNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setExpression(String expression) {
		// TODO Auto-generated method stub
		
	}

	public Object exec() {
		// TODO Auto-generated method stub
		return null;
	}
}
