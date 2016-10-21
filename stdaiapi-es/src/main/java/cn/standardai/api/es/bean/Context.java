package cn.standardai.api.es.bean;

public class Context {

	private String elasticsearchHost;

	private int elasticsearchPort;

	private String elasticsearchCluster;

	public String getElasticsearchHost() {
		return elasticsearchHost;
	}

	public void setElasticsearchHost(String elasticsearchHost) {
		this.elasticsearchHost = elasticsearchHost;
	}

	public int getElasticsearchPort() {
		return elasticsearchPort;
	}

	public void setElasticsearchPort(int elasticsearchPort) {
		this.elasticsearchPort = elasticsearchPort;
	}

	public String getElasticsearchCluster() {
		return elasticsearchCluster;
	}

	public void setElasticsearchCluster(String elasticsearchCluster) {
		this.elasticsearchCluster = elasticsearchCluster;
	}
}
