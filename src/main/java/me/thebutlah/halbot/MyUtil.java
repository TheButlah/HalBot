package me.thebutlah.halbot;

import java.awt.Rectangle;
import robocode.Rules;
import robocode.util.Utils;

public class MyUtil {
	
	
		
	/**
	 * Uses a linear prediction algorithm to guess where the target will be. Weak against robots that turn a lot.
	 * 
	 * @param target The MData of the enemy to target
	 * @param shooter The MData of the shooter
	 * @param power The power of the bullet to be shot
	 * @return A point representing the predicted location
	 */
	public static Point2D getLinearTargetPoint(MData target, Point2D shooter, double power, Rectangle battlefield) {
		//LINEAR TARGETING ALGORITHM//////
		double bulletSpeed = Rules.getBulletSpeed(power);
		double targetBearing = target.getBearing();
		double targetSpeed = target.getSpeed();
		double targetX = target.getX();
		double targetY = target.getY();
		double shooterX = shooter.getX();
		double shooterY = shooter.getY();
		
		double A = (targetX - shooterX) / bulletSpeed;
		double B = targetSpeed/bulletSpeed*Math.sin(targetBearing);
		double C = (targetY - shooterY) / bulletSpeed;
		double D = targetSpeed/bulletSpeed*Math.cos(targetBearing);
		
		double a = A*A + C*C;
		double b = 2*(A*B + C*D);
		double c = B*B + D*D - 1;
		
		double discriminant = b*b - 4*a*c;
		
		if (discriminant >= 0) {
			//below we have gotten two possible time values
			double t1 = 2*a / (-b + Math.sqrt(discriminant));
			double t2 = 2*a / (-b - Math.sqrt(discriminant));
			
			//determine which one to use based on which is closer to zero but positive
			double time = (Math.min(t1, t2) >= 0) ? Math.min(t1, t2) : Math.max(t1, t2);
			return target.predictPositionWithBounds(time, battlefield.getMaxX(), battlefield.getMinX(), battlefield.getMaxY(), battlefield.getMinY());
		} else {
			System.out.println("Discriminant < 0 !!!! Linear Prediction resorting to direct prediction...");
			return target.getPosition();
		}
	}
	
	/**
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return The bearing in Radians. This value starts with zero at north, goes clockwise, and ends at 2PI (exclusive)
	 */
	public static double cartesianToBearing(double x, double y) {
		//different params than you would expect because we need to move where the reference angle is to be north instead of east
		//and we need to change direction of rotation
		double temp = Math.atan2(x, y);
		return Utils.normalAbsoluteAngle(temp);
	}
	
	/**
	 * @return the value bounded between the lower and upper limits
	 */
	public static double limit(double value, double lower, double upper) {
		return Math.max(Math.min(value, upper), lower);
	}
	
	/**
	 * @param bearing the bearingin radians. This value starts with zero at north, and goes clockwise.
	 * @return A Point2D representing the cartesian point resulting from a unit vector with that bearing
	 */
	public static Point2D bearingToCartesian(double bearing) {
		//values appear wrong but arent, because the angle starts on the pos y-axis instead of pos x-axis
		return new Point2D(Math.sin(bearing), Math.cos(bearing));
	}
	
	/**
	 * Calculates how long a bullet traveling with the given power would take to reach the given distance.
	 * @param distance Distance to test over in pixels
	 * @param power Power of the bullet
	 * @return Amount of time in turns
	 */
	public static double getTimeToHit(double distance, double power) {
		power = MyUtil.limit(power, Rules.MIN_BULLET_POWER, Rules.MAX_BULLET_POWER);
		return distance/Rules.getBulletSpeed(power);
	}
	
	/*public static Point2D predictPosition(Vector2D trajectory, Point2D currentPos, double delta, double robotWidth, double battleFieldWidth, double battleFieldHeight) {
		//limits to ensure that the predicted value falls in the battlefield
		double speed = trajectory.getMagnitude();
		double bearing = trajectory.getBearing();
		double predictedX = MyUtil.limit(currentPos.getX() + speed*delta*Math.sin(bearing), robotWidth, battleFieldWidth-robotWidth);
		double predictedY = MyUtil.limit(currentPos.getY() + speed*delta*Math.cos(bearing), robotWidth, battleFieldHeight-robotWidth);
		return new Point2D(predictedX, predictedY);
	}*/

}
