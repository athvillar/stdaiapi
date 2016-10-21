/**
* Converter.java
* Copyright 2014 standardai Co.ltd.
*/
package cn.standardai.lib.base.tool;

/**
 * 格式转换函数
 * @author 韩晴
 *
 */
public class Converter {

	/**
	 * 将byte数组转换为二进制字符串显示
	 * @param input
	 * 输入byte数组
	 * @return 2进制字符串
	 */
	public static String bytes2BinaryString(byte[] input) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < input.length; i++) {
			int iInput = Byte.toUnsignedInt(input[i]);
			for (int j = 7; j >= 0; j--) {
				if (iInput % 2 == 1) {
					sb.append('1');
				} else {
					sb.append('0');
				}
				iInput >>>= 1;
			}
		}

		return sb.toString();
	}

	/**
	 * 将二进制字符串转换为byte数组
	 * @param input
	 * 输入2进制字符串
	 * @return byte数组
	 */
	public static byte[] binaryString2Bytes(String input) {
		byte[] b = new byte[(input.length() - 1) / 8 + 1];
		char[] c = input.toCharArray();
		for (int j = 0; j < b.length; j++) {
			for (int k = j * 8; k < j * 8 + 8; k++) {
				if (k < c.length && c[k] == '1') {
					b[j] += Math.pow(2, k - j * 8);
				}
			}
		}

		return b;
	}

	/**
	 * 将二进制字符串转换为数值
	 * @param input
	 * 输入2进制字符串
	 * @return 数值
	 */
	public static int binaryString2Int(String input) {
		int result = 0;
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '1') {
				result += Math.pow(2, i);
			}
		}

		return result;
	}
}
