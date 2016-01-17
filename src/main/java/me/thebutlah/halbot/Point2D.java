package me.thebutlah.halbot;

public class Point2D implements Cloneable{

	public double x;
	public double y;
	
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double distanceTo(Point2D other) {
		double dx = this.x - other.x;
		double dy = this.y - other.y;
		
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public double bearingTo(Point2D other) {
		double dx = other.x - this.x;
		double dy = other.y - this.y;
		return MyUtil.cartesianToBearing(dx, dy);
	}
	
	public Vector2D vectorTo(Point2D other) {
		double dx = other.x - this.x;
		double dy = other.y - this.y;
		double magnitude = Math.sqrt(dx*dx + dy*dy);
		return new Vector2D(MyUtil.cartesianToBearing(dx, dy), magnitude);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	@Override
	public Point2D clone() {
		return new Point2D(this.x, this.y);
	}
}
