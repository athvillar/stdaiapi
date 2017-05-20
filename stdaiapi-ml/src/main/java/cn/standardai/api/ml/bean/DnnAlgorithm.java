package cn.standardai.api.ml.bean;

import java.util.HashMap;
import java.util.Map;

public enum DnnAlgorithm {

	cnn("cnn"), lstm("lstm");

	String algorithm;

	private DnnAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	private static final Map<String, DnnAlgorithm> mappings = new HashMap<String, DnnAlgorithm>();

	static {
		for (DnnAlgorithm algorithm : values()) {
			mappings.put(algorithm.algorithm, algorithm);
		}
	}

	public static DnnAlgorithm resolve(String algorithm) {
		return (algorithm != null ? mappings.get(algorithm) : null);
	}
}