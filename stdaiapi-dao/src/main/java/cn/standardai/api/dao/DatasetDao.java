package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.Dataset;

public interface DatasetDao {

	@Select({"SELECT COUNT(*) FROM DATASET WHERE DATASETID = #{param.datasetId}"})
	Integer selectCoundById(@Param("datasetId") String datasetId);

	@Select({"SELECT COUNT(*) FROM DATASET WHERE DATASETID = #{param.datasetId} AND USERID = #{param.userId}"})
	Integer selectCoundByIdUser(@Param("param") Dataset param);

	@Select({"SELECT DATASETID FROM DATASET WHERE DATASETNAME = #{param.datasetName} AND USERID = #{param.userId}"})
	String selectIdByKey(@Param("param") Dataset param);

	@Select({"SELECT * FROM DATASET WHERE USERID = #{userId}"})
	List<Dataset> selectByUserId(@Param("userId") String userId);

	@Insert({"INSERT INTO DATASET (DATASETID, DATASETNAME, USERID) ",
		"VALUES (#{param.datasetId}, #{param.datasetName}, #{param.userId})"})
	void insert(@Param("param") Dataset param);

	@Update({"UPDATE DATASET SET DATASETNAME = #{param.datasetName} WHERE DATASETID = #{param.datasetId})"})
	void updateById(@Param("param") Dataset param);

	@Delete({"DELETE DATASET WHERE DATASETID = #{datasetId}"})
	void deleteByKey(@Param("datasetId") String datasetId);
}
