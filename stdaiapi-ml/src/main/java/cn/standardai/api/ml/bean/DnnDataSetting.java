package cn.standardai.api.ml.bean;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.ml.exception.JSONFormatException;

public class DnnDataSetting {

	private String datasetId;

	private String datasetName;

	private String xColumn;

	private String xFilter;

	private String yColumn;

	private String yFilter;

	private JSONObject structure;

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getxColumn() {
		return xColumn;
	}

	public void setxColumn(String xColumn) {
		this.xColumn = xColumn;
	}

	public String getxFilter() {
		return xFilter;
	}

	public void setxFilter(String xFilter) {
		this.xFilter = xFilter;
	}

	public String getyColumn() {
		return yColumn;
	}

	public void setyColumn(String yColumn) {
		this.yColumn = yColumn;
	}

	public String getyFilter() {
		return yFilter;
	}

	public void setyFilter(String yFilter) {
		this.yFilter = yFilter;
	}
	
	/*
	 * "data": {
	 *	 "datasetId": "xxx",
	 *	 "datasetName": "xxx",
	 *	 "x": {
	 *	   "column": "table.data.ref",
	 *	   "filter": "jpg2RGB2Double2"
	 *	 },
	 *	 "y": {
	 *	   "colume": "table.data.y",
	 *	   "filter": "subString(1)|lookupDic2Integer(xxxx)"
	 *	 }
	 * }
	 */
	public static DnnDataSetting parse(JSONObject data) throws JSONFormatException {

		DnnDataSetting ds = new DnnDataSetting();

		ds.datasetId = data.getString("datasetId");
		ds.datasetName = data.getString("datasetName");

		JSONObject x = data.getJSONObject("x");
		if (x == null) throw new JSONFormatException("缺少参数(data.x)");

		ds.xColumn = x.getString("column");
		ds.xFilter = x.getString("filter");
		//if (ds.xColumn == null) throw new JSONFormatException("缺少参数(data.x.column)");
		//if (ds.xFilter == null) throw new JSONFormatException("缺少参数(data.x.filter)");

		JSONObject y = data.getJSONObject("y");
		if (y == null) throw new JSONFormatException("缺少参数(data.y)");

		ds.yColumn = y.getString("column");
		ds.yFilter = y.getString("filter");
		//if (ds.yColumn == null) throw new JSONFormatException("缺少参数(data.y.column)");
		//if (ds.yFilter == null) throw new JSONFormatException("缺少参数(data.y.filter)");

		return ds;
	}

	public static String getData(Data data, String dataColumn) {
		switch (dataColumn.substring(dataColumn.lastIndexOf(".") + 1)) {
		case "x":
			return data.getX();
		case "y":
			return data.getY();
		case "ref":
			return data.getRef();
		default:
			return null;
		}
	}

	public JSONObject getStructure() {
		return structure;
	}

	public void setStructure(JSONObject structure) {
		this.structure = structure;
	}
}
