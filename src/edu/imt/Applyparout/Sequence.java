package edu.imt.Applyparout;

import edu.imt.specification.structure.Operator;
import edu.imt.inputData.Event;
import edu.imt.specification.structure.BlockStructure;

public class Sequence extends AbstractParout {

	/**
	 * Applies the Tseq function over the block structure b, if no block inside b
	 * have a parallel operator then no action is performed, otherwise the parallel
	 * operator is carried as first row operator adding synchronization among some
	 * new events. For more details, look at the function Tseq in Fig 3 of the
	 * paper.
	 * 
	 * @see Sequence#interpreter(BlockStructure)
	 * @author S. Belluccini
	 */
	@Override
	public BlockStructure interpreter(BlockStructure block) {

		BlockStructure b = new BlockStructure(Operator.SEQUENCE);

		for (int i = 0; i < block.size(); i++)
			b.addBlockAtPosition(ApplyParout.Tp(block.getBlock(i)), i);

		if (!AbstractParout.hasParallel(b))
			return b;

		int indexParBlock = -1;
		for (int i = 0; i < b.size(); i++) {
			if (b.getBlock(i).hasOp() && b.getBlock(i).getOp().equals(Operator.PARALLEL)) {
				indexParBlock = i;
				break;
			}
		}
		// Generating the left side part of the new parallel block, first remove the
		// parallel block from the sequence

		BlockStructure leftSideSequence = b.removeBlockAtIndex(indexParBlock);

		// Constructing t and Q
		BlockStructure parBlockFirstBlock = b.getBlock(indexParBlock).getBlock(0);
		Event t = AbstractParout.getTemporaryEvent();
		// Adding t.Q.t
		int j = indexParBlock;
		leftSideSequence.addBlockAtPosition(new BlockStructure(t), j);
		leftSideSequence.addBlockAtPosition(parBlockFirstBlock, ++j);
		leftSideSequence.addBlockAtPosition(new BlockStructure(t), ++j);

		// Add t and t' to the comm, allow and hide sets
		updateCommAllHide(t, b.getBlock(indexParBlock).size());
		// Repeat the procedure over the new sequence to check if there are other
		// parallel blocks
		leftSideSequence = new Sequence().interpreter(leftSideSequence);

		// Put all togheter in a unique parallel block
		BlockStructure parallelBlock = new BlockStructure(Operator.PARALLEL);

		parallelBlock.addBlockAtPosition(leftSideSequence, 0);
		for (int i = 1; i < b.getBlock(indexParBlock).size(); i++) {
			// t.Qi.t
			BlockStructure[] tmp = new BlockStructure[3];
			tmp[0] = new BlockStructure(t);
			tmp[1] = b.getBlock(indexParBlock).getBlock(i);
			tmp[2] = new BlockStructure(t);
			parallelBlock.addBlockAtPosition(new BlockStructure(tmp, Operator.SEQUENCE), i);
		}
		return parallelBlock;
	}

}
