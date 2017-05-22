package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.standardai.api.dao.bean.Data;

public interface DataDao {

	@Select({"SELECT COUNT(*) FROM DATA WHERE DATASETID = #{datasetId}"})
	Integer selectCountByDatasetId(@Param("datasetId") String datasetId);

	@Select({"SELECT * FROM DATA WHERE DATASETID = #{datasetId}"})
	List<Data> selectDataByDatasetId(@Param("datasetId") String datasetId);

	@Insert({"INSERT INTO DATA (DATAID, DATASETID, IDX, REF, X, Y) ",
		"VALUES (#{param.dataId}, #{param.datasetId}, #{param.idx}, #{param.ref}, #{param.x}, #{param.y})"})
	Long insert(@Param("param") Data param);

	@Delete({"DELETE FROM DATA WHERE DATASETID = #{datasetId}"})
	Long deleteByDatasetId(@Param("datasetId") String datasetId);
}
