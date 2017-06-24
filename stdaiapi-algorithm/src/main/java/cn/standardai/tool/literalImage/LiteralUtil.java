/**
* Literal.java
* Copyright 2017 standardai Co.ltd.
*/
package cn.standardai.tool.literalImage;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import cn.standardai.lib.base.function.Statistic;
import cn.standardai.tool.ImageUtil;
import cn.standardai.tool.ImageUtil.BVMethod;

public class LiteralUtil {

	public static void main(String[] args) {
		String file1 = "/Users/athvillar/Documents/test/test1.jpg";
		String file2 = "/Users/athvillar/Documents/test/test2.jpg";
		String path = "/Users/athvillar/Documents/test/";
		try {
			// 获得灰度值
			Integer[][] pixels = ImageUtil.getGray(file1);
			// 二值化
			Integer[][] bv = ImageUtil.binaryValue(pixels, BVMethod.localAvg);
			// 去除噪点
			bv = ImageUtil.clearNoise(bv, 1);
			// 输出二值化图片，测试用
			ImageUtil.drawGray(path + "bv1.jpg", bv);
			// 分割出文字
			List<List<Slice>> slices = LiteralUtil.cut(bv, 0.03, 0.0, 5);
			// 输出文字图片，测试用
			LiteralUtil.drawWords(bv, slices, path, 1, 38, 38);

			// 获得灰度值
			pixels = ImageUtil.getGray(file2);
			// 二值化
			bv = ImageUtil.binaryValue(pixels, BVMethod.localAvg);
			// 去除噪点
			bv = ImageUtil.clearNoise(bv, 1);
			// 输出二值化图片，测试用
			ImageUtil.drawGray(path + "bv2.jpg", bv);
			// 分割出文字
			slices = LiteralUtil.cut(bv, 0.03, 0.0, 5);
			// 输出文字图片，测试用
			LiteralUtil.drawWords(bv, slices, path, 101, 38, 38);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<List<Slice>> cut(Integer[][] pixels, double th1, double th2, int min) {

		if (pixels == null) return null;
		List<List<Slice>> slices = new ArrayList<List<Slice>>();

		// Separate lines
		Integer[] c1 = ImageUtil.grayCalculas(pixels, 0);
		List<Integer[]> yList = split(c1, th1, min);

		for (int i = 0; i < yList.size(); i++) {
			Integer[] c2 = ImageUtil.grayCalculas(pixels, 1, 0, yList.get(i)[0], pixels.length, yList.get(i)[1]);
			List<Integer[]> xList = split(c2, th2, min);
			List<Slice> words1Line = new ArrayList<Slice>();
			for (int j = 0; j < xList.size(); j++) {
				Integer[] c3 = ImageUtil.grayCalculas(pixels, 0, xList.get(j)[0], yList.get(i)[0], xList.get(j)[1], yList.get(i)[1]);
				List<Integer[]> y2List = split(c3, 0.0, min);
				if (y2List == null || y2List.size() == 0) continue;
				Slice word1 = new Slice(xList.get(j)[0], yList.get(i)[0] + y2List.get(0)[0], xList.get(j)[1], yList.get(i)[0] + y2List.get(y2List.size() - 1)[1]);
				words1Line.add(word1);
			}
			slices.add(words1Line);
		}

		return slices;
	}

	public static int drawWords(Integer[][] pixels, List<List<Slice>> slices, String path, int index, Integer width, Integer height) throws IOException {

		int idx = 0;
		index--;
		for (int i = 0; i < slices.size(); i++) {
			for (int j = 0; j < slices.get(i).size(); j++) {
				Integer[][] scope = slices.get(i).get(j).getScope(pixels);
				ImageUtil.drawGray(path + "split_" + ++index, scope);
				idx++;

				if (width == null && height == null) continue;
				int newWidth = 0;
				int newHeight = 0;
				if (width == null) {
					newWidth = scope.length * height / scope[0].length;
					newHeight = height;
				} else if (height == null) {
					newWidth = width;
					newHeight = scope[0].length * width / scope.length;
				} else {
					if ((0.0 + height) / scope[0].length >= (0.0 + width) / scope.length) {
						newWidth = width;
						newHeight = scope[0].length * width / scope.length;
					} else {
						newWidth = scope.length * height / scope[0].length;
						newHeight = height;
					}
				}
				resize(path + "split_" + index, newWidth, newHeight);
				// 获得灰度值
				Integer[][] pixels2 = ImageUtil.getGray(path + "split_" + index);
				// 二值化
				Integer[][] bv = ImageUtil.binaryValue(pixels2, BVMethod.localAvg);
				ImageUtil.drawGray(path + "split_" + index, bv);
			}
		}
		return idx;
	}

	private static List<Integer[]> split(Integer[] c, double th, int min) {
		int maxC = Statistic.max(c);
		List<Integer[]> xList = new ArrayList<Integer[]>();
		boolean open = false;
		Integer[] x1 = null;
		for (int i = 0; i < c.length; i++) {
			if (c[i] > maxC * th) {
				if (open) {
					continue;
				} else {
					x1 = new Integer[2];
					x1[0] = i;
					open = true;
				}
			} else {
				if (open) {
					if (i - x1[0] < min) {
						open = false;
						continue;
					};
					x1[1] = i;
					xList.add(x1);
					open = false;
				} else {
					continue;
				}
			}
		}
		if (open && x1[0] != c.length - 1) {
			x1[1] = c.length - 1;
			xList.add(x1);
		}
		return xList;
	}

	public static void resize(String fileName, Integer newWidth, Integer newHeight) throws IOException {

		if (newWidth == null || newWidth == null) return;

		File srcFile = new File(fileName);
		Image srcImg = ImageIO.read(srcFile);
		BufferedImage buffImg = null;
		buffImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		buffImg.getGraphics().drawImage(
				srcImg.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT), 0,
				0, null);

		ImageIO.write(buffImg, "JPEG", new File(fileName));
	}
}
