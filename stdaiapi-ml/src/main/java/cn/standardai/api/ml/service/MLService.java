package cn.standardai.api.ml.service;

import org.springframework.http.HttpHeaders;

import cn.standardai.api.core.base.BaseService;
import cn.standardai.api.ml.agent.TokenAgent;

public class MLService extends BaseService {

	public String getUserIdByToken(HttpHeaders headers) throws Exception {

		TokenAgent tokenAgent = new TokenAgent();
		try {
			return tokenAgent.getUserIdByToken(headers.get("token"));
		} catch (Exception e) {
			throw e;
		} finally {
			if (tokenAgent != null) tokenAgent.done();
		}
	}
}
