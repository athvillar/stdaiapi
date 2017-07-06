package cn.standardai.tool.literalImage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import sun.misc.BASE64Encoder;

public class ImageBean {

	private Integer[][] pixels;

	private String fileName;

	private String base64;

	public ImageBean(Integer[][] pixels) {
		this.pixels = pixels;
	}

	public void draw(String fileName) throws IOException {

		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		try {
			int imageWidth = pixels.length;
			int imageHeight = pixels[0].length;
			BufferedImage imageBuffer = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < imageWidth; i++) {
				for (int j = 0; j < imageHeight; j++) {
					imageBuffer.setRGB(i, j, pixels[i][j] * 256 * 256 + pixels[i][j] * 256 + pixels[i][j]);
				}
			}
			ImageIO.write(imageBuffer, "jpg", new File(fileName));
			ImageIO.write(imageBuffer, "jpg", byteOutputStream);

			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encodeBuffer(byteOutputStream.toByteArray()).trim();
			base64 = base64.replaceAll("\r\n", "");

			this.fileName = fileName;
			this.base64 = base64;

		} catch (IOException e) {
			throw e;
		} finally {
			if (byteOutputStream != null) {
				try {
					byteOutputStream.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	public Integer[][] getPixels() {
		return pixels;
	}

	public String getFileName() {
		return fileName;
	}

	public String getBase64() throws IOException {
		return base64;
	}
}