package cn.standardai.api.ml.agent;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.standardai.api.core.base.AuthAgent;
import cn.standardai.api.core.bean.Context;
import cn.standardai.api.ml.bean.DnnModelSetting;
import cn.standardai.api.ml.bean.TreeNode;
import cn.standardai.api.ml.daohandler.ModelHandler;
import cn.standardai.api.ml.exception.FilterException;
import cn.standardai.api.ml.exception.MLException;
import cn.standardai.api.ml.filter.DataFilter;
import cn.standardai.lib.algorithm.cnn.Cnn;
import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.tool.ImageUtil;
import cn.standardai.tool.ImageUtil.BVMethod;
import cn.standardai.tool.literalImage.LiteralUtil;
import cn.standardai.tool.literalImage.Slice;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class FormulaAgent extends AuthAgent {

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

	private ModelHandler mh = new ModelHandler(daoHandler);

	public JSONObject process(MultipartFile[] uploadFiles) throws MLException {
		if (uploadFiles == null || uploadFiles.length == 0) return new JSONObject();
		try {
			// 获得灰度值
			Integer[][] gray = ImageUtil.getGray(uploadFiles[0]);
			// 二值化
			Integer[][] bv = ImageUtil.binaryValue(gray, BVMethod.localAvg);
			// 去除噪点
			bv = ImageUtil.clearNoise(bv, 1);
			// 输出二值化图片，测试用
			if (Context.getProp().getLocal().getDebug())
				ImageUtil.drawGray(Context.getProp().getLocal().getDebugTemp() + "bv.jpg", bv);
			// 分割出文字
			List<List<Slice>> slices = LiteralUtil.cut(bv, 0.03, 0.0, 5);
			// 输出文字图片，测试用
			//if (Context.getProp().getLocal().getDebug()) {
			Integer[][][][] words = LiteralUtil.drawWords(bv, slices, Context.getProp().getLocal().getDebugTemp(), 1, 38, 38, true);
			//}
			// 识别文字图片
			//String[][] wordString = recognize(bv, slices);
			String[][] wordString = recognize(bv, words);
			// 检查正误
			return check(slices, wordString);
		} catch (IOException | DnnException e) {
			throw new MLException("文件解析失败", e);
		}
	}

	private String[][] recognize(Integer[][] bv, Integer[][][][] words) throws MLException, DnnException {
		DnnModelSetting ms = mh.findLastestModel("hanqing", "fn5");
		if (ms == null) throw new MLException("找不到模型(hanqing/fn5)");

		DataFilter<?, ?>[] xFilters = DataFilter.parseFilters(
				ms.getTrainDataSetting().getxFilter().substring(ms.getTrainDataSetting().getxFilter().indexOf("|") + 1));
		DataFilter<?, ?>[] yFilters = DataFilter.parseFilters(ms.getTrainDataSetting().getyFilter());
		for (DataFilter<?, ?> f : xFilters) {
			if (f != null && f.needInit()) {
				f.init("hanqing", this.daoHandler);
			}
		}
		for (DataFilter<?, ?> f : yFilters) {
			if (f != null && f.needInit()) {
				f.init("hanqing", this.daoHandler);
			}
		}

		Cnn cnn = createModel(ms);
		String[][] ys = new String[words.length][];
		for (int i = 0; i < words.length; i++) {
			ys[i] = new String[words[i].length];
			for (int j = 0; j < words[i].length; j++) {
				Integer[][][] x = DataFilter.encode(words[i][j], xFilters);
				ys[i][j] = DataFilter.decode(cnn.predictY(x), yFilters);
			}
		}

		return ys;
	}

	private JSONObject check(List<List<Slice>> slices, String[][] words) {

		JSONArray details = new JSONArray();
		for (int i = 0; i < words.length; i++) {
			if (slices.get(i).size() == 0) continue;
			JSONObject detail1 = new JSONObject();
			detail1.put("x", slices.get(i).get(slices.get(i).size() - 1).getX2());
			detail1.put("y", slices.get(i).get(slices.get(i).size() - 1).getY1());
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
				j.put("message", "表达式错误:");
				j.put("content", concatString(symbols));
				return j;
			}
			Integer value = null;
			try {
				value = "".equals(valueString) ? null : Integer.parseInt(valueString);
			} catch (NumberFormatException e) {
				j.put("message", "表达式错误:");
				j.put("content", concatString(symbols));
				return j;
			}
			TreeNode node = parseFormula(symbols, 0, i - 1);
			if (value == null || node == null) {
				j.put("message", "表达式错误:");
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
		} catch (MLException e) {
			j.put("message", "表达式错误:");
			j.put("content", concatString(symbols));
			return j;
		}
	}

	private TreeNode parseFormula(String[] symbols, int start, int end) throws MLException {

		if (start < 0 || start >= symbols.length || end < 0 || end >= symbols.length || start > end)
			throw new MLException("表达式错误:" + concatString(symbols));

		// a- -> wrong
		if (isOp(symbols[end]))
			throw new MLException("表达式错误:" + concatString(symbols));

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
				throw new MLException("表达式错误:" + concatString(symbols));
			case ")":
				if (i == end) {
					// ) -> wrong
					if (i == start) throw new MLException("表达式错误:" + concatString(symbols));
					int index = findFirst("(", symbols, start, i - 1);
					// a+b) -> wrong
					if (index == -1) throw new MLException("表达式错误:" + concatString(symbols));
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
					throw new MLException("表达式错误:" + concatString(symbols));
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
				throw new MLException("表达式错误:" + concatString(symbols));
			default:
				item = symbols[i] + item;
				continue;
			}
		}

		return new TreeNode(Integer.parseInt(item));
	}

	private String[][] recognize(Integer[][] pixels, List<List<Slice>> slices) throws MLException, DnnException {

		DnnModelSetting ms = mh.findLastestModel("hanqing", "fn5");
		if (ms == null) throw new MLException("找不到模型(hanqing/fn5)");

		DataFilter<?, ?>[] xFilters = DataFilter.parseFilters(
				ms.getTrainDataSetting().getxFilter().substring(ms.getTrainDataSetting().getxFilter().indexOf("|") + 1));
		DataFilter<?, ?>[] yFilters = DataFilter.parseFilters(ms.getTrainDataSetting().getyFilter());
		for (DataFilter<?, ?> f : xFilters) {
			if (f != null && f.needInit()) {
				f.init("hanqing", this.daoHandler);
			}
		}
		for (DataFilter<?, ?> f : yFilters) {
			if (f != null && f.needInit()) {
				f.init("hanqing", this.daoHandler);
			}
		}

		Cnn cnn = createModel(ms);
		String[][] ys = new String[slices.size()][];
		for (int i = 0; i < slices.size(); i++) {
			ys[i] = new String[slices.get(i).size()];
			for (int j = 0; j < slices.get(i).size(); j++) {
				Integer[][][] x = DataFilter.encode(slices.get(i).get(j).getScope(pixels), xFilters);
				ys[i][j] = DataFilter.decode(cnn.predictY(x), yFilters);
			}
		}

		return ys;
	}

	private Cnn createModel(DnnModelSetting model) throws DnnException {
		Cnn cnn;
		if (model.getStructure() == null) {
			// 无模型，新建模型
			JSONObject structure = JSONObject.parseObject(model.getScript());
			cnn = Cnn.getInstance(structure);
		} else {
			// 有模型，使用最新模型继续训练
			cnn = Cnn.getInstance(model.getStructure());
		}
		return cnn;
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

	public JSONObject exportImg(MultipartFile[] uploadFiles, JSONObject details) throws MLException {
		if (uploadFiles == null || uploadFiles.length == 0) return details;
		ByteArrayInputStream byteInputStream = null;
		BufferedImage imageBuffer = null;
		ByteArrayOutputStream byteOutputStream = null;
		try {
			byteInputStream = new ByteArrayInputStream(uploadFiles[0].getBytes());
			imageBuffer = ImageIO.read(byteInputStream);
			int imageWidth = imageBuffer.getWidth();

			Graphics graphics = imageBuffer.getGraphics();
			Font f = new Font("宋体", Font.PLAIN, 25);
			graphics.setFont(f);
			Color mycolor = Color.black;
			graphics.setColor(mycolor);

			JSONArray detaArray = details.getJSONArray("details");
			for (int i = 0; i < detaArray.size(); i++) {
				JSONObject data = detaArray.getJSONObject(i);
				int x = data.getIntValue("x");
				int y = data.getIntValue("y");
				String message = data.getString("message");
				FontMetrics fontMetrics = graphics.getFontMetrics(f);
				int fontWidth = fontMetrics.stringWidth(message);
				int fontHeight = fontMetrics.getHeight();
				if (x + fontWidth > imageWidth) x = imageWidth - fontWidth;
				if (y - fontHeight < 0) y = fontHeight;
				graphics.drawString(message, x, y);
			}
			graphics.dispose();

			byteOutputStream = new ByteArrayOutputStream();
			ImageIO.write(imageBuffer, "jpg", byteOutputStream);
			BASE64Encoder encoder = new BASE64Encoder();  
			String imageString = encoder.encodeBuffer(byteOutputStream.toByteArray()).trim();
			imageString = imageString.replaceAll("\r\n", "");
			details.put("image", imageString);
			return details;
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
}
