package edu.imt.Applyparout;

import java.util.Map;
import java.util.Set;

import org.javatuples.Quartet;
import edu.imt.inputData.Event;
import edu.imt.specification.structure.*;

/**
 * Applies the Tp function over a block structure in order to transform a block
 * structure into a block structure that follows the pCRL format. Look at Fig 3
 * for more details
 * 
 * @see #applyTp(BlockStructure)
 * @author S. Belluccini
 *
 */
public class ApplyParout {

	/**
	 * Applies the Tp function over a block structure
	 * 
	 * @param b the block structure
	 * @return a quartet <B,commSet, allowSet, hideSet> where B in the block
	 *         structure resulting from Tp, commSet is the set of communication
	 *         function, allowSet is the set with the actions allowed to be performed
	 *         and hideSet is the set with the action to be hided
	 */
	public static Quartet<BlockStructure, Map<Event[], Event>, Set<Event>, Set<Event>> applyTp(BlockStructure b) {
		BlockStructure block = Tp(b);
		return Quartet.with(block, AbstractParout.commSet, AbstractParout.allowSet, AbstractParout.hideSet);
	}

	protected static BlockStructure Tp(BlockStructure b) {
		if (b.hasEvent())
			return b;
		else if (b.getOp() == Operator.PARALLEL)
			b = new Parallel().interpreter(b);
		else if (b.getOp() == Operator.SEQUENCE) {
			b = new Sequence().interpreter(b);
		} else if (b.getOp() == Operator.CHOICE) {
			b = new Choice().interpreter(b);
		} else if (b.getOp() == Operator.LOOP) {
			b = new Loop().interpreter(b);
		}
		return b;
	}

}
