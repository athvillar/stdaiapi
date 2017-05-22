package cn.standardai.api.ml.filter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.ml.exception.FilterException;

public class RGBImageFilter extends ImageFilter<Integer[][][]> {

	@Override
	public Integer[][][] encode(String s) throws FilterException {

		File file = new File(s);
		//File file2 = new File(file.getAbsolutePath());
		BufferedImage image;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			throw new FilterException("文件读取失败", e);
		}

		int width = image.getWidth();
		int height = image.getHeight();
		Integer[][][] data = new Integer[width][height][3];

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

	@Override
	public String decode(Integer[][][] data) throws FilterException {

		int imageWidth = data.length;
		int imageHeight = data[0].length;
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < imageWidth; i++) {
			for (int j = 0; j < imageHeight; j++) {
				image.setRGB(i, j, data[i][j][0] + data[i][j][1] + data[i][j][2]);
			}
		}
		String fileName = MathUtil.random(32);
		try {
			ImageIO.write(image, "PNG", new File(this.filePath + "/" + fileName));
		} catch (IOException e) {
			throw new FilterException("文件写入失败", e);
		}

		return fileName;
	}

	@Override
	public String getDescription() {
		return "根据图片文件路径读取图片，并将图像每个像素的RGB值转化为1～255之间的Integer类型，输出三维数组，数组大小为图像的长＊高＊3。";
	}
}
