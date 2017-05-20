package cn.standardai.api.ml.daohandler;

import java.util.List;

import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Dataset;

public class DataHandler {

	private DaoHandler daoHandler;

	public DataHandler(DaoHandler daoHandler) {
		this.daoHandler = daoHandler;
	}

	public Dataset getDataset(String userId, String datasetId, String datasetName) {

		if (datasetId == null && datasetName == null) return null;

		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);
		if (datasetId != null) {
			return datasetDao.selectById(datasetId);
		} else {
			int i;
			if ((i = datasetName.indexOf('/')) != -1 && i < datasetName.length() - 1) {
				userId = datasetName.substring(0, i);
				datasetName = datasetName.substring(i + 1);
			}
			return datasetDao.selectByKey(datasetName, userId);
		}
	}

	public List<Data> getData(Dataset dataset) {
		if (dataset == null) return null;
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		List<Data> data = dataDao.selectDataByDatasetId(dataset.getDatasetId());
		return data;
	}
}
