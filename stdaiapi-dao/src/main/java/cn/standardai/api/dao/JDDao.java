package cn.standardai.api.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface JDDao {

	@Insert({"INSERT INTO JDUSER (USERID, LEVEL) ",
		"VALUES (#{userId}, #{level})"})
	void insertUser(@Param("userId") String userId, @Param("level") String level);

	@Insert({"INSERT INTO JDSKU ",
		"VALUES (#{p0}, #{p1}, #{p2}, #{p3}, #{p4}, #{p5})"})
	void insertSku(@Param("p0") String p0, @Param("p1") String p1, @Param("p2") String p2,
			@Param("p3") String p3, @Param("p4") String p4, @Param("p5") String p5);

	@Insert({"INSERT INTO JDCOMMENT ",
		"VALUES (#{p0}, #{p1}, #{p2}, #{p3})"})
	void insertComment(@Param("p0") String p0, @Param("p1") String p1, @Param("p2") String p2,
			@Param("p3") Double p3);

	@Insert({"INSERT INTO JDACTION ",
		"VALUES (#{p0}, #{p1}, #{p2}, #{p3}, #{p4}, #{p5}, #{p6}, #{p7}, #{p8}, #{p9}, #{p10}, #{p11}, #{p12}, #{p13}, #{p14}, #{p15}, #{p16}, #{p17}, #{p18}, #{p19}, #{p20})"})
	void insertAction(@Param("p0") String p0, @Param("p1") String p1, @Param("p2") Date p2,
			@Param("p3") String p3, @Param("p4") String p4, @Param("p5") String p5, @Param("p6") String p6,
			@Param("p7") String p7, @Param("p8") String p8, @Param("p9") Date p9,
			@Param("p10") String p10, @Param("p11") String p11, @Param("p12") String p12, @Param("p13") String p13,
			@Param("p14") String p14, @Param("p15") String p15, @Param("p16") Date p16,
			@Param("p17") String p17, @Param("p18") String p18, @Param("p19") String p19, @Param("p20") String p20);

}
