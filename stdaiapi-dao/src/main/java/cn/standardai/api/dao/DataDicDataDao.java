package cn.standardai.api.dao;

import java.util.List;

import cn.standardai.api.dao.bean.DataDicData;
//import cn.standardai.api.dao.bean.Image;

public interface DataDicDataDao {

	List<DataDicData> selectById(String dataDicId);

	//void insert(List<Image> param);

	void deleteByDatasetId(String dataDicId);
}
