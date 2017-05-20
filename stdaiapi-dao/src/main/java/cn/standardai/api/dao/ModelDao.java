package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.standardai.api.dao.bean.Model;

public interface ModelDao {

	@Select({"SELECT COUNT(*) FROM MODEL WHERE MODELID = #{modelId}"})
	Integer selectCountById(@Param("modelId") String modelId);

	@Select({"SELECT COUNT(*) FROM MODEL WHERE MODELTEMPLATEID = #{modelTemplateId}"})
	Integer selectCountByModelTemplateId(@Param("modelTemplateId") String modelTemplateId);

	@Select({"SELECT COUNT(*) FROM MODEL WHERE MODELID = #{modelId} AND MODELTEMPLATEID = #{modelTemplateId}"})
	Integer selectCountByIdModelTemplateId(@Param("modelId") String modelId, @Param("modelTemplateId") String modelTemplateId);
	
	@Select({"SELECT COUNT(*) FROM MODEL WHERE MODELID = #{modelId} AND USERID = #{userId}"})
	Integer selectCountByIdUserId(@Param("modelId") String modelId, @Param("userId") String userId);

	@Select({"SELECT * FROM MODEL WHERE MODELID = #{modelId}"})
	Model selectById(@Param("modelId") String modelId);

	@Select({"SELECT * FROM MODEL WHERE USERID = #{userId}"})
	List<Model> selectByUserId(@Param("userId") String userId);

	@Select({"SELECT * FROM (SELECT * FROM MODEL WHERE MODELTEMPLATEID = #{modelTemplateId} ORDER BY CREATETIME DESC) T LIMIT 1"})
	Model selectLatestByModelTemplateId(@Param("modelTemplateId") String modelTemplateId);

	@Select({"SELECT * FROM MODEL WHERE MODELID = #{modelId} AND MODELTEMPLATEID = #{modelTemplateId} ORDER BY UPDATETIME DESC"})
	Model selectByIdModelTemplateId(@Param("modelId") String modelId, @Param("modelTemplateId") String modelTemplateId);

	@Insert({"INSERT INTO MODEL ",
		"(MODELID, MODELTEMPLATEID, USERID, STATUS, DATASETID, DATADICID,",
		"PARENTMODELID, STRUCTURE, CREATETIME, UPDATETIME) ",
		"VALUES (#{param.modelId}, #{param.modelTemplateId}, #{param.userId}, ",
		"#{param.status}, #{param.datasetId}, #{param.dataDicId},",
		"#{param.parentModelId}, #{param.structure}, #{param.createTime}, #{param.createTime})"})
	void insert(@Param("param") Model param);

	@Select({"UPDATE MODEL SET STATUS = #{param.status}, UPDATETIME = NOW() WHERE MODELID = #{param.modelId}"})
	List<Model> updateStatusById(@Param("param") Model param);

	@Select({"UPDATE MODEL SET STRUCTURE = #{param.structure}, STATUS = #{param.status}, UPDATETIME = NOW() WHERE MODELID = #{param.modelId}"})
	List<Model> updateStructureById(@Param("param") Model param);

	@Delete({"DELETE FROM MODEL WHERE MODELID = #{modelId}"})
	Integer deleteById(@Param("modelId") String modelId);
}
