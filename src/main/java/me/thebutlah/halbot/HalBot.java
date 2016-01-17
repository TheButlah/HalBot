package me.thebutlah.halbot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import robocode.*;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Robot that takes a highly object-oriented approach to all of its decisions and actions. This enables the robot to be easily readable and modular.
 * Written in 2014-2015 in my senior year of High School
 * 
 * @author TheButlah
 *
 */
public class HalBot extends AdvancedRobot {

	GunManager gman = new GunManager(this);
	RadarManager rman = new RadarManager(this);
	
	Enemy target = null;
	
	List<EnemyBullet> bullets = new ArrayList<EnemyBullet>();
	
	//battlefield, but shrunk to account for size of robot
	public Rectangle battlefield;
	
	private long lastTime;
	private long delta;
	
	public Graphics2D graphics;

	@Override
	public void run() {
		// init code
		setColors(Color.DARK_GRAY, Color.RED, Color.GRAY, Color.WHITE, Color.LIGHT_GRAY);
		graphics = getGraphics();
		
		battlefield = new Rectangle((int) (this.getWidth()/2.0), (int) (this.getHeight()/2.0),(int) (this.getBattleFieldWidth()-this.getWidth()),(int) (this.getBattleFieldHeight()-this.getHeight()));
		
		gman.init();
		rman.init();
		
		calculateDelta();

		rman.sweepAngle(Math.PI);
		
		//positive is forwards, negative is backwards
		int mdirection = -1;
		//ROBOT MAIN LOOP///////
		while (true) {
			calculateDelta();
			//display radar & gun orientation
			
			
			
			//CODE TO TRACK ENEMY BULLETS/////
			Rectangle battlefield = new Rectangle((int) this.getBattleFieldWidth(), (int) this.getBattleFieldHeight());
			bulletLoop:
			for (int i = 0; i<bullets.size(); i++) {
				EnemyBullet b = bullets.get(i);
				b.updatePosition(delta);
				b.displayPotentialPositions();
				for (Point2D loc : b.getPossibleLocations()) {
					if (battlefield.contains(loc.getX(), loc.getY())) {
						//move on to the next bullet
						continue bulletLoop;
					}
				}
				//if this line is reached, it means that there is not a single possible location for the current bullet that falls within the battlefield
				bullets.remove(i);
			}
			////////////////////////////////////
			
			
			//BEHAVIOR TO TURN RADAR WHEN IT IS FINISHED///
			if (getRadarTurnRemainingRadians() == 0) {
				//positive is clockwise, negative is counter-clockwise
				int rdirection = (rman.lastScanClockwise()) ? -1 : 1;
				rman.sweepAngle(rdirection*Double.POSITIVE_INFINITY);
			}
			//////////////////////////////////////
			
			gman.doFire();
			
			//MOVEMENT OSCILLATION/////
			if (getDistanceRemaining() == 0) {
				mdirection *= -1;
				setAhead(200*mdirection);
			}
			///////////////////////////
			
			execute();
		}
		///////////////////////
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		//DETERMINE THE TARGET///
		if (target == null) {
			target = new Enemy(e, this);
		} else if (e.getDistance() < target.getDistance() - 30) { // distance - # is to provide a buffer before switching targets
			//will not just update info, but also switch targets as well for us
			target.updateInfo(e);
		} else if (e.getName().equals(target.getName())) {
			target.updateInfo(e);
		} else {
			return;
		}	
		/////////////////////////
		
		target.paintMovementHistory();
		
		
		
		//BEHAVIOR CONCERNING ENERGY CHANGE////
		double energyChange = target.getEnergyChange();
		if (energyChange<0) {
			bullets.add(new EnemyBullet(target.getMData().predictPosition(-1), Math.abs(energyChange), this));
		}
		///////////////////////////////////////
		
		//CONTROL GUN////////////
		gman.attackTarget(target);
		/*//targeting vector used for the gun
		Vector2D targetVector = this.getPosition().vectorTo(target.getPosition());
		gman.shootAlong(targetVector);
		//min damage out at 400 pixels, max damage at 200 pixels
		gman.setFirePower(Math.max(.1, Math.min(3, 3 - (target.getDistance()-200)*3/200.0 )));*/
		/////////////////////////
		
		
		//CONTROL RADAR//////////
		rman.trackTarget(target);
		/*Vector2D predictedVector = this.getPosition().vectorTo(target.predictPosition(delta));
		if (target.isLocked()) {
			rman.scanAlong(predictedVector);
		} else {
			//how many radians the radar is away from pointing directly @ the enemy
			double theta = Utils.normalRelativeAngle(predictedVector.getBearing() - this.getRadarHeadingRadians());
			if (rman.lastScanClockwise()) {
				//turn radar .5 radians PAST where the enemy ought to be
				rman.sweepAngle(theta - .5);
			} else {
				//turn radar .5 radians PAST where the enemy ought to be
				rman.sweepAngle(theta + .5);
			}
		}*/
		//////////////////////////
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
		if (target != null && e.getName().equals(target.getName())) {
			target.updateEnergy(target.getEnergy() + Rules.getBulletHitBonus(e.getPower()));
		}
	}
	
	@Override
	public void onBulletHit(BulletHitEvent e) {
		if (target != null && e.getName().equals(target.getName())) {
			target.updateEnergy(e.getEnergy());
		}
	}
	
	@Override
	public void onHitRobot(HitRobotEvent e) {
		if (target != null && e.getName().equals(target.getName())) {
			target.updateEnergy(e.getEnergy());
		}
	}

	@Override
	public void onHitWall(HitWallEvent e) {
		//TODO: add stuff here
	}
	
	@Override
	public void onRobotDeath(RobotDeathEvent e) {
		if (target != null && e.getName() == target.getName()) target = null;
	}
	
	public MData getMData() {
		return new MData(this.getX(), this.getY(), getTime(), this.getHeadingRadians(), this.getVelocity());
	}
	
	/**
	 * Calculates and updates the turns elapsed since it was last called.a
	 * Should be called every turn
	 */
	public void calculateDelta(){
		long currentTime = getTime();
		long delta = currentTime - lastTime;
		lastTime = currentTime;
		this.delta = delta;
	}
	
	public long getDelta() {
		return this.delta;
	}
	
	public Point2D getPosition() {
		return new Point2D(this.getX(), this.getY());
	};
}