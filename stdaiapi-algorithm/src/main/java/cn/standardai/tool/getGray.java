/**
* ImageConverter.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.tool;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;

import javax.imageio.ImageIO;

/**
 * 图片处理
 * @author 韩晴
 *
 */
public class getGray {

	public static void convertImageBinary(String filepath, String newFilepath) throws IOException{

		File file = new File(filepath);
		BufferedImage image = ImageIO.read(file);

		int width = image.getWidth();
		int height = image.getHeight();

		FileOutputStream fos = new FileOutputStream(new File(newFilepath));
		for (int i= 0 ; i < width ; i++) {
			for (int j = 0 ; j < height; j++) {
				int rgb = image.getRGB(i, j);
				fos.write(Byte.parseByte(""+rgb+"\t"));
			}
		}

		fos.close();
	}

	public static void getGray(String baseDir) {

		String srcDir = baseDir;
		String desDir = baseDir + File.separator + "destiny";
		
		// 清空目录
		clearDir(desDir);

		File root = new File(srcDir);
		File[] files = root.listFiles();
		for (File file:files) {
			if (!file.isDirectory()) {
				try {
					convertImageBinary(file.getAbsolutePath(), desDir + File.separator + file.getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void clearDir(String dir) { 

		File root = new File(dir);
		if (!root.exists()) {
			root.mkdir();
			return;
		}

		File[] files = root.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	public static void main(String[] args) {
		String trainDataDir = "C:\\work\\trainImages";
		getGray(trainDataDir);
	}
}
