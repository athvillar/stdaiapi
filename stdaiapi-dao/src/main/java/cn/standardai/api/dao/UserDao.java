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

	@Insert({"INSERT INTO USER (USERID, PASSWORD) ",
		"VALUES (#{param.userId}, #{param.password})"})
	void insert(@Param("param") User param);

	@Update({"UPDATE USER SET PASSWORD = #{param.password} WHERE USERID = #{param.userId}"})
	void updateById(@Param("param") User param);

	@Delete({"DELETE FROM USER WHERE USERID = #{userId}"})
	void deleteById(@Param("userId") String userId);
}
