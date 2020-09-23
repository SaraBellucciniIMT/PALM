package edu.imt.inputData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.plaf.ListUI;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This is the Trace class that represent a single trace in the log
 * 
 * @author S. Belluccini
 */
public class Trace implements Iterable<Event> {
	/**
	 * The list of events generating the trace
	 */
	private List<Event> trace;
	/**
	 * The happened-before relation of the trace
	 */
	private Map<Event, Set<Event>> hbRel;

	private Map<String, Double> frequencyLoopMap;

	/**
	 * Constructor for class Trace, initialize the list of events with the elements
	 * in the input array and an empty map for the happened-before relation
	 * 
	 * @param e an array, also empty, of events
	 */
	public Trace(Event... e) {
		this.trace = new ArrayList<Event>(e.length);
		for (int i = 0; i < e.length; i++) {
			this.trace.add(e[i]);
		}
		hbRel = new HashMap<Event, Set<Event>>();
		this.frequencyLoopMap = new HashMap<>();
	}

	/**
	 * Remove the piece of trace included between the two indexes
	 * 
	 * @param i first index
	 * @param j last index
	 */
	private void removeIndex(int i, int j) {
		List<Event> pre = this.trace.subList(0, i);
		List<Event> post = new ArrayList<Event>();
		if (j + 1 < trace.size())
			post = this.trace.subList(j + 1, trace.size());
		this.trace = pre;
		trace.addAll(post);
	}

	public void removeSubTrace(int i, int j) {
		List<Event> pre = this.trace.subList(0, i);
		List<Event> post = new ArrayList<Event>();
		if(j<this.trace.size()-1) {
			post = this.trace.subList(j+1, this.trace.size());
		}
		this.trace = pre;
		pre.addAll(post);
		updateHB();
	}
	/**
	 * Remove the given subtrace from the trace, it also remove the associated
	 * elements in the happened-before relation
	 * 
	 * @param t the trace to be removed
	 * @return the left index form which the trace has been removed
	 */
	public int removeSubTrace(Trace t) {
		int j = 0;
		int firstIndex = -1;
		int lastIndex = -1;
		for (int i = 0; i < trace.size(); i++) {
			if (trace.get(i).equals(t.getEvent(j))) {
				if (j == t.length() - 1) {
					lastIndex = i;
					break;
				}
				if (firstIndex == -1)
					firstIndex = i;
				j++;
			} else {
				j = 0;
				firstIndex = -1;
			}
		}
		if (firstIndex != -1) {
			removeIndex(firstIndex, lastIndex);
			updateHB();
		}
		return firstIndex;

	}

	/**
	 * Adds a relation s.t. preevent > e
	 * 
	 * @param preevent, event preceding e
	 * @param e,        event following preeevet
	 */
	public void addPreHBRelation(Event preevent, Event e) {
		if (this.hbRel.containsKey(preevent)) {
			this.hbRel.get(preevent).add(e);
		} else {
			this.hbRel.put(preevent, Sets.newHashSet(e));
		}

	}

	/**
	 * Adds a relation s.t. e > postevet
	 * 
	 * @param postevent, event that follows e
	 * @param e,         event preceding e
	 */
	public void addPostHBRelation(Event postevent, Event e) {
		if (this.hbRel.containsKey(e)) {
			this.hbRel.get(e).add(postevent);
		} else {
			this.hbRel.put(e, Sets.newHashSet(postevent));
		}
	}
	
	public void removePostHBRelation(Event e) {
		if(this.hbRel.containsKey(e))
			this.hbRel.remove(e);
	}

	/**
	 * Set the happened-before relation map with the one given as input
	 * 
	 * @param map
	 */
	public void setHBrel(Map<Event, Set<Event>> map) {
		this.hbRel = map;
	}

	/**
	 * Check if the event exists inside the trace
	 * 
	 * @param e is the event to check
	 * @return true if the event is inside the trace, false otherwise
	 */
	public boolean contains(Event e) {
		return trace.contains(e);
	}

	public boolean containsRelation(Event e, Event e1) {
		if (hbRel.containsKey(e) && hbRel.get(e).contains(e1))
			return true;
		return false;
	}

	/**
	 * The length of the trace is the number of event that are inside it
	 * 
	 * @return the number of event of the trace
	 */
	public int length() {
		return this.trace.size();
	}

	/**
	 * Give this trace
	 * 
	 * @return a list of event, i.e. the trace
	 */
	/*
	 * public List<Event> getTrace() { return trace; }
	 */

	/**
	 * Gives the event correspondent to the input index
	 * 
	 * @param index the position of the event inside the trace
	 * @return the corresponding event
	 */
	public Event getEvent(int index) {
		return this.trace.get(index);
	}

	/**
	 * Gives the happened-before relation of this trace
	 * 
	 * @return the happened-before relation
	 */
	public Map<Event, Set<Event>> getHBRel() {
		return hbRel;
	}

	public int getIndexEvent(Event e) {
		return this.trace.indexOf(e);
	}

	/**
	 * The alphabet of trace is given by the name of all event inside it without
	 * repetition
	 * 
	 * @return set of event i.e. the alphabet of the trace
	 */
	public Set<Event> getAlphabet() {
		return trace.stream().collect(Collectors.toSet());
	}

	/**
	 * Adds an event to the trace appending it to the list
	 * 
	 * @param e the event to be added
	 */
	public void add(Event e) {
		this.trace.add(e);
	}

	/**
	 * Substitute the subtrace the decided event
	 * 
	 * @param t the subtrace to be substituted
	 * @param e the event that will substitute the subtrace
	 * @return true if the substitute has happened, falte otherwise TODO: check over
	 *         this method again
	 */
	/*
	 * public boolean substituteloop(Trace t, Event e) { int j = 0; boolean mod =
	 * false; while (j < this.trace.size()) { for (int i = 0; i < t.length(); i++) {
	 * if (j < this.trace.size() && this.trace.get(j).equals(t.getEvent(i))) { if (i
	 * == t.length() - 1) { int start = j - t.length() + 1; List<Event> first = new
	 * ArrayList<>(trace.subList(0, start)); first.add(start, e); List<Event> second
	 * = new ArrayList<>(trace.subList(start + t.length(), this.trace.size()));
	 * first.addAll(second); j = start; this.trace = first; mod = true; break; }
	 * j++; } else { j++; break; } } } // System.out.println(this.trace.toString() +
	 * " " + t.toString()); // So if the lenght of now is smaller this means if
	 * (mod) return true; else return false; }
	 */

	/**
	 * If there are more instance of a the input events this are reduced to only
	 * one, i.e.T = e,e1,e2,M,M,M,M -> T = e,e1,e2,M
	 * 
	 * @param e event of which we are reducing the number of instances to one
	 */
	/*
	 * public void reductionlooprule(Event e) { List<Event> cas = new
	 * ArrayList<Event>(); Boolean loopname = false; for (int i = 0; i <
	 * this.trace.size(); i++) { if (!this.trace.get(i).equals(e)) {
	 * cas.add(this.trace.get(i)); loopname = false; } else if
	 * (this.trace.get(i).equals(e)) { if (loopname == false) { cas.add(e); loopname
	 * = true; } } } this.trace = cas;
	 * 
	 * }
	 */

	/**
	 * Add an event at the index i of the trace
	 * 
	 * @param i position in the trace
	 * @param e the event to be added
	 */
	public void add(int i, Event e) {
		this.trace.add(i, e);
	}

	/**
	 * Check if the trace is empty, i.e. without events
	 * 
	 * @return true if is empty, false otherwise
	 */
	public boolean isEmpty() {
		return trace.isEmpty();
	}

	/*
	 * True if contain the same events and also the order is respected
	 */
	/*
	 * public boolean isSubTraceOfMe(Trace c) { List<Event> subTrace = c.getTrace();
	 * for (int i = 0; i < trace.size(); i++) { boolean b = false; if
	 * (trace.get(i).equals(subTrace.get(0))) { for (int k = i; k < (i +
	 * c.length()); k++) { if (trace.size() > k &&
	 * trace.get(k).equals(subTrace.get(k - i))) b = true; else { b = false; break;
	 * } } } if (b) return true; } return false; }
	 */

	/*
	 * public int indexOfSubTrace(Trace c) { List<Event> subTrace = c.getTrace();
	 * int index = 0; for (int i = 0; i < trace.size(); i++) { boolean b = false; if
	 * (trace.get(i).equals(subTrace.get(0))) { index = i; for (int k = i; k < (i +
	 * c.length()); k++) { if (trace.size() > k &&
	 * trace.get(k).equals(subTrace.get(k - i))) b = true; else { b = false; break;
	 * } } } if (b) return index; } return -1; }
	 */
	/**
	 * Gives the first event in the trace
	 * 
	 * @return first event
	 */
	public Event first() {
		return this.trace.get(0);

	}

	public Event last() {
		return this.trace.get(this.trace.size() - 1);
	}

	/**
	 * Gives the set of events s.t. e_i > e in hb-relation
	 * 
	 * @param e the event of which we want to know the preceding events
	 * @return a set of events that precede the event e
	 */
	public Set<Event> getPreEventHB(Event e) {
		Set<Event> incomingevent = new HashSet<Event>();
		if (!hbRel.isEmpty()) {
			for (Entry<Event, Set<Event>> entry : hbRel.entrySet()) {
				if (entry.getValue().contains(e))
					incomingevent.add(entry.getKey());
			}
		}
		return incomingevent;
	}

	/**
	 * Gives the set of events s.t. e > e_i in hb-relation
	 * 
	 * @param e the event of which we want to know the following events
	 * @return a set of events that follow the event e
	 */
	public Set<Event> getPostEventHB(Event e) {
		return this.hbRel.get(e);
	}

	/**
	 * Gives the happened-before relation of the given trace base on the hb-relation
	 * already existing in this trace cutting the connection between the last
	 * element and the first one
	 * 
	 * @param t the trace to which we want to know the hb-relation
	 * @return the happened-before relation
	 */
	public Map<Event, Set<Event>> getSubHBrel(Trace t) {
		//System.out.println(t);
		//System.out.println(hbRel.toString());
		Map<Event, Set<Event>> subhbrel = new HashMap<>();
		for (Event e : t) {
			Collection<Event> postE = hbRel.get(e);
			if(postE!= null) {
			for (Event pe : postE) {
				if (t.contains(pe) && !e.equals(t.last()) && !pe.equals(t.first())) {
					if (subhbrel.containsKey(e)) {
						subhbrel.get(e).add(pe);
					} else {
						subhbrel.put(e, Sets.newHashSet(pe));
					}

				}
			}}
		}
		return subhbrel;
	}

	/**
	 * Gives the subtrace included between two events, if exists, excluding the given loopname
	 * 
	 * @param first, event to be in the trace
	 * @param last,  event to be in the trace
	 * @return the trace if exists otherwise an empty trace
	 */
	public Triple<Trace,Integer,Integer> getSubTrace(Event first, Event last, Event loopName) {
		Trace subTrace = new Trace();
		int findFirst = -1;
		int findLast = -1;
		for (int i = 0; i < trace.size(); i++) {
			if (trace.get(i).equals(first)) {
				findFirst = i;
				subTrace = new Trace();
			}if (findFirst!= -1) {
				if(trace.get(i).equals(loopName)) {
					findFirst = -1;
					subTrace = new Trace();
				}
				subTrace.add(trace.get(i));
			}
			if (trace.get(i).equals(last) && (findFirst!=-1)) {
				findLast = i;
				break;
			}
		}
		if (findFirst==-1 || findLast == -1)
			subTrace = new Trace();
		return Triple.of(subTrace, findFirst, findLast);
	}

	/**
	 * Gives a trace included between two indexes (included)
	 * 
	 * @param i, index of the first element
	 * @param j, index of the last element
	 * @return the subtrace between this two elements
	 */
	public Trace getSubTraceFrom(int i, int j) {
		return new Trace(this.trace.subList(i, j).toArray(new Event[j - i]));
	}

	/**
	 * Counts the number of times that a subtrace appears inside the trace
	 * 
	 * @param c the subtrace to be counted
	 * @return the number of times that the subtrace is repeated inside the trace
	 */
	public int getMoltiplicity(Trace c) {
		int count = 0;
		int j = 0;
		for (int i = 0; i < trace.size(); i++) {
			if (trace.get(i).equals(c.getEvent(j)) && j == c.length() - 1) {
				j = 0;
				count++;
			} else if (trace.get(i).equals(c.getEvent(j)) && j < c.length() - 1)
				j++;
			else if (!trace.get(i).equals(c.getEvent(j)) && j != 0)
				j = 0;
		}
		return count;
	}

	@Override
	public String toString() {
		return "Trace [trace=" + trace + "]";
	}

	/**
	 * Compute all the possible paths made by the trace following its
	 * happened-before relation
	 * 
	 * @return a list of events corresponding to the paths
	 */
	public List<List<Event>> computePaths() {
		List<List<Event>> alleventpath = new ArrayList<>();
		if (hbRel.isEmpty()) {
			trace.forEach(e -> alleventpath.add(Lists.newArrayList(e)));
			return alleventpath;
		}
		Set<Event> startingprocesses = startingProcess();
		for (Event first : startingprocesses) {
			List<List<Event>> eacheventpath = new ArrayList<>();
			List<Event> mypath = new ArrayList<Event>();
			mypath.add(first);
			eacheventpath.add(mypath);
			alleventpath.addAll(singlePaths(first, eacheventpath));
		}
		return alleventpath;
	}

	/**
	 * Recursive function that computes the path of a given event
	 * 
	 * @param e          the event from which we want to compute the path
	 * @param beforelist all the sequnce of path already executed until the event e
	 * @return the lists a of path starting with an event e TODO: check this
	 *         function, empty and skip event are really usefull?
	 */
	private List<List<Event>> singlePaths(Event e, List<List<Event>> beforelist) {
		Collection<Event> values = this.hbRel.get(e);
		if (values == null)
			return beforelist;
		else if (values.size() == 1) {
			for (Event v : values) {
				if (skipEvent(beforelist, v))
					continue;
				for (List<Event> l : beforelist)
					l.addAll(values);
				singlePaths(v, beforelist);
			}
		} else {
			List<List<Event>> resultList = new ArrayList<>();
			for (Event v : values) {
				if (skipEvent(beforelist, v))
					continue;
				List<List<Event>> copyBL = new ArrayList<>();
				for (List<Event> blist : beforelist) {
					List<Event> tmp = new ArrayList<Event>();
					for (int i = 0; i < blist.size(); i++)
						tmp.add(blist.get(i));
					tmp.add(v);
					copyBL.add(tmp);
				}
				List<List<Event>> singlepaht = singlePaths(v, copyBL);
				resultList.addAll(singlepaht);
			}
			beforelist.clear();
			beforelist.addAll(resultList);
		}
		return beforelist;
	}

	/**
	 * Skip an event if it exists already in that list of list
	 * 
	 * @param list list of list of event
	 * @param v    event that we want to skip
	 * @return true if the event has to be skipped, false otherwise
	 */
	private boolean skipEvent(List<List<Event>> list, Event v) {
		for (List<Event> l : list) {
			if (l.contains(v))
				return true;
		}
		return false;
	}

	/**
	 * Compute the initial event of each path in the trace
	 * 
	 * @return a set of event, i.e. the initial one of each path
	 */
	private Set<Event> startingProcess() {

		Set<Event> startingP = new HashSet<Event>();

		for (Event key : this.hbRel.keySet()) {
			boolean found = false;
			for (Set<Event> set : this.hbRel.values()) {
				if (set.contains(key)) {
					found = true;
					break;
				}
			}
			if (!found)
				startingP.add(key);
		}
		return startingP;
	}

	/**
	 * Compute the happened before relation in a trace : this means each couple of
	 * activity that as complete-start relation will be in the set
	 * 
	 * @param xtrace
	 * @throws Exception
	 */
	/*
	 * public void computeHappenedBeforeRel(XTrace xtrace) throws Exception { for
	 * (int i = 0; i < xtrace.size(); i++) { XEvent xe = xtrace.get(i);
	 * XAttributeLiteralImpl att = (XAttributeLiteralImpl)
	 * xe.getAttributes().get(InputParsingUtils.lct); if
	 * (!att.getValue().equals(InputParsingUtils.COMPLETE)) continue; boolean
	 * atLeasOne = false; int j = i + 1; while (j < xtrace.size()) { if
	 * (((XAttributeLiteralImpl)
	 * xtrace.get(j).getAttributes().get(InputParsingUtils.lct)).getValue()
	 * .equals(InputParsingUtils.START)) { String myName =
	 * xe.getAttributes().get(InputParsingUtils.cn).toString(); String otherName =
	 * xtrace.get(j).getAttributes().get(InputParsingUtils.cn).toString();
	 * hbRel.put(new Event(myName), new Event(otherName)); atLeasOne = true; } if
	 * (((XAttributeLiteralImpl)
	 * xtrace.get(j).getAttributes().get(InputParsingUtils.lct)).getValue()
	 * .equals(InputParsingUtils.COMPLETE) && atLeasOne) break; j++;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * TRUE if contain the same set of key-value, order is not important
	 */
	/**
	 * Check if the input happened-before relation is equal to this hb-relation
	 * 
	 * @param hbother happened-before relation that we want to compare
	 * @return true if they are equal, false otherwise
	 */
	public boolean equalHB(Map<Event, Set<Event>> hbother) {
		if (this.hbRel.equals(hbother))
			return true;
		return false;

	}

	/**
	 * Compute and set the frequency of the given loop set name, if was already in
	 * the map of the loops the current frequency is added to the old one
	 * 
	 * @param l   loopset name
	 * @param occ number of time the elements of the loop l appear inside this trace
	 */
	public void addLoopWithFrequency(String l, double lLength) {
		double frequency = ((lLength) / (trace.size()+(lLength)-1)) * 100;
		if (frequencyLoopMap.containsKey(l))
			frequencyLoopMap.put(l, frequencyLoopMap.get(l) + frequency);
		else
			this.frequencyLoopMap.put(l, frequency);
	}

	private void updateHB() {
		Map<Event, Set<Event>> map = new HashMap<>();
		for (Entry<Event, Set<Event>> entry : hbRel.entrySet()) {
			for (Event e : entry.getValue()) {
				if (trace.contains(entry.getKey()) && trace.contains(e)) {
					if (map.containsKey(entry.getKey())) {
						map.get(entry.getKey()).add(e);
					} else {
						map.put(entry.getKey(), Sets.newHashSet(e));
					}
				}
			}
		}
		hbRel = map;
	}

	@Override
	public Iterator<Event> iterator() {
		return this.trace.iterator();
	}

	protected double getFrequencyLoop(String s) {
		if (this.frequencyLoopMap.containsKey(s))
			return this.frequencyLoopMap.get(s);
		else
			return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((frequencyLoopMap == null) ? 0 : frequencyLoopMap.hashCode());
		result = prime * result + ((hbRel == null) ? 0 : hbRel.hashCode());
		result = prime * result + ((trace == null) ? 0 : trace.hashCode());
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
		Trace other = (Trace) obj;
		if (frequencyLoopMap == null) {
			if (other.frequencyLoopMap != null)
				return false;
		} else if (!frequencyLoopMap.equals(other.frequencyLoopMap))
			return false;
		if (hbRel == null) {
			if (other.hbRel != null)
				return false;
		} else if (!hbRel.equals(other.hbRel))
			return false;
		if (trace == null) {
			if (other.trace != null)
				return false;
		} else if (!trace.equals(other.trace))
			return false;
		return true;
	}

}
