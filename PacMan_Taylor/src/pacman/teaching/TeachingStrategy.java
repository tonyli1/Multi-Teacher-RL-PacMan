package pacman.teaching;

import java.util.ArrayList;

import pacman.entries.pacman.BasicRLPacMan;
import pacman.game.Constants.MOVE;

/**
 * Determines whether advice is given.
 */
public abstract class TeachingStrategy {
	
	public abstract boolean giveAdvice(ArrayList<BasicRLPacMan> teacher, MOVE choice, MOVE advice);
	public abstract boolean inUse();
	
	public void startEpisode() {} // Override to do start-of-episode stuff
		
	public double[] episodeData() { // Override to add data to learning curves
		double[] data = new double[0];
		return data;
	}
	
	public ArrayList<MOVE> getTeachersAdvice(ArrayList<MOVE> all_moves) {
		return new ArrayList<MOVE>();
	}	//override in correctImportant
}
