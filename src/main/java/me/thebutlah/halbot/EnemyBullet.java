package me.thebutlah.halbot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import robocode.Rules;

public class EnemyBullet {
	
	//private final Enemy shooter;
	
	Color bulletColor = new Color(255, 0, 0, 64);
	
	private final HalBot robot;
	
	private final double power;
	private final Point2D startLocation;
	private final double speed;
	private final double timeFired;
	
	//represent vectors of paths
	private Vector2D[] possiblePaths = new Vector2D[2];
	//represent current bullet location (should be updated every turn)
	private Point2D[]  possibleLocations = new Point2D[2];
	
	public EnemyBullet(Point2D startLocation, double power, HalBot robot) {
		this.robot = robot;
		this.power = power;
		this.startLocation = startLocation;
		this.speed = Rules.getBulletSpeed(power);
		this.timeFired = robot.getTime();
		
		//System.out.println("Power: " + this.power + ", Speed: " + this.speed);
		
		//initialize all the starting locations
		for (int i=0; i<possibleLocations.length; i++) {
			possibleLocations[i] = startLocation;
		}
		
		//Direct
		possiblePaths[0] = startLocation.vectorTo(robot.getMData().predictPosition(-1));
		//Linear
		possiblePaths[1] = startLocation.vectorTo(MyUtil.getLinearTargetPoint(robot.getMData(), startLocation, power, robot.battlefield));
	}
	
	public void updatePosition(long delta) {
		for (int i = 0; i<possiblePaths.length; i++) {
			Point2D currentPos = possibleLocations[i];
			Vector2D path = possiblePaths[i].getNormalized();
			possibleLocations[i] = new Point2D(currentPos.getX() + speed*delta*path.getDX(), currentPos.getY() + speed*delta*path.getDY());
		}
	}
	
	public void displayPotentialPositions() {
		Graphics2D graphics = robot.getGraphics();
		graphics.setColor(bulletColor);
		//radius in pixels of the circle to be drawn
		int radius = 10;
		for (int i = 0; i<possibleLocations.length; i++) {
			Point2D position = possibleLocations[i];
			graphics.fillOval((int) (position.getX() - radius), (int) (position.getY() - radius), radius*2, radius*2);
		}
	}
	
	public double getPower() {
		return power;
	}
	
	/**
	 * @return A clone of the start position
	 */
	public Point2D getStartPosition() {
		return (Point2D) this.startLocation.clone();
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public double getTimeFired() {
		return timeFired;
	}
	
	public List<Vector2D> getPossiblePaths() {
		return Arrays.asList(this.possiblePaths);
	}
	
	public List<Point2D> getPossibleLocations() {
		return Arrays.asList(this.possibleLocations);
	}

}
