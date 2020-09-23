package edu.imt.specification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.javatuples.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.imt.utils.*;

import edu.imt.inputData.Event;
import edu.imt.inputData.Event.EventTemp;
import edu.imt.specification.structure.BlockStructure;
import edu.imt.specification.structure.Operator;

/**
 * This class represents a mCRL2 specification as given in .mcrl2 file
 * 
 * @author S. Belluccini
 *
 */
public class MCRL2 {

	private Set<Event> actSet;
	/**
	 * Map of all the specification processes : name = specification
	 */
	private Map<String, String> procspec;
	/**
	 * Set of all the actions allow to be performed in the specification
	 */
	private Set<Event> allowedAction;
	/**
	 * This map says which is the product between two variables, for example
	 * "t0|t0->t1"
	 */
	private Map<Event[], Event> commFunction;
	/**
	 * Set of all the action in the hide set
	 */
	private Set<Event> hideAction;
	/**
	 * We are the processes in the init set
	 */
	private Set<String> initSet;

	private Multimap<Event, Event> messages;
	public static final String DEADLOCK_FORMULA = "[!Terminate*]<true>true";
	public static final Event TAU = new Event("", "tau");

	public MCRL2() {
		initializeBasicSet();
	}

	private void initializeBasicSet() {
		this.actSet = new HashSet<Event>();
		this.procspec = new HashMap<String, String>();
		this.allowedAction = new HashSet<Event>();
		this.commFunction = new HashMap<Event[], Event>();
		this.hideAction = new HashSet<Event>();
		this.initSet = new HashSet<String>();
		this.messages = HashMultimap.create();
	}

	public Set<Event> getActSet() {
		return this.actSet;
	}

	public void setActSet(Set<Event> set) {
		this.actSet.addAll(set);
	}

	public void addActSet(Event a) {
		/*
		 * if (Event.multiaction.matcher(a.getName()).matches()) { for (String t :
		 * a.getEvent()) { if (!t.equals(Operator.PARALLEL.getStringOp()))
		 * this.actSet.add(new Event(t)); } } else
		 */
		this.actSet.add(a);
	}

	public void addActSet(Set<Event> set) {
		this.actSet.addAll(set);
	}

	public Map<String, String> getProcspec() {
		return procspec;
	}

	public Set<String> getInitSet() {
		return this.initSet;
	}

	public void addProcSpec(String nameProc, String defProc) {
		this.procspec.put(nameProc, defProc);
	}

	public void addProcSpec(Map<String, String> proc) {

		this.procspec.putAll(proc);

	}

	public Set<Event> getAllowedAction() {
		return allowedAction;
	}

	public void setAllowedAction(Set<Event> allowedAction) {
		this.allowedAction.addAll(allowedAction);
	}

	public void addAllowedAction(Event action) {
		if (!this.allowedAction.contains(action))
			this.allowedAction.add(action);
	}

	public void addAllowedAction(Set<Event> set) {
		this.allowedAction.addAll(set);
	}

	public void removedAllowedAction(Event... a) {
		for (Event act : a)
			this.allowedAction.remove(act);
	}

	public Map<Event[], Event> getCommFunction() {
		return commFunction;
	}

	public Event getCommFunctionElement(Event[] arr) {
		return commFunction.get(arr);
	}

	public boolean isKeyCommFunction(Event a) {
		for (Event[] key : commFunction.keySet()) {
			if (ArrayUtils.contains(key, a)) {
				return true;
			}
		}
		return false;
	}

	public void addCommFunction(Map<Event[], Event> commFunction) {
		this.commFunction.putAll(commFunction);
	}

	public void setCommFunction(Map<Event[], Event> commFunction) {
		this.commFunction.clear();
		this.commFunction.putAll(commFunction);
	}

	public void addCommFunction(Event[] inputf, Event outputf) {
		if (!(this.commFunction.containsKey(inputf) && this.commFunction.get(inputf).equals(outputf)))
			this.commFunction.put(inputf, outputf);
	}

	public Set<Event> getHideAction() {
		return hideAction;
	}

	public void setHideAction(Set<Event> hideAction) {
		this.hideAction.addAll(hideAction);
	}

	public void addHideAction(Event action) {
		this.hideAction.add(action);
	}

	public void addInitSet(String s) {
		this.initSet.add(s);
	}

	public void addInitSet(Set<String> s) {
		this.initSet.addAll(s);
	}

	public Multimap<Event, Event> getMessage() {
		return this.messages;
	}

	public void addHideAction(Set<Event> set) {
		this.hideAction.addAll(set);
	}

	public void addMessage(String message, Event task) {
		for (Event e : messages.keys()) {
			if (e.getRealName().equals(message)) {
				messages.get(e).add(task);
				return;
			}

		}
		messages.put(new Event(message), task);
	}

	public void addMessage(Multimap<String, Event> map) {
		for (Entry<String, Event> entry : map.entries()) {
			String nameEvent = "";
			for (Event key : messages.keys()) {
				if (key.getRealName().equals(entry.getKey())) {
					nameEvent = key.getName();
					break;
				}
			}
			if (!nameEvent.isEmpty())
				messages.put(new Event(entry.getKey(), nameEvent), entry.getValue());
			else
				messages.put(new Event(entry.getKey()), entry.getValue());
		}
	}

	/**
	 * It appends the messagges to the map using their real name
	 * 
	 * @param map
	 */
	public void appendMessage(Multimap<Event, Event> map) {
		for (Entry<Event, Event> entrymap : map.entries()) {
			String realMessage = entrymap.getKey().getRealName();
			addMessage(realMessage, entrymap.getValue());
		}
	}

	/**
	 * Adds to this specification a processes, act and functions defined by a
	 * blockstrcuture
	 * 
	 * @param b the block structure
	 */
	public void fromBlockStrucutureToMcrl2Processes(BlockStructure b, int threshold) {
		String pair = T(b, threshold);
		String initName = Utils.getProcessName();
		procspec.put(initName, pair);
		initSet.add(initName);
		for (Entry<String, String> p : foldL.entrySet())
			procspec.put(p.getValue(), p.getKey() + Operator.SEQUENCE.getOperator() + p.getValue()
					+ Operator.CHOICE.getOperator() + TAU.getName());
		for(Entry<String,String> entry : unfoldL.entrySet()) {
			procspec.put(entry.getValue(), entry.getKey());
		}
	}

	// Process loop - name loop
	private Map<String, String> unfoldL = new HashMap<String, String>();
	private Map<String, String> foldL = new HashMap<String, String>();

	private String T(BlockStructure b, int threshold) {
		if (b.isEmpty()) {
			return TAU.getName();
		} else if (b.hasEvent()) {
			Event e = b.getEvent();
			if (!e.getClass().equals(EventTemp.class)) {
				actSet.add(e);
				allowedAction.add(e);
			}
			return e.getName();
		} else {
			String s = "";
			String tmp;
			if (b.getOp().equals(Operator.LOOP)) {
				String loop = "";
				for (int i = 0; i < b.size(); i++) {
					tmp = T(b.getBlock(i), threshold);
					if (!loop.isEmpty())
						loop = loop.concat(Operator.CHOICE.getOperator());
					loop = loop.concat(tmp);
				}
				if (b.getFrequency() < threshold) {
					loop = unrollLoop(loop, b.getRepetition());
					if (unfoldL.containsKey(loop)) {
						s = unfoldL.get(loop);
					} else {
						s = Utils.getProcessName();
						unfoldL.put(loop, s);
					}
				} else {
					if (!foldL.containsKey(loop)) {
						s = Utils.getProcessName();
						foldL.put(loop, s);
					} else
						s = foldL.get(loop);
				}
			} else {
				for (int i = 0; i < b.size(); i++) {
					tmp = T(b.getBlock(i), threshold);
					if (!s.isEmpty())
						s = s.concat(b.getOp().getOperator());
					s = s.concat(tmp);
				}
				s = "(" + s + ")";
			}
			return s;
		}
	}

	/**
	 * Based on its number of repetition n, each trace is represented as a choice
	 * among the n! sequence of its actions. This is repeated for each block in the
	 * loop and combined as a choice among all the possibilities. Example L
	 * {S{a,b}S{a,f,b}} if repetetion = 3 then the result will be: L = (a.b+a.b.f) +
	 * (a.b+a.b.f).(a.b+a.b.f) + (a.b+a.b.f).(a.b+a.b.f).(a.b+a.b.f) + tau
	 * 
	 * @param b
	 * @return
	 */
	private String unrollLoop(String loop, int repetition) {
		String unrolled = "";
		while (repetition != 0) {
			String s = "";
			for (int i = 0; i < repetition; i++) {
				if (!s.isEmpty())
					s += Operator.SEQUENCE.getOperator();
				s += loop;
			}
			if (!unrolled.isEmpty())
				unrolled += Operator.CHOICE.getOperator();
			unrolled += s;
			repetition--;
		}
		if (unrolled.isEmpty())
			return loop + TAU.getName();
		else
			return unrolled + Operator.CHOICE.getOperator() + TAU.getName();
	}
}
