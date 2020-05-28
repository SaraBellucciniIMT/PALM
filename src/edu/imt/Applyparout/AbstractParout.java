package edu.imt.Applyparout;

import edu.imt.specification.operators.Activity;

public abstract class AbstractParout implements IParout{


	protected static Activity t = new Activity("t");
	private static long tindex = 0;
	
	protected String gettemporary() {
		return "t" + (tindex++);
	}
	
	protected void updateCommAllHide(Activity activity, int size) {
		Activity[] commfun = new Activity[size];
		for(int i=0; i<commfun.length; i++)
			commfun[i] = activity;
		ApplyParout.spec.addActSet(t);
		ApplyParout.spec.addCommFunction(commfun, t);
		ApplyParout.spec.addAllowedAction(t);
		ApplyParout.spec.addHideAction(t);
	}
}
