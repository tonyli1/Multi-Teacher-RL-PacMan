package pacman.teaching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pacman.entries.pacman.BasicRLPacMan;
import pacman.entries.pacman.RLPacMan;
import pacman.game.Game;
import pacman.game.Constants.MOVE;

/**
 * Superclass for all student learners.
 */
public class Student extends RLPacMan {

//	private BasicRLPacMan teacher; // Gives advice : consider changing to array of teachers
	private ArrayList<BasicRLPacMan> teacher;
	private BasicRLPacMan student; // Takes advice
	private TeachingStrategy strategy; // Determines when advice is given
	
	private boolean testMode; // When set, will not explore or learn or take advice
	private int adviceCount; // During the last episode
	
//	public Student(BasicRLPacMan teacher, BasicRLPacMan student, TeachingStrategy strategy) {
	public Student(ArrayList<BasicRLPacMan> teacher, BasicRLPacMan student, TeachingStrategy strategy) {
		this.teacher = teacher;
		this.student = student;
		this.strategy = strategy;
	}
	
	public int getAdviceCount()
	{
		return adviceCount;
	}

	/** Prepare for the first move. */
	public void startEpisode(Game game, boolean testMode) {
		this.testMode = testMode;
		adviceCount = 0;
		student.startEpisode(game, testMode);
		
		if (!testMode && strategy.inUse()) {
			strategy.startEpisode();
			for (int i = 0; i < teacher.size(); i++) {
		        teacher.get(i).startEpisode(game, true);
		    }
		}
	}
	
	/** Of the teachers' advice, choose the one most frequently given */
	public MOVE majorityVote(ArrayList<MOVE> lst) {
	    Map<MOVE, Integer> m = new HashMap<MOVE, Integer>();

	    for (int i = 0; i < lst.size(); i++) {
	        Integer freq = m.get(lst.get(i));
//	        System.out.println("MOVE: " + lst.get(i));
//	        System.out.println("freq: " + freq);
	        m.put(lst.get(i), (freq == null) ? 1 : freq + 1);
	    }
	    
	    int max = -1;
	    ArrayList<MOVE> mostFrequent = new ArrayList<MOVE>();
	    mostFrequent.add(MOVE.NEUTRAL);

	    for (Map.Entry<MOVE, Integer> e : m.entrySet()) {
	        if (e.getValue() > max) {
	            mostFrequent = new ArrayList<MOVE>();
	            mostFrequent.add(e.getKey());
	            max = e.getValue();
	        }
	        else if (e.getValue() == max) {
	        	mostFrequent.add(e.getKey());
	        }
	    }
	    Random rand_i = new Random(182);
//	    System.out.println("mostFreq: " + mostFrequent.get(0));
//	    System.out.println("~~~~~~~~~~~~~~~~~~~~~~");
	    return mostFrequent.get(rand_i.nextInt(mostFrequent.size()));
	}
	
	/** Choose a move, possibly with advice. */
	public MOVE getMove(Game game, long timeDue) {
		
		MOVE choice = student.getMove(game, timeDue);
		
		if (!testMode && strategy.inUse()) {
			ArrayList<MOVE> teachers_advice = new ArrayList<MOVE>();
			 for (int i = 0; i < teacher.size(); i++) {
			        teachers_advice.add(teacher.get(i).getMove(game, timeDue));
			    }
			MOVE advice = majorityVote(teachers_advice);
//			MOVE advice = teacher.getMove(game, timeDue);
			
			if (strategy.giveAdvice(teacher, choice, advice)) {
				ArrayList<MOVE> final_advice = strategy.getTeachersAdvice(teachers_advice);
				if (final_advice.size() > 0) {
					// limits teachers if implemented (CorrectImportant)
					advice = majorityVote(final_advice);
				}
				student.setMove(advice);
				adviceCount++;
				return advice;
			}
		}

		return choice;
	}
	
	/** Prepare for the next move. */
	public void processStep(Game game) {
		student.processStep(game);
		
		if (!testMode && strategy.inUse())
			for (int i = 0; i < teacher.size(); i++) {
				teacher.get(i).processStep(game);
		    }
			
	}
	
	/** Save the current policy to a file. */
	public void savePolicy(String filename) {
		student.savePolicy(filename);
	}

	/** Report amount of advice given in the last episode,
	 *  along with any other data the strategy wants to record. */
	public double[] episodeData() {
		
		double[] extraData = strategy.episodeData();
		
		double[] data = new double[extraData.length+1];
		data[0] = adviceCount;
		
		for (int d=0; d<extraData.length; d++)
			data[d+1] = extraData[d];
		
		return data;
	}
}
