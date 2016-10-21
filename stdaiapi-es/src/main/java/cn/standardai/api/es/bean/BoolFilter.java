package cn.standardai.api.es.bean;

/**
 * BoolFilter
 *
 */
public class BoolFilter extends Filter {

	private Filter[] filters;

	private boolean must;

	public BoolFilter(Filter[] filters) {
		super(FilterType.bool);
		this.setFilters(filters);
	}

	public BoolFilter(String field, Filter[] filters) {
		super(FilterType.bool, field);
		this.setFilters(filters);
	}

	public Filter[] getFilters() {
		return filters;
	}

	public void setFilters(Filter[] filters) {
		this.filters = filters;
	}

	public boolean isMust() {
		return must;
	}

	public void setMust(boolean must) {
		this.must = must;
	}
}
