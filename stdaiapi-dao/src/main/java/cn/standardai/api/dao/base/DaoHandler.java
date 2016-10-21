package cn.standardai.api.dao.base;

import org.apache.ibatis.session.SqlSession;

public class DaoHandler {

	private SqlSession ss4Mysql;

	private boolean autoCommit = true;

	public DaoHandler() {
		super();
	}

	public DaoHandler(boolean autoCommit) {
		super();
		this.autoCommit = autoCommit;
	}

	private SqlSession getMySQLSession() {
		if (ss4Mysql != null) return ss4Mysql;
		return (ss4Mysql = LocalSqlSessionFactory.getMySQLInstance(autoCommit));
	}

	public <T> T getMySQLMapper(Class<T> type) {
		return getMySQLSession().getMapper(type);
	}

	public <T> T getMapperByParkId(String parkId, Class<T> type) {
		return getMySQLSession().getMapper(type);
	}

	public void commit() {
		if (ss4Mysql != null) ss4Mysql.commit();
	}

	public void rollback() {
		if (ss4Mysql != null) ss4Mysql.rollback();
	}

	public void releaseSession() {
		if (ss4Mysql != null) {
			ss4Mysql.close();
			ss4Mysql = null;
		}
	}
}
