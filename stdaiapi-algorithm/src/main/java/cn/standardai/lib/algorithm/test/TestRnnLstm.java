package cn.standardai.lib.algorithm.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.standardai.lib.algorithm.exception.DnnException;
import cn.standardai.lib.algorithm.rnn.lstm.DeepLstm;
import cn.standardai.lib.algorithm.rnn.lstm.Lstm;
import cn.standardai.lib.algorithm.rnn.lstm.LstmData;
import cn.standardai.lib.base.matrix.MatrixException;

public class TestRnnLstm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//test121_sentence();
			//testM21_count1();
			//testDeep_reverse();
			testDeep_translate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testDeep_translate() throws DnnException, MatrixException {

		String[][] data = {
				{"i am a", "我是a"},
				{"b likes d", "b爱d"},
				{"d am b", "d是b"},
				{"c likes b", "c爱b"},
				{"i am c", "我是c"},
				{"i likes b", "我爱b"},
				{"a likes b", "a爱b"},
				{"i am d", "我是d"},
				{"d likes b", "d爱b"},
		};
		int epochSize = data.length;
		int testCount = data.length - epochSize;

		Map<String, Integer> dic1 = getEnglishDic(data, 0);
		Map<String, Integer> dic2 = getChineseDic(data, 1);
		DeepLstm deepLstm = new DeepLstm(new int[] {15, 15}, new Double(Math.log(dic1.size()) / Math.log(2)).intValue() + 1, dic2.size());

		LstmData[] lstmData = new LstmData[epochSize];
		for (int i = 0; i < epochSize; i++) {
			String xWords = data[i][0];
			String yWords = data[i][1];
			Double[][] xs = getX(xWords, dic1, " ");
			Integer[] ys = getY(yWords, dic2, null);
			lstmData[i] = new LstmData(xs, ys, LstmData.Delay.NO);
		}

		deepLstm.reset();
		deepLstm.setLearningRate(0.2);
		deepLstm.setEpoch(3000);
		deepLstm.setBatchSize(epochSize);
		deepLstm.setWatchEpoch(10);
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
			Integer[] result = deepLstm.predict(xs);
			for (int j = 0; j < ys.length; j++) {
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
			String resultWords = parse(result, dic2, null);
			System.out.println("(Origin) " + xWords + " \t-> " + resultWords + " <-\t " + yWords + " (Expected)");
		}
		System.out.println("Position correct rate: " + (correctCount / totalCount * 100) + "%");
		System.out.println("Existance correct rate: " + (correctCount2 / totalCount * 100) + "%");
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

	private static Map<String, Integer> getLanguageDic1() {
		Map<String, Integer> dic = new HashMap<String, Integer>();
		dic.put("i", 0);
		dic.put("am", 1);
		dic.put("a", 2);
		dic.put("you", 3);
		dic.put("are", 4);
		dic.put("b", 5);
		return dic;
	}

	private static Map<String, Integer> getLanguageDic2() {
		Map<String, Integer> dic = new HashMap<String, Integer>();
		dic.put("wo", 0);
		dic.put("shi", 1);
		dic.put("a", 2);
		dic.put("ni", 3);
		dic.put("b", 4);
		return dic;
	}

	private static void testDeep_reverse() throws DnnException, MatrixException {
		//char[] dic = getNumberDic();
		//char[] dic = getEnglishDic();
		int trainTime = 1;
		int testCount = 1000;
		int epochSize = 50;
		int xLength = 4;
		//String paragraph = words[0];
		//char[] dic = getDic(paragraph);
		char[] dic = getEnglishDic();
		DeepLstm deepLstm = new DeepLstm(new int[] {7, 4}, dic.length, dic.length);
		//int totalLength = paragraph.length();

		for (int i2 = 0; i2 < trainTime; i2++) {
			LstmData[] data = new LstmData[epochSize];
			for (int i = 0; i < epochSize; i++) {
				//int start = new Double(Math.random() * (totalLength - xLength)).intValue();
				String xWords = random(dic, xLength);
				String yWords = reverse(xWords) + '\0';
				xWords += '\0';
				Double[][] xs = getX(xWords, dic);
				Integer[] ys = getY(yWords, dic);
				data[i] = new LstmData(xs, ys, LstmData.Delay.NO);
			}

			deepLstm.reset();
			deepLstm.setLearningRate(1.0);
			deepLstm.setEpoch(20000);
			deepLstm.setBatchSize(epochSize);
			deepLstm.setWatchEpoch(20);
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
			String yWords = reverse(xWords);
			xWords += '\0';
			Double[][] xs = getX(xWords, dic);
			Integer[] ys = getY(yWords, dic);
			Integer[] result = deepLstm.predict(xs);
			for (int j = 0; j < xLength; j++) {
				if (ys[j] == result[j]) {
					correctCount++;
				}
			}
			for (int j = 0; j < xLength; j++) {
				for (int k = 0; k < xLength; k++) {
					if (ys[k] == result[j]) {
						correctCount2++;
						break;
					}
				}
			}
			//String resultWords = parse(result, dic);
			//System.out.println("(Origin) " + xWords.substring(0, 4) + " -> " + resultWords + " <- " + yWords + " (Expected)");
		}
		System.out.println("Position correct rate: " + (correctCount / (testCount * xLength)));
		System.out.println("Existance correct rate: " + (correctCount2 / (testCount * xLength)));
	}

	private static String random(char[] dic, int len) {
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

	public static void test121_sentence() throws Exception {

		//char[] dic = getEnglishDic();
		//char[] dic = getNumberDic();
		int trainTime = 1;
		int epochSize = 6;
		int xLength = 13;
		String paragraph = words[0];
		char[] dic = getDic(paragraph);
		Lstm lstm = new Lstm(3, dic.length, dic.length);

		for (int i2 = 0; i2 < trainTime; i2++) {
			int totalLength = paragraph.length();
			LstmData[] data = new LstmData[epochSize];
			for (int i = 0; i < epochSize; i++) {
				int start = new Double(Math.random() * (totalLength - xLength)).intValue();
				String xWords = paragraph.substring(start, start + xLength);
				String yWords = xWords.substring(1) + paragraph.substring(start + xLength, start + xLength + 1);
				Double[][] xs = getX(xWords, dic);
				Integer[] ys = getY(yWords, dic);
				data[i] = new LstmData(xs, ys, LstmData.Delay.NO);
			}

			lstm.reset();
			lstm.setLearningRate(2.0);
			lstm.setEpoch(20000);
			lstm.setBatchSize(epochSize);
			lstm.setWatchEpoch(10);
			lstm.train(data);
		}

		System.out.println("Training finished!");

		String hint = " ";
		Double[][] predictXs = getX(hint, dic);
		Integer[] result = lstm.predict(predictXs, 100);
		for (int i = 0; i < result.length; i++) {
			System.out.print(dic[result[i]]);
		}
		System.out.println("");
	}

	public static void testM21_count1() throws Exception {

		char[] dic = getNumberDic();
		String[] xString = new String[20];
		LstmData[] data = new LstmData[xString.length];
		for (int i = 0; i < xString.length; i++) {
			xString[i] = new Double(Math.random()).toString().replace('-', '9').replace('.', '9').substring(2, 7);
			Double[][] xs = getX(xString[i], dic);
			Integer[] ys = new Integer[] {0};
			//for (char c : xString[i].toCharArray()) {
			//	if (c == '1') ys[0]++;
			//}
			ys[0] = Integer.parseInt(xString[i].substring(2, 3));
			//if (ys[0] > 10) ys[0] = 10;
			LstmData data1 = new LstmData(xs, ys, LstmData.Delay.YES);
			data[i] = data1;
		}

		Lstm lstm = new Lstm(4, dic.length, dic.length);
		lstm.setLearningRate(0.1);
		lstm.setEpoch(5000);
		lstm.setBatchSize(20);
		lstm.setWatchEpoch(5);
		lstm.train(data);
		System.out.println("Training finished!");

		double correct = 0.0;
		int totalPredictCount = 100;
		String[] predictXString = new String[totalPredictCount];
		for (int i = 0; i < predictXString.length; i++) {
			predictXString[i] = new Double(Math.random()).toString().replace('-', '9').replace('.', '9').substring(2, 7);
			Double[][] predictXs = getX(predictXString[i], dic);
			Integer[] predictYs = new Integer[] {0};
			//for (char c : predictXString[i].toCharArray()) {
			//	if (c == '1') predictYs[0]++;
			//}
			predictYs[0] = Integer.parseInt(predictXString[i].substring(2, 3));
			//if (predictYs[0] > 10) predictYs[0] = 10;
			Integer[] result = lstm.predict(predictXs, 1);
			if (result[0] == predictYs[0]) {
				correct++;
			}
		}
		System.out.println("Correct rate: " + correct / totalPredictCount);
	}

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
		char[] d = new char[10];
		int i = 0;
		for (char c = '0'; c <= '9'; c++) {
			d[i] = c;
			i++;
		}
		return d;
	}
}
