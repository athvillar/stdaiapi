package cn.standardai.api.ml.agent;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.ml.exception.DataException;
import cn.standardai.tool.literalImage.LiteralUtil;
import cn.standardai.tool.literalImage.Word;

public class FormulaAgent extends AuthAgent {

	public Integer[][] parse(MultipartFile[] uploadfiles) throws DataException {
		Integer[][] imagePixel = null;
		if (uploadfiles != null && uploadfiles.length != 0) {
			MultipartFile imageFile = uploadfiles[0];
			ByteArrayInputStream byteInputStream = null;
			BufferedImage bufferedImage = null;
			try {
				byteInputStream = new ByteArrayInputStream(imageFile.getBytes());
				bufferedImage = ImageIO.read(byteInputStream);

				int width = bufferedImage.getWidth();
				int height = bufferedImage.getHeight();
				imagePixel = new Integer[width][height];

				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int rgb = bufferedImage.getRGB(i, j);
						int R = (rgb & 0xff0000) >> 16;
						int G = (rgb & 0xff00) >> 8;
						int B = (rgb & 0xff);
						imagePixel[i][j] = (R + G + B) / 3;
					}
				}
			} catch (IOException e) {
				throw new DataException("读取图片数据异常", e);
			} finally {
				if (byteInputStream != null) {
					try {
						byteInputStream.close();
					} catch (IOException e) {
						throw new DataException("读取图片数据异常", e);
					}
				}
			}
		}
		return imagePixel;
	}

	public JSONObject check(Integer[][] gray, String modelName) {

		List<List<Word>> words = LiteralUtil.cut(gray, 0.0, 0.0);
		return null;
	}
}
