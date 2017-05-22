package cn.standardai.api.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.standardai.api.dao.bean.DataDic;

public interface DataDicDao {

	@Select({"SELECT * FROM DATADIC WHERE DATADICNAME = #{dataDicName} AND USERID = #{userId}"})
	DataDic selectByKey(@Param("dataDicName") String dataDicName, @Param("userId") String userId);

	@Insert({"INSERT INTO DATADIC (DATADICID, DATADICNAME, USERID) ",
		"VALUES (#{param.dataDicId}, #{param.dataDicName}, #{param.userId})"})
	void insert(@Param("param") DataDic param);

	@Delete({"DELETE FROM DATADIC WHERE DATADICID = #{dataDicId}"})
	void deleteByDatasetId(@Param("dataDicId") String dataDicId);
}
