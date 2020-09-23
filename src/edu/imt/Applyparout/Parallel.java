package edu.imt.Applyparout;

import edu.imt.specification.structure.BlockStructure;
import edu.imt.specification.structure.Operator;

/**
 * Applies the Tp function over the block structure b by applying the Tp
 * function over every block inside this block.
 * 
 * @see Parallel#interpreter(BlockStructure)
 * @author S. Belluccini
 */

public class Parallel extends AbstractParout {

	@Override
	public BlockStructure interpreter(BlockStructure block) {

		BlockStructure b = new BlockStructure(Operator.PARALLEL);
		
		for (int i = 0; i < block.size(); i++)
			b.addBlockAtPosition(ApplyParout.Tp(block.getBlock(i)), i);

		return b;
	}

}
