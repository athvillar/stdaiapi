package cn.standardai.lib.algorithm.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.standardai.lib.algorithm.rnn.lstm.Lstm;

public class TestRnnLstm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			testSentence();
			//testAction();
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

	private static Integer[][] action = {
			{ 1, 1, 1 },
			{ 1, 1, 6 },
			{ 1, 2, 1 },
			{ 1, 2, 6 },
			{ 1, 3, 1 },
			{ 1, 3, 6 },
			{ 1, 3, 5 },
			{ 1, 3, 4 },
			{ 1, 4, 1 },
			{ 1, 4, 6 },
			{ 1, 4, 5 }
	};

	public static void testSentence() throws Exception {

		//char[] dic = getEnglishDic();
		//char[] dic = getNumberDic();
		String xWords = words[2];
		char[] dic = getDic(xWords);
		String yWords = xWords.substring(1) + " ";
		Double[][] xs = getX(xWords, dic);
		Integer[] ys = getY(yWords, dic);

		Lstm lstm = new Lstm(30, dic.length, dic.length);
		lstm.setParam(1, 0.001121, 1, 1, 1, 50);
		//lstm.setParam(5, 0.1, 1, 0.1, 1, 1500);
		lstm.train(xs, ys, 10000);
		System.out.println("Training finished!");

		String hint = xWords.substring(0, 1);
		Double[][] predictXs = getX("I", dic);
		Integer[] result = lstm.predict(predictXs, 100);
		for (int i = 0; i < result.length; i++) {
			System.out.print(dic[result[i]]);
		}
		System.out.println("");

		predictXs = getX(" ", dic);
		result = lstm.predict(predictXs, 100);
		for (int i = 0; i < result.length; i++) {
			System.out.print(dic[result[i]]);
		}
		System.out.println("");

		predictXs = getX("sunjing", dic);
		result = lstm.predict(predictXs, 100);
		for (int i = 0; i < result.length; i++) {
			System.out.print(dic[result[i]]);
		}
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

	public static void testAction() throws Exception {

		char[] dic = getEnglishDic();
		//char[] dic = getNumberDic();
		String xWords = words[0];
		String yWords = xWords.substring(1) + "X";
		Double[][] xs = getX(xWords, dic);
		Integer[] ys = getY(yWords, dic);

		Lstm lstm = new Lstm(80, dic.length, dic.length);
		lstm.setParam(4, 1, 0.9, 1, 0.85, 1);
		lstm.train(xs, ys, 500);
		System.out.println("Training finished!");

		String hint = xWords.substring(0, 5);
		Double[][] predictXs = getX(hint, dic);
		Integer[] result = lstm.predict(predictXs, xWords.length());
		for (int i = 0; i < result.length; i++) {
			System.out.print(dic[result[i]]);
		}
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
