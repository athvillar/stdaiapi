package cn.standardai.api.ml.daohandler;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.ml.exception.MLException;

public class DataHandler {

	private DaoHandler daoHandler;

	public DataHandler(DaoHandler daoHandler) {
		this.daoHandler = daoHandler;
	}

	public Dataset getDataset(String userId, JSONObject json) throws MLException {

		Dataset dataset;
		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		String datasetId = json.getString("datasetId");
		if (datasetId != null) {
			dataset = datasetDao.selectById(datasetId);
			if (dataset == null) throw new MLException("dataset不存在");
		} else {
			String datasetName = json.getString("datasetName");
			if (datasetName != null) {
				dataset = datasetDao.selectByKey(datasetName, userId);
				if (dataset == null) throw new MLException("dataset不存在");
			} else {
				throw new MLException("未指定dataset");
			}
		}

		return dataset;
	}

	public List<Data> getData(Dataset dataset) throws MLException {
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		List<Data> data = dataDao.selectDataByDatasetId(dataset.getDatasetId());
		if (data == null) throw new MLException("指定dataset无数据");
		return data;
	}
}
