package edu.imt.Applyparout;

import org.apache.commons.lang3.ArrayUtils;
import edu.imt.inputData.*;
import edu.imt.specification.structure.Operator;
import edu.imt.specification.structure.BlockStructure;

/**
 * Applies the Tch function over the block structure b, if no block inside b
 * have a parallel operator then no action is performed, otherwise the parallel
 * operator is carried as first row operator adding synchronization among some
 * new events. For more details, look at the function Tch in Fig 3 of the paper.
 * 
 * @see Choice#interpreter(BlockStructure)
 * @author S. Belluccini
 */

public class Choice extends AbstractParout {

	@Override
	public BlockStructure interpreter(BlockStructure block) {

		BlockStructure b = new BlockStructure(Operator.CHOICE);

		for (int i = 0; i < block.size(); i++)
			b.addBlockAtPosition(ApplyParout.Tp(block.getBlock(i)), i);

		if (!AbstractParout.hasParallel(b))
			return b;

		// Index of the block with the parallel operator
		int indexParBlock = -1;
		for (int i = 0; i < b.size(); i++) {
			if (b.getBlock(i).hasOp() && b.getBlock(i).getOp().equals(Operator.PARALLEL)) {
				indexParBlock = i;
				break;
			}
		}

		// Construct the left side
		BlockStructure leftSide = new BlockStructure(Operator.CHOICE);

		BlockStructure[] otherSide = new BlockStructure[b.size() - 1];
		int j = 0;
		for (int i = 0; i < b.size(); i++) {
			if (i != indexParBlock) {
				// ti.Pi.ti
				Event t = AbstractParout.getTemporaryEvent();
				BlockStructure[] tmpLeft = new BlockStructure[3];
				tmpLeft[0] = new BlockStructure(t);
				tmpLeft[1] = b.getBlock(i);
				tmpLeft[2] = new BlockStructure(t);
				leftSide.addBlockAtPosition(new BlockStructure(tmpLeft, Operator.SEQUENCE), j);
				// ti.ti
				BlockStructure[] tmpOther = new BlockStructure[2];
				tmpOther[0] = new BlockStructure(t);
				tmpOther[1] = new BlockStructure(t);
				updateCommAllHide(t, b.getBlock(indexParBlock).size());
				otherSide[j] = new BlockStructure(tmpOther, Operator.SEQUENCE);
				j++;
			}
		}

		// t.Q1.t
		Event t = AbstractParout.getTemporaryEvent();
		BlockStructure[] firstParBlock = new BlockStructure[3];
		firstParBlock[0] = new BlockStructure(t);
		firstParBlock[1] = b.getBlock(indexParBlock).getBlock(0);
		firstParBlock[2] = new BlockStructure(t);
		updateCommAllHide(t, b.getBlock(indexParBlock).size());
		leftSide.addBlockAtPosition(new BlockStructure(firstParBlock, Operator.SEQUENCE), leftSide.size());

		leftSide = new Choice().interpreter(leftSide);

		// Put all togheter in a unique parallel block
		BlockStructure parallelBlock = new BlockStructure(Operator.PARALLEL);

		// Add the left side
		parallelBlock.addBlockAtPosition(leftSide, 0);
		// Construct and add the other sides
		for (int i = 1; i < b.getBlock(indexParBlock).size(); i++) {
			// t.Qi.t
			BlockStructure[] tmp = new BlockStructure[3];
			tmp[0] = new BlockStructure(t);
			tmp[1] = b.getBlock(indexParBlock).getBlock(i);
			tmp[2] = new BlockStructure(t);

			BlockStructure tmpPar = new BlockStructure(tmp, Operator.SEQUENCE);
			BlockStructure[] tmpChoice = ArrayUtils.add(otherSide, tmpPar);
			parallelBlock.addBlockAtPosition(new BlockStructure(tmpChoice, Operator.CHOICE), i);
		}
		return parallelBlock;

	}

}
