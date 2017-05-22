package cn.standardai.api.ml.filter;

import cn.standardai.api.ml.exception.FilterException;
import cn.standardai.api.ml.run.ModelGhost;

public abstract class DataFilter<T1, T2> {

	public abstract T2 encode(T1 s) throws FilterException;

	public abstract T1 decode(T2 t) throws FilterException;

	public abstract boolean needInit();

	public abstract void init(ModelGhost mg);

	public static <T3, T4> T4 encode(T3 data, DataFilter<?, ?>[] filters) throws FilterException {

		Object srcData;
		Object desData = data;
		for (int i = 0; i < filters.length; i++) {
			srcData = desData;
			DataFilter<Object, Object> f = (DataFilter<Object, Object>)filters[i];
			if (f == null) continue;
			desData = f.encode(srcData);
		}
		return (T4)desData;
	}

	public static <T3, T4> T4 decode(T3 data, DataFilter<?, ?>[] filters) throws FilterException {

		Object srcData;
		Object desData = data;
		for (int i = 0; i < filters.length; i++) {
			srcData = desData;
			DataFilter<Object, Object> f = (DataFilter<Object, Object>)filters[i];
			desData = f.decode(srcData);
		}
		return (T4)desData;
	}

	public static DataFilter<?, ?>[] parseFilters(String s) throws FilterException {

		if (s == null) return null;
		String[] ss = s.split("[|]");

		DataFilter<?, ?>[] filters = new DataFilter<?, ?>[ss.length];
		for (int i = 0; i < filters.length; i++) {
			filters[i] = DataFilter.parse(ss[i]);
		}
		
		return filters;
	}

	public static DataFilter<?, ?> parse(String s) throws FilterException {

		if (s == null) return null;

		int pStart = s.indexOf('(');
		int pEnd = s.indexOf(')');
		if (pStart != -1 && pEnd != -1 && pStart < pEnd) {
			String p = s.substring(pStart + 1, pEnd);
			String[] ps = p.split(",");
			DynamicFilter<?, ?, String> filter = (DynamicFilter<?, ?, String>)DataFilter.getInstance(s.substring(0, pStart));
			if (filter == null) return null;
			for (int i = 0; i < ps.length; i++) {
				filter.setParam(i, ps[i]);
			}
			return filter;
		} else {
			DataFilter<?, ?> filter = (DataFilter<?, ?>)DataFilter.getInstance(s);
			return filter;
		}
	}

	private static DataFilter<?, ?> getInstance(String s) throws FilterException {

		try {
			Class<? extends DataFilter<?, ?>> filter = FilterType.resolve(s);
			if (filter == null) return null;
			return filter.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new FilterException("创建失败(" + s + ")", e);
		}
	}
}
