package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.ModelTemplate;

public interface ModelTemplateDao {

	@Select({"SELECT COUNT(*) FROM MODELTEMPLATE WHERE MODELTEMPLATEID = #{modelTemplateId}"})
	Integer selectCountById(@Param("modelTemplateId") String modelTemplateId);

	@Select({"SELECT COUNT(*) FROM MODELTEMPLATE WHERE MODELTEMPLATENAME = #{modelTemplateName} AND USERID = #{userId}"})
	Integer selectCountByKey(@Param("modelTemplateName") String modelTemplateName, @Param("userId") String userId);

	@Select({"SELECT * FROM MODELTEMPLATE WHERE MODELTEMPLATEID = #{modelTemplateId}"})
	ModelTemplate selectById(@Param("modelTemplateId") String modelTemplateId);

	@Select({"SELECT * FROM MODELTEMPLATE WHERE MODELTEMPLATENAME = #{modelTemplateName} AND USERID = #{userId}"})
	ModelTemplate selectByKey(@Param("modelTemplateName") String modelTemplateName, @Param("userId") String userId);

	@Select({"SELECT * FROM MODELTEMPLATE WHERE USERID = #{userId}"})
	List<ModelTemplate> selectByUserId(@Param("userId") String userId);

	@Select({"SELECT * FROM MODELTEMPLATE WHERE USERID = #{userId} OR SHAREPOLICY IN ('1','2')"})
	List<ModelTemplate> selectByPrivilege(@Param("userId") String userId);

	@Select({"SELECT * FROM MODELTEMPLATE WHERE USERID = #{userId} AND SHAREPOLICY IN ('1','2')"})
	List<ModelTemplate> selectByUserIdPrivilege(@Param("userId") String userId);

	@Insert({"INSERT INTO MODELTEMPLATE (MODELTEMPLATEID, MODELTEMPLATENAME, USERID, ALGORITHM, SCRIPT, ",
		"DATASETID, XCOLUMN, XFILTER, YCOLUMN, YFILTER, SHAREPOLICY, CREATETIME) ",
		"VALUES (#{param.modelTemplateId}, #{param.modelTemplateName}, #{param.userId}, #{param.algorithm}, ",
		"#{param.script}, #{param.datasetId}, #{param.xColumn}, #{param.xFilter}, #{param.yColumn}, #{param.yFilter}, ",
		"#{param.sharePolicy}, #{param.createTime})"})
	void insert(@Param("param") ModelTemplate param);

	@Update({"UPDATE MODELTEMPLATE SET SCRIPT = #{param.script}, CREATETIME = #{param.createTime} WHERE MODELTEMPLATENAME = #{param.modelTemplateName} AND USERID = #{param.userId}"})
	void updateByKey(@Param("param") ModelTemplate param);

	@Delete({"DELETE FROM MODELTEMPLATE WHERE MODELTEMPLATEID = #{modelTemplateId}"})
	void deleteById(@Param("modelTemplateId") String modelTemplateId);

	@Delete({"DELETE FROM MODELTEMPLATE WHERE MODELTEMPLATENAME = #{modelTemplateName} AND USERID = #{userId}"})
	Integer deleteByKey(@Param("modelTemplateName") String modelTemplateName, @Param("userId") String userId);
}
