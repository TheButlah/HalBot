package me.thebutlah.halbot;

import java.awt.Color;
import java.util.LinkedList;

import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Enemy {
	
	public static final int HISTORY_SIZE = 20;
	
	private String name;
	/*private double bearing;
	private double speed;
	private double x;
	private double y;*/
	private MData mdata;
	private LinkedList<MData> mHistory = new LinkedList<MData>();
	private double energy;
	private double lastEnergy;
	private double timeScanned;
		
	private final HalBot robot;
	
	public Enemy(ScannedRobotEvent e, HalBot robot) {
		this.robot = robot;
		this.lastEnergy = e.getEnergy();
		updateInfo(e);
	}
	
	public void updateInfo(ScannedRobotEvent e) {
		this.name = e.getName();
				
		//the absolute bearing of the angle of the vector from the robot to the enemy
		double absoluteBearing = Utils.normalAbsoluteAngle(robot.getHeadingRadians() + e.getBearingRadians());
		double distance = e.getDistance();
		
		double x = robot.getX() + distance*Math.sin(absoluteBearing);
		double y = robot.getY() + distance*Math.cos(absoluteBearing);
		
		double bearing = e.getHeadingRadians();
		double speed = e.getVelocity();
		
		this.timeScanned = robot.getTime();

		
		this.mdata = new MData(x, y, timeScanned, bearing, speed);
		if (mHistory.size() == HISTORY_SIZE) mHistory.removeFirst();
		this.mHistory.addLast(this.mdata);
		 
		
		updateEnergy(e.getEnergy());
		
	}
	
	public Point2D predictPosition(double time) {
		double robotwidth = robot.getWidth();
		double robotheight = robot.getHeight();
		return mdata.predictPositionWithBounds(time, robot.getBattleFieldWidth()-robotwidth, robotwidth, robot.getBattleFieldHeight()-robotheight, robotheight);
	}
	
	public void paintMovementHistory() {
		robot.graphics.setColor(Color.GRAY);
		for (MData mdata : mHistory) {
			Point2D pos = mdata.getPosition();
			robot.graphics.drawOval((int)pos.getX()-10, (int)pos.getY()-10, 20, 20);
		}
	}
	
	public LinkedList<MData> getMovementHistory() {
		return mHistory;
	}
	
	/**
	 * @return Negative if energy has dropped, positive if energy has increased
	 */
	public double getEnergyChange() {
		return this.energy - this.lastEnergy;
	}
	
	public String getName() {
		return name;
	}
	
	public Vector2D getVelocity() {
		return new Vector2D(mdata.getBearing(), mdata.getSpeed());
	}
	
	public double getSpeed() {
		return mdata.getSpeed();
	}
	
	/**
	 * @return The absolute bearing in radians of the Enemy.
	 */
	public double getBearing() {
		return mdata.getBearing();
	}
	
	public Point2D getPosition() {
		return new Point2D(mdata.getX(),mdata.getY());
	}
	
	public double getEnergy() {
		return energy;
	}
	
	public double timeScanned() {
		return timeScanned;
	}
	
	public double getDistance() {
		return this.getPosition().distanceTo(new Point2D(robot.getX(), robot.getY()));
	}
	
	public void updateEnergy(double newEnergy) {
		this.lastEnergy = this.energy;
		this.energy = newEnergy;
	}
	
	/**
	 * @return A clone of this Enemy's MData
	 */
	public MData getMData() {
		return this.mdata.clone();
	}
}
