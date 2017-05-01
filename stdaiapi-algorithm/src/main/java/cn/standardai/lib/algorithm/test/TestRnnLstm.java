package cn.standardai.lib.algorithm.test;

import cn.standardai.lib.algorithm.rnn.lstm.Lstm;

public class TestRnnLstm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			testWord();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String[] words = {
			"hello world",
			"i have a dream."
	};
	public static void testWord() throws Exception {

		char[] dic = getEnglishDic();
		//char[] dic = getNumberDic();
		String xWords = words[0];
		String yWords = xWords.substring(1) + "X";
		Double[][] xs = getX(xWords, dic);
		Integer[] ys = getY(yWords, dic);

		Lstm lstm = new Lstm(50, dic.length, dic.length);
		lstm.train(xs, ys, 500);
		System.out.println("Training finished!");

		String hint = xWords.substring(0, 2);
		Double[][] predictXs = getX(hint, dic);
		Integer[] result = lstm.predict(predictXs, 8);
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
