package cn.standardai.lib.algorithm.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DataUtil {

	public static char[] String2CharDic(String s) {
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

	public static Integer[] getY(String words, char[] dic) {
		char[] c = words.toCharArray();
		return getY(c, dic);
	}

	public static Integer[] getY(char[] words, char[] dic) {
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

	public static Double[][] getX(String words, char[] dic) {
		char[] c = words.toCharArray();
		return getX(c, dic);
	}

	public static Double[][] getX(char[] words, char[] dic) {
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

	public static char[] getEnglishDic() {
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

	public static char[] getNumberDic() {
		char[] d = new char[10];
		int i = 0;
		for (char c = '0'; c <= '9'; c++) {
			d[i] = c;
			i++;
		}
		return d;
	}
}
