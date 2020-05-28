package edu.imt.inputData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.imt.algorithm.SchimmAlgorithm;
import edu.imt.utils.InputParsingUtils;

public class Case {

	private List<Event> trace;
	private Multimap<Event, Event> hbRel;

	public Case() {
		this.trace = new ArrayList<Event>();
		hbRel = HashMultimap.create();
	};

	public Case(List<Event> trace) {
		this.trace = new ArrayList<Event>(trace.size());
		trace.forEach(t -> this.trace.add(t));
		hbRel = HashMultimap.create();
	}
	
	public Case(Event... e) {
		this.trace = new ArrayList<Event>(e.length);
		for(int i =0; i<e.length; i++) {
			this.trace.add(e[i]);
		}
	}
	// Remove the events from index i until index j inclusive
	public void removeIndex(int i, int j) {
		List<Event> pre = this.trace.subList(0, i);
		List<Event> post = new ArrayList<Event>();
		if (j + 1 < trace.size())
			post = this.trace.subList(j + 1, trace.size());
		this.trace = pre;
		trace.addAll(post);
	}

	public void addHBRelation(Set<Event> preevent, Set<Event> postevent, Event e) {
		for (Event pree : preevent)
			this.hbRel.put(pree, e);
		for (Event poste : postevent)
			this.hbRel.put(e, poste);
	}
	
	public void addHBRelation(Entry<Event,Event> entry) {
		this.hbRel.put(entry.getKey(), entry.getValue());
	}
	public void addHBRelation(Multimap<Event,Event> map) {
		this.hbRel.putAll(map);
	}

	public void setHBrel(Multimap<Event, Event> map) {
		this.hbRel = map;
	}

	public boolean contains(Event e) {
		return trace.contains(e);
	}

	public int length() {
		return this.trace.size();
	}

	public List<Event> getTrace() {
		return trace;
	}

	public Event getEvent(int index) {
		return this.trace.get(index);
	}

	
	public Multimap<Event, Event> getHBRel() {
		return hbRel;
	}

	/*
	 * Alphabet of a case is given by its set of events without repetition
	 */
	public Set<Event> getAlphabet() {
		return trace.stream().collect(Collectors.toSet());
	}

	/*
	 * Append the event at the end of the trace
	 */
	public void add(Event e) {
		this.trace.add(e);
	}
	
	public boolean substituteloop(Case t,Event e) {
		int length = this.trace.size();
		int j=0;
		boolean mod=false;
		while(j<this.trace.size()){
			for(int i=0; i<t.length(); i++) {
				if( j < this.trace.size() && this.trace.get(j).equals(t.getTrace().get(i))) {
					if(i == t.length()-1) {
						int start = j-t.length()+1;
						List<Event> first = new ArrayList<>(trace.subList(0, start));
						first.add(start,e);
						List<Event> second = new ArrayList<>(trace.subList(start+t.length(), this.trace.size()));
						first.addAll(second);
						j = start;
						this.trace = first;
						mod = true;
						break;
					}
					j++;
				}else {					
					j++;
					break;
				}
			}
		}
		//System.out.println(this.trace.toString() + "  " + t.toString());
		//So if the lenght of now is smaller this means 
		if(mod)
			return true;
		else
			return false;
	}

	// If T = e,e1,e2,M,M,M,M -> T = e,e1,e2,M
	public void reductionlooprule(Event e) {
		List<Event> cas = new ArrayList<Event>();
		Boolean loopname = false;
		for(int i =0; i<this.trace.size(); i++) {
			if(!this.trace.get(i).equals(e)) {
				cas.add(this.trace.get(i));
				loopname = false;
			}else if(this.trace.get(i).equals(e)) {
				if(loopname == false) {
					cas.add(e);
					loopname =true;
				}
			}
		}
		this.trace = cas;
		
	}
	
	public void add(int i, Event e) {
		this.trace.add(i, e);
	}

	public void add(int i, List<Event> e) {
		this.trace.addAll(i, e);
	}
	/*
	 * true if has no traces
	 */
	public boolean isEmpty() {
		return trace.isEmpty();
	}


	/*
	 * True if contain the same events and also the order is respected
	 */
	public boolean isSubTraceOfMe(Case c) {
		List<Event> subTrace = c.getTrace();
		for (int i = 0; i < trace.size(); i++) {
			boolean b = false;
			if (trace.get(i).equals(subTrace.get(0))) {
				for (int k = i; k < (i + c.length()); k++) {
					if (trace.size() > k && trace.get(k).equals(subTrace.get(k - i)))
						b = true;
					else {
						b = false;
						break;
					}
				}
			}
			if (b)
				return true;
		}
		return false;
	}

	public int indexOfSubTrace(Case c) {
		List<Event> subTrace = c.getTrace();
		int index = 0;
		for (int i = 0; i < trace.size(); i++) {
			boolean b = false;
			if (trace.get(i).equals(subTrace.get(0))) {
				index = i;
				for (int k = i; k < (i + c.length()); k++) {
					if (trace.size() > k && trace.get(k).equals(subTrace.get(k - i)))
						b = true;
					else {
						b = false;
						break;
					}
				}
			}
			if (b)
				return index;
		}
		return -1;
	}

	// Return a set of event that are happening
	public Set<Event> getPreEvent(Event e) {
		Set<Event> incomingevent = new HashSet<Event>();
		if (!hbRel.isEmpty()) {
			for (Entry<Event, Event> entry : hbRel.entries()) {
				if (entry.getValue().equals(e))
					incomingevent.add(entry.getKey());
			}
		}
		return incomingevent;
	}

	public Set<Event> getPostEvent(Event e) {
		Set<Event> incomingevent = new HashSet<Event>();
		for (Entry<Event, Event> entry : hbRel.entries()) {
			if (entry.getKey().equals(e))
				incomingevent.add(entry.getValue());
		}
		return incomingevent;
	}

	// Return the happened-before relation among the elements in the case c based on
	// the hbrelation of the present trace without the last connection
	public Multimap<Event, Event> getSubHBrel(Case c) {
		Multimap<Event, Event> subhbrel = HashMultimap.create();
		for (int i = 0; i < c.length(); i++) {
			for (int j = 1; j < c.length(); j++) {
				if (hbRel.containsEntry(c.getEvent(i), c.getEvent(j)) && (j != 0) && i != (c.length() - 1))
					subhbrel.put(c.getEvent(i), c.getEvent(j));
			}
		}
		if(subhbrel.containsEntry(c.getTrace().get(c.getTrace().size()-1), c.getTrace().get(0)))
			System.out.println("what");
		return subhbrel;
	}

	public void removeHB(Multimap<Event, Event> map) {
		hbRel.removeAll(map);
	}

	// Return the set of subtrace from an event to another
	public Set<Case> getSubTrace(Event e1, Event e2) {
		Set<Case> subtraceset = new HashSet<Case>();
		Case subcas = new Case();
		// a->a the subtrace is a
		if (e1.equals(e2)) {
			subcas.add(e1);
			subtraceset.add(subcas);
		} else {
			for (int i = 0; i < trace.size(); i++) {
				Event e = trace.get(i);
				if (e.equals(e1)) {
					subcas = new Case();
					subcas.add(e1);
				} else if (e.equals(e2) && !subcas.isEmpty()) {
					subcas.add(e2);
					subtraceset.add(subcas);
					subcas = new Case();
				} else if(!e.equals(e1) && !e.equals(e2) && !subcas.isEmpty()) {
					subcas.add(e);
				}
			}
		}
		return subtraceset;
	}

	// Count the numbe of times that c is repeated inside the trace
	public int getMoltiplicity(Case c) {
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
		return "Case [trace=" + trace + "]";
	}


	public List<List<Event>> computePaths() {
		List<List<Event>> alleventpath = new ArrayList<>();
		if (hbRel == null || hbRel.isEmpty()) {
			alleventpath.add(trace);
			return alleventpath;
		}
		List<Event> startingprocesses = startingProcess(hbRel);

		for (Event first : startingprocesses) {
			List<List<Event>> eacheventpath = new ArrayList<>();
			List<Event> mypath = new ArrayList<Event>();
			mypath.add(first);
			eacheventpath.add(mypath);
			alleventpath.addAll(singlePaths(first, eacheventpath));
		}
		return alleventpath;
	}

	private List<List<Event>> singlePaths(Event e, List<List<Event>> beforelist) {
		Collection<Event> values = hbRel.asMap().get(e);
		if (values == null )
			return beforelist;
		else if (values.size() == 1) {
			for (Event v : values) {
				if (skipEvent(beforelist, v)|| v.equals(SchimmAlgorithm.empty))
					continue;
				for (List<Event> l : beforelist)
					l.addAll(values);
				singlePaths(v, beforelist);
			}
		} else {
			// When a = [b,c] then
			List<List<Event>> resultList = new ArrayList<>();
			for (Event v : values) {
				if (skipEvent(beforelist, v) || v.equals(SchimmAlgorithm.empty))
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

	private boolean skipEvent(List<List<Event>> list, Event v) {
		for (List<Event> l : list) {
			if (l.contains(v))
				return true;
		}
		return false;
	}

	private List<Event> startingProcess(Multimap<Event, Event> cluster) {
		List<Event> startingP = new ArrayList<Event>();
		for (Event key : cluster.keySet()) {
			/*if(cluster.containsEntry(key, SchimmAlgorithm.empty)) {
				startingP.add(key);
				continue;
			}*/
			boolean f = false;
			for (Event e : cluster.values()) {
				if (e.equals(key)) {
					f = true;
					break;
				}
			}
			if (f == false && !startingP.contains(key))
				startingP.add(key);
		}
		return startingP;
	}

	/*
	 * Compute the happened before relation in a trace/case : this means each couple
	 * of activity that as complete-start relation will be in the set
	 */
	public void computeHappenedBeforeRel(XTrace xtrace) throws Exception {
		for (int i = 0; i < xtrace.size(); i++) {
			XEvent xe = xtrace.get(i);
			XAttributeLiteralImpl att = (XAttributeLiteralImpl) xe.getAttributes().get(InputParsingUtils.lct);
			if (!att.getValue().equals("complete"))
				continue;
			boolean atLeasOne = false;
			int j = i + 1;
			while (j < xtrace.size()) {
				if (((XAttributeLiteralImpl) xtrace.get(j).getAttributes().get(InputParsingUtils.lct)).getValue()
						.equals("start")) {
					String myName = xe.getAttributes().get(InputParsingUtils.cn).toString();
					String otherName = xtrace.get(j).getAttributes().get(InputParsingUtils.cn).toString();
					hbRel.put(new Event(myName), new Event(otherName));
					atLeasOne = true;
				}
				if (((XAttributeLiteralImpl) xtrace.get(j).getAttributes().get(InputParsingUtils.lct)).getValue()
						.equals("complete") && atLeasOne)
					break;
				j++;

			}

		}

	}

	/*
	 * TRUE if contain the same set of key-value, order is not important
	 */
	public boolean checkEqualRelation(Multimap<Event, Event> hbother) {
		if (hbRel.size() != hbother.size())
			return false;
		for (Entry<Event, Event> entry : hbRel.entries()) {
			boolean found = false;
			for (Entry<Event, Event> entrytmp : hbother.entries()) {
				if (entrytmp.getKey().equals(entry.getKey()) && entrytmp.getValue().equals(entry.getValue())) {
					found = !found;
					break;
				}
			}
			if (found == false)
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Case other = (Case) obj;
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
