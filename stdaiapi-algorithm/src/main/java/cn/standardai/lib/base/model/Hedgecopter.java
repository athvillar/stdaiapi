package cn.standardai.lib.base.model;

import cn.standardai.lib.algorithm.ann.BPNetwork;
import cn.standardai.lib.algorithm.exception.AnnException;
import cn.standardai.lib.base.tool.Physics;

public class Hedgecopter {

	private BPNetwork decisionCore;

	private BPNetwork predictCore;
	
	private Propeller propeller;
	
	private double mass;
	
	private double rps;
	
	private double height;
	
	private double speed;
	
	private double acceleration;
	
	private double clock = 0.1;
	
	private double insHeight = 5.0;
	
	private double insTimeLimit = 10;
	
	private double insDeviation = 0.1;
	
	public Hedgecopter(BPNetwork core1, BPNetwork core2, Propeller propeller, double mass) {
		super();
		this.decisionCore = core1;
		this.predictCore = core2;
		this.propeller = propeller;
		this.mass = mass;
		this.rps = 0;
		this.height = 0;
		this.speed = 0;
		this.acceleration = 0;
	}

	public boolean crash() {
		return height < 0;
	}
	
	public void next() {
		double[] input4DesicionCore = new double[] {height, speed, acceleration, mass, insHeight, insTimeLimit, insDeviation};
		double[] desicionCoreOutput = decisionCore.predict(input4DesicionCore);
		rps += desicionCoreOutput[0];
		
		double[] input4PredictCore = new double[] {height, speed, acceleration, mass, rps};
		double[] predictCoreOutput = predictCore.predict(input4PredictCore);
		// TODO sleep
		double[] predictCoreExpect = getRealStatus();
		
		try {
			if (isCorrect(desicionCoreOutput)) {
				decisionCore.train(new double[][] {input4DesicionCore}, new double[][] {desicionCoreOutput});
			}
			predictCore.train(new double[][] {input4PredictCore}, new double[][] {predictCoreExpect});
		} catch (AnnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private double getHeightByTime(double height, double speed, double acceleration, double time) {
		return height + speed * time + speed * time * time / 2;
	}
	
	private boolean isCorrect(double[] desicions, double[] predicts) {
		double heightInFuture = getHeightByTime(height, speed, acceleration, time);
		if (height < insHeight * (1 + insDeviation) && height > insHeight * (1 - insDeviation)) {
			return true;
		} else {
			return false;
		}
		for (int i = 0; i < insTimeLimit; i++) {
			
		}
	}
	
	private double[] getRealStatus() {

		double force = Physics.getRotateForce(propeller.diameter, propeller.pitch, propeller.width, 1, rps);
		acceleration = Physics.getAcceloration(force, mass);
		if (height != 0 || acceleration > 0) {
			speed += acceleration * clock;
		}
		height += speed * clock;
		return new double[] {height, speed, acceleration};
	}
}
