package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.Data;

public interface DataDao {

	@Select({"SELECT COUNT(*) FROM DATA WHERE DATASETID = #{datasetId}"})
	Integer selectCountByDatasetId(@Param("datasetId") String datasetId);

	@Select({"SELECT DATA, TYPE FROM DATA WHERE DATASETID = #{datasetId}"})
	List<Data> selectDataByDatasetId(@Param("datasetId") String datasetId);

	@Insert({"INSERT INTO DATA (DATAID, DATASETID, IDX, DATA) ",
		"VALUES (#{param.dataId}, #{param.datasetId}, #{param.idx}, #{param.data})"})
	void insert(@Param("param") Data param);

	@Update({"UPDATE DATA SET DATADICKEY1 = #{param.dataDicKey1}, DATADICVALUE1 = #{param.dataDicValue1} ",
		" WHERE DATAID = #{param.dataId}"})
	void updateDic1ById(@Param("param") Data param);

	@Update({"UPDATE DATA SET DATADICKEY2 = #{param.dataDicKey2}, DATADICVALUE2 = #{param.dataDicValue2} ",
		" WHERE DATAID = #{param.dataId}"})
	void updateDic2ById(@Param("param") Data param);

	@Update({"UPDATE DATA SET DATADICKEY3 = #{param.dataDicKey3}, DATADICVALUE3 = #{param.dataDicValue3} ",
		" WHERE DATAID = #{param.dataId}"})
	void updateDic3ById(@Param("param") Data param);

	@Update({"UPDATE DATA SET DATADICKEY1 = #{param.dataDicKey1}, DATADICVALUE1 = #{param.dataDicValue1} ",
		" WHERE DATASETID = #{param.datasetId} AND IDX >= #{from} AND IDX <= #{to}"})
	void updateDic1ByRange(@Param("param") Data param, @Param("from") Integer from, @Param("to") Integer to);

	@Update({"UPDATE DATA SET DATADICKEY2 = #{param.dataDicKey2}, DATADICVALUE2 = #{param.dataDicValue2} ",
		" WHERE DATASETID = #{param.datasetId} AND IDX >= #{from} AND IDX <= #{to}"})
	void updateDic2ByRange(@Param("param") Data param, @Param("from") Integer from, @Param("to") Integer to);

	@Update({"UPDATE DATA SET DATADICKEY3 = #{param.dataDicKey3}, DATADICVALUE3 = #{param.dataDicValue3} ",
		" WHERE DATASETID = #{param.datasetId} AND IDX >= #{from} AND IDX <= #{to}"})
	void updateDic3ByRange(@Param("param") Data param, @Param("from") Integer from, @Param("to") Integer to);

	@Delete({"DELETE FROM DATA WHERE DATASETID = #{datasetId}"})
	void deleteByDatasetId(@Param("datasetId") String datasetId);
}
