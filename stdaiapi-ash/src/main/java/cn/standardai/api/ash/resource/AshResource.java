package cn.standardai.api.ash.resource;

import java.util.HashMap;
import java.util.Map;

public abstract class AshResource {

	public enum Resource {

		model("model");

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

	public abstract String help();

	public static AshResource getInstance(String resourceString) {
		Resource resource = Resource.resolve(resourceString);
		if (resource == null) return null;
		switch (resource) {
		case model:
			return new AshModel();
		}
		return null;
	}
}
