package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import cn.standardai.api.dao.bean.Token;

public interface TokenDao {

	@Select({"SELECT * FROM TOKEN WHERE TOKEN = #{param.token} AND EXPIRETIME >= #{param.expireTime}"})
	List<Token> selectByToken(@Param("param") Token param);

	@Select({"SELECT USERID FROM TOKEN WHERE TOKEN = #{token}"})
	String selectUserIdByToken(@Param("token") String token);

	@Insert({"INSERT INTO TOKEN (TOKEN, USERID, EXPIRETIME) VALUES (#{param.token}, #{param.userId}, #{param.expireTime})"})
	void insert(@Param("param") Token param);

	@Delete({"DELETE FROM TOKEN WHERE USERID = #{userId}"})
	void deleteByUserId(@Param("userId") String userId);
}
