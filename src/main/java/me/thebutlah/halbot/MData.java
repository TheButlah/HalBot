package me.thebutlah.halbot;

import robocode.util.Utils;

/**
 * This class represents the movement data of an object or particle. The class contains information about the object's position AND velocity.
 * The class also provides relevant functions that pertain to the movement of said object or particle.
 * 
 * @author TheButlah
 */
public class MData implements Cloneable{

	private double time;
	private double x;
	private double y;
	private double bearing;
	private double speed;
	
	public MData(double x, double y, double time, double bearing, double speed) {
		this.x = x;
		this.y = y;
		this.time = time;
		this.bearing = bearing;
		this.speed = speed;
	}
	
	/**
	 * Function to predict the location of the object in the future or past represented by this MData object.
	 * @param delta The time elapsed. Should be in terms of the same units as the speed was specified in.
	 * In other words, if the speed that this.getSpeed() represents is in meters/second, delta should be in seconds.
	 * A negative value will predict positions in the past.
	 * @return The predicted position of the object. Assumes that the object retains its inertia.
	 */
	public Point2D predictPosition(double delta) {
		double predictedX = this.getX() + this.getSpeed()*delta*Math.sin(this.getBearing());
		double predictedY = this.getY() + this.getSpeed()*delta*Math.cos(this.getBearing());
		return new Point2D(predictedX, predictedY);
	}
	
	/**
	 * Uses linear projection to predict location with a given time. Will be fully accurate as long as the robot maintains that exact velocity.
	 * Also takes walls into account.
	 * @param delta Time in turns to predict over
	 * @return The predicted location
	 */
	public Point2D predictPositionWithBounds(double delta, double upperboundX, double lowerboundX, double upperboundY, double lowerboundY) {
		
		double predictedX = this.getX() + this.getSpeed()*delta*Math.sin(this.getBearing());
		double predictedY = this.getY() + this.getSpeed()*delta*Math.cos(this.getBearing());
		double dx = predictedX - this.getX();
		double dy = predictedY - this.getY();
		
		//Scale down vector if x component is out of bounds
		if (predictedX > upperboundX || predictedX < lowerboundX) {
			double diffX = (predictedX>upperboundX) ? predictedX - (upperboundX) : -predictedX + lowerboundX;
			double scale = 1-Math.abs(diffX/dx);
			//Fix the weird behavior that occurs when this component's velocity is zero
			if (Utils.isNear(dx, 0)) scale = 1;
			dx = dx*(scale);
			dy = dy*(scale);
			predictedX = this.getX() + dx;
			predictedY = this.getY() + dy;
		}
		//scale down new vector if y component is out of bounds
		if (predictedY > upperboundY || predictedY < lowerboundY) {
			double diffY = (predictedY>upperboundY) ? predictedY - (upperboundY) : -predictedY + lowerboundY;
			double scale = 1-Math.abs(diffY/dy);
			//Fix the weird behavior that occurs when this component's velocity is zero
			if (Utils.isNear(dy, 0)) scale = 1;
			dx = dx*(scale);
			dy = dy*(scale);
			predictedX = this.getX() + dx;
			predictedY = this.getY() + dy;
		}
		//Just in case...
		if (Double.isNaN(predictedX) || Double.isNaN(predictedY)) {
			System.out.println("PREDICTED POS IS NaN in MData.java! Resorting to non-bounded prediction!\nX: " + predictedX + ", Y: " + predictedY);
			return predictPosition(delta);
		}
		
		//finally! that was a lot harder than expected
		return new Point2D(predictedX, predictedY);
	}
	
	//GETTERS//////////////////////////////////////
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getTime() {
		return time;
	}
	
	public double getBearing() {
		return bearing;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	/**
	 * Gets the object's position. Updating this object wont change the mdata's data.
	 */
	public Point2D getPosition() {
		return new Point2D(this.x, this.y);
	}
	//////////////////////////////////////////////
	
	
	//SETTERS/////////////////////////////////////	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setTime(double time) {
		this.time = time;
	}
	
	public void setBearing(double bearing) {
		this.bearing = bearing;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	//////////////////////////////////////////////////
	
	@Override
	public MData clone() {
		return new MData(this.getX(), this.getY(), this.getTime(), this.getBearing(), this.getSpeed());
		
	}
}
