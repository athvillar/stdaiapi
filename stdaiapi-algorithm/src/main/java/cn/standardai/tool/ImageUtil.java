/**
* ImageUtil.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.tool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import cn.standardai.lib.base.function.Statistic;

public class ImageUtil {

	public enum BVMethod { middle, avg, localAvg, mean };

	public static void main(String[] args) {
		String file1 = "/Users/athvillar/Documents/test/test1.jpg";
		String file2 = "/Users/athvillar/Documents/test/test2.jpg";
		String file3 = "/Users/athvillar/Documents/test/test3.jpg";
		try {
			Integer[] c1 = grayCalculas(getGray(file1), 0);
			drawCalculas(file2, c1, 0);
			Integer[] c2 = grayCalculas(getGray(file1), 1);
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

	public static Integer[][] binaryValue(Integer[][] src, BVMethod method) {
		switch (method) {
		case middle:
			return binaryValue(src, 90);
		case avg:
			int avg = Statistic.avg(src);
			return binaryValue(src, avg);
		case localAvg:
			return localBinaryValue(src, 7, 2);
		case mean:
			int oldMean, mean = 127;
			while (true) {
				oldMean = mean;
				int oSum = 0;
				int oNum = 0;
				int bSum = 0;
				int bNum = 0;
				for (int i = 0; i < src.length; i++) {
					for (int j = 0; j < src[i].length; j++) {
						if (src[i][j] > mean) {
							bSum += src[i][j];
							bNum++;
						} else {
							oSum += src[i][j];
							oNum++;
						}
					}
				}
				mean = (bSum / bNum + oSum / oNum) / 2;
				if (mean == oldMean) break;
			}
			return binaryValue(src, mean);
		default:
			return null;
		}
	}

	public static Integer[][] binaryValue(Integer[][] src, int th) {
		Integer[][] dst = new Integer[src.length][];
		for (int i = 0; i < dst.length; i++) {
			dst[i] = new Integer[src[i].length];
			for (int j = 0; j < dst[i].length; j++) {
				dst[i][j] = src[i][j] > th ? 255 : 0;
			}
		}
		return dst;
	}

	public static Integer[][] localBinaryValue(Integer[][] src, int spat, int dens) {

		Integer[][] avg = new Integer[src.length][];
		for (int i = 0; i < avg.length; i++) {
			avg[i] = new Integer[src[i].length];
			for (int j = 0; j < avg[i].length; j++) {
				int num = 0;
				int sum = 0;
				for (int i2 = i - spat; i2 <= i + spat; i2++) {
					for (int j2 = j - spat; j2 <= j + spat; j2++) {
						if (i2 < 0 || i2 >= src.length || j2 < 0 || j2 >= src[0].length) continue;
						num++;
						sum += src[i2][j2];
					}
				}
				avg[i][j] = sum / num;
			}
		}
		Integer[][] dst = new Integer[src.length][];
		for (int i = 0; i < dst.length; i++) {
			dst[i] = new Integer[src[i].length];
			for (int j = 0; j < dst[i].length; j++) {
				dst[i][j] = src[i][j] >= avg[i][j] - spat * dens ? 255 : 0;
			}
		}
		return dst;
	}

	public static Integer[][] clearNoise(Integer[][] src, int size) {

		Integer[][] dst = new Integer[src.length][];
		for (int i = 0; i < src.length; i++) {
			dst[i] = new Integer[src[i].length];
			for (int j = 0; j < src[i].length; j++) {
				int sum1 = 0;
				int sum2 = 0;
				for (int i2 = i - size; i2 <= i + size; i2++) {
					for (int j2 = j - size; j2 <= j + size; j2++) {
						if (i2 < 0 || i2 >= src.length || j2 < 0 || j2 >= src[0].length) continue;
						if (src[i2][j2] == 0) {
							sum1++;
						} else if (src[i2][j2] == 255) {
							sum2++;
						}
					}
				}
				dst[i][j] = sum1 > sum2 ? 0 : 255;
			}
		}
		return dst;
	}

	public static Integer[][][] getR(String filepath) throws IOException{

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
				int R = (rgb & 0xff0000 ) >> 16;
				//int G = (rgb & 0xff00 ) >> 8;
				//int B = (rgb & 0xff );
				data[i][j][0] = R;
			}
		}

		return data;
	}

	public static Integer[][] getGray(BufferedImage imageBuffer) throws IOException{

		int width = imageBuffer.getWidth();
		int height = imageBuffer.getHeight();
		Integer[][] data = new Integer[width][height];
		for (int i= 0 ; i < width ; i++) {
			for (int j = 0 ; j < height; j++) {
				int rgb = imageBuffer.getRGB(i, j);
				int R = (rgb & 0xff0000) >> 16;
				int G = (rgb & 0xff00) >> 8;
				int B = (rgb & 0xff);
				data[i][j] = (R + G + B) / 3;
			}
		}
		return data;
	}

	public static Integer[][] getGray(String filepath) throws IOException{
		File file = new File(filepath);
		File file2 = new File(file.getAbsolutePath());
		BufferedImage imageBuffer = ImageIO.read(file2);
		return getGray(imageBuffer);
	}

	public static Integer[][] getGray(MultipartFile imageFile) throws IOException {

		Integer[][] imagePixel = null;
		ByteArrayInputStream byteInputStream = null;
		BufferedImage imageBuffer = null;
		try {
			byteInputStream = new ByteArrayInputStream(imageFile.getBytes());
			imageBuffer = ImageIO.read(byteInputStream);
			imagePixel = getGray(imageBuffer);
		} catch (IOException e) {
			throw e;
		} finally {
			if (byteInputStream != null) {
				byteInputStream.close();
			}
		}

		return imagePixel;
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
