package edu.imt.inputData;

import java.util.List;

import edu.imt.utils.Utils;

public class Event {
	
	private String name = null;
	
	public Event() {
	
	}
	
	public Event(String name) {
		this.name = Utils.removeblankspaceAndDots(name);
	}
	
	
	public int size() {
		return name.length();
	}
	public Event(List<Event> l) {
		
		for(Event e : l) {
			if(this.name == null) {
				this.name = e.getName();
				continue;
			}
			this.name = this.name.concat(e.getName());
		}
	}
	public String getName() {
		return name;
	}	
	
	public Event concatenation(Event e) {
		if(this.name == null)
			return new Event(e.getName());
		
		return new Event(this.name + e.getName());
	}
	
	
	
	public Event lastElement() {
		Character c = this.getName().charAt(this.toString().length()-1);
		return new Event(new String(c.toString()));
	}
	public void remove(Event e) {
		if(this.contain(e)) {
			this.name = name.replace(e.getName(), "");
		}
			
	}
	public boolean contain(Event e) {
		String otherE = e.getName();
		if(this.name.contains(otherE))
			return true;
		return false;
	}
	@Override
	public String toString() {
		return name;
	}

	public boolean isEmpty() {
		return this.name == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Event other = (Event) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
	
}
