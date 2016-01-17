package me.thebutlah.halbot;

import java.awt.Color;

import robocode.util.Utils;

public class GunManager {
	
	private final HalBot robot;
	
	private boolean shouldFire = false;
	private double firePower = .1;
		
	public GunManager(HalBot robot) {
		this.robot = robot;		
	}
	
	public void init() {
		robot.setAdjustGunForRobotTurn(true);
	}
	
	public void attackTarget(Enemy target) {
		
		//DETERMINE POWER////////////////////
		//double power = Math.max(.1, Math.min(3, 3 - (target.getDistance()-200)*3/200.0 ));
		double power = 3;
		this.setFirePower(power);
		/////////////////////////////////////
		
		//DETERMINE TARGET AND SHOOT IT//////
		//Point2D predictedLoc = target.getPosition();
		Point2D predictedLoc = MyUtil.getLinearTargetPoint(target.getMData(), robot.getPosition(), power, robot.battlefield);
		this.shootAlong(robot.getPosition().vectorTo(predictedLoc));
		/////////////////////////////////////
		
		
		//DRAW PREDICTION LOCATIONS//////////
		robot.getGraphics().setColor(Color.GREEN);
		//oval is drawn with the center at the bottom left corder. center it by subtracting half the diameter
		robot.getGraphics().drawOval((int)Math.round(predictedLoc.getX())-10, (int)Math.round(predictedLoc.getY())-10, 20,20);
		/////////////////////////////////////
		
	}
	
	/**
	 * Shoots along a given vector once; only begins shooting when the gun is in the correct position.
	 * Note that the vector should stay the same regardless of the Robot's movement 
	 * @param vector Vector to shoot along
	 */
	public void shootAlong(Vector2D vector) {
		activateFire(true);
		double gunBearing = robot.getGunHeadingRadians();
		//angle converted to -pi to pi so that instead of rotating, for example, 350 degrees right, it can just rotate 10 degrees lefte
		robot.setTurnGunRightRadians(Utils.normalRelativeAngle(vector.getBearing() - gunBearing));
		//if the gun has positioned itself
		
	}
	
	public void shootPoint(Point2D point) {
		//TODO: implement this
	}
	
	/**
	 * Code to fire the gun
	 */
	public void doFire() {
		if (Math.abs(robot.getGunTurnRemainingRadians()) <= 0.2 && shouldFire()) robot.setFire(getFirePower());
		activateFire(false);
	}
	
	public boolean shouldFire() {
		return shouldFire;
	}
	
	public void activateFire(boolean shouldFire) {
		this.shouldFire = shouldFire;
	}
	
	public double getFirePower() {
		return firePower;
	}
	
	public void setFirePower(double power) {
		firePower = power;
	}
}
