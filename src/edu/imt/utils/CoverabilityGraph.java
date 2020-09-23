package edu.imt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Sets;

public class CoverabilityGraph {

	// First Node, Second node, label following the schema of the sg files
	private Set<Triple<String, String, String>> triple;
	private String initialmarking;
	private String s = "ss";
	private int i = 0;

	public CoverabilityGraph() {
		triple = new HashSet<Triple<String, String, String>>();
	}

	// On the left the exit node and on the right the label
	public Set<Pair<String, String>> getPairWithInputNode(String inputnode) {
		Set<Pair<String, String>> set = new HashSet<Pair<String, String>>();
		for (Triple<String, String, String> t : triple) {
			if (t.getLeft().equals(inputnode))
				set.add(Pair.of(t.getMiddle(), t.getRight()));
		}
		return set;
	}

	public void setInitialMarking(String s) {
		this.initialmarking = s;
	}

	public boolean isFinalState(String node) {
		for (Triple<String, String, String> t : triple) {
			if (t.getLeft().equals(node))
				return false;
		}
		return true;
	}

	public String getInitialMarking() {
		return this.initialmarking;
	}

	/**
	 * Adds a triple with input state, output state and action to the CG
	 * @param s1 input state
	 * @param s2 output state 
	 * @param s3 action
	 */
	public void addTriple(String s1, String s2, String s3) {
		//_.00 is the way that sg file use to indicate blanck spaces
		s3=s3.replace("_.00", " ");
		triple.add(Triple.of(s1, s2, s3));
	}

	public Set<Triple<String, String, String>> getTriple() {
		return this.triple;
	}

	// Return an ordered list of node( from initial marking to last one)
	public List<String> getOrderedNode() {
		List<String> node = new ArrayList<String>();
		node.add(initialmarking);
		List<String> next = navigateNode(initialmarking);
		for (int i = 0; i < next.size(); i++) {
			if (!node.contains(next.get(i))) {
				node.add(next.get(i));
				navigateNode(next.get(i)).forEach(e -> {
					if (!node.contains(e))
						next.add(e);
				});
			}
		}
		return node;
	}

	private List<String> navigateNode(String s) {
		List<String> next = new ArrayList<String>();
		for (Triple<String, String, String> triple : triple) {
			if (triple.getLeft().equals(s))
				next.add(triple.getMiddle());
		}
		return next;
	}

	public void solveNameConflicts() {
		Set<String> nodes = new HashSet<String>();
		Set<String> label = new HashSet<String>();
		triple.forEach(t -> {
			nodes.add(t.getLeft());
			nodes.add(t.getMiddle());
			label.add(t.getRight());
		});
		Set<String> intersection = Sets.intersection(nodes, label);
		Map<String, String> map = new HashMap<String, String>();
		intersection.forEach(e -> {
			String name = s + i;
			while (nodes.contains(name) || label.contains(name))
				name = s + ++i;
			map.put(e, name);
		});
		Set<Triple<String, String, String>> set = new HashSet<Triple<String, String, String>>();
		for(Triple<String,String,String> t : triple) {
			String lname = t.getLeft();
			String rname = t.getMiddle();
			if(intersection.contains(lname))
				lname = map.get(lname);
			if(intersection.contains(rname))
				rname = map.get(rname);
			set.add(Triple.of(lname, rname, t.getRight()));
		}
		triple = set;
	}
}
