package cn.standardai.api.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.standardai.api.dao.bean.Project;

public interface UserProjectContributionDao {
	
	//是否有查找的信息
	@Select({"SELECT COUNT(*) FROM USER_PROJECT_CONTRIBUTION WHERE USERID = #{param.userId} AND USERID = #{param.projectId}"})
	Integer selectCountById(@Param("param") Project param);
	
//	//查找全部
//	@Select({"SELECT COUNT(*) FROM USER_PROJECT_CONTRIBUTION"})
//	Integer selectAll();
//	
//	//查找某条
//	@Select({"SELECT * FROM USER_PROJECT_CONTRIBUTION WHERE PROJECTID = #{projectId}"})
//	List<User> selectById(@Param("projectId") String projectId);
//	
//	//新增项目
//	@Insert({"INSERT INTO USER_PROJECT_CONTRIBUTION (PROJECTID, PROJECTNAME,COSTMONEY,RAISEMONEY,RELEASEDATE) ",
//		"VALUES (#{param.projectId}, #{param.projectName},#{param.costMoney},#{param.raiseMoney},now()"})
//	void insert(@Param("param") Project param);
//	
//	//UPDATE mytest.tb,test.tb SET mytest.tb.data='liangCK', test.tb.data='liangCK' 
//	//WHERE mytest.tb.id=test.tb.id AND mytest.tb.id=2;
//
//	//删除某条
//	@Delete({"DELETE FROM USER_PROJECT_CONTRIBUTION WHERE PROJECTID = #{projectId}"})
//	void deleteById(@Param("projectId") String projectId);
	
}
