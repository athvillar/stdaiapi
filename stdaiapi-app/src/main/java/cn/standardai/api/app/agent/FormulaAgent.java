package cn.standardai.api.app.agent;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.app.agent.HttpHandler.HttpMethod;
import cn.standardai.api.app.bean.TreeNode;
import cn.standardai.api.app.exception.AppException;
import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.core.util.MathUtil;
import cn.standardai.api.dao.DataDao;
import cn.standardai.api.dao.DatasetDao;
import cn.standardai.api.dao.bean.Data;
import cn.standardai.api.dao.bean.Dataset;
import cn.standardai.api.ml.agent.DnnAgent;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.tool.ImageUtil;
import cn.standardai.tool.ImageUtil.BVMethod;
import cn.standardai.tool.literalImage.ImageBean;
import cn.standardai.tool.literalImage.LiteralUtil;
import cn.standardai.tool.literalImage.Slice;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class FormulaAgent extends AuthAgent {

	private static final String TYPE_FILE = "FILE";

	private final String userId = "hanqing";

	private final String datasetName = "formula";

	private final String modelName = "formula";

	private final int k = 1;

	private final double yTh = 0.03;

	private final double xTh = 0.0;

	private final int minPixel = 5;

	private final int width = 48;

	private final int height = 48;

	private final Integer trainSeconds = 20;

	/*
	public static void main(String[] args) {
		String[] cs = new String[] {
				"1+1=2",
				"11+23=34",
				"1-11+111=101",
				"5*(-1)=-5",
				"3*4-5=7",
				"3+4*5=23",
				"(3+4)*5=35",
				"3+(4*5)=23",
				"33+(4*(50+20))=313",
				"-1+(4*(3+2))+11=30"
		};
		String[] ws = new String[] {
				"1+1=3",
				"11+23=33",
				"1-11+111=102",
				"5*(-1)=-4",
				"3*4-5=8",
				"3+4*5=22",
				"(3+4)*5=32",
				"3+(4*5)=22",
				"33+(4*(50+20))=311",
				"131=31"
		};
		String[] es = new String[] {
				"1+1=",
				"11+234",
				"1-11+=101",
				"5*-1)=-5",
				"*4-5=7",
				"3+*5=23",
				"(3+4*5=35",
				"3+((4*5)=23",
				"33+(4*(50+20)))=313"
		};
		for (String s : cs) {
			String r = new FormulaAgent().check(s.split(""));
			if (!"正确".equals(r)) {
				System.out.println("该对的不对");
				System.out.println(s);
			}
		}
		for (String s : ws) {
			String r = new FormulaAgent().check(s.split(""));
			if (!"错误".equals(r)) {
				System.out.println("不该对的对了");
				System.out.println(s);
			}
		}
		for (String s : es) {
			String r = new FormulaAgent().check(s.split(""));
			if (!r.startsWith("表达式错误")) {
				System.out.println("看不懂还看");
				System.out.println(s);
			}
		}
	}
	*/

	public JSONObject splitImage(MultipartFile[] uploadFiles) throws AppException {

		int startIdx = 1;
		JSONObject jResult = new JSONObject();
		if (uploadFiles == null || uploadFiles.length == 0) return jResult;

		try {
			JSONArray jImages = new JSONArray();
			for (MultipartFile file1 : uploadFiles) {
				JSONObject jImage1 = new JSONObject();
				String imageId = MathUtil.random(8);
				// 获得灰度值
				Integer[][] gray = ImageUtil.getGray(file1);
				// 二值化
				Integer[][] bv = ImageUtil.binaryValue(gray, BVMethod.localAvg);
				// 去除噪点
				bv = ImageUtil.clearNoise(bv, k);
				// 输出二值化图片
				//ImageUtil.drawGray(Context.getProp().getLocal().getUploadTemp() + imageId + "_bv", bv);
				// 分割出文字
				List<List<Slice>> slices = LiteralUtil.cut(bv, yTh, xTh, minPixel);
				// 输出文字图片
				ImageBean[][] wordImages = LiteralUtil.drawWords(bv, slices, Context.getProp().getLocal().getUploadTemp(), imageId + "_", startIdx, width, height, true);
				// 识别文字图片
				String[][] wordString;
				try {
					wordString = recognize(wordImages);
				} catch (MLException e) {
					jImage1.put("message", e.getMessage());
					continue;
				}
				JSONArray jImage1SubImages = new JSONArray();
				for (int i = 0; i < wordImages.length; i++) {
					for (int j = 0; j < wordImages[i].length; j++) {
						JSONObject jImage1SubImage1 = new JSONObject();
						jImage1SubImage1.put("id", imageId + "_" + startIdx++);
						jImage1SubImage1.put("base64", wordImages[i][j].getBase64());
						jImage1SubImage1.put("word", wordString[i][j]);
						jImage1SubImages.add(jImage1SubImage1);
					}
				}
				jImage1.put("cnt", jImage1SubImages.size());
				jImage1.put("subImages", jImage1SubImages);
				jImages.add(jImage1);
			}
			jResult.put("images", jImages);
			return jResult;
		} catch (IOException | DnnException e) {
			throw new AppException("文件解析失败", e);
		}
	}

	public JSONObject commitImages(JSONObject request) throws AppException {

		JSONArray jImages = request.getJSONArray("images");
		if (jImages == null || jImages.size() == 0) return new JSONObject();

		DatasetDao datasetDao = daoHandler.getMySQLMapper(DatasetDao.class);

		Dataset dataset = datasetDao.selectByKey(this.datasetName, this.userId);
		if (dataset == null) throw new AppException("找不到该数据集");
		if (!TYPE_FILE.equalsIgnoreCase(dataset.getType())) {
			throw new AppException("该数据非文件类型，不支持上传文件(dataName=" + datasetName + ")");
		}
		String datasetId = dataset.getDatasetId();
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Integer baseIdx = dataDao.selectCountByDatasetId(datasetId);

		int idx = 0;
		for (int i = 0; i < jImages.size(); i++) {
			JSONArray jSubImages = jImages.getJSONObject(i).getJSONArray("subImages");
			for (int j = 0; j < jSubImages.size(); j++) {
				JSONObject jSubImage1 = jSubImages.getJSONObject(j);
				String path = Context.getProp().getLocal().getUploadTemp() + jSubImage1.getString("id");
				insertData(MathUtil.random(32), datasetId, baseIdx + idx, path, "", jSubImage1.getString("word"));
				idx++;
			}
		}

		return new JSONObject();
	}

	public JSONObject check(MultipartFile[] uploadFiles) throws AppException {

		JSONObject jResult = new JSONObject();
		if (uploadFiles == null || uploadFiles.length == 0) return jResult;

		try {
			JSONArray jImages = new JSONArray();
			for (MultipartFile file1 : uploadFiles) {
				JSONObject jImage1 = new JSONObject();
				String imageId = MathUtil.random(8);
				// 获得灰度值
				Integer[][] gray = ImageUtil.getGray(file1);
				// 二值化
				Integer[][] bv = ImageUtil.binaryValue(gray, BVMethod.localAvg);
				// 去除噪点
				bv = ImageUtil.clearNoise(bv, k);
				// 输出二值化图片
				//ImageUtil.drawGray(Context.getProp().getLocal().getUploadTemp() + imageId + "_bv", bv);
				// 分割出文字
				List<List<Slice>> slices = LiteralUtil.cut(bv, yTh, xTh, minPixel);
				// 输出文字图片
				ImageBean[][] wordImages = LiteralUtil.drawWords(bv, slices, Context.getProp().getLocal().getUploadTemp(), imageId + "_", 1, width, height, true);
				// 识别文字图片
				String[][] wordString;
				try {
					wordString = recognize(wordImages);
				} catch (MLException e) {
					jImage1.put("message", e.getMessage());
					continue;
				}
				// 检查正误
				jImages.add(check(slices, wordString));
			}
			jResult.put("images", jImages);
			return jResult;
		} catch (IOException | DnnException e) {
			throw new AppException("文件解析失败", e);
		}
	}

	private JSONObject check(List<List<Slice>> slices, String[][] words) {

		JSONArray details = new JSONArray();
		for (int i = 0; i < words.length; i++) {
			if (slices.get(i).size() == 0) continue;
			JSONObject detail1 = new JSONObject();
			detail1.put("x", slices.get(i).get(slices.get(i).size() - 1).getX2());
			detail1.put("y", 
					(slices.get(i).get(slices.get(i).size() - 1).getY1() + slices.get(i).get(slices.get(i).size() - 1).getY2()) / 2);
			JSONObject j = check(words[i]);
			detail1.put("content", j.getString("content"));
			detail1.put("message", j.getString("message"));
			details.add(detail1);
		}

		JSONObject result = new JSONObject();
		result.put("details", details);
		return result;
	}

	private JSONObject check(String[] symbols) {

		JSONObject j = new JSONObject();
		try {
			String valueString = "";
			int i;
			for (i = symbols.length - 1; i >= 0; i--) {
				if ("=".equals(symbols[i])) break;
				valueString = symbols[i] + valueString;
			}
			if (i < 0) {
				j.put("message", "表达式错误");
				j.put("content", concatString(symbols));
				return j;
			}
			Integer value = null;
			try {
				value = "".equals(valueString) ? null : Integer.parseInt(valueString);
			} catch (NumberFormatException e) {
				j.put("message", "表达式错误");
				j.put("content", concatString(symbols));
				return j;
			}
			TreeNode node = parseFormula(symbols, 0, i - 1);
			if (value == null || node == null) {
				j.put("message", "表达式错误");
				j.put("content", concatString(symbols));
				return j;
			}
			if (value.equals(node.getValue())) {
				j.put("message", "正确");
				j.put("content", concatString(symbols));
				return j;
			} else {
				j.put("message", "错误");
				j.put("content", concatString(symbols));
				return j;
			}
		} catch (AppException e) {
			j.put("message", "表达式错误");
			j.put("content", concatString(symbols));
			return j;
		}
	}

	public void train() throws AppException {

		while (true) {
			train1();
			try {
				Thread.sleep(1000 * (trainSeconds + trainSeconds));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void train1() throws AppException {

		String testDatasetName = null;
		Double learningRate = null;
		Double dth = null;
		Integer[] diverseDataRate = new Integer[] {10, 0, 0};
		Integer batchSize = 30;
		Integer watchEpoch = 20;
		Integer epoch = null;
		Integer testLossIncreaseTolerance = null;
		Boolean keepOld = false;
		Integer dataLimit = 1000;

		JSONObject body = new JSONObject();
		JSONObject train = new JSONObject();

		if (testDatasetName != null) train.put("testDatasetName", testDatasetName);
		train.put("learningRate", learningRate == null ? 0.1 : learningRate);
		train.put("dth", dth == null ? 1.0 : dth);
		if (diverseDataRate != null && diverseDataRate.length == 3) {
			JSONArray rateJ = new JSONArray();
			for (int i = 0; i < 3; i++) {
				rateJ.add(diverseDataRate[i]);
			}
			train.put("diverseDataRate", rateJ);
		}
		if (batchSize != null) train.put("batchSize", batchSize);
		if (watchEpoch != null) train.put("watchEpoch", watchEpoch);
		if (epoch != null) train.put("epoch", epoch);
		if (trainSeconds != null) train.put("trainSecond", trainSeconds);
		if (testLossIncreaseTolerance != null) train.put("testLossIncreaseTolerance", testLossIncreaseTolerance);
		if (dataLimit != null) train.put("dataLimit", dataLimit);

		body.put("new", !keepOld);
		body.put("train", train);

		HttpHandler.http(HttpMethod.POST, Context.getProp().getUrl().getMl() + "/dnn/" + this.userId + "/" + modelName, null, body, token);
	}

	private TreeNode parseFormula(String[] symbols, int start, int end) throws AppException {

		if (start < 0 || start >= symbols.length || end < 0 || end >= symbols.length || start > end)
			throw new AppException("表达式错误" + concatString(symbols));

		// a- -> wrong
		if (isOp(symbols[end]))
			throw new AppException("表达式错误" + concatString(symbols));

		// (a+b) -> a+b
		if ("(".equals(symbols[start]) && ")".equals(symbols[end])) return parseFormula(symbols, start + 1, end - 1);

		TreeNode node;
		/*
		switch (symbols[start]) {
		case "+":
			// +5 -> 5
			return parseFormula(symbols, start + 1, end);
		case "-":
			// -5 -> 0-5
			node = new TreeNode();
			node.setOp("-");
			node.setLc(new TreeNode(0));
			node.setRc(parseFormula(symbols, start + 1, end));
			return node;
		case "*":
		case "/":
			// *5 -> 1*5
			node = new TreeNode();
			node.setOp(symbols[start]);
			node.setLc(new TreeNode(1));
			node.setRc(parseFormula(symbols, start + 1, end));
			return node;
		}*/

		String item = "";
		for (int i = end; i >= start; i--) {
			switch (symbols[i]) {
			case "(":
				// a+(b+c -> wrong
				throw new AppException("表达式错误" + concatString(symbols));
			case ")":
				if (i == end) {
					// ) -> wrong
					if (i == start) throw new AppException("表达式错误" + concatString(symbols));
					int index = findFirst("(", symbols, start, i - 1);
					// a+b) -> wrong
					if (index == -1) throw new AppException("表达式错误" + concatString(symbols));
					// (a+b) -> a+b
					if (index == start) return parseFormula(symbols, start + 1, end - 1);
					if (isOp(symbols[index - 1])) {
						// a+(b+c)
						node = new TreeNode();
						node.setOp(symbols[index - 1]);
						node.setLc(parseFormula(symbols, start, index - 2));
						node.setRc(parseFormula(symbols, index, end));
						return node;
					} else {
						// a(b+c)
						node = new TreeNode();
						node.setOp("*");
						node.setLc(parseFormula(symbols, start, index - 1));
						node.setRc(parseFormula(symbols, index + 1, end));
						return node;
					}
				} else {
					// )5 -> wrong
					throw new AppException("表达式错误" + concatString(symbols));
				}
			case "+":
				// a+b
				node = new TreeNode();
				node.setOp(symbols[i]);
				node.setLc(parseFormula(symbols, start, i - 1));
				node.setRc(parseFormula(symbols, i + 1, end));
				return node;
			case "-":
				if (i == start) {
					// -b -> 0-b
					node = new TreeNode();
					node.setOp(symbols[i]);
					node.setLc(new TreeNode(0));
					node.setRc(parseFormula(symbols, i + 1, end));
					return node;
				} else {
					// a-b
					node = new TreeNode();
					node.setOp(symbols[i]);
					node.setLc(parseFormula(symbols, start, i - 1));
					node.setRc(parseFormula(symbols, i + 1, end));
					return node;
				}
			case "*":
			case "/":
				// a*b
				int index1 = findLast(")", symbols, start, i - 1);
				int index2 = findLastOp(symbols, start, i - 1);
				if (index1 != -1 && index2 != -1) {
					if (index1 > index2) {
						// (a+b)*c
						node = new TreeNode();
						node.setOp(symbols[i]);
						node.setLc(parseFormula(symbols, start, i - 1));
						node.setRc(parseFormula(symbols, i + 1, end));
						return node;
					}
				}
				if (index2 == -1) {
					// a*b
					node = new TreeNode();
					node.setOp(symbols[i]);
					node.setLc(parseFormula(symbols, start, i - 1));
					node.setRc(parseFormula(symbols, i + 1, end));
					return node;
				}
				switch (symbols[index2]) {
				case "+":
				case "-":
					// a+b*c -> a+(b*c)
					node = new TreeNode();
					node.setOp(symbols[index2]);
					node.setLc(parseFormula(symbols, start, index2 - 1));
					node.setRc(parseFormula(symbols, index2 + 1, end));
					return node;
				case "*":
				case "/":
				default:
					// a*b*c -> (a*b)*c
					node = new TreeNode();
					node.setOp(symbols[i]);
					node.setLc(parseFormula(symbols, start, i - 1));
					node.setRc(parseFormula(symbols, i + 1, end));
					return node;
				}
			case "=":
				throw new AppException("表达式错误" + concatString(symbols));
			default:
				item = symbols[i] + item;
				continue;
			}
		}

		return new TreeNode(Integer.parseInt(item));
	}

	private int findLastOp(String[] symbols, int start, int end) {
		for (int i = end; i >= start; i--) {
			if (isOp(symbols[i])) return i;
		}
		return -1;
	}

	private int findFirst(String s, String[] symbols, int start, int end) {
		for (int i = start; i <= end; i++) {
			if (s.equals(symbols[i])) return i;
		}
		return -1;
	}

	private int findLast(String s, String[] symbols, int start, int end) {
		for (int i = end; i >= start; i--) {
			if (s.equals(symbols[i])) return i;
		}
		return -1;
	}

	private boolean isOp(String s) {
		return ("+".equals(s) || "-".equals(s) || "*".equals(s) || "/".equals(s));
	}

	private static String concatString(String[] s) {
		String str = "";
		for (String s1 : s) {
			str += s1;
		}
		return str;
	}

	public JSONObject exportImg(MultipartFile[] uploadFiles, JSONObject images) throws MLException {
		if (uploadFiles == null || uploadFiles.length == 0) return images;
		ByteArrayInputStream byteInputStream = null;
		BufferedImage imageBuffer = null;
		ByteArrayOutputStream byteOutputStream = null;
		try {
			byteInputStream = new ByteArrayInputStream(uploadFiles[0].getBytes());
			imageBuffer = ImageIO.read(byteInputStream);
			int imageWidth = imageBuffer.getWidth();

			Graphics graphics = imageBuffer.getGraphics();
			
			JSONArray imageArray = images.getJSONArray("images");
			for (int i = 0; i < imageArray.size(); i++) {
				JSONObject imageObject = imageArray.getJSONObject(i);
				JSONArray  detailArray = imageObject.getJSONArray("details");
				for (int j = 0; j < detailArray.size(); j++) {
					JSONObject data = detailArray.getJSONObject(j);
					// 文字位置
					int x = data.getIntValue("x");
					int y = data.getIntValue("y");
					// 提示信息
					String message = data.getString("message");
					Font textType = new Font("宋体", Font.PLAIN, 25);
					graphics.setFont(textType);
					FontMetrics fontMetrics = graphics.getFontMetrics(textType);
					int fontWidth = fontMetrics.stringWidth(message);
					int fontHeight = fontMetrics.getHeight();
					// 修正文字位置
					if (x + fontWidth > imageWidth) x = imageWidth - fontWidth;
					if (y - fontHeight < 0) y = fontHeight;
					// 文字颜色
					Color textColor = getTextColor(data.getString("color"));
					graphics.setColor(textColor);
					graphics.drawString(message, x, y);
				}
				graphics.dispose();

				byteOutputStream = new ByteArrayOutputStream();
				ImageIO.write(imageBuffer, "jpg", byteOutputStream);
				BASE64Encoder encoder = new BASE64Encoder();  
				String imageString = encoder.encodeBuffer(byteOutputStream.toByteArray()).trim();
				imageString = imageString.replaceAll("\r\n", "");
				imageObject.put("base64", imageString);
			}
			return images;
		} catch (IOException e) {
			throw new MLException("文件解析失败", e);
		} finally {
			if (byteInputStream != null) {
				try {
					byteInputStream.close();
				} catch (IOException e) {
					throw new MLException("文件解析失败", e);
				}
			}
			if (byteOutputStream != null) {
				try {
					byteOutputStream.close();
				} catch (IOException e) {
					throw new MLException("文件生成失败", e);
				}
			}
		}
	}

	private Color getTextColor(String colorStr) {
		if(colorStr == null) return Color.black;
		switch (colorStr) {
		case "white":
			return Color.white;
		case "lightGray":
			return Color.lightGray;
		case "gray":
			return Color.gray;
		case "darkGray":
			return Color.darkGray;
		case "red":
			return Color.red;
		case "pink":
			return Color.pink;
		case "orange":
			return Color.orange;
		case "yellow":
			return Color.yellow;
		case "green":
			return Color.green;
		case "magenta":
			return Color.magenta;
		case "cyan":
			return Color.cyan;
		case "blue":
			return Color.blue;
		default:
			return Color.black;
		}
	}  

	private String[] recognize41Line(ImageBean[][] wordImages) throws DnnException, MLException {

		List<Integer[][]> data = new ArrayList<Integer[][]>();

		DnnAgent agent = new DnnAgent();
		for (int i = 0; i < wordImages.length; i++) {
			for (int j = 0; j < wordImages[i].length; j++) {
				data.add(wordImages[i][j].getPixels());
			}
		}
		return agent.predict(this.userId, this.modelName, data);
	}

	private String[][] recognize(ImageBean[][] wordImages) throws DnnException, MLException {

		String[] wordString1Line = recognize41Line(wordImages);

		int idx = 0;
		String[][] wordString = new String[wordImages.length][];
		for (int i = 0; i < wordImages.length; i++) {
			wordString[i] = new String[wordImages[i].length];
			for (int j = 0; j < wordImages[i].length; j++) {
				wordString[i][j] = wordString1Line[idx];
				idx++;
			}
		}

		return wordString;
	}

	private long insertData(String dataId, String datasetId, int idx, String ref, String x, String y) {
		DataDao dataDao = daoHandler.getMySQLMapper(DataDao.class);
		Data param = new Data();
		param.setDataId(dataId);
		param.setDatasetId(datasetId);
		param.setIdx(idx);
		param.setRef(ref);
		param.setX(x);
		param.setY(y);
		return dataDao.insert(param);
	}
}
