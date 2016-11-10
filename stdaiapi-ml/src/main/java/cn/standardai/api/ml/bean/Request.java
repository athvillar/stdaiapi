package cn.standardai.api.ml.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.TokenDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Dataset;

public class Request {

	public static enum DataType { training, target };

	public static enum DataFormat { json, csv };

	private JSONObject requestJSONObject;

	private Map<DataType, Dataset> datasetMap = new HashMap<DataType, Dataset>();

	private TokenDao tokenDao;

	private DataDao dataDao;

	private DatasetDao datasetDao;

	public Request(JSONObject requestJSONObject, DaoHandler daoHandler) {
		this.requestJSONObject = requestJSONObject;
		this.tokenDao = daoHandler.getMySQLMapper(TokenDao.class);
		this.dataDao = daoHandler.getMySQLMapper(DataDao.class);
		this.datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> loadData(DataType dataType, Class<T> type) {
		String dataId = getDatasetId(dataType);
		List<T> dataList = new ArrayList<T>();
		if (dataId == null || "".equals(dataId)) {
			JSONObject dataJSONObject = getDatasetJSONObject(dataType);
			JSONArray data = dataJSONObject.getJSONArray("data");
			if (data == null || data.size() == 0) return null;
			for (int i = 0; i < data.size(); i++) {
				dataList.add(data.getObject(i, type));
			}
		} else {
			List<Data> data = dataDao.selectDataByDatasetId(dataId);
			if (type == JSONObject.class) {
				for (Data data1 : data) {
					dataList.add((T)JSONObject.parse(data1.getData()));
				}
			} else {
				for (Data data1 : data) {
					dataList.add((T)(data1.getData()));
				}
			}
		}
		return dataList;
	}

	public String getDatasetId(DataType dataType) {
		if (datasetMap.get(dataType) != null) return datasetMap.get(dataType).getDatasetId();
		setDataset(dataType);
		if (datasetMap.get(dataType) == null) return null;
		return datasetMap.get(dataType).getDatasetId();
	}

	public DataFormat getDataFormat(DataType dataType) {
		if (datasetMap.get(dataType) != null) return parseDataFormat(datasetMap.get(dataType).getFormat());
		setDataset(dataType);
		if (datasetMap.get(dataType) == null) return null;
		return parseDataFormat(datasetMap.get(dataType).getFormat());
	}

	private DataFormat parseDataFormat(String format) {
		switch (format) {
		case "json":
			return DataFormat.json;
		case "csv":
			return DataFormat.csv;
		default:
			return null;
		}
	}

	private void setDataset(DataType dataType) {
		if (getDatasetJSONObject(dataType) == null) return;
		String datasetId = getDatasetJSONObject(dataType).getString("id");
		if (datasetId != null) datasetMap.put(dataType, datasetDao.selectById(datasetId));
		String token = requestJSONObject.getString("token");
		String datasetName = getDatasetJSONObject(dataType).getString("name");
		if (token == null || datasetName == null) return;
		String userId = tokenDao.selectUserIdByToken(token);
		if (userId != null) datasetMap.put(dataType, datasetDao.selectByKey(datasetName, userId));
	}

	private JSONObject getDatasetJSONObject(DataType dataType) {
		switch (dataType) {
		case training:
			return requestJSONObject.getJSONObject("trainingSet");
		case target:
			return requestJSONObject.getJSONObject("targetSet");
		default:
			return null;
		}
	}
}
