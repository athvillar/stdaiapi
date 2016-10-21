package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.standardai.api.dao.bean.JsonData;

public interface DataDao {

	@Select({"SELECT * FROM JSONDATA WHERE DATASETID = #{dataSetId}"})
	List<JsonData> selectJsonDataByDataSetId(String dataSetId);

	@Insert({"INSERT INTO JSONDATA (ID, DATASETID, DATA) ",
		"VALUES (#{param.id}, #{param.dataSetId}, #{param.data})"})
	void insertJsonData(@Param("data") JsonData param);
}
