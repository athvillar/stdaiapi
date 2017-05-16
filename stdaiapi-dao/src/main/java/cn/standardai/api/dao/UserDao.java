package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.User;

public interface UserDao {

	@Select({"SELECT COUNT(*) FROM USER WHERE USERID = #{userId}"})
	Integer selectCountById(@Param("userId") String userId);

	@Select({"SELECT COUNT(*) FROM USER WHERE USERID = #{param.userId} AND PASSWORD = #{param.password}"})
	Integer selectCountByAuth(@Param("param") User param);

	@Select({"SELECT * FROM USER WHERE USERID = #{userId}"})
	List<User> selectById(@Param("userId") String userId);

	@Select({"SELECT * FROM USER"})
	List<User> select();

	@Insert({"INSERT INTO USER (USERID, PASSWORD, EMAIL, REGISTTIME, LASTLOGINTIME, STATUS) ",
		"VALUES (#{param.userId}, #{param.password}, #{param.email}, NOW(), NOW(), '2')"})
	void insert(@Param("param") User param);

	@Update({"UPDATE USER SET PASSWORD = #{param.password} WHERE USERID = #{param.userId}"})
	void updatePasswordById(@Param("param") User param);
	
	@Update({"UPDATE USER SET EMAIL= #{param.email} WHERE USERID = #{param.userId}"})
	void updateById(@Param("param") User param);

	@Delete({"DELETE FROM USER WHERE USERID = #{userId}"})
	void deleteById(@Param("userId") String userId);
}
