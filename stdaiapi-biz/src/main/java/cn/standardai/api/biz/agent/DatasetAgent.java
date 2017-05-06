package cn.standardai.api.biz.agent;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;

public class DatasetAgent extends AuthAgent {

	public void removeById(String datasetId) {
		DatasetDao dao1 = daoHandler.getMySQLMapper(DatasetDao.class);
		dao1.deleteById(datasetId);
		DataDao dao2 = daoHandler.getMySQLMapper(DataDao.class);
		dao2.deleteByDatasetId(datasetId);
	}
}
