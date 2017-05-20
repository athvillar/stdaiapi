package cn.standardai.lib.algorithm.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.rnn.lstm.DeepLstm;
import cn.standardai.lib.algorithm.rnn.lstm.Lstm;
import cn.standardai.lib.algorithm.rnn.lstm.LstmData;
import cn.standardai.lib.base.function.Roulette;
import cn.standardai.lib.base.matrix.MatrixException;
import cn.standardai.lib.base.matrix.MatrixUtil;
import cn.standardai.tool.Image2Data;

public class TestRnnLstm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//testDeep_reverse();
			testDeep_translate();
			//testDeep_fixPic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testDeep_fixPic() throws DnnException, MatrixException, IOException {

		int epochSize = 1;
		int testCount = 1;
		int fix = 3;

		DeepLstm deepLstm = new DeepLstm(new int[] {30}, 100, 100);
		LstmData[] lstmData = new LstmData[epochSize];
		for (int i = 1; i <= epochSize; i++) {
			Integer[][] image1 = Image2Data.getGray2("/Users/athvillar/Documents/work/yale/s" + i + ".bmp");
			Double[][] xs = MatrixUtil.subMatrix(MatrixUtil.devide(image1, 123 * fix), 50, 100, 1);
			Double[][] ys = MatrixUtil.subMatrix(MatrixUtil.devide(image1, 123 * fix), 50, 100, 2);
			lstmData[i - 1] = new LstmData(xs, ys);
		}

		deepLstm.setDth(2.0);
		deepLstm.setLearningRate(0.011);
		deepLstm.setEpoch(100);
		//deepLstm.setTrainSecond(2);
		deepLstm.setBatchSize(epochSize);
		deepLstm.setWatchEpoch(2);
		//deepLstm.setTestLossIncreaseTolerance(2);
		//deepLstm.setDelay(true);
		deepLstm.mountData(lstmData);
		deepLstm.train();

		System.out.println("Training finished!");

		for (int rIndex = 1; rIndex <= epochSize; rIndex++) {
			Integer[][] image1 = Image2Data.getGray2("/Users/athvillar/Documents/work/yale/s" + rIndex + ".bmp");
			Double[][] xs = MatrixUtil.subMatrix(MatrixUtil.devide(image1, 123 * fix), 50, 100, 1);
			Double[][] ys = deepLstm.predictYs(xs, 50);
			Integer[][] pixels = new Integer[100][100];

			double l1Norm = MatrixUtil.l2Norm(ys);
			if (l1Norm < 1) {
				ys = MatrixUtil.multiply(ys, 1 / l1Norm);
			}
			for (int i = 0; i < 100; i++) {
				for (int j = 0; j < 100; j++) {
					if (i < 50) {
						pixels[i][j] = new Double(xs[i][j] * 123 * fix).intValue();
					} else {
						pixels[i][j] = new Double(ys[i - 50][j] * 123 * fix * 40).intValue();
					}
				}
			}
			Image2Data.drawGray("/Users/athvillar/Documents/" + rIndex + ".png", pixels);
		}

	}

	private static void testDeep_translate() throws DnnException, MatrixException {

		/*
		String[][] data = {
				{"d am b .", "d是b."},
				{"c likes b .", "c喜欢b."},
				{"i am c .", "我是c."},
				{"i likes b .", "我喜欢b."},
				{"a likes i .", "a喜欢我."},
				{"b am i .", "b是我."},
				{"b likes b .", "b喜欢b."},
				{"a am b .", "a是a."},
				{"a likes a .", "a喜欢a."},
				{"d likes b .", "d喜欢b."},
				{"b am d .", "b是d."},
				{"i likes c .", "我喜欢c."},
				{"a likes b .", "a喜欢b."},
				{"i am d .", "我是d."},
				{"d likes b .", "d喜欢b."},
				{"c am i .", "c是我."}
		};
		*/
		String[][] data = {
				{"she will be president of china .", "她将是国家主席."},
				{"i have a dream that one day i will be president of china .", "我希望有一天能当国家主席."},
				{"you are ok .", "你没问题."},
				{"he will be president of china .", "他将是国家主席."},
				{"you have a dream that one day you will be president of china .", "你希望有一天能当国家主席."},
				{"he is ok .", "他没问题."},
				{"i will be president of china .", "我将是国家主席."},
				{"he has a dream that one day he will be president of china .", "他希望有一天能当国家主席."},
				{"i am ok .", "我没问题."},
				{"you will be president of china .", "你将是国家主席."},
				{"she is ok .", "她没问题."},
		};
		int epochSize = data.length;
		int testCount = data.length - epochSize;

		Map<String, Integer> dic1 = getEnglishDic(data, 0);
		Map<String, Integer> dic2 = getChineseDic(data, 1);
		DeepLstm deepLstm = new DeepLstm(new int[] {4, dic2.size(),4}, new Double(Math.log(dic1.size()) / Math.log(2)).intValue() + 1, dic2.size());
		//DeepLstm deepLstm = new DeepLstm(new int[] {8,5}, new Double(Math.log(dic1.size()) / Math.log(2)).intValue() + 1, new Double(Math.log(dic2.size()) / Math.log(2)).intValue() + 1);

		LstmData[] lstmData = new LstmData[epochSize];
		for (int i = 0; i < epochSize; i++) {
			String xWords = data[i][0];
			String yWords = data[i][1];
			Double[][] xs = getX(xWords, dic1, " ");
			//Double[][] ys = getX(yWords, dic2, null);
			Integer[] ys = getY(yWords, dic2, null);
			lstmData[i] = new LstmData(xs, ys);
		}

		deepLstm.setDth(1.1);
		deepLstm.setLearningRate(0.1);
		deepLstm.setEpoch(300);
		deepLstm.setDiverseDataRate(new int[] {6, 4, 0});
		//deepLstm.setTrainSecond(2);
		deepLstm.setBatchSize(epochSize);
		deepLstm.setWatchEpoch(5);
		//deepLstm.setTestLossIncreaseTolerance(15);
		deepLstm.setDelay(true);
		deepLstm.setSelfConnect(true);
		deepLstm.mountData(lstmData);
		deepLstm.train();

		System.out.println("Training finished!");

		double correctCount = 0.0;
		double correctCount2 = 0.0;
		Integer totalCount = 0;
		//for (int i = 0; i < testCount; i++) {
		for (int i = 0; i < data.length; i++) {
			String xWords = data[i][0];
			String yWords = data[i][1];
			Double[][] xs = getX(xWords, dic1, " ");
			Integer[] ys = getY(yWords, dic2, null);
			Integer[] result = deepLstm.predictY(xs, findDicKey(dic2, "."), null);
			for (int j = 0; j < Math.min(result.length, ys.length); j++) {
				totalCount++;
				if (ys[j] == result[j]) {
					correctCount++;
				}
			}
			for (int j = 0; j < result.length; j++) {
				for (int k = 0; k < ys.length; k++) {
					if (ys[k] == result[j]) {
						correctCount2++;
						break;
					}
				}
			}

			//Double[][] result = deepLstm.predictYs(xs, 3);
			
			String resultWords = parse(result, dic2, null);
			System.out.println("(Origin) " + xWords + " \t-> " + resultWords + " <-\t " + yWords + " (Expected)");
		}
		System.out.println("Position correct rate: " + (correctCount / totalCount * 100) + "%");
		System.out.println("Existance correct rate: " + (correctCount2 / totalCount * 100) + "%");
	}

	private static Integer findDicKey(Map<String, Integer> dic, String s) {
		for (Entry<String, Integer> entry : dic.entrySet()) {
			if (entry.getKey().equals(s)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private static Map<String, Integer> getEnglishDic(String[][] data, int index) {
		Map<String, Integer> dic = new HashMap<String, Integer>();
		Integer count = 0;
		for (int i = 0; i < data.length; i++) {
			String sentence = data[i][index];
			String[] words = sentence.split(" ");
			for (int j = 0; j < words.length; j++) {
				if (dic.containsKey(words[j])) {
					continue;
				} else {
					dic.put(words[j], count++);
				}
			}
		}
		return dic;
	}

	private static Map<String, Integer> getChineseDic(String[][] data, int index) {
		Map<String, Integer> dic = new HashMap<String, Integer>();
		Integer count = 0;
		for (int i = 0; i < data.length; i++) {
			String sentence = data[i][index];
			String[] words = new String[sentence.length()];
			for (int j = 0; j < words.length; j++) {
				words[j] = sentence.substring(j, j + 1);
				if (dic.containsKey(words[j])) {
					continue;
				} else {
					dic.put(words[j], count++);
				}
			}
		}
		return dic;
	}

	private static String parse(Integer[] values, Map<String, Integer> dic, String split) {
		String result = "";
		for (int i = 0; i < values.length; i++) {
			for (Entry<String, Integer> entry : dic.entrySet()) {
				if (values[i] == entry.getValue()) {
					result += entry.getKey();
					if (split != null) result += split;
					break;
				}
			}
		}
		return result;
	}

	private static String parse(Double[][] values, Map<String, Integer> dic, String split) {
		String result = "";
		for (int i = 0; i < values.length; i++) {
			Roulette r = new Roulette(values[i]);
			for (Entry<String, Integer> entry : dic.entrySet()) {
				if (r.getY() == entry.getValue()) {
					result += entry.getKey();
					if (split != null) result += split;
					break;
				}
			}
		}
		return result;
	}

	private static Double[][] getX(String sentence, Map<String, Integer> dic, String split) {
		String[] words;
		if (split != null) {
			words = sentence.split(" ");
		} else {
			words = new String[sentence.length()];
			for (int j = 0; j < words.length; j++) {
				words[j] = sentence.substring(j, j + 1);
			}
		}
		int width = new Double(Math.log(dic.size()) / Math.log(2)).intValue() + 1;
		Double[][] x = new Double[words.length][width];
		for (int i = 0; i < words.length; i++) {
			int index = 0;
			for (Entry<String, Integer> entry : dic.entrySet()) {
				if (entry.getKey().equals(words[i])) {
					index = entry.getValue();
					break;
				}
			}
			for (int j = 0; j < width; j++) {
				if (index % 2 == 1) {
					x[i][j] = 1.0;
					index -= 1;
				} else {
					x[i][j] = 0.0;
				}
				index /= 2;
			}
 		}
		return x;
	}

	private static Integer[] getY(String sentence, Map<String, Integer> dic, String split) {
		String[] words;
		if (split != null) {
			words = sentence.split(" ");
		} else {
			words = new String[sentence.length()];
			for (int j = 0; j < words.length; j++) {
				words[j] = sentence.substring(j, j + 1);
			}
		}
		Integer[] y = new Integer[words.length];
		for (int i = 0; i < words.length; i++) {
			y[i] = 0;
			for (Entry<String, Integer> entry : dic.entrySet()) {
				if (entry.getKey().equals(words[i])) {
					y[i] = entry.getValue();
					break;
				}
			}
 		}
		return y;
	}

	private static void testDeep_reverse() throws DnnException, MatrixException {
		//char[] dic = getNumberDic();
		//char[] dic = getEnglishDic();
		int trainTime = 1;
		int testCount = 30;
		int epochSize = 100;
		int xLength = 3;
		//String paragraph = words[0];
		//char[] dic = getDic(paragraph);
		char[] dic = getNumberDic();
		DeepLstm deepLstm = new DeepLstm(new int[] {6,6}, dic.length, dic.length);
		//int totalLength = paragraph.length();

		for (int i2 = 0; i2 < trainTime; i2++) {
			LstmData[] data = new LstmData[epochSize];
			for (int i = 0; i < epochSize; i++) {
				//int start = new Double(Math.random() * (totalLength - xLength)).intValue();
				String xWords = random(dic, xLength);
				String yWords = reverse(xWords) + '.';
				xWords += '.';
				Double[][] xs = getX(xWords, dic);
				//Integer[] ys = getY(yWords, dic);
				Double[][] ys = getX(yWords, dic);
				data[i] = new LstmData(xs, ys);
			}

			deepLstm.setDth(1.0);
			deepLstm.setLearningRate(0.2);
			deepLstm.setEpoch(3000);
			//deepLstm.setTrainSecond(2);
			deepLstm.setBatchSize(epochSize);
			deepLstm.setWatchEpoch(1);
			//deepLstm.setTestLossIncreaseTolerance(2);
			deepLstm.setDelay(true);
			deepLstm.mountData(data);
			deepLstm.train();
		}

		System.out.println("Training finished!");

		//String paragraph2 = words[3];
		//int totalLength2 = paragraph2.length();
		double correctCount = 0.0;
		double correctCount2 = 0.0;
		for (int i = 0; i < testCount; i++) {
			//int start = new Double(Math.random() * (totalLength2 - xLength)).intValue();
			//String xWords = paragraph2.substring(start, start + xLength);
			String xWords = random(dic, xLength);
			String yWords = reverse(xWords) + '.';
			xWords += '.';
			Double[][] xs = getX(xWords, dic);
			Integer[] ys = getY(yWords, dic);
			Integer[] result = deepLstm.predictY(xs, 10, null);
			for (int j = 0; j < Math.min(ys.length, result.length); j++) {
				if (ys[j] == result[j]) {
					correctCount++;
				}
			}
			for (int j = 0; j < result.length; j++) {
				for (int k = 0; k < ys.length; k++) {
					if (ys[k] == result[j]) {
						correctCount2++;
						break;
					}
				}
			}
			String resultWords = parse(result, dic);
			System.out.println("(Origin) " + xWords.substring(0, 4) + " -> " + resultWords + " <- " + yWords + " (Expected)");
		}
		System.out.println("Position correct rate: " + (correctCount / (testCount * xLength)));
		System.out.println("Existance correct rate: " + (correctCount2 / (testCount * xLength)));
	}

	private static String random(char[] dic, int len) {

		/*
		if (Math.random() >= 0.5) {
			return "abc";
		} else {
			return "ade";
		}
		*/
		String result = "";
		for (int i = 0; i < len; i++) {
			result += dic[new Double(Math.random() * dic.length).intValue()];
		}
		return result;
	}

	private static String parse(Integer[] keys, char[] dic) {
		String result = "";
		for (int i = 0; i < keys.length; i++) {
			result += dic[keys[i]];
		}
		return result;
	}

	private static String reverse(String x) {
		String y = new String();
		for (int i = x.length() - 1; i >= 0; i--) {
			y += x.charAt(i);
		}
		return y;
	}

	private static String[] words = {
			"abcdefgh123456789abcdefgh123456789abcdefgh123456789abcdefgh123456789",
			"i have a dream, we are happy.",
			"Yes, you’ve guessed it right-water. Our bodies consist of about 70% "
			+ "of water and therefore we cannot live without it. Whenever you hear about water, "
			+ "you surely connect it to purification and wellbeing of the body. The Mayo Clinic "
			+ "acknowledges that most doctors recommend drinking 6 to 8 glasses of water daily "
			+ "so you body functions perfectly. Drinking this amount of water every day will hydrate "
			+ "your body from the inside out, including a better function of your internal organs"
			+ "(stomach, kidneys, head) as well as making your external one(skin) radiant. "
			+ "Keeping your body hydrated is one of the most important and secure ways to be healthy. "
			+ "Here are the three most important benefits water makes for our bodies: Aid in losing weight, "
			+ "in which it satisfies our thirst which most people mix with hunger and overeat. for that "
			+ "matter it is suggested to drink a glass of water half an hour before a meal Boosting your "
			+ "flush all the toxins and fat.",
			"acecfhg392167a9d0b7edhgg91722f"
	};

	private static String[][] word12M = {
			{"1","one"},
			{"2","two"},
			{"3","three"},
	};

	private static char[] getDic(String s) {
		Map<Character, String> cMap = new HashMap<Character, String>();
		for (char c : s.toCharArray()) {
			if (!cMap.containsKey(c)) {
				cMap.put(c, "");
			}
		}
		char[] dic = new char[cMap.size() + 1];
		int i = 0;
		for (Entry<Character, String> e : cMap.entrySet()) {
			dic[i] = e.getKey();
			i++;
		}
		dic[dic.length - 1] = '\0';
		return dic;
	}

	private static Integer[] getY(String words, char[] dic) {
		char[] c = words.toCharArray();
		return getY(c, dic);
	}

	private static Integer[] getY(char[] words, char[] dic) {
		Integer[] result = new Integer[words.length];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < dic.length; j++) {
				if (words[i] == dic[j]) {
					result[i] = j;
					break;
				}
			}
		}
		return result;
	}

	private static Double[][] getX(String words, char[] dic) {
		char[] c = words.toCharArray();
		return getX(c, dic);
	}

	private static Double[][] getX(char[] words, char[] dic) {
		Double[][] result = new Double[words.length][dic.length];
		for (int i = 0; i < result.length; i++) {
			Double[] result1 = new Double[dic.length];
			for (int j = 0; j < dic.length; j++) {
				if (words[i] == dic[j]) {
					result1[j] = 1.0;
				} else {
					result1[j] = 0.0;
				}
			}
			result[i] = result1;
		}
		return result;
	}

	private static char[] getEnglishDic() {
		char[] d = new char[57];
		int i = 0;
		for (char c = 'a'; c <= 'z'; c++) {
			d[i] = c;
			i++;
		}
		for (char c = 'A'; c <= 'Z'; c++) {
			d[i] = c;
			i++;
		}
		d[52] = ' ';
		d[53] = ',';
		d[54] = '.';
		d[55] = '\'';
		d[56] = '\0';
		return d;
	}

	private static char[] getNumberDic() {
		char[] d = new char[11];
		int i = 0;
		for (char c = '0'; c <= '9'; c++) {
			d[i] = c;
			i++;
		}
		d[10] = '.';
		return d;
	}
}
