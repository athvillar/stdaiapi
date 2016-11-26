package cn.standardai.lib.algorithm.common;

public class ByteUtil {

	public static void putInt(byte[] bytes, int value, int off) {
		for (int i = 0; i < Integer.BYTES; i++) {
			bytes[off + i] = (byte) value;
			value = value >> 8;
		}
	}

	public static int getInt(byte[] bytes, int off) {
		int value = 0;
		for (int i = Integer.BYTES - 1; i >= 0; i--) {
			value |= bytes[i + off] & 0xFF;
			if (i != 0) value = value << 8;
		}
		return value;
	}

	public static void putDouble(byte[] bytes, double value, int off) {
		long l = Double.doubleToLongBits(value);
		for (int i = 0; i < Double.BYTES; i++) {
			bytes[off + i] = new Long(l).byteValue();
			l = l >> 8;
		}
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
}