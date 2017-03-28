package pacman.teaching;

import java.util.ArrayList;

import pacman.Experiments;
import pacman.entries.pacman.BasicRLPacMan;
import pacman.game.Constants.MOVE;
import pacman.utils.Stats;

/**
 * Gives a fixed amount of advice in important states.
 */
public class AdviseImportantStates extends TeachingStrategy {
	
	public int left; // Advice to give
	public double maxvar;
	private int threshold; // Of action importance
	public static String TEACHDIR = "myDataTeacher/AdviseImportant"; // Where to store data
//	public static int[] BESTTHRESHOLDS = {40, 40, 40, 40, 40}; // empirically-chosen thresholds for 5 teachers
	private ArrayList<Integer> budgets_left; //individual advice to give

	
//	public class TeacherMeta {
//		public double q_gap;
//		public MOVE advice;
//	}
	
	public AdviseImportantStates(int t) {
		left = Experiments.BUDGET;
		threshold = t;
	//	maxvar=0;
		budgets_left = Experiments.INDIEBUDGETS;
	}  

	/** When the state has widely varying Q-values. */
	public boolean giveAdvice(ArrayList<BasicRLPacMan> teachers, MOVE _choice, MOVE _advice) {
		
		
		boolean UseVariance = true;
		
		// counter of how many teachers flag state as important
		int teacher_flags = 0;
		// keep track of teachers' budget usage
		int[] teacher_usage = new int[teachers.size()];
		
		for (int i = 0; i < teachers.size(); i++) {
		
			double[] qvalues = teachers.get(i).getQValues();
			double gap;
			
			
			//threshold *=2.5; //REMOVE just testing
				
//			if (UseVariance == false)
//			{
				
			gap = Stats.max(qvalues) - Stats.min(qvalues);
			
//			}
//			else
//			{
//			//threshold = (int) 72500;//(99/100 * maxvar);
//			gap = VarianceGap(qvalues);
//			//gap = CVgap(qvalues);
//			//threshold = (int) (80/100 * Experiments.varTHRESHOLD);
//	
//			//System.out.println(gap+"var");
//	
//			//threshold = VarianceThreshold(qvalues,threshold);
//			}
			
//			teacher_data[i].q_gap = gap;
//			System.out.println(i+": "+gap);
			
			boolean important = gap > threshold;//> *1000
			Experiments.writer.println(i+","+gap+","+left);
	
			if (important) {
//				return true;
				teacher_usage[i] = 1;
				teacher_flags++;
			}
			
		}
		
//		System.out.println("teacher_flags: "+teacher_flags);
		
		if (teacher_flags > (teachers.size() / 2)) {
			left--;
			if (budgets_left.size() > 0) {
				for (int i=0; i < teachers.size(); i++) {
					// update individual budgets
					budgets_left.set(i, (budgets_left.get(i) - teacher_usage[i]));
				}
			}
			return true;
		}
		
		return false; 
	}
	 
	/** Until none left. */
	public boolean inUse() {
		return (left > 0);
	}
	
	public double VarianceGap(double[] qvalues)
	{
		//double currentvar = Stats.variance(qvalues, Stats.average(qvalues));
		double currentvar = Stats.absdeviation(qvalues, Stats.average(qvalues));

		//System.out.println(currentvar+"var1");
		//System.out.println(Experiments.varTHRESHOLD+"var2");
		//if (currentvar > Experiments.varTHRESHOLD) 
			//{
			
			//Experiments.varTHRESHOLD = currentvar;

			//}
		//System.out.println(currentvar+"var3");

		return currentvar;
}
	
	public double CVgap(double[] qvalues)
	{
		double currentvar = Math.sqrt(Stats.variance(qvalues, Stats.average(qvalues)))/Stats.average(qvalues);
		//double currentvar = Stats.absdeviation(qvalues, Stats.average(qvalues));

		//System.out.println(currentvar+"var1");
		//System.out.println(Experiments.varTHRESHOLD+"var2");
		//if (currentvar > Experiments.varTHRESHOLD) 
			//{
			
			//Experiments.varTHRESHOLD = currentvar;

			//}
		//System.out.println(currentvar+"var3");

		return currentvar;
}
	
	public int VarianceThreshold(double[] qvalues,int oldthreshold)
	{
	  double[] thresholdqvalues = new double[] {Stats.max(qvalues),(Stats.min(qvalues)+Stats.max(qvalues))/2,Stats.min(qvalues)};
	  return (int) Stats.variance(thresholdqvalues, Stats.average(thresholdqvalues));
		//return	  (int) Math.pow((Stats.average(qvalues)- (double) oldthreshold), 2);
	}
	
	}
	

