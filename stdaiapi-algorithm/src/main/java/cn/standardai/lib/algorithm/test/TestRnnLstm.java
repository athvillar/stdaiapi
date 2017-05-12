package cn.standardai.lib.algorithm.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.standardai.lib.algorithm.rnn.lstm.Lstm;
import cn.standardai.lib.algorithm.rnn.lstm.LstmData;

public class TestRnnLstm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//test121_sentence();
			testM21_count1();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String[] words = {
			"abcdefgh123456789abcdefgh123456789abcdefgh123456789abcdefgh123456789",
			"i have a dream, i like sunjing, she loves me too, we are happy.",
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
			+ "flush all the toxins and fat."
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
		char[] dic = new char[cMap.size()];
		int i = 0;
		for (Entry<Character, String> e : cMap.entrySet()) {
			dic[i] = e.getKey();
			i++;
		}
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
		char[] d = new char[56];
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
