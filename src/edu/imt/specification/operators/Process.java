package edu.imt.specification.operators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

/*
 * A Process can be:
 * - an activity
 * - a sequence of processes with an operator
 */
public class Process {

	private Activity a;
	private Operator op = null;
	private Process[] arr;

	// An empty process
	public Process() {
		this.arr = new Process[0];
	}

	public Process(Activity a) {
		this.a = a;
		this.arr = null;
	}

	/*
	 * public Process(Event e, Operator op) { this.arr = new Process[e.size()]; for
	 * (int i = 0; i < e.size(); i++) { this.arr[i] = new Process(new
	 * Activity(String.valueOf(e.getName().charAt(i)))); } this.op = op; this.a =
	 * null; }
	 */

	public Process(Process[] pr, Operator op) {
		this.a = null;
		this.arr = pr;
		this.op = op;
	}

	public int size() {
		if (a != null)
			return 1;
		if (arr != null)
			return this.arr.length;
		return 0;
	}

	public Operator getOp() {
		return this.op;
	}

	public Activity getActivity() {
		return this.a;
	}

	public boolean isempty() {
		if(this.a == null && ArrayUtils.isEmpty(this.arr))
			return true;
		else
			return false;
	}
	public Process[] getProcess() {
		return arr;
	}

	public int getTotalDepth() {
		int myLen = 0;
		if (this.a != null)
			return 1;
		else {
			for (int i = 0; i < this.arr.length; i++) {
				myLen += this.arr[i].getTotalDepth();
			}
		}
		return myLen;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + Arrays.hashCode(arr);
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		return result;
	}

	@Override
	public String toString() {
		if (this.a != null)
			return "\"" + a.toString() + "\"";
		else {
			String s = op + "[";
			for (int i = 0; i < arr.length; i++) {
				if(arr[i] != null)
					s = s.concat(arr[i].toString());
			}
			s = s.concat("]");
			return s;
		}

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Process other = (Process) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (op != other.op)
			return false;
		if (op != null && op.equals(Operator.PARALLEL)) {
			// Equals without order
			return equalProcessNoOrder(arr, other.arr);
		}
		if (!Arrays.equals(arr, other.arr))
			return false;

		return true;
	}

	public boolean firstRowOperatorContain(Operator op) {
		for (int i = 0; i < this.arr.length; i++) {
			if (this.arr[i].getOp() != null && this.arr[i].getOp().equals(op))
				return true;
		}
		return false;
	}

	//Append the process at the end of the array
	public void insertProcess(Process p) {
		arr = ArrayUtils.add(arr, p);
	}
	public void insertProcessAtPosition(Process p, int i) {
		if (arr != null) {
			Process[] tmpB = ArrayUtils.subarray(arr, 0, i);
			Process[] tmpA = ArrayUtils.subarray(arr, i, arr.length);
			arr = tmpB;
			arr = ArrayUtils.add(arr, p);
			arr = ArrayUtils.addAll(arr, tmpA);
		} else {
			this.arr = new Process[i+1];
			this.arr[i] = p;
		}
	}

	public void modifyProcessActivity(Activity a) {
		this.arr = null;
		this.a = a;
		this.op = null;
	}

	public void cleanActivity() {
		this.a = null;
	}

	/*
	 * return a new process without that element
	 */
	public void removeElementAtPosition(int index) {
		this.arr = ArrayUtils.remove(arr, index);

	}

	public void modifyProcessArray(Process[] p, Operator op) {
		this.arr = p;
		this.a = null;
		this.op = op;
	}

	public int getPositionProcess(Process p) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(p))
				return i;
		}
		return -1;
	}

	public void modifyProcessOp(Operator sequence) {
		this.op = sequence;
	}

	private boolean equalProcessNoOrder(Process[] p1, Process[] p2) {
		if (p1.length != p2.length)
			return false;
		List<Process> l1 = Arrays.asList(p1);
		List<Process> l2 = Arrays.asList(p2);

		Iterator<Process> it1 = l1.iterator();
		while (it1.hasNext()) {
			Process p = it1.next();
			if (!l2.contains(p))
				return false;
		}

		Iterator<Process> it2 = l2.iterator();
		while (it2.hasNext()) {
			Process p = it2.next();
			if (!l1.contains(p))
				return false;
		}
		return true;
	}
}
