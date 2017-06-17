package cn.standardai.api.node.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.distribute.verb.Dispatcher;

public class NodeAgent extends AuthAgent {

	public JSONObject exec(JSONObject request) {

		String expression = request.getString("expression");
		Dispatcher dispatcher = new Dispatcher();
		dispatcher.setExpression(expression);
		JSONObject result = new JSONObject();
		result.put("value", dispatcher.exec());
		return result;
	}
}
