package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.standardai.api.dao.bean.DataDic;

public interface DataDicDao {

	@Select({"SELECT * FROM DATADIC WHERE DATADICNAME = #{dataDicName} AND USERID = #{userId}"})
	DataDic selectByKey(@Param("dataDicName") String dataDicName, @Param("userId") String userId);

	@Select({"SELECT * FROM DATADIC WHERE USERID = #{userId}"})
	List<DataDic> selectByUserId(@Param("userId") String userId);

	@Insert({"INSERT INTO DATADIC (DATADICID, DATADICNAME, DESCRIPTION, USERID, SHAREPOLICY) ",
		"VALUES (#{param.dataDicId}, #{param.dataDicName}, #{param.description}, #{param.userId}, #{param.sharePolicy})"})
	void insert(@Param("param") DataDic param);

	@Delete({"DELETE FROM DATADIC WHERE DATADICID = #{dataDicId}"})
	void deleteByDatasetId(@Param("dataDicId") String dataDicId);

	@Delete({"DELETE FROM DATADIC WHERE DATADICNAME = #{dataDicName} AND USERID = #{userId}"})
	void deleteByKey(@Param("dataDicName") String dataDicName, @Param("userId") String userId);
}
