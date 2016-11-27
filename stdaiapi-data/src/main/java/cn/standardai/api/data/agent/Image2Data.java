/**
* ImageConverter.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.api.data.agent;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 图片处理
 * @author 韩晴
 *
 */
public class Image2Data {

	/*
	public static void main(String[] args) {
		String file = "/Users/athvillar/Downloads/q1.jpg";
		try {
			Integer[][][] d1 = getRGB(file);
			for (int i = 0; i < d1.length; i++) {
				for (int j = 0; j < d1[i].length; j++) {
					for (int k = 0; k < d1[i][j].length; k++) {
						System.out.print(d1[i][j][k] + ",");
					}
					System.out.println();
				}
				System.out.println("--------");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

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
				int R =(rgb & 0xff0000 ) >> 16 ;
				int G= (rgb & 0xff00 ) >> 8 ;
				int B= (rgb & 0xff );
				data[i][j][0] = R;
			}
		}

		return data;
	}
}
