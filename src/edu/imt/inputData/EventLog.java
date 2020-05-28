package edu.imt.inputData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.google.common.collect.Sets;

public class EventLog {
	
	private List<Case> log = new ArrayList<Case>(); 

	
	public EventLog() {};
	
	public EventLog(List<Case> log) {
		for(Case c : log) {
			Case caso = new Case();
			 for(int j=0; j<c.length(); j++) {
				caso.add(new Event(c.getEvent(j).getName()));
			 }
			 caso.setHBrel(c.getHBRel());
			 this.log.add(caso);
		}
		//this.log = Lists.newArrayList(log);
	}
	
	public void add(Case c) {
		this.log.add(c);
	}
	
	public void addAll(List<Case> c) {
		this.log.addAll(c);
	}
	public Iterator<Case> getIterator(){
		return log.iterator();
	}
	
	public Case getSingleCase() {
		if(log.size() == 1)
			return log.stream().findFirst().get();
		else
			return log.stream().findAny().get();
	}
	
	public List<Case> getCase(){
		return log;
	}
	public boolean containCase(Case c) {
		for(Case casec : log) {
			if(casec.equals(c))
				return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return "EventLog [" + log + "]";
	}
	/*
	 * Number of cases in the eventlog
	 */
	public int size() {
		return this.log.size();
	}
	
	public Set<Event> geAlphabet(){
		Set<Event> alphabet = new HashSet<Event>();
		for(Case c : log) {
			alphabet = Sets.union(alphabet, c.getAlphabet());
		}
		return alphabet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((log == null) ? 0 : log.hashCode());
		return result;
	}

	public int getTraceCardinality() {
		return this.log.size();
	}
	
	public int getEventCardinality() {
		Set<Event> events = new HashSet<Event>();
		for(Case c : log) {
			c.getTrace().forEach(e->{events.add(e);});
		}
		return events.size();
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
		if (log == null) {
			if (other.log != null)
				return false;
		} else if (!log.equals(other.log))
			return false;
		return true;
	}

}
