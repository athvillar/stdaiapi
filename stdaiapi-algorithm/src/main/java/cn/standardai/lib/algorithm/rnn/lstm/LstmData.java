package cn.standardai.lib.algorithm.rnn.lstm;

import java.util.HashMap;
import java.util.Map;

public class LstmData {

	public enum Delay {
		YES, NO
	}

	public enum Reflect {

		I1OM('1'), IMO1('2'), IMOM('3'), I1O1('4');

		public char reflect;

		private Reflect(char reflect) {
			this.reflect = reflect;
		}

		private static final Map<Character, Reflect> mappings = new HashMap<Character, Reflect>(4);

		static {
			for (Reflect reflect : values()) {
				mappings.put(reflect.reflect, reflect);
			}
		}

		public static Reflect resolve(Character reflect) {
			return (reflect != null ? mappings.get(reflect) : null);
		}
	}

	public Double[][] x;

	public Integer[] y;

	public Delay yDelay;

	public LstmData(Double[][] x, Integer[] y, Delay yDelay) {
		this.x = x;
		this.y = y;
		this.yDelay = yDelay;
	}
}
