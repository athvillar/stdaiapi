package cn.standardai.api.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import cn.standardai.api.dao.bean.Message;

public interface MessageDao {

	@Select({"SELECT * FROM MESSAGE WHERE FROMUSERID = #{fromUserId} ORDER BY CREATETIME"})
	List<Message> selectByFromUserId(@Param("fromUserId") String fromUserId);

	@Select({"SELECT * FROM MESSAGE WHERE TOUSERID = #{toUserId} ORDER BY CREATETIME"})
	List<Message> selectByToUserId(@Param("toUserId") String toUserId);

	@Select({"SELECT * FROM MESSAGE WHERE FROMUSERID = #{fromUserId} AND TOUSERID = #{toUserId} ORDER BY CREATETIME"})
	List<Message> selectByBothUserId(@Param("fromUserId") String fromUserId, @Param("toUserId") String toUserId);

	@Select({"SELECT * FROM MESSAGE WHERE FROMUSERID = #{fromUserId} AND ISREAD = FALSE ORDER BY CREATETIME"})
	List<Message> selectUnreadByFromUserId(@Param("fromUserId") String fromUserId);

	@Select({"SELECT * FROM MESSAGE WHERE TOUSERID = #{toUserId} AND ISREAD = FALSE ORDER BY CREATETIME"})
	List<Message> selectUnreadByToUserId(@Param("toUserId") String toUserId);

	@Select({"SELECT * FROM MESSAGE WHERE FROMUSERID = #{fromUserId} AND TOUSERID = #{toUserId} AND ISREAD = FALSE ORDER BY CREATETIME"})
	List<Message> selectUnreadByBothUserId(@Param("fromUserId") String fromUserId, @Param("toUserId") String toUserId);

	@Update({"UPDATE MESSAGE SET ISREAD = TRUE WHERE MESSAGEID = #{messageId}"})
	void updateIsReadById(@Param("messageId") String messageId);

	@Insert({"INSERT INTO MESSAGE (MESSAGEID, FROMUSERID, TOUSERID, MESSAGE, ISREAD, CREATETIME) ",
		"VALUES (#{param.messageId}, #{param.fromUserId}, #{param.toUserId}, ",
		"#{param.message}, #{param.isRead}, NOW())"})
	void insert(@Param("param") Message param);

	@Delete({"DELETE FROM MESSAGE WHERE FROMUSERID = #{fromUserId}"})
	void deleteByFromUserId(@Param("fromUserId") String fromUserId);

	@Delete({"DELETE FROM MESSAGE WHERE TOUSERID = #{toUserId}"})
	void deleteByToUserId(@Param("toUserId") String toUserId);
}
