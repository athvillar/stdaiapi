package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.Project;
import cn.standardai.api.dao.bean.User;

public interface ProjectDao {
	
	//查找全部
	@Select({"SELECT COUNT(*) FROM PROJECT"})
	List<Project> selectAll();
	
	//查找某条
	@Select({"SELECT * FROM PROJECT WHERE PROJECTID = #{projectId}"})
	List<Project> selectById(@Param("projectId") String projectId);
	
	//新增项目
	@Insert({"INSERT INTO PROJECT (PROJECTID, PROJECTNAME,COSTMONEY,RAISEMONEY,RELEASEDATE) ",
		"VALUES (#{param.projectId}, #{param.projectName},#{param.costMoney},now()"})
	void insert(@Param("param") Project param);
	
//	//UPDATE mytest.tb,test.tb SET mytest.tb.data='liangCK', test.tb.data='liangCK' 
//	//WHERE mytest.tb.id=test.tb.id AND mytest.tb.id=2;
//	//更新项目与用户表和用户支持项目表
//	@Update({"PROJECT.p,UPDATE USER.u,USER_PROJECT_CONTRIBUTION.c SET "
//			+ "PROJECT.p.RAISEMONEY + #{param.raiseMoney} USER.u.SUPPORTMONEY + #{param.raiseMoney} WHERE USERID = #{param.userId}"})
//	void updatePassword(@Param("param") User param);

	//删除某条
	@Delete({"DELETE FROM PROJECT WHERE PROJECTID = #{projectId}"})
	void deleteById(@Param("projectId") String projectId);
	
}
