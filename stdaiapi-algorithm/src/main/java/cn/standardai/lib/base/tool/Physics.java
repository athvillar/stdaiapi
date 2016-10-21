package cn.standardai.lib.base.tool;

public class Physics {
	
	public static double g = 9.8;

	public static double getRotateForce(double diameter, double pitch, double width, double pressure, double frequency) {
		return diameter * pitch * width * pressure * frequency * frequency * 0.25;
	}

	public static double getAcceloration(double force, double mass) {
		double totalForce = force - mass * g;
		return totalForce > 0 ? totalForce / mass : 0;
	}
}
