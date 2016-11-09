package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.standardai.api.dao.bean.JsonData;

public interface DataDao {

	@Select({"SELECT COUNT(*) FROM JSONDATA WHERE DATASETID = #{datasetId}"})
	Integer selectCountByDatasetId(@Param("datasetId") String datasetId);

	@Select({"SELECT DATA FROM JSONDATA WHERE DATASETID = #{datasetId}"})
	List<String> selectDataByDatasetId(@Param("datasetId") String datasetId);

	@Insert({"INSERT INTO JSONDATA (DATASETID, IDX, DATA) ",
		"VALUES (#{param.datasetId}, #{param.idx}, #{param.data})"})
	void insert(@Param("param") JsonData param);

	@Delete({"DELETE FROM JSONDATA WHERE DATASETID = #{datasetId}"})
	void deleteByDatasetId(@Param("datasetId") String datasetId);
}
