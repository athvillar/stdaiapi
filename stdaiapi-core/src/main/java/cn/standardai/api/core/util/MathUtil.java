package cn.standardai.api.core.util;

import java.util.HashMap;
import java.util.Map;

public class MathUtil {

	private static final int AVAILABLE_CHAR_NUM = 36;

	private static final char[] AVAILABLE_CHARS = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	private static int preSCG;

	private static long SCGA = 1664525L;

	private static long SCGC = 1013904223L;

	private static long SCGM = 2147483648L;

	static {
		reSeed();
	}

	private static int scg(long a, long c, long m) {
		/*
		System.out.println("(a * preSCG + c) = " + (a * preSCG + c));
		System.out.println("(a * preSCG + c) % m = " + (a * preSCG + c) % m);
		System.out.println("preSCG = " + preSCG);
		*/
		return (preSCG = (int)((a * preSCG + c) % m));
	}

	public static String random(int n) {
		int scg = preSCG;
		char[] c = new char[n];
		for (int i = 0; i < n; i++) {
			//System.out.println("scg:" + scg);
			//System.out.println("mod:" + scg % AVAILABLE_CHAR_NUM);
			if (scg == 0) scg = scg(SCGA, SCGC, SCGM);
			//System.out.println("new scg:" + scg);
			int mod = scg % AVAILABLE_CHAR_NUM;
			c[i] = AVAILABLE_CHARS[mod];
			scg = (scg - mod) / AVAILABLE_CHAR_NUM;
		}
		return String.valueOf(c);
	}

	private static void reSeed() {
		preSCG = (int)(Math.random() * Integer.MAX_VALUE);
	}

	public static void main(String[] args) throws InterruptedException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int x = 2000;
		int y = 2000;
		int totalCnt = x * y;
		int duplicateCnt = 0;
		for (int j = 0; j < x; j++) {
			MathUtil.reSeed();
			for (int i = 0; i < y; i++) {
				String s = random(16);
				if (map.containsKey(s)) {
					Thread.sleep(13L);
					MathUtil.reSeed();
					duplicateCnt++;
					System.out.println("Duplicate! No." + (j * x + i) + " duplicated with " + map.get(s) + ", String:" + s);
				} else {
					//System.out.println(s);
					map.put(s, i);
				}
			}
		}
		System.out.println("Total number: " + totalCnt);
		System.out.println("Duplicate number: " + duplicateCnt);
		/*
		System.out.println(Math.pow(36.0, 6));
		System.out.println(Math.pow(36.0, 12));
		System.out.println(Math.pow(36.0, 16));
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Long.MAX_VALUE);
		System.out.println((int)(2147483647L*2147483647L));
		*/
	}

	public static String random(char[] chars, int n) {
		if (chars == null) return random(n);
		int scg = preSCG;
		char[] c = new char[n];
		for (int i = 0; i < n; i++) {
			if (scg == 0) scg = scg(SCGA, SCGC, SCGM);
			int mod = scg % AVAILABLE_CHAR_NUM;
			int mod2 = mod % chars.length;
			c[i] = chars[mod2];
			scg = (scg - mod) / AVAILABLE_CHAR_NUM;
		}
		return String.valueOf(c);
	}

	public static Integer getGreatestCommonDivisor(Integer a, Integer b) {
		if (a == null || b == null) return null;
		if (a == 0 || b == 0) return null;
		int p1, p2;
		if (a > b) {
			p1 = a;
			p2 = b;
		} else {
			p1 = b;
			p2 = a;
		}
		while (true) {
			int remainder = p1 % p2;
			if (remainder == 0) return p2;
			p1 = p2;
			p2 = remainder;
		}
	}
}
