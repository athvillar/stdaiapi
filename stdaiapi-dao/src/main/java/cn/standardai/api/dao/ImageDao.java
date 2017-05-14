package cn.standardai.api.dao;

import java.util.List;
import java.util.Map;

import cn.standardai.api.dao.bean.Image;

public interface ImageDao {

	Integer selectCount(Map<String, Object> params);
	
	List<Image> select(Map<String, Object> params);

	void insert(List<Image> param);

	void updateByAxis(Image param);

	void deleteByAxis(Image userId);
}
