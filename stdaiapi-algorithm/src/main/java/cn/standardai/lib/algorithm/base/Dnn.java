package cn.standardai.lib.algorithm.base;

import java.util.HashMap;
import java.util.Map;

import cn.standardai.lib.algorithm.exception.UsageException;

public abstract class Dnn implements Monitorable {

	public final String lock = "lock";

	public Map<String, Map<Integer, Double>> indicator = new HashMap<String, Map<Integer, Double>>();

	public void addIndicator(String catalog) {
		this.indicator.put(catalog, new HashMap<Integer, Double>());
	}

	public boolean containCatalog(String catalog) {
		return this.indicator.containsKey(catalog);
	}

	public void finish() throws UsageException {
		// TODO nothing added, reserved, now just for finish indicator
		this.indicator.put("final", new HashMap<Integer, Double>());
	}

	public void record(String catalog, Integer epoch, Double value) throws UsageException {
		Map<Integer, Double> catalogMap = this.indicator.get(catalog);
		if (catalogMap == null) throw new UsageException("无此监控项目(" + catalog + ")");
		catalogMap.put(epoch, value);
	}

	public Map<Integer, Double> getValues(String catalog) throws UsageException {
		Map<Integer, Double> catalogMap = this.indicator.get(catalog);
		if (catalogMap == null) throw new UsageException("无此监控项目(" + catalog + ")");
		return catalogMap;
	}

	public Double getValue(String catalog, Integer epoch) throws UsageException {
		Map<Integer, Double> catalogMap = this.indicator.get(catalog);
		if (catalogMap == null) throw new UsageException("无此监控项目(" + catalog + ")");
		return catalogMap.get(epoch);
	}
}
