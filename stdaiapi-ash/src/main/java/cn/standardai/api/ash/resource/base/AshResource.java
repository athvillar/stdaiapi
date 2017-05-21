package cn.standardai.api.ash.resource.base;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.ash.bean.AshCommandParams;
import cn.standardai.api.ash.bean.AshReply;
import cn.standardai.api.ash.command.AshLs;
import cn.standardai.api.ash.command.AshMk;
import cn.standardai.api.ash.command.AshRm;
import cn.standardai.api.ash.command.base.AshResourceRelatedCommand;
import cn.standardai.api.ash.command.base.AshCommand;
import cn.standardai.api.ash.command.base.AshCommand.HttpMethod;
import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.exception.HttpException;
import cn.standardai.api.ash.resource.ResModel;
import cn.standardai.api.ash.resource.ResUser;

public abstract class AshResource {

	protected String userId;

	private String token;

	protected AshCommandParams params;

	protected AshReply reply;

	public enum Resource {

		model("model"), user("user"), dataset("dataset");

		String resource;

		private Resource(String resource) {
			this.resource = resource;
		}

		private static final Map<String, Resource> mappings = new HashMap<String, Resource>();

		static {
			for (Resource resource : values()) {
				mappings.put(resource.resource, resource);
			}
		}

		public static Resource resolve(String resource) {
			return (resource != null ? mappings.get(resource) : null);
		}
	}

	public AshResource() {
		this.reply = new AshReply();
	}

	public abstract void mk() throws AshException;

	public abstract void ls() throws AshException;

	public abstract void rm() throws AshException;

	public abstract void parseParam(AshCommandParams params);

	public abstract String[] getMkSteps();

	public abstract AshReply help();

	public static AshResource getInstance(String resourceString) {
		Resource resource = Resource.resolve(resourceString);
		return getInstance(resource);
	}

	public static AshResource getInstance(Resource resource) {
		if (resource == null) return null;
		switch (resource) {
		case model:
			return new ResModel();
		case dataset:
			return new ResModel();
		case user:
			return new ResUser();
		}
		return null;
	}

	public AshReply invoke(Class<? extends AshResourceRelatedCommand> cls, AshCommandParams params, String userId, String token) throws AshException {
		parseParam(params);
		this.userId = userId;
		this.token = token;
		this.params = params;
		if (cls == AshMk.class) {
			this.mk();
		} else if (cls == AshLs.class) {
			this.ls();
		} else if (cls == AshRm.class) {
			this.rm();
		}
		return this.reply;
	}

	public JSONObject http(HttpMethod method, String url, Map<String, String> params, JSONObject body) throws HttpException {
		return AshCommand.http(method, url, params, body, this.token);
	}
}
