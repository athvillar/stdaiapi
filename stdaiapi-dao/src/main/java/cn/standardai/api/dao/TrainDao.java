package cn.standardai.api.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.Train;

public interface TrainDao {

	@Select({"SELECT * FROM (SELECT * FROM TRAIN WHERE MODELID = #{modelId} ORDER BY STARTTIME DESC) T1 LIMIT 1"})
	Train selectLatestByModelId(@Param("modelId") String modelId);

	@Insert({"INSERT INTO TRAIN (TRAINID, MODELID, EPOCHDATACNT, EPOCHCNT,",
		"STARTTIME, ENDTIME, TOTALSECOND) ",
		"VALUES (#{param.trainId}, #{param.modelId}, #{param.epochDataCnt}, #{param.epochCnt},",
		"#{param.startTime}, #{param.endTime}, #{param.totalSecond})"})
	Long insert(@Param("param") Train param);

	@Update({"UPDATE TRAIN SET EPOCHCNT = #{param.epochCnt}, ENDTIME = #{param.endTime}, ",
		" TOTALSECOND = #{param.totalSecond} WHERE TRAINID = #{param.trainId}"})
	Long updateById(@Param("param") Train param);
}
