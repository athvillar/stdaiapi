package cn.standardai.lib.algorithm.common;

public class ByteUtil {

	public static int putBoolean(byte[] bytes, Boolean value, int off) {
		bytes[off] = value ? (byte)1 : (byte)0;
		return 1;
	}

	public static boolean getBoolean(byte[] bytes, int off) {
		return (bytes[off] == (byte)1);
	}

	public static int putInt(byte[] bytes, int value, int off) {
		for (int i = 0; i < Integer.BYTES; i++) {
			bytes[off + i] = (byte) value;
			value = value >> 8;
		}
		return Integer.BYTES;
	}

	public static int getInt(byte[] bytes, int off) {
		int value = 0;
		for (int i = Integer.BYTES - 1; i >= 0; i--) {
			value |= bytes[i + off] & 0xFF;
			if (i != 0) value = value << 8;
		}
		return value;
	}

	public static int putDouble(byte[] bytes, double value, int off) {
		long l = Double.doubleToLongBits(value);
		for (int i = 0; i < Double.BYTES; i++) {
			bytes[off + i] = new Long(l).byteValue();
			l = l >> 8;
		}
		return Double.BYTES;
	}

    public static double getDouble(byte[] bytes, int off) {
        long l;
        l = bytes[off];
        l &= 0xff;
        l |= ((long) bytes[off + 1] << 8);
        l &= 0xffff;
        l |= ((long) bytes[off + 2] << 16);
        l &= 0xffffff;
        l |= ((long) bytes[off + 3] << 24);
        l &= 0xffffffffl;
        l |= ((long) bytes[off + 4] << 32);
        l &= 0xffffffffffl;
        l |= ((long) bytes[off + 5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) bytes[off + 6] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) bytes[off + 7] << 56);
        return Double.longBitsToDouble(l);
    }

	public static int putDoubles(byte[] bytes, Double[][] ds, int index) {
		int length = index;
		if (ds == null || ds[0] == null) return 0;
		length += ByteUtil.putInt(bytes, ds.length, length);
		length += ByteUtil.putInt(bytes, ds[0].length, length);
		for (int i = 0; i < ds.length; i++) {
			for (int j = 0; j < ds[i].length; j++) {
				length += ByteUtil.putDouble(bytes, ds[i][j], length);
			}
		}
		return length - index;
	}

	public static int putDoubles(byte[] bytes, Double[] ds, int index) {
		int length = index;
		if (ds == null) return 0;
		length += ByteUtil.putInt(bytes, ds.length, length);
		for (int i = 0; i < ds.length; i++) {
			length += ByteUtil.putDouble(bytes, ds[i], length);
		}
		return length - index;
	}

	public static Double[][] getDouble2s(byte[] bytes, int index) {
		int m = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		int n = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		Double[][] ds = new Double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				ds[i][j] = ByteUtil.getDouble(bytes, index);
				index += Double.BYTES;
			}
		}
		return ds;
	}

	public static Double[] getDouble1s(byte[] bytes, int index) {
		int m = ByteUtil.getInt(bytes, index);
		index += Integer.BYTES;
		Double[] ds = new Double[m];
		for (int i = 0; i < m; i++) {
			ds[i] = ByteUtil.getDouble(bytes, index);
			index += Double.BYTES;
		}
		return ds;
	}
}