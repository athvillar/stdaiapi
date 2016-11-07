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

	@Insert({"INSERT INTO TOKEN (USERID, TOKEN) VALUES (#{param.userId}, #{param.token})"})
	void insert(@Param("param") Token param);

	@Delete({"DELETE TOKEN WHERE USERID  = #{userId}"})
	void deleteByUserId(@Param("userId") String userId);

	@Delete({"DELETE TOKEN WHERE USERID IN (SELECT USERID WHERE TOKEN = #{token})"})
	void deleteByToken(@Param("token") String token);
}
