/**
* Word.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.tool.literalImage;

public class Word {

	private Integer[][] image;

	private Integer x1;

	private Integer y1;

	private Integer x2;

	private Integer y2;

	private Integer[][] scope;

	public Word(Integer[][] image, Integer x1, Integer y1, Integer x2, Integer y2) {
		this.image = image;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public Integer[][] getImage() {
		return image;
	}

	public void setImage(Integer[][] image) {
		this.image = image;
	}

	public Integer getX1() {
		return x1;
	}

	public void setX1(Integer x1) {
		this.x1 = x1;
	}

	public Integer getY1() {
		return y1;
	}

	public void setY1(Integer y1) {
		this.y1 = y1;
	}

	public Integer getX2() {
		return x2;
	}

	public void setX2(Integer x2) {
		this.x2 = x2;
	}

	public Integer getY2() {
		return y2;
	}

	public void setY2(Integer y2) {
		this.y2 = y2;
	}

	public Integer[][] getScope() {
		if (scope != null) return scope;
		if (x1 == null || y1 == null || x2 == null || y2 == null) return null;
		if (image == null) return null;

		Integer[][] scope = new Integer[x2 - x1][y2 - y1];
		for (int i = 0; i < x2 - x1; i++) {
			for (int j = 0; j < y2 - y1; j++) {
				scope[i][j] = image[x1 + i][y1 + j];
			}
		}
		setScope(scope);
		return scope;
	}

	public void setScope(Integer[][] scope) {
		this.scope = scope;
	}
}
