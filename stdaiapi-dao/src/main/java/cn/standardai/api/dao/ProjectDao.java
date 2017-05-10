package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.Project;

public interface ProjectDao {

	@Select({"SELECT COUNT(*) FROM PROJECT"})
	List<Project> selectAll();

	@Select({"SELECT * FROM PROJECT WHERE PROJECTID = #{projectId}"})
	List<Project> selectById(@Param("projectId") String projectId);

	@Insert({"INSERT INTO PROJECT (PROJECTID, PROJECTNAME, DESCRIPTION, COSTMONEY, RELEASETIME) ",
		"VALUES (#{param.projectId}, #{param.projectName}, #{param.description}, #{param.costMoney}, NOW())"})
	void insert(@Param("param") Project param);

	@Update({"UPDATE PROJECT SET SUPPORTEDMONEY = SUPPORTEDMONEY + #{param.supportedMoney} WHERE PROJECTID = #{param.projectId}"})
	void updateMoneyById(@Param("param") Project param);

	@Update({"UPDATE PROJECT SET STARTTIME = #{param.startTime}, STATUS = #{param.status} WHERE PROJECTID = #{param.projectId}"})
	void updateStartTimeStatusById(@Param("param") Project param);

	@Update({"UPDATE PROJECT SET STATUS = #{param.status} WHERE PROJECTID = #{param.projectId}"})
	void updateStatusById(@Param("param") Project param);

	@Delete({"DELETE FROM PROJECT WHERE PROJECTID = #{projectId}"})
	void deleteById(@Param("projectId") String projectId);
}