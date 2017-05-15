package cn.standardai.api.biz.agent;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONArray;
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

		String userId = request.getString("userId");
		String imageType = request.getString("imageType");
		JSONArray axisArray = request.getJSONArray("axis");
		List<Map<String, Object>> axisList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < axisArray.size(); i++) {
			JSONObject axisObject = axisArray.getJSONObject(i);
			Map<String, Object> axis = new HashMap<String, Object>();
			axis.put("xAxis", axisObject.getInteger("xAxis"));
			axis.put("yAxis", axisObject.getInteger("yAxis"));
			axis.put("color", axisObject.getString("color"));
			axisList.add(axis);
		}

		ImageDao imageDao = daoHandler.getMySQLMapper(ImageDao.class);
		// 检索可用像素数
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("imageType", imageType);
		params1.put("status", "0");
		int count = imageDao.selectCount(params1);
		if (count < axisList.size()) {
			throw new BizException("Image available axis count not enough");
		}
		// 检索像素是否被占用
		Map<String, Object> params2 = new HashMap<String, Object>();
		params2.put("imageType", imageType);
		params2.put("status", "1");
		params2.put("isUserId", "1");
		params2.put("userId", userId);
		params2.put("axisList", axisList);
		List<Image> imagePointList = imageDao.select(params2);
		if (imagePointList != null && imagePointList.size() > 0) {
			throw new BizException("Image axis occupied");
		}

		for (Map<String, Object> axis : axisList) {
			Image imageParam = makeImageParam(imageType, Integer.parseInt(String.valueOf(axis.get("xAxis"))),
					Integer.parseInt(String.valueOf(axis.get("yAxis"))), 0, String.valueOf(axis.get("color")), userId,
					"1");
			imageDao.updateByAxis(imageParam);
		}

		// make result
		result.put("result", "success");
		return result;
	}

	public JSONObject exportImage(String imageName, String imageFormat, String imageType) {
		JSONObject result = new JSONObject();

		try {
			ImageDao imageDao = daoHandler.getMySQLMapper(ImageDao.class);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("imageType", imageType);
			List<Image> imagePointList = imageDao.select(params);

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
