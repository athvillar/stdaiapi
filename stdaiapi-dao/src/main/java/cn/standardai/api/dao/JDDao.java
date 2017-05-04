package cn.standardai.api.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface JDDao {

	@Insert({"INSERT INTO JDUSER (USERID, LEVEL) ",
		"VALUES (#{userId}, #{level})"})
	void insertUser(@Param("userId") String userId, @Param("level") String level);

	@Insert({"INSERT INTO JDSKU ",
		"VALUES (#{p1}, #{p1}, #{p2}, #{p3}, #{p4}, #{p5})"})
	void insertSku(@Param("p0") String p0, @Param("p1") String p1, @Param("p2") String p2,
			@Param("p3") String p3, @Param("p4") String p4, @Param("p5") String p5);

	@Insert({"INSERT INTO JDCOMMENT ",
		"VALUES (#{p1}, #{p1}, #{p2}, #{p3})"})
	void insertComment(@Param("p0") String p0, @Param("p1") String p1, @Param("p2") String p2,
			@Param("p3") Double p3);

	@Insert({"INSERT INTO JDACTION ",
		"VALUES (#{p1}, #{p1}, #{p2}, #{p3}, #{p4}, #{p5}, #{p6})"})
	void insertAction(@Param("p0") String p0, @Param("p1") String p1, @Param("p2") Date p2,
			@Param("p3") String p3, @Param("p4") String p4, @Param("p5") String p5, @Param("p6") String p6);

}
