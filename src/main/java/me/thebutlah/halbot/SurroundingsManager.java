package me.thebutlah.halbot;

/**
 * This class is in charge of compiling all the data about the robot's surroundings and analyzing them. 
 * The class distills this information and enables other classes to access its guess of the robot's surroundings.
 * For example, this class is in charge of estimating incoming projectiles, enemy robots, and more.
 * The class will most likely recieve almost all of its data from the RadarManager.
 *
 * NOTE: This class has not actually been implemented yet
 * 
 * @author TheButlah
 */
public class SurroundingsManager {

	private final OOPBot robot;
	
	public SurroundingsManager(OOPBot robot) {
		this.robot = robot;
	}
	
}
