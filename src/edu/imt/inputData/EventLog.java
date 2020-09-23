package edu.imt.inputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import edu.imt.algorithm.LoopSet;

/**
 * This is the EventLog class that represent a log of traces of events
 * @author S. Belluccini
 */
public class EventLog implements Iterable<Trace>{

	/**
	 * The log 
	 */
	private List<Trace> log;
	/**
	 *<RealName, Id>
	 */
	private Map<String,String> eventMap;

	/**
	 * Inizialize an empty log
	 */
	public EventLog() {
		this.log = new ArrayList<Trace>();
		this.eventMap = new HashMap<String,String>();
	};
	
	public EventLog(List<Trace> t) {
		this.log = t;
		this.eventMap = new HashMap<>();
	}

	/**
	 * Add at the end of the list a trace t
	 * @param t a trace of type Trace
	 * @return 
	 */
	public void add(Trace t) {
		this.log.add(t);
	}

	/**
	 * Add at the end of the list a list of traces 
	 * @param traces a list of trace of type Trace
	 * @return 
	 */
	public void addAll(List<Trace> traces) {
		this.log.addAll(traces);
	}

	public void addEventToMap(String realName, String id) {
		this.eventMap.put(realName, id);
	}
	
	public boolean isInEventMap(String realName) {
		return this.eventMap.containsKey(realName);
	}
	/**
	 * Give the list of traces in the log
	 * @param 
	 * @return a list of traces of type Trace
	 */
	public List<Trace> getTrace() {
		return log;
	}
	
	/**
	 * Returns a map containing key=RealName value=Id of each event in the log
	 * @return a map containing key=RealName value=Id of each event in the log
	 */
	public Map<String,String> getEventMap() {
		return this.eventMap;
	}

	public String getIdEventMap(String realName) {
		return this.eventMap.get(realName);
	}
	@Override
	public String toString() {
		return "EventLog [" + log + "]";
	}

	/**
	 * Give the number of traces in the eventlog
	 * @return dimension of the log
	 */
	public int size() {
		return this.log.size();
	}

	/**
	 * Give the name of all the events inside the log
	 * @return a set of events of type Event
	 */
	public Set<Event> geAlphabet() {
		Set<Event> alphabet = new HashSet<Event>();
		for (Trace c : log) {
			alphabet = Sets.union(alphabet, c.getAlphabet());
		}
		return alphabet;
	}

	/**
	 * Get the number of different (e1 =/= e2) events present in the log
	 * 
	 * @return an int, i.e. the number of events 
	 */
	public int getEventCardinality() {
		Set<Event> events = new HashSet<Event>();
		for (Trace t : log) {
			events.addAll(t.getAlphabet());
		}
		return events.size();
	}
	
	/**
	 * Compute and return the frequency of a given loop set as defined in Definition 3
	 * @param l loop set of which we want to know the frequency
	 * @return number, i.e. frequency 
	 */
	public double getFrequencyLoop(LoopSet l) {
		int sum_floopcase=0;
		int n_cases = 0;
		for(Trace t : log) {
			double floopcase = t.getFrequencyLoop(l.getName().getName());
			if(floopcase!=-1) {
				sum_floopcase += floopcase;
				n_cases++;
			}
		}
		return ((sum_floopcase/n_cases) + (n_cases*100/ log.size()))/2;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventMap == null) ? 0 : eventMap.hashCode());
		result = prime * result + ((log == null) ? 0 : log.hashCode());
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
		EventLog other = (EventLog) obj;
		if (eventMap == null) {
			if (other.eventMap != null)
				return false;
		} else if (!eventMap.equals(other.eventMap))
			return false;
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}

	@Override
	public Iterator<Trace> iterator() {
		return this.log.iterator();
	}
	
	



}
