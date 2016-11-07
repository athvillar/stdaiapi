package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.dao.bean.JsonData;

public interface DataDao {

	@Select({"SELECT DATA FROM JSONDATA WHERE DATASETID = #{datasetId}"})
	List<JSONObject> selectByDatasetId(@Param("datasetId") String datasetId);

	@Insert({"INSERT INTO JSONDATA (DATASETID, INDEX, DATA) ",
		"VALUES (#{param.datasetId}, #{param.index}, #{param.data})"})
	void insert(@Param("param") JsonData param);
}
