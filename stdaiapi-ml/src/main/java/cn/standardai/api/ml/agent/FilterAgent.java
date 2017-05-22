package cn.standardai.api.ml.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.exception.AuthException;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.api.ml.filter.FilterType;

public class FilterAgent extends AuthAgent {

	public JSONObject list() {
		JSONObject result = new JSONObject();
		JSONArray filtersJ = new JSONArray();
		for (int i = 0; i < FilterType.values().length; i++) {
			JSONObject filterJ = new JSONObject();
			filterJ.put("filterName", FilterType.values()[i].clsName);
			filterJ.put("description", FilterType.getDescription(FilterType.values()[i].clsName));
			filtersJ.add(filterJ);
		}
		result.put("filters", filtersJ);
		return result;
	}

	public JSONObject view(String filter) throws MLException, AuthException {
		String desc = FilterType.getDescription(filter);
		if (desc == null) throw new MLException("filter不存在");
		JSONObject result = new JSONObject();
		result.put("filterName", filter);
		result.put("description", desc);
		return result;
	}
}
