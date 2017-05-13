package cn.standardai.api.biz.agent;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import cn.standardai.api.biz.exception.BizException;
import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.dao.ImageDao;
import cn.standardai.api.dao.base.DaoHandler;
import cn.standardai.api.dao.bean.Image;

public class ImageAgent extends AuthAgent {

	private DaoHandler daoHandler = new DaoHandler(true);

	private final int insertCount = 10;

	public JSONObject importImage(JSONObject request) throws BizException {
		JSONObject result = new JSONObject();

		try {
			// input file path
			String inputFilePath = Context.getProp().getLocal().getImagePath() + request.getString("imageName") + "."
					+ request.getString("imageFormat");
			BufferedImage bufferedImage = ImageIO.read(new File(inputFilePath));
			if (bufferedImage.getWidth() != Context.getProp().getLocal().getImageWidth()) {
				throw new BizException("Image width is not " + Context.getProp().getLocal().getImageWidth());
			}
			if (bufferedImage.getHeight() != Context.getProp().getLocal().getImageHeight()) {
				throw new BizException("Image height is not " + Context.getProp().getLocal().getImageHeight());
			}

			ImageDao imageDao = daoHandler.getMySQLMapper(ImageDao.class);
			List<Image> insertList = new ArrayList<Image>();
			for (int x = 0; x < bufferedImage.getWidth(); x++) {
				for (int y = 0; y < bufferedImage.getHeight(); y++) {
					Image imageParam = makeImageParam(request.getString("imageType"), x, y, bufferedImage.getRGB(x, y),
							null, request.getString("userId"), "0");
					insertList.add(imageParam);
					if (insertList.size() == insertCount) {
						imageDao.insert(insertList);
						insertList.clear();
					}
				}
			}
			if (insertList.size() > 0) {
				imageDao.insert(insertList);
			}
		} catch (IOException e) {
			throw new BizException("Failed to Image open.");
		}

		// make result
		result.put("result", "success");
		return result;
	}

	private Image makeImageParam(String imageType, int x, int y, int pixel, String color, String userId,
			String status) {
		Image imageParam = new Image();
		imageParam.setImageType(imageType);
		imageParam.setxAxis(x);
		imageParam.setyAxis(y);
		if (StringUtil.isEmpty(color)) {
			imageParam.setColor(toHexValue(pixel));
		} else {
			imageParam.setColor(color);
		}
		imageParam.setStatus(status);
		imageParam.setUserId(userId);
		imageParam.setCreateTime(new Date());
		return imageParam;
	}

	private String toHexValue(int pixelNum) {
		int red = (pixelNum & 0xff0000) >> 16;
		int green = (pixelNum & 0xff00) >> 8;
		int blue = (pixelNum & 0xff);
		StringBuilder redBuilder = new StringBuilder(Integer.toHexString(red & 0xff));
		if (redBuilder.length() < 2) {
			redBuilder.insert(0, "0");
		}
		StringBuilder greenBuilder = new StringBuilder(Integer.toHexString(green & 0xff));
		if (greenBuilder.length() < 2) {
			greenBuilder.insert(0, "0");
		}
		StringBuilder blueBuilder = new StringBuilder(Integer.toHexString(blue & 0xff));
		if (blueBuilder.length() < 2) {
			blueBuilder.insert(0, "0");
		}
		return "#" + redBuilder.toString().toUpperCase() + greenBuilder.toString().toUpperCase()
				+ blueBuilder.toString().toUpperCase();
	}

	private int toPixelValue(String colorStr) {
		String redStr = colorStr.substring(1, 3);
		String greenStr = colorStr.substring(3, 5);
		String blueStr = colorStr.substring(5, 7);
		int red = Integer.parseInt(redStr, 16);
		int green = Integer.parseInt(greenStr, 16);
		int blue = Integer.parseInt(blueStr, 16);
		Color color = new Color(red, green, blue);
		return color.getRGB();
	}

	public JSONObject updateImage(JSONObject request) throws BizException {
		JSONObject result = new JSONObject();

		// make result
		result.put("result", "success");
		return result;
	}

	public JSONObject exportImage(String imageName, String imageFormat, String imageType) {
		JSONObject result = new JSONObject();

		try {
			ImageDao imageDao = daoHandler.getMySQLMapper(ImageDao.class);
			List<Image> imagePointList = imageDao.selectByImageType(imageType);

			int width = Context.getProp().getLocal().getImageWidth();
			int Height = Context.getProp().getLocal().getImageHeight();
			BufferedImage bufferedImage = new BufferedImage(width, Height, 5);

			for (Image imageInfo : imagePointList) {
				int pixel = toPixelValue(imageInfo.getColor());
				bufferedImage.setRGB(imageInfo.getxAxis(), imageInfo.getyAxis(), pixel);
			}
			// output file path
			String outputFilePath = Context.getProp().getLocal().getImagePath() + imageName + "." + imageFormat;
			File outputImage = new File(outputFilePath);
			ImageIO.write(bufferedImage, imageFormat, outputImage);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// make result
		result.put("result", "success");
		return result;
	}

	public void done() {
		daoHandler.releaseSession();
	}
}
