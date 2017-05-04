package cn.standardai.api.dao;

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

}
