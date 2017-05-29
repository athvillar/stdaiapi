package cn.standardai.api.es.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Order;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.bean.PropertyConfig.ElasticSearch;
import cn.standardai.api.es.bean.AggVerb;
import cn.standardai.api.es.bean.BoolFilter;
import cn.standardai.api.es.bean.DatehistogramAggVerb;
import cn.standardai.api.es.bean.Filter;
import cn.standardai.api.es.bean.Range;
import cn.standardai.api.es.bean.RangeAggVerb;
import cn.standardai.api.es.bean.RangeFilter;
import cn.standardai.api.es.bean.Sort;
import cn.standardai.api.es.bean.StatsAggVerb;
import cn.standardai.api.es.bean.TermAggVerb;
import cn.standardai.api.es.bean.TermFilter;
import cn.standardai.api.es.bean.AggVerb.AggType;
import cn.standardai.api.es.bean.StatsAggVerb.StatsType;
import cn.standardai.api.es.exception.ESException;

public class ESService {

	private static TransportClient client = null;

	public static void init(ElasticSearch es) throws ESException {
		Settings settings = Settings.settingsBuilder().put("cluster.name", es.getCluster()).build();
		try {
			String[] esHosts = es.getHost().split(",");
			String[] esPorts = es.getPort().split(",");
			if (esHosts == null || esHosts.length == 0) {
				throw new ESException("No elasticsearch.host in property file.");
			}
			client = TransportClient.builder().settings(settings).build();
			for (String esHost : esHosts) {
				for (String esPort : esPorts) {
					client.addTransportAddress(
							new InetSocketTransportAddress(InetAddress.getByName(esHost), Integer.parseInt(esPort)));
				}
			}
		} catch (UnknownHostException e) {
			throw new ESException("无法识别的主机", e);
		}
	}

	public static JSON aggregate(String indice, String type, List<Filter> filters, List<AggVerb> aggverbs) throws ESException {

		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		// Generate term queries
		if (filters != null) {
			for (Filter filter : filters) {
				switch (filter.getFilterType()) {
				case term:
					qb.must(QueryBuilders.termQuery(filter.getField(), ((TermFilter)filter).getValue()));
					break;
				case range:
					qb.must(QueryBuilders.rangeQuery(filter.getField())
							.from(((RangeFilter)filter).getRange().getStart())
							.to(((RangeFilter)filter).getRange().getEnd()));
					break;
				case bool:
					BoolQueryBuilder subqb = QueryBuilders.boolQuery();
					for (Filter subfilter : ((BoolFilter)filter).getFilters()) {
						if (TermFilter.class.equals(subfilter.getClass())) {
							if (((BoolFilter)filter).isMust()) {
								subqb.must(QueryBuilders.termQuery(subfilter.getField(), ((TermFilter)subfilter).getValue()));
							} else {
								subqb.should(QueryBuilders.termQuery(subfilter.getField(), ((TermFilter)subfilter).getValue()));
							}
						} else if (RangeFilter.class.equals(subfilter.getClass())) {
							if (((BoolFilter)filter).isMust()) {
								subqb.must(QueryBuilders.rangeQuery(subfilter.getField())
										.from(((RangeFilter)subfilter).getRange().getStart())
										.to(((RangeFilter)subfilter).getRange().getEnd()));
							} else {
								subqb.should(QueryBuilders.rangeQuery(subfilter.getField())
										.from(((RangeFilter)subfilter).getRange().getStart())
										.to(((RangeFilter)subfilter).getRange().getEnd()));
							}
						}
					}
					qb.must(subqb);
					break;
				default:
					break;
				}
			}
		}
		// Generate aggregation terms
		AbstractAggregationBuilder[] aggTerms = new AbstractAggregationBuilder[aggverbs.size()];
		AggregationBuilder<?> tempParent = null;
		for (int i = 0; i < aggverbs.size(); i++) {
			if (aggverbs.get(i) == null) {
				continue;
			}
			if (aggverbs.get(i).getAggType() == AggType.stats) {
				AbstractAggregationBuilder[] statsAggTerms = getAggregationBuilders((StatsAggVerb)aggverbs.get(i));
				for (AbstractAggregationBuilder term : statsAggTerms) {
					if (tempParent != null) {
						tempParent.subAggregation(term);
					} else {
						aggTerms[i] = term;
					}
				}
			} else {
				aggTerms[i] = getAggregationBuilder(aggverbs.get(i));
				if (tempParent != null) {
					tempParent.subAggregation(aggTerms[i]);
				}
				tempParent = (AggregationBuilder<?>) aggTerms[i];
			}
		}

		// Execute query
		SearchRequestBuilder sReqBuilder = null;
		synchronized (client) {
			sReqBuilder = client.prepareSearch(indice).setTypes(type);
		}
		sReqBuilder = sReqBuilder.setSearchType(SearchType.QUERY_AND_FETCH).setQuery(qb);
		if (aggTerms != null && aggTerms.length > 0) {
			sReqBuilder = sReqBuilder.addAggregation(aggTerms[0]).setSize(0);
		}
		SearchResponse sr;
		synchronized (client) {
			sr = sReqBuilder.execute().actionGet();
		}

		return ESResultParser.parse(JSONObject.parseObject(sr.toString()), aggverbs);
	}
	
	public static JSON aggregateEx(String indice, String type, List<Filter> filters, List<AggVerb> aggverbs, Map<String, String> sorts) throws ESException {

		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		// Generate term queries
		if (filters != null) {
			for (Filter filter : filters) {
				switch (filter.getFilterType()) {
				case term:
					qb.must(QueryBuilders.termQuery(filter.getField(), ((TermFilter)filter).getValue()));
					break;
				case range:
					qb.must(QueryBuilders.rangeQuery(filter.getField())
							.from(((RangeFilter)filter).getRange().getStart())
							.to(((RangeFilter)filter).getRange().getEnd()));
					break;
				case bool:
					BoolQueryBuilder subqb = QueryBuilders.boolQuery();
					for (Filter subfilter : ((BoolFilter)filter).getFilters()) {
						if (TermFilter.class.equals(subfilter.getClass())) {
							if (((BoolFilter)filter).isMust()) {
								subqb.must(QueryBuilders.termQuery(subfilter.getField(), ((TermFilter)subfilter).getValue()));
							} else {
								subqb.should(QueryBuilders.termQuery(subfilter.getField(), ((TermFilter)subfilter).getValue()));
							}
						} else if (RangeFilter.class.equals(subfilter.getClass())) {
							if (((BoolFilter)filter).isMust()) {
								subqb.must(QueryBuilders.rangeQuery(subfilter.getField())
										.from(((RangeFilter)subfilter).getRange().getStart())
										.to(((RangeFilter)subfilter).getRange().getEnd()));
							} else {
								subqb.should(QueryBuilders.rangeQuery(subfilter.getField())
										.from(((RangeFilter)subfilter).getRange().getStart())
										.to(((RangeFilter)subfilter).getRange().getEnd()));
							}
						}
					}
					qb.must(subqb);
					break;
				default:
					break;
				}
			}
		}

		// Execute query
		SearchRequestBuilder sReqBuilder = null;
		synchronized (client) {
			sReqBuilder = client.prepareSearch(indice).setTypes(type);
		}
		sReqBuilder = sReqBuilder.setSearchType(SearchType.QUERY_AND_FETCH).setQuery(qb);
		if (sorts != null && sorts.size() > 0) {
			for (Entry<String, String> entry : sorts.entrySet()) {
				if ("ASC".equals(entry.getValue())) {
					sReqBuilder = sReqBuilder.addSort(entry.getKey(), SortOrder.ASC);
				} else {
					sReqBuilder = sReqBuilder.addSort(entry.getKey(), SortOrder.DESC);
				}
			}
		}
		SearchResponse sr;
		synchronized (client) {
			sr = sReqBuilder.execute().actionGet();
		}

		return JSONObject.parseObject(sr.toString());
	}

	public static void insert(String indice, String type, List<Map<String, Object>> data) throws ESException {

		BulkRequestBuilder bulkRequest;
		synchronized (client) {
			bulkRequest = client.prepareBulk();
			for (Map<String, Object> data1 : data) {
				bulkRequest.add(client.prepareIndex(indice, type).setSource(data1));
			}
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		}
	}

	public static void delete(String indice, String type, List<Map<String, Object>> data) throws ESException {

		for (Map<String, Object> data1 : data) {

			BoolQueryBuilder qb = QueryBuilders.boolQuery();
			for (Entry<String, Object> entry : data1.entrySet()) {
				qb.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));		
			}
			SearchRequestBuilder sReqBuilder = null;
			SearchResponse sr;
			synchronized (client) {
				sReqBuilder = client.prepareSearch(indice).setTypes(type);
				sReqBuilder = sReqBuilder.setSearchType(SearchType.QUERY_THEN_FETCH).setQuery(qb).setSize(9999);
				sr = sReqBuilder.execute().actionGet();
			}

			SearchHit[] hits = sr.getHits().getHits();
			if (hits == null || hits.length == 0) continue;
			for (SearchHit hit : hits) {
				DeleteRequestBuilder deleteRequest;
				synchronized (client) {
					deleteRequest = client.prepareDelete();
					deleteRequest.setIndex(indice).setType(type).setId(hit.getId());
				}
				DeleteResponse response = deleteRequest.get();
			}
		}
	}

	private static AbstractAggregationBuilder[] getAggregationBuilders(StatsAggVerb aggverb) {
		AbstractAggregationBuilder[] aggTerms = new AbstractAggregationBuilder[aggverb.getStatsTypes().size()];
		for (int i = 0; i < aggverb.getStatsTypes().size(); i++) {
			switch (aggverb.getStatsTypes().get(i)) {
			case avg:
				aggTerms[i] = AggregationBuilders.avg(StatsAggVerb.AVG_ + aggverb.getField()).field(aggverb.getField());
				break;
			case max:
				aggTerms[i] = AggregationBuilders.max(StatsAggVerb.MAX_ + aggverb.getField()).field(aggverb.getField());
				break;
			case min:
				aggTerms[i] = AggregationBuilders.min(StatsAggVerb.MIN_ + aggverb.getField()).field(aggverb.getField());
				break;
			case sum:
				aggTerms[i] = AggregationBuilders.sum(StatsAggVerb.SUM_ + aggverb.getField()).field(aggverb.getField());
				break;
			default:
				continue;
			}
		}
		return aggTerms;
	}

	@SuppressWarnings("unchecked")
	private static AbstractAggregationBuilder getAggregationBuilder(AggVerb aggverb) {
		switch (aggverb.getAggType()) {
		case term:
			TermsBuilder tb = AggregationBuilders.terms(AggVerb.TERM_ + aggverb.getField()).field(aggverb.getField());
			// Set size
			tb.size(((TermAggVerb)aggverb).getSize());
			// Sort by count
			Sort sort = ((TermAggVerb)aggverb).getSort();
			if (sort != null) {
				if (sort.getSortMode() == StatsType.cnt) {
					tb.order(Terms.Order.count(sort.isAsc()));
				}
			} else {
				tb.order(Terms.Order.term(true));
			}
			return tb;
		case range:
			RangeBuilder rb =  AggregationBuilders.range(AggVerb.RANGE_ + aggverb.getField()).field(aggverb.getField());
			for (Map.Entry<String, Range<?>> range : ((RangeAggVerb)aggverb).getRanges().entrySet()) {
				rb.addRange(range.getKey(), ((Range<Double>)range.getValue()).getStart(), ((Range<Double>)range.getValue()).getEnd());
			}
			return rb;
		case datehistogram:
			DateHistogramBuilder dhb = AggregationBuilders.dateHistogram(AggVerb.DATEHISTOGRAM_ + aggverb.getField()).timeZone("+08:00").field(aggverb.getField())
					.format(((DatehistogramAggVerb)aggverb).getFormat()).minDocCount(0)
					.order(Order.KEY_ASC)
					.missing(0)
					.interval(new DateHistogramInterval(((DatehistogramAggVerb)aggverb).getInterval()));
			if (((DatehistogramAggVerb)aggverb).getMax() != 0L && ((DatehistogramAggVerb)aggverb).getMin() != 0L) {
				dhb = dhb.extendedBounds(((DatehistogramAggVerb)aggverb).getMin(), ((DatehistogramAggVerb)aggverb).getMax());
			}
			return dhb;
		default:
			return null;
		}
	}

	public void disconnect() {
		if (client != null) {
			client.close();
			client = null;
		}
	}
}
