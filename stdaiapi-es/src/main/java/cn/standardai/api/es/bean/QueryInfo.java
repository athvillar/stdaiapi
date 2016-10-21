package cn.standardai.api.es.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.es.bean.AggVerb;
import cn.standardai.api.es.bean.BoolFilter;
import cn.standardai.api.es.bean.DatehistogramAggVerb;
import cn.standardai.api.es.bean.Filter;
import cn.standardai.api.es.bean.Range;
import cn.standardai.api.es.bean.RangeFilter;
import cn.standardai.api.es.bean.StatsAggVerb;
import cn.standardai.api.es.bean.TermAggVerb;
import cn.standardai.api.es.bean.TermFilter;
import cn.standardai.api.es.bean.AggVerb.AggType;
import cn.standardai.api.es.bean.StatsAggVerb.StatsType;
import cn.standardai.api.es.exception.ESException;

public class QueryInfo {

	private String datapointBase;

	private String datapointId;

	private String[] datapointIds;

	private String parkId;

	private String[] parkIds;

	private String time;

	private Date startTime;

	private Date endTime;

	private char operator;

	private String[] methods;

	private String param;

	private String[] secondaryMethods;

	private String[] aggr;

	private List<String> aggrKeys;

	private boolean realtime;

	public QueryInfo(QueryInfo queryInfo) {
		super();
		this.datapointBase = queryInfo.getDatapointBase();
		this.datapointId = queryInfo.getDatapointId();
		this.datapointIds = queryInfo.getDatapointIds();
		this.parkId = queryInfo.getParkId();
		this.parkIds = queryInfo.getParkIds();
		this.time = queryInfo.getTime();
		this.startTime = queryInfo.getStartTime();
		this.endTime = queryInfo.getEndTime();
		this.operator = queryInfo.getOperator();
		this.methods = queryInfo.getMethods();
		this.param = queryInfo.getParam();
		this.secondaryMethods = queryInfo.getSecondaryMethods();
		this.aggr = queryInfo.getAggr();
		this.aggrKeys = queryInfo.getAggrKeys();
		this.realtime = queryInfo.isRealtime();
	}

	public QueryInfo() {
		super();
	}

	public String[] getDatapointIds() {
		return datapointIds;
	}

	public void setDatapointIds(String[] datapointIds) {
		this.datapointIds = datapointIds;
	}

	public String[] getParkIds() {
		return parkIds;
	}

	public void setParkIds(String[] parkIds) {
		this.parkIds = parkIds;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String[] getMethods() {
		return methods;
	}

	public void setMethods(String[] methods) {
		this.methods = methods;
	}

	public String[] getAggr() {
		return aggr;
	}

	public void setAggr(String[] aggr) {
		this.aggr = aggr;
	}

	public char getOperator() {
		return operator;
	}

	public void setOperator(char operator) {
		this.operator = operator;
	}

	public List<String> getAggrKeys() {
		return aggrKeys;
	}

	public void setAggrKeys(List<String> aggrKeys) {
		this.aggrKeys = aggrKeys;
	}

	public boolean isRealtime() {
		return realtime;
	}

	public void setRealtime(boolean realtime) {
		this.realtime = realtime;
	}

	public String getDatapointId() {
		return datapointId;
	}

	public void setDatapointId(String datapointId) {
		this.datapointId = datapointId;
	}

	public String getDatapointBase() {
		return datapointBase;
	}

	public void setDatapointBase(String datapointBase) {
		this.datapointBase = datapointBase;
	}

	public String getParkId() {
		return parkId;
	}

	public void setParkId(String parkId) {
		this.parkId = parkId;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public static QueryInfo parse(JSONObject jsonObject) throws ESException {

		QueryInfo queryInfo = new QueryInfo();

		// datapoints
		String datapoints = jsonObject.getString("datapointId");
		if (datapoints == null) throw new ESException("Missing parameter(datapointId)");

		if (datapoints.length() == 4) {
			queryInfo.setDatapointBase(datapoints);
		} else if (datapoints.length() == 5) {
			queryInfo.setDatapointBase(datapoints.substring(0, 4));
			queryInfo.setDatapointId(datapoints);
		} else {
			throw new ESException("Wrong parameter datapointId(" + datapoints + ")");
		}

		// parks
		String parks = jsonObject.getString("parkId");
		//if (parks == null) throw new ESException("Missing parameter(parkId)");
		if (parks != null) {
			queryInfo.setParkId(parks);
			if (parks.indexOf(',') != -1) {
				queryInfo.setParkIds(parks.split(","));
			} else {
				queryInfo.setParkIds(new String[] { parks });
			}
		}

		// time
		String time = jsonObject.getString("time");
		if (time != null) queryInfo.setTime(time);
		String startTime = jsonObject.getString("startTime");
		if (startTime != null) queryInfo.setStartTime(DateUtil.parse(startTime, DateUtil.YYYY__MM__DD__HH__MM__SS));
		String endTime = jsonObject.getString("endTime");
		if (endTime != null) queryInfo.setEndTime(DateUtil.parse(endTime, DateUtil.YYYY__MM__DD__HH__MM__SS));

		// parameter for method
		String param = jsonObject.getString("param");
		String aggr = jsonObject.getString("aggr");
		String secondaryMethod = jsonObject.getString("secondaryMethod");
		String method = jsonObject.getString("method");
		if (param != null) {
			String[] params = param.split(":");
			if (params != null) {
				// 求同期日，应将method设为sum，按照日期聚类，然后对合计取secondary method
				queryInfo.setParam(jsonObject.getString("param"));
				if (aggr == null) {
					aggr = "startTime";
				} else {
					aggr += ",startTime";
					aggr = aggr.replaceAll("startTime,", "");
				}
				if (secondaryMethod == null) {
					secondaryMethod = method;
					method = "sum";
				}
			}
		}

		// method
		if (method != null) {
			if (method.indexOf(',') != -1) {
				queryInfo.setMethods(method.split(","));
			} else {
				queryInfo.setMethods(new String[] { method });
			}
		}

		// aggregation
		if (aggr != null) {
			if (aggr.indexOf(',') != -1) {
				queryInfo.setAggr(aggr.split(","));
			} else {
				queryInfo.setAggr(new String[] { aggr });
			}
		}

		// Secondary method
		if (secondaryMethod != null) {
			if (secondaryMethod.indexOf(',') != -1) {
				queryInfo.setSecondaryMethods(secondaryMethod.split(","));
			} else {
				queryInfo.setSecondaryMethods(new String[] { secondaryMethod });
			}
		}

		// realtime
		queryInfo.setRealtime(datapoints.length() == 4);

		return queryInfo;
	}

	public static List<Filter> makeFilters(QueryInfo queryInfo) throws ESException {

		// Make filters
		List<Filter> filters = new ArrayList<Filter>();

		// time range
		if (queryInfo.getStartTime() != null || queryInfo.getEndTime() != null) {
			Filter[] subfilters = new Filter[2];
			Range<Object> dateRange = new Range<Object>(queryInfo.getStartTime(), queryInfo.getEndTime());
			subfilters[0] = new RangeFilter("startTime", dateRange);
			subfilters[1] = new RangeFilter("endTime", dateRange);
			BoolFilter boolFilter = new BoolFilter(subfilters);
			boolFilter.setMust(true);
			filters.add(boolFilter);
		}

		// Parse time range
		String timeRange = queryInfo.getTime();
		if (timeRange != null) {
			SimpleDateFormat sdf;
			int rangeField;
			switch (timeRange.length()) {
			case 4:
				sdf = new SimpleDateFormat("yyyy");
				rangeField = Calendar.YEAR;
				break;
			case 7:
				sdf = new SimpleDateFormat("yyyy/MM");
				rangeField = Calendar.MONTH;
				break;
			case 10:
				sdf = new SimpleDateFormat("yyyy/MM/dd");
				rangeField = Calendar.DATE;
				break;
			case 13:
				sdf = new SimpleDateFormat("yyyy/MM/dd HH");
				rangeField = Calendar.HOUR_OF_DAY;
				break;
			default:
				throw new ESException("Wrong time format: (" + timeRange + ")");
			}
			Calendar calendar = Calendar.getInstance();
			try {
				calendar.setTime(sdf.parse(timeRange));
			} catch (ParseException e) {
				throw new ESException("Wrong time format: (" + timeRange + ")", e);
			}

			Date startTime = calendar.getTime();
			calendar.add(rangeField, 1);
			calendar.add(Calendar.MILLISECOND, -1);
			Date endTime = calendar.getTime();

			// Time range filters
			Range<Date> range = new Range<Date>(startTime, endTime);
			Filter rangeFilters = new RangeFilter("startTime", range);
			filters.add(rangeFilters);
		}

		// Park id filters
		String[] parkIds = queryInfo.getParkIds();
		if (parkIds != null && parkIds.length == 1) {
			filters.add(new TermFilter("parkId", parkIds[0]));
		}
		if (parkIds != null && parkIds.length > 1) {
			Filter[] subfilters = new Filter[parkIds.length];
			for (int i = 0; i < parkIds.length; i++) {
				subfilters[i] = new TermFilter("parkId", parkIds[i]);
			}
			BoolFilter boolFilter = new BoolFilter(subfilters);
			boolFilter.setMust(false);
			filters.add(boolFilter);
		}

		return filters;
	}

	public static List<Filter> makeFilters(QueryInfo queryInfo, List<Date> dates) throws ESException {

		// Make filters
		List<Filter> filters = makeFilters(queryInfo);

		if (dates == null || dates.size() == 0) return filters;

		Calendar c = Calendar.getInstance();
		Filter[] subfilters = new Filter[dates.size()];
		for (int i = 0; i < dates.size(); i++) {
			//subfilters[i] = new TermFilter("startTime", sdf.format(dates.get(i)));
			c.setTime(dates.get(i));
			c.add(Calendar.DATE, 1);
			c.add(Calendar.MILLISECOND, -1);
			Range<Date> range = new Range<Date>(dates.get(i), c.getTime());
			subfilters[i] = new RangeFilter("startTime", range);
		}
		BoolFilter boolFilter = new BoolFilter(subfilters);
		boolFilter.setMust(false);
		filters.add(boolFilter);

		return filters;
	}

	public static List<AggVerb> makeAggrVerbs(QueryInfo queryInfo) throws ESException {

		String datapointId = queryInfo.getDatapointId();
		String[] termAggrs = queryInfo.getAggr();
		String[] methods = queryInfo.getMethods();
		List<String> aggrKeys = new ArrayList<String>();

		// Make aggregation verbs
		List<AggVerb> aggrVerbs = new ArrayList<AggVerb>();

		// Term verbs
		AggVerb termverb = null;
		if (termAggrs != null) {
			for (String termAggr : termAggrs) {
				termverb = new TermAggVerb(AggType.term, termAggr);
				aggrVerbs.add(termverb);
				aggrKeys.add(termAggr);
			}
		}

		StatsAggVerb statsverb = new StatsAggVerb(AggType.stats, "value");
		if (methods == null) {
			// 若没有聚类函数method，则默认按照日期聚类，取sum
			char intervalAbbr = datapointId.charAt(4);
			DatehistogramAggVerb dateHistogramVerb = null;

			Date startTime = null;
			Date endTime = null;
			// Parse time range
			String timeRange = queryInfo.getTime();
			if (timeRange != null) {
				SimpleDateFormat sdf;
				int rangeField;
				switch (timeRange.length()) {
				case 4:
					sdf = new SimpleDateFormat("yyyy");
					rangeField = Calendar.YEAR;
					break;
				case 7:
					sdf = new SimpleDateFormat("yyyy/MM");
					rangeField = Calendar.MONTH;
					break;
				case 10:
					sdf = new SimpleDateFormat("yyyy/MM/dd");
					rangeField = Calendar.DATE;
					break;
				case 13:
					sdf = new SimpleDateFormat("yyyy/MM/dd HH");
					rangeField = Calendar.HOUR_OF_DAY;
					break;
				default:
					throw new ESException("Wrong time format: (" + timeRange + ")");
				}
				Calendar calendar = Calendar.getInstance();
				try {
					calendar.setTime(sdf.parse(timeRange));
				} catch (ParseException e) {
					throw new ESException("Wrong time format: (" + timeRange + ")", e);
				}

				startTime = calendar.getTime();
				calendar.add(rangeField, 1);
				calendar.add(Calendar.MILLISECOND, -1);
				endTime = calendar.getTime();
			} else {
				startTime = queryInfo.getStartTime();
				endTime = queryInfo.getEndTime();
			}

			switch (intervalAbbr) {
			case 'Y':
				dateHistogramVerb = new DatehistogramAggVerb(
						AggType.datehistogram, "startTime", "yyyy", "year");
				break;
			case 'M':
				dateHistogramVerb = new DatehistogramAggVerb(
						AggType.datehistogram, "startTime", "yyyy/MM", "month");
				break;
			case 'D':
				dateHistogramVerb = new DatehistogramAggVerb(
						AggType.datehistogram, "startTime", "yyyy/MM/dd", "day");
				break;
			case 'H':
				dateHistogramVerb = new DatehistogramAggVerb(
						AggType.datehistogram, "startTime", "yyyy/MM/dd HH", "hour");
				break;
			}
			if (endTime != null) dateHistogramVerb.setMax(endTime.getTime());
			if (startTime != null) dateHistogramVerb.setMin(startTime.getTime());
			aggrVerbs.add(dateHistogramVerb);
			aggrKeys.add("time");
			statsverb.getStatsTypes().add(StatsType.sum);
			aggrKeys.add("sum");
		} else {
			for (String method : methods) {
				if (StatsAggVerb.AVG.equals(method)) {
					statsverb.getStatsTypes().add(StatsType.avg);
					aggrKeys.add("avg");
				} else if (StatsAggVerb.MAX.equals(method)) {
					statsverb.getStatsTypes().add(StatsType.max);
					aggrKeys.add("max");
				} else if (StatsAggVerb.MIN.equals(method)) {
					statsverb.getStatsTypes().add(StatsType.min);
					aggrKeys.add("min");
				} else if (StatsAggVerb.SUM.equals(method)) {
					statsverb.getStatsTypes().add(StatsType.sum);
					aggrKeys.add("sum");
				} else if (StatsAggVerb.CNT.equals(method)) {
					statsverb.getStatsTypes().add(StatsType.cnt);
					aggrKeys.add("cnt");
				} else {
					throw new ESException("Wrong aggregation verb: (" + method + ")");
				}
			}
		}
		aggrVerbs.add(statsverb);
		queryInfo.setAggrKeys(aggrKeys);

		return aggrVerbs;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String[] getSecondaryMethods() {
		return secondaryMethods;
	}

	public void setSecondaryMethods(String[] secondaryMethods) {
		this.secondaryMethods = secondaryMethods;
	}
}
