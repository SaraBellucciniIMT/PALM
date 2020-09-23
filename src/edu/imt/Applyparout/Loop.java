package edu.imt.Applyparout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.imt.specification.structure.BlockStructure;
import edu.imt.specification.structure.Operator;

/**
 * This class applies the T(K) = K function and Td over the block structure b.
 * If by applying Td a parallel operator is found then the parallel needs to be
 * removed by resolving it's interleaving (e||e' it's bisimilar to e.e'+ e'.e)
 * For more details look at Fig 3 and pag 9 of the paper
 * 
 * @see #Td(BlockStructure)
 * @author S. Belluccini
 *
 */
public class Loop extends AbstractParout {

	@Override
	public BlockStructure interpreter(BlockStructure block) {
		return Td(block);
	}

	/**
	 * The Td function applies Td over every block inside inside b and if b has the
	 * parallel operator than this block is transformed in a choice among the
	 * sequence of events inside the block. For more details look at pag 9 of the
	 * paper
	 * 
	 * @see #seq(BlockStructure)
	 * 
	 * @param b the block structure
	 * @return the block structure resulting from Td
	 */
	private BlockStructure Td(BlockStructure b) {
		if (b.hasEvent()) {
			return b;
		} else if (b.getOp() == Operator.SEQUENCE || b.getOp() == Operator.CHOICE || b.getOp() == Operator.LOOP) {
			b = onEveryBlock(b);
		} else if (b.getOp() == Operator.PARALLEL) {
			Set<List<BlockStructure>> blockList = seq(onEveryBlock(b));
			BlockStructure[] arrC = new BlockStructure[blockList.size()];
			int j = 0;
			for (List<BlockStructure> l : blockList) {
				BlockStructure[] arrS = new BlockStructure[l.size()];
				for (int i = 0; i < arrS.length; i++)
					arrS[i] = l.get(i);
				BlockStructure sequence = new BlockStructure(arrS, Operator.SEQUENCE);
				arrC[j++] = sequence;
			}
			b = new BlockStructure(arrC, Operator.CHOICE);
		}

		return b;
	}

	/**
	 * Function that applies Td on every block inside this block
	 * 
	 * @param b the block structure
	 * @return the result of the application of Td over the blocks inside b
	 */
	private BlockStructure onEveryBlock(BlockStructure b) {
		BlockStructure newB = new BlockStructure(b.getOp());
		if(b.getFrequency()!= 0) {
			newB.setFrequency(b.getFrequency());
			newB.setRepetition(b.getRepetition());
		}
		for (int i = 0; i < b.size(); i++)
			newB.addBlockAtPosition(Td(b.getBlock(i)), i);
		return newB;
	}

	/**
	 * Method to find all the possible interleaving among the blocks of a block;
	 * It's inspired to the CCS's expansion law.
	 * @see #f(BlockStructure)
	 * @param b
	 * @return
	 */
	private Set<List<BlockStructure>> seq(BlockStructure b) {
		Set<List<BlockStructure>> blockList = new HashSet<List<BlockStructure>>();
		for (int i = 0; i < b.size(); i++) {
			BlockStructure remainingBlock;
			Set<Pair<BlockStructure, BlockStructure>> pair = f(b.getBlock(i));
			for (Pair<BlockStructure, BlockStructure> p : pair) {
				remainingBlock = b.removeBlockAtIndex(i);
				if (!p.getValue1().isEmpty())
					remainingBlock.addBlockAtPosition(p.getValue1(), i);

				if (remainingBlock.hasBlock()) {
					Set<List<BlockStructure>> partialResult = seq(remainingBlock);
					partialResult.forEach(pR -> {
						pR.add(0, p.getValue0());
					});
					blockList.addAll(partialResult);
				} else {
					List<BlockStructure> singleList = Lists.newArrayList(p.getValue0());
					if (remainingBlock.hasEvent())
						singleList.add(new BlockStructure(remainingBlock.getEvent()));
					blockList.add(singleList);
				}
			}
		}
		return blockList;
	}

	/**
	 * This method detect the first action that is executed inside the block and
	 * returns the remaining block after that action its executed; in case of choice
	 * this action is taken for all the blocks inside the choice block
	 * 
	 * @param block
	 * @return
	 */

	private Set<Pair<BlockStructure, BlockStructure>> f(BlockStructure block) {
		Set<Pair<BlockStructure, BlockStructure>> set = Sets.newHashSet();
		if (block.hasEvent() || block.getOp() == Operator.LOOP) {
			set.add(new Pair<BlockStructure, BlockStructure>(block, new BlockStructure()));
		} else if (block.getOp() == Operator.CHOICE) {
			for (BlockStructure b : block.getBlock())
				set.addAll(f(b));
		} else {
			set = f(block.getBlock(0));
			BlockStructure remainingBlock = block.removeBlockAtIndex(0);

			if (!remainingBlock.isEmpty()) {
				Set<Pair<BlockStructure, BlockStructure>> tmpSet = Sets.newHashSet();
				for (Pair<BlockStructure, BlockStructure> p : set) {
					if (!p.getValue1().isEmpty()) {
						BlockStructure tmp = p.getValue1();
						tmp.addBlockAtPosition(remainingBlock, tmp.size());
						tmpSet.add(new Pair<BlockStructure, BlockStructure>(p.getValue0(), tmp));
					} else
						tmpSet.add(new Pair<BlockStructure, BlockStructure>(p.getValue0(), remainingBlock));
				}
				set = tmpSet;
			}
		}
		return set;

	}

}
