package cn.standardai.api.ash.resource.base;

import java.util.HashMap;
import java.util.Map;

import cn.standardai.api.ash.exception.AshException;
import cn.standardai.api.ash.resource.ResData;
import cn.standardai.api.ash.resource.ResDic;
import cn.standardai.api.ash.resource.ResDoc;
import cn.standardai.api.ash.resource.ResFile;
import cn.standardai.api.ash.resource.ResModel;
import cn.standardai.api.ash.resource.ResNode;
import cn.standardai.api.ash.resource.ResUser;

public abstract class AshResource {

	public enum Resource {

		data("data", ResData.class),
		dic("dic", ResDic.class),
		doc("doc", ResDoc.class),
		file("file", ResFile.class),
		model("model", ResModel.class),
		node("node", ResNode.class),
		user("user", ResUser.class);

		public String resource;

		public Class<? extends AshResource> cls;

		private Resource(String resource, Class<? extends AshResource> cls) {
			this.resource = resource;
			this.cls = cls;
		}

		private static final Map<String, Class<? extends AshResource>> mappings = new HashMap<String, Class<? extends AshResource>>();

		static {
			for (Resource value : values()) {
				mappings.put(value.resource, value.cls);
			}
		}

		public static Class<? extends AshResource> resolve(String resource) {
			return (resource != null ? mappings.get(resource) : null);
		}
	}

	public abstract String help();

	public static AshResource getInstance(String resourceString) throws AshException {
		Class<? extends AshResource> cls = Resource.resolve(resourceString);
		if (cls == null) return null;
		try {
			return cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new AshException("创建资源错误");
		}
	}
}
