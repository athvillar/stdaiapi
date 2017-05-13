package cn.standardai.api.dao;

import java.util.List;

import cn.standardai.api.dao.bean.DataDicData;

public interface DataDicDataDao {

	List<DataDicData> selectById(String dataDicId);

	void insert(List<DataDicData> param);

	void deleteByDatasetId(String dataDicId);
}
