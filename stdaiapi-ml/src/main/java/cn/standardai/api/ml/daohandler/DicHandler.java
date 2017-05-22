package cn.standardai.api.ml.daohandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.standardai.api.dao.DataDicDao;
import cn.standardai.api.dao.DataDicDataDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.DataDic;
import cn.standardai.api.dao.bean.DataDicData;

public class DicHandler {

	private DaoHandler daoHandler;

	public DicHandler(DaoHandler daoHandler) {
		this.daoHandler = daoHandler;
	}

	public DataDic getDataDic(String userId, String dicName) {

		if (dicName == null) return null;

		DataDicDao dataDicDao = daoHandler.getMySQLMapper(DataDicDao.class);
		int i;
		if ((i = dicName.indexOf('/')) != -1 && i < dicName.length() - 1) {
			userId = dicName.substring(0, i);
			dicName = dicName.substring(i + 1);
		}
		return dataDicDao.selectByKey(dicName, userId);
	}

	public List<DataDicData> getData(DataDic dataDic) {
		if (dataDic == null) return null;
		DataDicDataDao dataDao = daoHandler.getMySQLMapper(DataDicDataDao.class);
		return dataDao.selectByDataDicId(dataDic.getDataDicId());
	}

	public Map<String, Integer> get(String userId, String dicName) {
		List<DataDicData> data = getData(getDataDic(userId, dicName));
		Map<String, Integer> dataMap = new HashMap<String, Integer>();
		for (int i = 0; i < data.size(); i++) {
			dataMap.put(data.get(i).getValue(), data.get(i).getKii());
		}
		return dataMap;
	}
}
