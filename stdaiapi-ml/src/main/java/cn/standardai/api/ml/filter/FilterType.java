package cn.standardai.api.ml.filter;

import java.util.HashMap;
import java.util.Map;

public enum FilterType {

	IntegerDicFilter("IntegerDicFilter", IntegerDicFilter.class),
	GrayImageFilter("GrayImageFilter", GrayImageFilter.class),
	RGBImageFilter("RGBImageFilter", RGBImageFilter.class),
	SequenceIntegerFilter("SequenceIntegerFilter", SequenceIntegerFilter.class),
	NormalizeIntegerFilter("NormalizeIntegerFilter", NormalizeIntegerFilter.class),
	ExpInteger1D("ExpInteger1D", ExpInteger1D.class),
	ExpInteger2D("ExpInteger2D", ExpInteger2D.class),
	ExpInteger3D("ExpInteger3D", ExpInteger3D.class);

	Class<? extends DataFilter<?, ?>> cls;

	String clsName;

	private FilterType(String clsName, Class<? extends DataFilter<?, ?>> cls) {
		this.clsName = clsName;
		this.cls = cls;
	}

	private static final Map<String, Class<? extends DataFilter<?, ?>>> mappings = new HashMap<String, Class<? extends DataFilter<?, ?>>>();

	static {
		for (FilterType type : values()) {
			mappings.put(type.clsName, type.cls);
		}
	}

	public static Class<? extends DataFilter<?, ?>> resolve(String type) {
		return (type != null ? mappings.get(type) : null);
	}
}