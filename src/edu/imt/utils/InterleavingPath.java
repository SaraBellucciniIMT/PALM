package edu.imt.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Sets;

import edu.imt.specification.operators.Activity;

public class InterleavingPath {
	private List<List<Activity>> paths;
	private List<Activity> exitCommElements;
	private List<Activity> startCommElements;
	private List<Pair<Calendar, Calendar>> timeList;

	public InterleavingPath() {
		this.paths = new ArrayList<>();
		this.startCommElements = new ArrayList<Activity>();
		this.exitCommElements = new ArrayList<Activity>();
	}

	public Set<String> getAlphabet() {
		Set<String> alphabet = new HashSet<String>();
		paths.forEach(l -> l.forEach(s -> {
			for (String t : s.getActivity())
				alphabet.add(t);
		}));
		return alphabet;
	}

	public List<Activity> getMyPath(Activity a) {
		for (List<Activity> path : paths) {
			for (Activity aPath : path) {
				if (a.isSubActivity(aPath))
					return path;
			}
		}
		return null;
	}

	public void addPath(List<Activity> path) {
		this.paths.add(path);
	}

	/*
	 * public Calendar getStartTime(Calendar st) { if (startTime == null)
	 * setTime(st); return startTime; }
	 */
	public void resetTime() {
		this.timeList = null;
	}
	/*
	 * public Calendar getEndTime() { return endTime; }
	 */

	public Pair<Calendar, Calendar> getTimePair(int index, Calendar time) {
		if (timeList == null) {
			timeList = new ArrayList<Pair<Calendar, Calendar>>();
			setTime(time);
		}
		if (index < timeList.size())
			return timeList.get(index);
		else
			return null;
	}

	/*
	 * Set the start end end time for this set of activities
	 */
	private void setTime(Calendar st) {
		int l = 0;
		Calendar startTime = st;
		Calendar endTime = Calendar.getInstance();
		while (l < totalPathSize()) {
			Random rd = new Random();
			endTime.setTimeInMillis(startTime.getTimeInMillis() + rd.nextInt(60 * 60000) + 60000);
			System.out.println(startTime.getTime() + ", " + endTime.getTime());
			Pair<Calendar, Calendar> tmp = Pair.of((Calendar) startTime.clone(), (Calendar) endTime.clone());
			timeList.add(tmp);
			startTime.setTimeInMillis(endTime.getTimeInMillis() + 1);
			l++;
		}
		timeList.forEach(p -> System.out.println("ST: " + p.getLeft().getTime() + " ET: " + p.getRight().getTime()));

		System.out.println("here");

	}

	private int totalPathSize() {
		int n = 0;
		for (List<Activity> l : paths) {
			for (Activity a : l) {
				n = n + a.size();
			}
		}
		return n;
	}

	public void addStartCommunication(Activity s) {
		if (!startCommElements.contains(s))
			this.startCommElements.add(s);
	}

	public void addStartCommunication(List<Activity> s) {
		this.startCommElements.addAll(s);
	}

	public void addEndCommunication(Activity s) {
		if (!exitCommElements.contains(s))
			this.exitCommElements.add(s);
	}

	public List<List<Activity>> getPaths() {
		return paths;
	}

	public Activity getExitCommEl(int index) {
		if (index >= 0 && index < this.exitCommElements.size())
			return this.exitCommElements.get(index);
		return null;
	}

	public List<Activity> getExitCommEl() {
		return exitCommElements;
	}

	public Activity getStartCommEl(int index) {
		if (index >= 0 && index < this.startCommElements.size())
			return this.startCommElements.get(index);
		return null;
	}

	/*
	 * public List<Activity> getStartCommEl() { return startCommElements; }
	 */

	// When the path is empty then the interleaving path is also empty
	public boolean isEmpty() {
		if (this.paths.isEmpty())
			return true;
		return false;
	}

	public boolean emptyExitComm() {
		if (exitCommElements == null)
			return true;
		return false;
	}

	public boolean emptyStartComm() {
		if (startCommElements == null)
			return true;
		return false;
	}

	public boolean emptyPath() {
		if (paths.isEmpty())
			return true;
		for (List<Activity> list : paths) {
			try {
				if (list.isEmpty())
					return true;
				for (Activity s : list) {
					if (s.isEmpty())
						return false;
				}
			} catch (Exception e) {
				System.out.println(paths.toString() + " " + list.toString());
			}
		}
		return true;
	}

	public List<Activity> dueListe(List<Activity> l1, List<Activity> l2) {
		longestPathOnHead();
		// Longest path of all paths
		Set<Activity> total = new HashSet<>();
		if (startCommElements.size() <2)
			for (int i = 0; i < l1.size(); i++) {
				int widthPath = 0;
				while (widthPath < l2.size()) {
					Set<Activity> tmp = new HashSet<>();
					for (Activity string : l1) {
						tmp.addAll(combinaTwoString(string, l2.get(widthPath)));
					}
					total.addAll(tmp);
					widthPath++;
				}
			}
		else {
			for (int i = 0; i < l1.size(); i++) {
				if (i < l2.size()) {
					Set<Activity> tmp = new HashSet<>();
					tmp.addAll(combinaTwoString(l1.get(i), l2.get(i)));
					total.addAll(tmp);
				} else
					break;
			}
		}
		return new ArrayList<>(total);
	}

	public List<Triple<Activity, List<Activity>, Activity>> interleavingTwoList() {

		longestPathOnHead();
		// Longest path of all paths
		List<Triple<Activity, List<Activity>, Activity>> triples = new ArrayList<>();
		List<Activity> list = dueListe(paths.get(0), paths.get(1));
		int deep = 2;
		List<Activity> result = new ArrayList<>();
		while (deep < paths.size()) {
			result = dueListe(list, paths.get(deep));
			list = result;
			deep++;
		}
		List<Activity> alreadyCheck = new ArrayList<>();
		for (int i = 0; i < paths.get(0).size(); i++) {
			List<Activity> listToBeInTheMiddle = new ArrayList<>();
			for (Activity a : list) {
				if (a.contains(paths.get(0).get(i)) && !alreadyCheck.contains(a)) {
					alreadyCheck.add(a);
					listToBeInTheMiddle.add(a);
				}
			}
			if (!startCommElements.isEmpty() && !exitCommElements.isEmpty()) {
				if (startCommElements.size() > i)
					triples.add(Triple.of(startCommElements.get(i), listToBeInTheMiddle, exitCommElements.get(i)));
				else
					triples.add(Triple.of(startCommElements.get(0), listToBeInTheMiddle, exitCommElements.get(0)));
			} else {
				triples.add(Triple.of(new Activity("-"), listToBeInTheMiddle, new Activity("-")));
			}
		}
		return triples;
	}

	/*
	 * Put the longest path at the head of the paths list such that when the
	 * interleaving between all the list starts, no cell to be interleaved is left
	 * out
	 */
	protected void longestPathOnHead() {
		int indexLongestPath = 0;
		int maxLength = 0;
		for (List<Activity> list : paths) {
			if (list.size() > maxLength) {
				maxLength = list.size();
				indexLongestPath = paths.indexOf(list);
			}
		}
		paths.add(0, paths.get(indexLongestPath));
		paths.remove(indexLongestPath + 1);
	}

	public static Set<Activity> combinaTwoString(Activity s1, Activity s2) {
		Set<Activity> result = allPossibleCombination(s1, s2);
		Set<Activity> tmpResult = allPossibleCombination(s2, s1);
		return Sets.union(result, tmpResult);
	}

	private static Set<Activity> allPossibleCombination(Activity s1, Activity s2) {
		Set<Activity> result = new HashSet<Activity>();
		int lengths2 = 1;
		int indexOns1 = 0;
		while (indexOns1 <= s1.getActivity().size()) {
			while (lengths2 <= s2.size()) {
				Activity s = s1.subActivity(0, indexOns1).concat(s2.subActivity(0, lengths2), s1.subActivity(indexOns1),
						s2.subActivity(lengths2));
				result.add(s);
				lengths2++;
			}
			lengths2 = 1;
			indexOns1++;
		}
		return result;
	}
}
