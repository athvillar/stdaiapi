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
	ExpInteger3D("ExpInteger3D", ExpInteger3D.class),
	SmartSplitFilter("SmartSplitFilter", SmartSplitFilter.class),
	SprInteger1D("SprInteger1D", SprInteger1D.class);

	public Class<? extends DataFilter<?, ?>> cls;

	public String clsName;

	private FilterType(String clsName, Class<? extends DataFilter<?, ?>> cls) {
		this.clsName = clsName;
		this.cls = cls;
	}

	private static final Map<String, Class<? extends DataFilter<?, ?>>> mappings = new HashMap<String, Class<? extends DataFilter<?, ?>>>();

	private static final Map<String, String> descMap = new HashMap<String, String>();

	static {
		for (FilterType type : values()) {
			mappings.put(type.clsName, type.cls);
		}
		for (FilterType type : values()) {
			try {
				descMap.put(type.clsName, type.cls.newInstance().getDescription());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static Class<? extends DataFilter<?, ?>> resolve(String type) {
		return (type != null ? mappings.get(type) : null);
	}

	public static String getDescription(String type) {
		return (type != null ? descMap.get(type) : null);
	}
}