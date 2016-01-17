package me.thebutlah.halbot;

import robocode.Rules;
import robocode.util.Utils;

public class RadarManager {
	
	private static final double OSCILLATION_VALUE = Rules.RADAR_TURN_RATE_RADIANS/2*.75;

	private final OOPBot robot;
	
	private boolean lastScanClockwise = true;
		
	public RadarManager(OOPBot robot) {
		this.robot = robot;
	}
	
	public void init() {
		robot.setAdjustRadarForGunTurn(true);
	}
	
	/**
	 * Tracks a robot with this robot's radar. Will use linear prediction over a small time interval to guess where the enemy will be next.
	 * If we lose our lock on the enemy, this function will switch into search mode, and attempt to re-acquire the lock
	 * @param target The enemy robot to track
	 */
	public void trackTarget(Enemy target) {
		Point2D predictedPoint = target.getMData().predictPosition(robot.getDelta());
		Vector2D predictedVector = robot.getPosition().vectorTo(predictedPoint);
		//how much the radar angle needs to change to be spot on
		double theta = Utils.normalRelativeAngle(predictedVector.getBearing() - robot.getRadarHeadingRadians());
		if (this.lastScanClockwise()) {
			this.sweepAngle(Utils.normalRelativeAngle(theta - OSCILLATION_VALUE));
		} else {
			this.sweepAngle(Utils.normalRelativeAngle(theta + OSCILLATION_VALUE));
		}
	}
	
	/**
	 * @param vector Vector to point radar along
	 */
	public void scanAlong(Vector2D vector) {
		double radarBearing = robot.getRadarHeadingRadians();
		double theta = Utils.normalRelativeAngle(vector.getBearing() - radarBearing);
		lastScanClockwise = (theta >= 0) ? true : false;
		this.sweepAngle(theta);
	}
	
	/**
	 * Sets the robot to sweep through the given angle. Positive values are clockwise, negative are counterclockwise.
	 * Use this method instead of setTurnRadarRightRadians()
	 * @param angle Angle in radians
	 */
	public void sweepAngle(double angle) {
		robot.setTurnRadarRightRadians(angle);
		lastScanClockwise = (angle >= 0) ? true : false;
	}
	
	public boolean lastScanClockwise() {
		return lastScanClockwise;
	}
}
