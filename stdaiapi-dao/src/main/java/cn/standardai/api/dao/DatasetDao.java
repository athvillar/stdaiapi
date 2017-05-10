package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.Dataset;

public interface DatasetDao {

	@Select({"SELECT COUNT(*) FROM DATASET WHERE DATASETID = #{datasetId}"})
	Integer selectCountById(@Param("datasetId") String datasetId);

	@Select({"SELECT COUNT(*) FROM DATASET WHERE DATASETID = #{param.datasetId} AND USERID = #{param.userId}"})
	Integer selectCountByIdUser(@Param("param") Dataset param);

	@Select({"SELECT * FROM DATASET WHERE DATASETID = #{datasetId}"})
	Dataset selectById(@Param("datasetId") String datasetId);

	@Select({"SELECT * FROM DATASET WHERE DATASETNAME = #{datasetName} AND USERID = #{userId}"})
	Dataset selectByKey(@Param("datasetName") String datasetName, @Param("userId") String userId);

	@Select({"SELECT * FROM DATASET WHERE USERID = #{userId}"})
	List<Dataset> selectByUserId(@Param("userId") String userId);

	@Insert({"INSERT INTO DATASET (DATASETID, DATASETNAME, USERID, FORMAT, KEYWORDS,",
		"TITLES, DATADICID1, DATADICID2, DATADICID3, SHAREPOLICY, CREATETIME) ",
		"VALUES (#{param.datasetId}, #{param.datasetName}, #{param.userId}, #{param.format},",
		"#{param.keywords}, #{param.titles}, #{param.dataDicId1}, #{param.dataDicId2},",
		"#{param.dataDicId3}, #{param.sharePolicy}, NOW())"})
	void insert(@Param("param") Dataset param);

	@Update({"UPDATE DATASET SET DATASETNAME = #{param.datasetName} WHERE DATASETID = #{param.datasetId}"})
	void updateById(@Param("param") Dataset param);

	@Delete({"DELETE FROM DATASET WHERE DATASETID = #{datasetId}"})
	void deleteById(@Param("datasetId") String datasetId);
}
