package pacman.teaching;

import java.util.ArrayList;

import pacman.Experiments;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.game.Constants.MOVE;
import pacman.utils.Stats;

/**
 * Gives a fixed amount of advice in important states where the student makes a mistake.
 */
public class CorrectImportantMistakes extends TeachingStrategy {
	
	private int left; // Advice to give
	private int threshold; // Of mistake importance
		
	public CorrectImportantMistakes(int t) {
		left = Experiments.BUDGET;
		threshold = t;
	}

	/** When the state has widely varying Q-values, and the student doesn't take the advice action. */
	public boolean giveAdvice(ArrayList<BasicRLPacMan> teachers, MOVE choice, MOVE advice) {
		
		int teacher_flags = 0;		
		for (int i = 0; i < teachers.size(); i++) {
			double[] qvalues = teachers.get(i).getQValues();
			double gap = Stats.max(qvalues) - Stats.min(qvalues);
			boolean important = (gap > threshold);
			
			Experiments.writer.println(i+","+gap);
	
			if (important) {
				teacher_flags++;
			}
		}
		
		if (teacher_flags > (teachers.size() / 2)) {
			//majority of teachers agree: important state
			boolean mistake = (choice != advice);
			
			if (mistake) {
				left--;
				return true;
			}
		}
		
		return false;
	}
	
	/** Until none left. */
	public boolean inUse() {
		return (left > 0);
	}
}
