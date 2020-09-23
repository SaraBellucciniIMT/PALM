package edu.imt.algorithm;

import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import edu.imt.inputData.Trace;
import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;

/**
 * The class Loop represents loops as given in (definition 1) of the paper
 * 
 * @author S. Belluccini
 */
public class LoopSet {

	/**
	 * First and last element of this loop set
	 */
	private Event first;
	private Event last;
	/**
	 * Index used to name every loop, name is assigned with the current index value
	 */
	private static int INDEX = 0;
	private Event name;
	/**
	 * All the loops that start and end with the same first and start
	 */
	private Trace[] loop;

	private int repetition = 0;

	public LoopSet(Event first, Event last) {
		this.loop = new Trace[0];
		this.name = new Event("", String.valueOf(LoopSet.INDEX++));
		this.first = first;
		this.last = last;
	}

	public Event first() {
		return this.first;
	}

	public Event last() {
		return this.last;
	}

	/**
	 * The length is given by the sum of the length of each loop inside the set
	 * 
	 * @return a number that is the length of the loop set
	 */
	public int getLenght() {
		int length = 0;
		for (Trace t : loop)
			length += t.length();
		return length;
	}

	public int getRepetition() {
		return this.repetition;
	}

	public boolean startEndevents(Event e, Event en) {
		if (this.first.equals(e) && this.last.equals(en))
			return true;
		return false;
	}

	public Event getName() {
		return this.name;
	}

	/**
	 * Returns the traces inside this loopset as a unique eventlog
	 * 
	 * @return the traces inside this loopset as a unique eventlog
	 */
	public EventLog getLoop() {
		EventLog e = new EventLog();
		for (Trace t : loop)
			e.add(t);
		return e;
	}

	/**
	 * Adds a new loop trace in the loopset if is not already in this set, while
	 * adding/updating its repetition value of 1
	 * 
	 * @param t the trace that we want to store
	 */
	public void addLoop(Trace t) {
		int index = containsloop(t);
		if (index == -1)
			this.loop = ArrayUtils.add(this.loop, t);
	}

	public void setRepetition(int i) {
		if(this.repetition<i)
			this.repetition = i;
	}
	/**
	 * Gives the index at which this trace is stored
	 * 
	 * @param t the trace
	 * @return an natural number, i.e. the index, otherwise -1
	 */
	public int containsloop(Trace t) {
		for (int i = 0; i < loop.length; i++) {
			if (loop[i].equals(t))
				return i;
		}
		return -1;
	}

	// Remove the corrent loops from the case c and add its name
	/*
	 * public Trace removeAndAdd(Trace c) { boolean anonymus = true; for (int i = 0;
	 * i < loop.length; i++) { int index = -1; while ((index =
	 * c.indexOfSubTrace(loop[i])) != -1) { c.removeIndex(index, index +
	 * loop[i].length() - 1); c.removeHB(c.getSubHBrel(loop[i])); if (anonymus) {
	 * c.add(index, name); anonymus = false; } } } return c; }
	 */

	@Override
	public String toString() {
		return "LoopBlock [name=" + name + ", loop=" + Arrays.toString(loop) + "]";
	}

}
