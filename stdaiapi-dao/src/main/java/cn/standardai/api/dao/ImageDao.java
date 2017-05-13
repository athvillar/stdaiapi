package cn.standardai.api.dao;

import java.util.List;

import cn.standardai.api.dao.bean.Image;

public interface ImageDao {
	
	List<Image> selectByImageType(String imageType);

	void insert(List<Image> param);

	void updateByAxis(Image param);

	void deleteByAxis(Image userId);
}
