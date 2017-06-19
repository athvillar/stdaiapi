/**
* ImageConverter.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.tool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.standardai.lib.base.function.Statistic;

public class Image2Data {

	public static void main(String[] args) {
		String file1 = "/Users/athvillar/Documents/test/test1.jpg";
		String file2 = "/Users/athvillar/Documents/test/test2.jpg";
		String file3 = "/Users/athvillar/Documents/test/test3.jpg";
		try {
			Integer[] c1 = grayCalculas(getGray2(file1), 0);
			drawCalculas(file2, c1, 0);
			Integer[] c2 = grayCalculas(getGray2(file1), 1);
			drawCalculas(file3, c2, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Integer[][][] getRGB(String filepath) throws IOException{

		File file = new File(filepath);
		File file2 = new File(file.getAbsolutePath());
		BufferedImage image = ImageIO.read(file2);

		int width = image.getWidth();
		int height = image.getHeight();
		Integer[][][] data = new Integer[width][height][3];

		//FileOutputStream fos = new FileOutputStream(new File(newFilepath));
		for (int i= 0 ; i < width ; i++) {
			for (int j = 0 ; j < height; j++) {
				int rgb = image.getRGB(i, j);
				int R =(rgb & 0xff0000 ) >> 16 ;
				int G= (rgb & 0xff00 ) >> 8 ;
				int B= (rgb & 0xff );
				data[i][j][0] = R;
				data[i][j][1] = G;
				data[i][j][2] = B;
			}
		}

		return data;
	}

	public static Integer[][][] getGray(String filepath) throws IOException{

		File file = new File(filepath);
		File file2 = new File(file.getAbsolutePath());
		BufferedImage image = ImageIO.read(file2);

		int width = image.getWidth();
		int height = image.getHeight();
		Integer[][][] data = new Integer[width][height][1];

		//FileOutputStream fos = new FileOutputStream(new File(newFilepath));
		for (int i= 0 ; i < width ; i++) {
			for (int j = 0 ; j < height; j++) {
				int rgb = image.getRGB(i, j);
				int R = (rgb & 0xff0000 ) >> 16 ;
				int G = (rgb & 0xff00 ) >> 8 ;
				int B = (rgb & 0xff );
				data[i][j][0] = R;
			}
		}

		return data;
	}

	public static Integer[][] getGray2(String filepath) throws IOException{

		File file = new File(filepath);
		File file2 = new File(file.getAbsolutePath());
		BufferedImage image = ImageIO.read(file2);

		int width = image.getWidth();
		int height = image.getHeight();
		Integer[][] data = new Integer[width][height];

		//FileOutputStream fos = new FileOutputStream(new File(newFilepath));
		for (int i= 0 ; i < width ; i++) {
			for (int j = 0 ; j < height; j++) {
				int rgb = image.getRGB(i, j);
				int R = (rgb & 0xff0000 ) >> 16 ;
				int G = (rgb & 0xff00 ) >> 8 ;
				int B = (rgb & 0xff );
				data[i][j] = (R + G + B) / 3;
			}
		}

		return data;
	}

	public static void drawGray(String fileName, Integer[][] pixels) throws IOException{

		int imageWidth = pixels.length;
		int imageHeight = pixels[0].length;
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < imageWidth; i++) {
			for (int j = 0; j < imageHeight; j++) {
				image.setRGB(i, j, pixels[i][j] * 256 * 256 + pixels[i][j] * 256 + pixels[i][j]);
			}
		}
		ImageIO.write(image, "PNG", new File(fileName));
	}

	public static Integer[] grayCalculas(Integer[][] gray, int direction) {
		return grayCalculas(gray, direction, null, null, null, null);
	}

	public static Integer[] grayCalculas(Integer[][] gray, int direction, Integer x1, Integer y1, Integer x2, Integer y2) {

		if (gray == null || gray.length == 0) return null;
		if (x1 == null) x1 = 0;
		if (y1 == null) y1 = 0;
		if (x2 == null) x2 = gray.length;
		if (y2 == null) y2 = gray[0].length;
		Double[][] newValue = normalize(gray);

		Integer[] calculas;
		if (direction == 0) {
			calculas = new Integer[y2 - y1];
			for (int i = 0; i < calculas.length; i++) {
				calculas[i] = 0;
				for (int j = x1; j < x2; j++) {
					if (newValue[j][y1 + i] < 0.5) calculas[i]++;
				}
			}
		} else {
			calculas = new Integer[x2 - x1];
			for (int i = 0; i < calculas.length; i++) {
				calculas[i] = 0;
				for (int j = y1; j < y2; j++) {
					if (newValue[x1 + i][j] < 0.5) calculas[i]++;
				}
			}
		}

		return calculas;
	}

	public static void drawCalculas(String fileName, Integer[] calculas, int direction) throws IOException {

		if (calculas == null || calculas.length == 0) return;
		Integer[][] pixels;

		if (direction == 0) {
			pixels = new Integer[Statistic.max(calculas)][calculas.length];
			for (int i = 0; i < pixels.length; i++) {
				for (int j = 0; j < pixels[i].length; j++) {
					if (calculas[j] > i) {
						pixels[i][j] = 0;
					} else {
						pixels[i][j] = 255;
					}
				}
			}
		} else {
			pixels = new Integer[calculas.length][Statistic.max(calculas)];
			for (int i = 0; i < pixels.length; i++) {
				for (int j = 0; j < pixels[i].length; j++) {
					if (calculas[i] > j) {
						pixels[i][j] = 0;
					} else {
						pixels[i][j] = 255;
					}
				}
			}
		}

		drawGray(fileName, pixels);
	}

	private static Double[][] normalize(Integer[][] value) {

		if (value == null || value.length == 0) return null;
		int max = Statistic.max(value);
		int min = Statistic.min(value);
		int differ = max - min;

		Double[][] newValue = new Double[value.length][value[0].length];
		for (int i = 0; i < value.length; i++) {
			for (int j = 0; j < value[i].length; j++) {
				newValue[i][j] = (0.0 + value[i][j] - min) / differ;
			}
		}

		return newValue;
	}
}
