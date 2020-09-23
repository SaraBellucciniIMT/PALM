package edu.imt.algorithm;

import edu.imt.specification.structure.BlockStructure;
import edu.imt.specification.structure.Operator;

public class TransformationRule {

	/**
	 * Returns the only block inside the block structure b
	 * 
	 * @param b the block structure
	 * @return the only block inside the block structure
	 */
	public static BlockStructure removeOperator(BlockStructure b) {
		return b.getBlock(0);
	}
	
	public static BlockStructure identity(BlockStructure b) {
		BlockStructure tmp = new BlockStructure(b.getOp());
		int i=0;
		for(BlockStructure block : b.getBlock()) {
			Operator opi = block.getOp();
			if(opi!= null && opi.equals(b.getOp())){
				for(int j=0; j<block.size(); j++)
					tmp.addBlockAtPosition(block.getBlock(j),i++);
			}else {
				tmp.addBlockAtPosition(block, i++);
			}
		}
		return tmp;
	}
	/**
	 * Apply P{S{e,...,en},S{e,e1',...,en'}} -> S{e,P{S{e1,...,en},S{e1',...,en'}}}
	 * and C{S{e,...,en},S{e,e1',...,en'}} -> S{e,C{S{e1,...,en},S{e1',...,en'}}}
	 * and P{S{e1,...,en,e},S{e1',...,en',e}} -> S{P{S{e1,...,en},S{e1',...,en'}},e}
	 * and C{S{e1,...,en,e},S{e1',...,en',e}} -> S{C{S{e1,...,en},S{e1',...,en'}},e}
	 * 
	 * @param b
	 * @return
	 */
	public static BlockStructure mergeSide(BlockStructure b, String s, int number) {
		BlockStructure[] children = b.getBlock();

		// The element that is equal for all the blocks
		BlockStructure firstElement = new BlockStructure();
		BlockStructure lastElement = new BlockStructure();
		
		if (s.equals(SchimmAlgorithm.LEFT)) {
			firstElement = children[0].getBlock(0, number - 1);

		} else {
			lastElement = children[0].getBlock((children[0].size() - number), children[0].size() - 1);

		}

		// A new block structure without the first/last element
		for (int i = 0; i < children.length; i++) {
			BlockStructure tmp;
			if (s.equals(SchimmAlgorithm.LEFT)) {
				tmp = children[i].removeBlockAtIndex(0, number);
				if(tmp.isEmpty())
					continue;
				lastElement.addBlockAtPosition(tmp, i);
			} else {
				tmp = children[i].removeBlockAtIndex(children[i].size() - number, children[i].size());
				if(tmp.isEmpty())
					continue;
				firstElement.addBlockAtPosition(tmp, i);
			}
		}
		// The sequence block enclosing the new blocks
		BlockStructure choice ;
		if(s.equals(SchimmAlgorithm.LEFT)) {
			choice = new BlockStructure(lastElement.getBlock(), b.getOp());
			firstElement.setOp(Operator.SEQUENCE);
			firstElement.addBlockAtPosition(choice, firstElement.size());
			return firstElement;
		}else {
			choice = new BlockStructure(firstElement.getBlock(), b.getOp());
			lastElement.setOp(Operator.SEQUENCE);
			lastElement.addBlockAtPosition(choice, 0);
			return lastElement;
		}
	}
}
