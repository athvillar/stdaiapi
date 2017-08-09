package cn.standardai.tool.trafficimage;

public class Ankor {

	public Integer x;

	public Integer y;

	public String name;

	public Ankor(String name, Integer x, Integer y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}