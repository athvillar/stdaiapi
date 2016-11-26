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

	@Select({"SELECT COUNT(*) FROM MODEL WHERE LABEL = #{label} AND MODELTEMPLATEID = #{modelTemplateId}"})
	Integer selectCountByLabelModelTemplateId(@Param("label") String label, @Param("modelTemplateId") String modelTemplateId);

	@Select({"SELECT * FROM MODEL WHERE MODELID = #{modelId}"})
	Model selectById(@Param("modelId") String modelId);

	@Select({"SELECT * FROM MODEL WHERE LABEL = #{label} AND MODELTEMPLATEID = #{modelTemplateId} ORDER BY CREATETIME DESC"})
	List<Model> selectByLabelModelTemplateId(@Param("label") String label, @Param("modelTemplateId") String modelTemplateId);

	@Insert({"INSERT INTO MODEL ",
		"(MODELID, MODELTEMPLATEID, PARENTMODELID, LABEL, DATASETID, DATACOUNT, BATCHSIZE, BATCHCOUNT, STRUCTURE, CREATETIME) ",
		"VALUES (#{param.modelId}, #{param.modelTemplateId}, #{param.parentModelId}, #{param.label}, #{param.datasetId}, ",
		"#{param.dataCount}, #{param.batchSize}, #{param.batchCount}, #{param.structure}, #{param.createTime})"})
	void insert(@Param("param") Model param);

	@Delete({"DELETE FROM MODEL WHERE MODELID = #{modelId}"})
	void deleteById(@Param("modelId") String modelId);

	@Delete({"DELETE FROM MODEL WHERE LABEL = #{label} AND MODELTEMPLATEID = #{modelTemplateId}"})
	void deleteByLabelModelTemplateId(@Param("label") String label, @Param("modelTemplateId") String modelTemplateId);
}
