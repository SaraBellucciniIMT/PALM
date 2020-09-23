package edu.imt.inputData;

import edu.imt.utils.Utils;

/**
 * This class describe a single event inside a log, every event is identified by
 * a made up name and the real name taken from the log
 * 
 * @author Sara
 *
 */
public class Event {
	/**
	 * This is the real name taken from the log
	 */
	private String realName = null;
	/**
	 * This is the name that will be used instead of the real one
	 */
	private String name = null;

	/**
	 * Constructor for class Event, for events without real names
	 */
	public Event() {
	}

	/**
	 * Another constructor for class Event, assign the given name as real name and compute another name for the current event
	 * @param name the real name
	 */
	public Event(String name) {
		this.realName = name;
		this.name = Utils.getEventName();
	}
	
	public Event(String realName, String id) {
		this.realName = realName;
		this.name = id;
	}
	
	public static class EventTemp extends Event {
		private static final String T = "t";
		private static long INDEX = 0;
		
		public EventTemp() {
			super();
			this.setName(T+ INDEX++);
		}
	}

	/**
	 * Set the event name to the given one
	 * @param s the given name
	 */
	public void setName(String s) {
		this.name = s;
	}

	/**
	 * Gives the real name as output if exists, otherwise null
	 * @return real name
	 */
	public String getRealName() {
		return this.realName;
	}
	
	/**
	 * Gives the name of this event as output
	 * @return name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Check if the name has been set
	 * @return true if the name is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.name == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((realName == null) ? 0 : realName.hashCode());
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
		if (realName == null) {
			if (other.realName != null)
				return false;
		} else if (!realName.equals(other.realName))
			return false;
		return true;
	}

	



}
