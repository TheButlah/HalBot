package me.thebutlah.halbot;

import robocode.util.Utils;

public class Vector2D implements Cloneable{
	
	private double dx;
	private double dy;
	
	private double magnitude;
	private double bearing;
	
	//private boolean synched = true;
	/**
	 * @param bearing Bearing of vector in radians. Starts 0 at north, loops to 2*PI (exclusive), goes clockwise
	 * @param magnitude Magnitude of vector
	 */
	public Vector2D(double bearing, double magnitude) {
		this.magnitude = magnitude;
		this.bearing = Utils.normalAbsoluteAngle(bearing);
		
		//values appear wrong but arent, because the angle starts on the pos y-axis instead of pos x-axis
		this.dx = magnitude*Math.sin(bearing);
		this.dy = magnitude*Math.cos(bearing);
	}
	
	/**
	 * @param factor The scalar value to multiply the vector by
	 * @return Returns a new vector that is equal to this vector scaled by a certain value
	 */
	public Vector2D scale(double factor) {
		return new Vector2D(this.bearing, this.magnitude*factor);
	}
	
	public double getDX() {
		return this.dx;
	}
	
	public double getDY() {
		return this.dy;
	}
	
	public double getMagnitude() {
		return this.magnitude;
	}
	
	/**
	 * @return Bearing of vector. Starts 0 at north, loops to 2*PI (exclusive), goes clockwise
	 */
	public double getBearing() {
		return this.bearing;
	}
	
	/**
	 * 
	 * @return A normalized (aka unit) vector with the same direction but a magnitude of 1
	 */
	public Vector2D getNormalized() {
		return new Vector2D(this.bearing, 1);
	}
	
	@Override
	public Vector2D clone() {
		return new Vector2D(this.bearing, this.magnitude);
	}
	
}
