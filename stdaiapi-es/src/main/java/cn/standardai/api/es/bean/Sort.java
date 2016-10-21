package cn.standardai.api.es.bean;

import cn.standardai.api.es.bean.StatsAggVerb.StatsType;

/**
 * Aggregation Verb
 *
 */
public class Sort {

	private String field = "startTime";

	private StatsType sortMode = null;

	private boolean asc = true;

	public Sort(StatsType sortMode) {
		super();
		this.sortMode = sortMode;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public StatsType getSortMode() {
		return sortMode;
	}

	public void setSortMode(StatsType sortMode) {
		this.sortMode = sortMode;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}
}
