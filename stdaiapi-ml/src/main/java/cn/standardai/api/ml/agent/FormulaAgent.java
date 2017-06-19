package cn.standardai.api.ml.agent;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.api.ml.filter.FilterType;
import cn.standardai.tool.literalImage.LiteralUtil;
import cn.standardai.tool.literalImage.Word;

public class FormulaAgent extends AuthAgent {

	public Integer[][] parse(JSONObject request) {
		// TODO
		return null;
	}

	public JSONObject check(Integer[][] gray, String modelName) {

		List<List<Word>> words = LiteralUtil.cut(gray, 0.0, 0.0);
		return null;
	}
}
