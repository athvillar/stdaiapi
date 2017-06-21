package cn.standardai.api.core.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import cn.standardai.api.dao.base.LocalSqlSessionFactory.Mysql;
import cn.standardai.api.dao.base.LocalSqlSessionFactory.Oracle;

@Configuration
@ConfigurationProperties()
public class PropertyConfig {

	private Local local;

	private Oracle oracle;

	private Mysql mysql;

	private ElasticSearch elasticsearch;

	private Redis redis;

	private Scheduler scheduler;

	private Url url;
	
	public static class Local {

		private String parkId;
		
		private String db;
		
		private String uploadTemp;
		
		private String debugTemp;
		
		private String imagePath;
		
		private Integer imageWidth;
		
		private Integer imageHeight;

		private Boolean debug;

		public String getParkId() {
			return parkId;
		}

		public void setParkId(String parkId) {
			this.parkId = parkId;
		}

		public String getDb() {
			return db;
		}

		public void setDb(String db) {
			this.db = db;
		}

		public String getUploadTemp() {
			return uploadTemp;
		}

		public void setUploadTemp(String uploadTemp) {
			this.uploadTemp = uploadTemp;
		}

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}

		public Integer getImageWidth() {
			return imageWidth;
		}

		public void setImageWidth(Integer imageWidth) {
			this.imageWidth = imageWidth;
		}

		public Integer getImageHeight() {
			return imageHeight;
		}

		public void setImageHeight(Integer imageHeight) {
			this.imageHeight = imageHeight;
		}

		public String getDebugTemp() {
			return debugTemp;
		}

		public void setDebugTemp(String debugTemp) {
			this.debugTemp = debugTemp;
		}

		public Boolean getDebug() {
			return debug;
		}

		public void setDebug(Boolean debug) {
			this.debug = debug;
		}
	}

	public static class ElasticSearch {

		private String host;

		private String port;

		private String cluster;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		public String getCluster() {
			return cluster;
		}

		public void setCluster(String cluster) {
			this.cluster = cluster;
		}
	}

	public static class Redis {

		private Integer database;

		private String host;

		private Integer port;

		private String password;

		private Integer poolMaxActive;
		
		private Integer poolMaxWait;
		
		private Integer poolMaxIdle;
		
		private Integer poolMinIdle;
		
		private Integer timeout;
		
		private String datapointArithmetic;
		
		private String datapointAll;
		
		private Integer retryCount = 0;
		
		private Integer retryTime = 0;

		public Integer getDatabase() {
			return database;
		}

		public void setDatabase(Integer database) {
			this.database = database;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Integer getPoolMaxActive() {
			return poolMaxActive;
		}

		public void setPoolMaxActive(Integer poolMaxActive) {
			this.poolMaxActive = poolMaxActive;
		}

		public Integer getPoolMaxWait() {
			return poolMaxWait;
		}

		public void setPoolMaxWait(Integer poolMaxWait) {
			this.poolMaxWait = poolMaxWait;
		}

		public Integer getPoolMaxIdle() {
			return poolMaxIdle;
		}

		public void setPoolMaxIdle(Integer poolMaxIdle) {
			this.poolMaxIdle = poolMaxIdle;
		}

		public Integer getPoolMinIdle() {
			return poolMinIdle;
		}

		public void setPoolMinIdle(Integer poolMinIdle) {
			this.poolMinIdle = poolMinIdle;
		}

		public Integer getTimeout() {
			return timeout;
		}

		public void setTimeout(Integer timeout) {
			this.timeout = timeout;
		}

		public String getDatapointArithmetic() {
			return datapointArithmetic;
		}

		public void setDatapointArithmetic(String datapointArithmetic) {
			this.datapointArithmetic = datapointArithmetic;
		}

		public String getDatapointAll() {
			return datapointAll;
		}

		public void setDatapointAll(String datapointAll) {
			this.datapointAll = datapointAll;
		}

		public Integer getRetryCount() {
			return retryCount;
		}

		public void setRetryCount(Integer retryCount) {
			this.retryCount = retryCount;
		}

		public Integer getRetryTime() {
			return retryTime;
		}

		public void setRetryTime(Integer retryTime) {
			this.retryTime = retryTime;
		}
	}

	public static class Scheduler {
		
		private String datapoint;
		
		private String datapointArithmetic;
		
		private String datapointAll;
		
		private Integer retryCount = 0;
		
		private Integer retryTime = 0;

		public String getDatapoint() {
			return datapoint;
		}

		public void setDatapoint(String datapoint) {
			this.datapoint = datapoint;
		}

		public Integer getRetryCount() {
			return retryCount;
		}

		public void setRetryCount(Integer retryCount) {
			this.retryCount = retryCount;
		}

		public Integer getRetryTime() {
			return retryTime;
		}

		public void setRetryTime(Integer retryTime) {
			this.retryTime = retryTime;
		}

		public String getDatapointArithmetic() {
			return datapointArithmetic;
		}

		public void setDatapointArithmetic(String datapointArithmetic) {
			this.datapointArithmetic = datapointArithmetic;
		}

		public String getDatapointAll() {
			return datapointAll;
		}

		public void setDatapointAll(String datapointAll) {
			this.datapointAll = datapointAll;
		}
	}

	public Oracle getOracle() {
		return oracle;
	}

	public void setOracle(Oracle oracle) {
		this.oracle = oracle;
	}

	public Mysql getMysql() {
		return mysql;
	}

	public void setMysql(Mysql mysql) {
		this.mysql = mysql;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public ElasticSearch getElasticsearch() {
		return elasticsearch;
	}

	public void setElasticsearch(ElasticSearch elasticsearch) {
		this.elasticsearch = elasticsearch;
	}

	public Redis getRedis() {
		return redis;
	}

	public void setRedis(Redis redis) {
		this.redis = redis;
	}

	public Local getLocal() {
		return local;
	}

	public void setLocal(Local local) {
		this.local = local;
	}

	public Url getUrl() {
		return url;
	}

	public void setUrl(Url url) {
		this.url = url;
	}

	public static class Url {

		private String ash;

		private String biz;

		private String data;

		private String math;

		private String ml;

		public String getAsh() {
			return ash;
		}

		public void setAsh(String ash) {
			this.ash = ash;
		}

		public String getBiz() {
			return biz;
		}

		public void setBiz(String biz) {
			this.biz = biz;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getMath() {
			return math;
		}

		public void setMath(String math) {
			this.math = math;
		}

		public String getMl() {
			return ml;
		}

		public void setMl(String ml) {
			this.ml = ml;
		}
	}
}
