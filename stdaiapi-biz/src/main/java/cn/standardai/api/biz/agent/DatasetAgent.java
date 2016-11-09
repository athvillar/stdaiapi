package cn.standardai.api.biz.agent;

import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.base.DaoHandler;

public class DatasetAgent {

	private DaoHandler daoHandler = new DaoHandler(true);

	public void removeById(String datasetId) {
		DatasetDao dao1 = daoHandler.getMySQLMapper(DatasetDao.class);
		dao1.deleteById(datasetId);
		DataDao dao2 = daoHandler.getMySQLMapper(DataDao.class);
		dao2.deleteByDatasetId(datasetId);
	}

	public void done() {
		if (daoHandler != null) daoHandler.releaseSession();
	}
}
