package edu.imt.Applyparout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.imt.inputData.Event;
import edu.imt.inputData.Event.EventTemp;
import edu.imt.specification.structure.BlockStructure;
import edu.imt.specification.structure.Operator;

public abstract class AbstractParout implements IParout{

	protected static Map<Event[], Event> commSet = new HashMap<Event[],Event>();
	protected static Set<Event> allowSet = new HashSet<Event>();
	protected static Set<Event> hideSet = new HashSet<Event>();
	
	/**
	 * Returns a unique action t
	 * @return a unique action t
	 */
	protected static Event getTemporaryEvent() {
		return new EventTemp();
	}
	
	/**
	 * Returns true if this block structure contains a block with a parallel operator, otherwise false
	 * @param b the block structure
	 * @return true if this block structure contains a block with a parallel operator, otherwise false
	 */
	protected static boolean hasParallel(BlockStructure b) {
		if (!b.hasBlock())
			return false;
		for (BlockStructure block : b.getBlock()) {
			if (block.getOp()!= null && block.getOp().equals(Operator.PARALLEL))
				return true;
		}
		return false;
	}

	
	protected static void updateCommAllHide(Event e, int size) {
		Event[] commfun = new Event[size];
		for(int i=0; i<commfun.length; i++)
			commfun[i] = e;
		Event resultSync = getTemporaryEvent();
		commSet.put(commfun, resultSync);
		allowSet.add(resultSync);
		hideSet.add(resultSync);
	}
}
