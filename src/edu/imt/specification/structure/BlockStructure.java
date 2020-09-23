package edu.imt.specification.structure;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import edu.imt.inputData.Event;

/**
 * This is the class Block Structure, a block structure can be: -an activity, an
 * array of processes with an operator
 * 
 * @author Sara
 */
public class BlockStructure {

	/**
	 * a is the activity, op the operator and arr the array of processes, frequency
	 * measure the frequency of appearance of this block (just for loop block)
	 */
	private Event e = null;
	private Operator op = null;
	private BlockStructure[] arr = null;
	private double frequency = 0;
	private int repetition;

	/**
	 * Constructor of process to initialize an empty array of processes
	 */
	public BlockStructure() {
		this.arr = new BlockStructure[0];
	}

	/**
	 * Another constructor of process, initialize the activity with the given one
	 * 
	 * @param a activity of the process
	 */
	public BlockStructure(Event e) {
		this.e = e;
	}

	/**
	 * Another constructor of block, initiliaze an empty block structure with the
	 * given operator
	 * 
	 * @param op
	 */
	public BlockStructure(Operator op) {
		this.op = op;
	}

	/**
	 * Another constructor of process, initialize the array of process with the
	 * correspondent operator
	 * 
	 * @param pr the array of processes
	 * @param op the corresponding operator
	 */
	public BlockStructure(BlockStructure[] pr, Operator op) {
		this.arr = pr;
		this.op = op;
	}

	/**
	 * Gives the number of processes inside this process, if is an activity the size
	 * of the process is equal to 1
	 * 
	 * @return a number, i.e. the size of the process
	 */
	public int size() {
		if (e != null)
			return 1;
		if (arr != null)
			return this.arr.length;
		return 0;
	}

	/**
	 * Set the frequency value for this block structure, used only for Loops block
	 * 
	 * @param f the frequency value
	 */
	public void setFrequency(double f) {
		this.frequency = f;
	}

	/**
	 * Returns the frequency value for this block structure
	 * 
	 * @return the frequency value for this block structure
	 */
	public double getFrequency() {
		return this.frequency;
	}

	public void setRepetition(int r) {
		this.repetition = r;
	}

	public int getRepetition() {
		return this.repetition;
	}

	/**
	 * Returns the operator of this process
	 * 
	 * @return the operator of this process
	 */
	public Operator getOp() {
		return this.op;
	}
	
	public void setOp(Operator op) {
		this.op = op;
	}

	/**
	 * Returns the operator of the first depth child of this block structure, if its
	 * equal for everyone otherwise null
	 * 
	 * @return the operator of the first row childs
	 */
	public Operator getFirstRowOp() {
		Operator op = null;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].hasEvent() || (op != null && op != arr[i].getOp())) {
				op = null;
				break;
			} else if (op == null)
				op = arr[i].getOp();
		}
		return op;
	}
	
	public boolean blockWithSamrOperator() {
		if(e!= null)
			return false;
		for(int i=0; i<arr.length; i++)
			if(!arr[i].hasEvent() && arr[i].getOp()!= null && arr[i].getOp().equals(this.op))
				return true;
		return false;
	}

	/**
	 * Returns the activity of this process
	 * 
	 * @return the activity of this process
	 */
	public Event getEvent() {
		return this.e;
	}

	public boolean hasEvent() {
		if (e != null)
			return true;
		else
			return false;
	}

	/**
	 * Returns true if the array of block is NOT empty, false otherwise
	 * 
	 * @return true if the array of block is NOT empty, false otherwise
	 */
	public boolean hasBlock() {
		if (ArrayUtils.isEmpty(this.arr))
			return false;
		return true;
	}

	public boolean hasOp() {
		if (op != null)
			return true;
		return false;
	}

	/**
	 * Returns true if the list has no activity nor elements in the array
	 * 
	 * @return true if the list has no activity nor elements in the array
	 */
	public boolean isEmpty() {
		if (this.e == null && ArrayUtils.isEmpty(this.arr))
			return true;
		else
			return false;
	}

	/**
	 * Returns the array of blocks
	 * 
	 * @return the array of blocks
	 */
	public BlockStructure[] getBlock() {
		return arr;
	}

	public BlockStructure getBlock(int i) {
		return arr[i];
	}

	public BlockStructure getBlock(int i, int j) {
		BlockStructure b = new BlockStructure();
		if (j < arr.length) {
			int k = 0;
			while (i <= j) {
				b.addBlockAtPosition(arr[i], k);
				i++;
				k++;
			}
		}
		return b;

	}

	@Override
	public String toString() {
		if (this.e != null)
			return "\"" + e.toString() + "\"";
		else {
			String s = op + "[";
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] != null)
					s = s.concat(arr[i].toString());
			}
			s = s.concat("]");
			return s;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arr);
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockStructure other = (BlockStructure) obj;
		if (!Arrays.equals(arr, other.arr))
			return false;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (op != null && op.equals(Operator.PARALLEL)) {
			// Equals without order
			return equalBlocksNoOrder(arr, other.arr);
		} else if (!e.equals(other.e))
			return false;

		if (op != other.op)
			return false;
		return true;
	}

	/**
	 * Adds an element in the block structure array, making it longer if needed
	 * 
	 * @param b the block structure to add
	 * @param i index of the position
	 */
	public void addBlockAtPosition(BlockStructure b, int i) {
		if (arr != null) {
			BlockStructure[] tmpB = ArrayUtils.subarray(arr, 0, i);
			BlockStructure[] tmpA = ArrayUtils.subarray(arr, i, arr.length);
			arr = tmpB;
			arr = ArrayUtils.add(arr, b);
			arr = ArrayUtils.addAll(arr, tmpA);
		} else {
			this.arr = new BlockStructure[i + 1];
			this.arr[i] = b;
		}
	}

	/**
	 * Returns a new block structure equals to this one except for the element at
	 * index i that has been removed
	 * 
	 * @param index of the element to be removed
	 * @return a block structure
	 */
	public BlockStructure removeBlockAtIndex(int index) {
		if (index < this.size())
			return new BlockStructure(ArrayUtils.remove(arr, index), op);
		else
			return new BlockStructure();
	}

	/**
	 * Returns a new block structure equals to this one except for the element
	 * between index i (inclusive) and j (exclusive)
	 * 
	 * @param i first index
	 * @param j second index
	 * @return a new block structure equals to this one except for the element
	 *         between index i (inclusive) and j (exclusive)
	 */
	public BlockStructure removeBlockAtIndex(int i, int j) {
		BlockStructure[] first = new BlockStructure[0];
		BlockStructure[] second = new BlockStructure[0];
		if (i < this.size())
			first = ArrayUtils.subarray(arr, 0, i);
		if (j < this.size())
			second = ArrayUtils.subarray(arr, j, arr.length);
		return new BlockStructure(ArrayUtils.addAll(first, second), op);
	}

	public int getPositionBlock(BlockStructure p) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(p))
				return i;
		}
		return -1;
	}

	private boolean equalBlocksNoOrder(BlockStructure[] p1, BlockStructure[] p2) {
		if (p1.length != p2.length)
			return false;
		List<BlockStructure> l1 = Arrays.asList(p1);
		List<BlockStructure> l2 = Arrays.asList(p2);

		Iterator<BlockStructure> it1 = l1.iterator();
		while (it1.hasNext()) {
			BlockStructure p = it1.next();
			if (!l2.contains(p))
				return false;
		}

		Iterator<BlockStructure> it2 = l2.iterator();
		while (it2.hasNext()) {
			BlockStructure p = it2.next();
			if (!l1.contains(p))
				return false;
		}
		return true;
	}

}
