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
	List<Data> selectByDatasetId(@Param("datasetId") String datasetId);

	@Select({"SELECT * FROM DATA WHERE DATASETID = #{datasetId} ORDER BY IDX"})
	List<Data> selectByDatasetIdOrder(@Param("datasetId") String datasetId);

	@Select({"SELECT * FROM DATA WHERE DATASETID = #{datasetId} LIMIT 1"})
	Data select1ByDatasetId(@Param("datasetId") String datasetId);

	@Insert({"UPDATE DATA SET X = #{param.x}, Y = #{param.y} ",
		"WHERE DATASETID = #{param.datasetId} AND IDX = #{param.idx}"})
	Long updateXYByKey(@Param("param") Data param);

	@Insert({"INSERT INTO DATA (DATAID, DATASETID, IDX, REF, X, Y) ",
		"VALUES (#{param.dataId}, #{param.datasetId}, #{param.idx}, #{param.ref}, #{param.x}, #{param.y})"})
	Long insert(@Param("param") Data param);

	@Delete({"DELETE FROM DATA WHERE DATASETID = #{datasetId}"})
	Long deleteByDatasetId(@Param("datasetId") String datasetId);
}
