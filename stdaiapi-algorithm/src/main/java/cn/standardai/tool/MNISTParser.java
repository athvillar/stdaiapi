/**
* ImageConverter.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.tool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * MNIST parser
 * @author 韩晴
 *
 */
public class MNISTParser {

	private static final int width = 28;

	private static final int height = 28;

	public static void main(String[] args) {

		String labelFile = "/Users/athvillar/Documents/work/MNIST/t10k-labels.idx1-ubyte";
		byte[] labels = getIdx1(labelFile);
		String imageFile = "/Users/athvillar/Documents/work/MNIST/t10k-images.idx3-ubyte";
		byte[] pixels = getIdx3(imageFile);

		for (int i = 0; i < 100; i++) {
			test(labels, pixels, i);
		}
	}

	private static void test(byte[] labels, byte[] pixels, int idx) {

		System.out.println(labels[idx]);

		byte[][] image1 = new byte[height][width];
		for (int i = 0; i < height; i++) {
			System.arraycopy(pixels, idx * width * height + i * width, image1[i], 0, width);
		}

		drawImage("/Users/athvillar/Documents/work/MNIST/image" + idx + ".jpg", width, height, image1);
	}

	public static byte[] getIdx1(String fileName) {

        File file = new File(fileName);
        InputStream in = null;
        try {
            int byteread = 0;
            in = new FileInputStream(file);
            // Get magic number
            byte[] magicNumber = new byte[4];
            byteread = in.read(magicNumber);
            if (byteread != 4 || magicNumber[2] != 0x08 || magicNumber[3] != 0x01) {
                System.out.println("magic number错误");
                return null;
            }
            // Get size
            byte[] sizes = new byte[4];
            byteread = in.read(sizes);
            if (byteread != 4) {
                System.out.println("size错误");
                return null;
            }
            int size = (sizes[0] & 0xFF) * 256 * 256 * 256 + (sizes[1] & 0xFF) * 256 * 256 + (sizes[2] & 0xFF) * 256 + (sizes[3] & 0xFF);
            // Get data
            byte[] data = new byte[size];
            byteread = in.read(data);
            if (byteread == -1) {
                System.out.println("data错误");
                return null;
            }
    		return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
	}

	public static byte[] getIdx3(String fileName) {

        File file = new File(fileName);
        InputStream in = null;
        try {
            int byteread = 0;
            in = new FileInputStream(file);
            // Get magic number
            byte[] magicNumber = new byte[4];
            byteread = in.read(magicNumber);
            if (byteread != 4 || magicNumber[2] != 0x08 || magicNumber[3] != 0x03) {
                System.out.println("magic number错误");
                return null;
            }
            // Get size
            byte[] sizes = new byte[12];
            byteread = in.read(sizes);
            if (byteread != 12) {
                System.out.println("size错误");
                return null;
            }
            int size1 = (sizes[0] & 0xFF) * 256 * 256 * 256 + (sizes[1] & 0xFF) * 256 * 256 + (sizes[2] & 0xFF) * 256 + (sizes[3] & 0xFF);
            int size2 = (sizes[4] & 0xFF) * 256 * 256 * 256 + (sizes[5] & 0xFF) * 256 * 256 + (sizes[6] & 0xFF) * 256 + (sizes[7] & 0xFF);
            int size3 = (sizes[8] & 0xFF) * 256 * 256 * 256 + (sizes[9] & 0xFF) * 256 * 256 + (sizes[10] & 0xFF) * 256 + (sizes[11] & 0xFF);

            // Get data
            byte[] data = new byte[size1 * size2 * size3];
            byteread = in.read(data);
            if (byteread == -1) {
                System.out.println("data错误");
                return null;
            }
    		return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
	}

	public static void drawImage(String filepath, int width, int height, byte[][] data) {

		File file = new File(filepath);
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
		//FileOutputStream fos = new FileOutputStream(new File(newFilepath));
		for (int i= 0 ; i < height ; i++) {
			for (int j = 0 ; j < width; j++) {
				int rgb = 0xFFFFFF - data[i][j] * 256 * 256 - data[i][j] * 256 - data[i][j];
				bi.setRGB(j, i, rgb);
				try {
					ImageIO.write(bi, "JPG", file);
				} catch (IOException e) {
	                System.out.println("图片保存失败(file=" + filepath + ")");
				}
			}
		}
	}
}
