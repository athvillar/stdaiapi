package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.standardai.api.dao.bean.DataDicData;

public interface DataDicDataDao {

	@Select({"SELECT * FROM DATADICDATA WHERE DATADICID = #{dataDicId}"})
	List<DataDicData> selectById(@Param("dataDicId") String dataDicId);

	@Insert({"INSERT INTO DATADICDATA (DATADICID, KEY, VALUE) ",
		"VALUES (#{param.dataDicId}, #{param.key}, #{param.value})"})
	void insert(@Param("param") DataDicData param);

	@Delete({"DELETE FROM DATADICDATA WHERE DATADICID = #{dataDicId}"})
	void deleteByDatasetId(@Param("dataDicId") String dataDicId);
}
