package cn.standardai.api.es.bean;

/**
 * Aggregation Verb for date histogram
 *
 */
public class DatehistogramAggVerb extends AggVerb {

	private String format;

	private String interval;

	private long min;

	private long max;

	public DatehistogramAggVerb(AggType aggType, String field, String format, String interval) {
		super(aggType, field);
		this.format = format;
		this.interval = interval;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}
}
