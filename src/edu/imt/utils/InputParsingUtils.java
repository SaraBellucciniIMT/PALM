package edu.imt.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import edu.imt.inputData.Case;
import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;
import edu.imt.specification.Mcrl2;
import edu.imt.specification.operators.Activity;
import edu.imt.specification.operators.Operator;

public class InputParsingUtils {

	/*
	 * Parse a XES file ".xes.gz" or ".xes"
	 */

	private static Map<String, Integer> openedProcess = new HashMap<String, Integer>();
	private static int kUnfolding = 2;
	protected static boolean multithreadMode = false;
	static protected String parethesis = "\\(.*\\)";
	protected static List<InterleavingPath> totalIP = new ArrayList<InterleavingPath>();
	protected static String sharedWaitingArray;
	// protected static Pair<String, String> sharedCommunication;
	protected static BlockingQueue<Activity> remainedProcess = new LinkedBlockingQueue<Activity>();
	protected static Mcrl2 spec;
	public static final String lct = "lifecycle:transition";
	public static final String mes = "message";
	public static final String cn = "concept:name";
	private static final String dotsg = ".sg";

	public static void modifyKunfolging(int newk) {
		kUnfolding = newk;
	}

	// It return a map contain the converability graph where Pair<String,String> are
	// the arcs and String is the label of the arc
	public static CoverabilityGraph parseSGfile(File f) {
		if (!f.getName().endsWith(dotsg)) {
			System.err.println("Wrong file extension, try again using a " + dotsg + " file");
			return null;
		}
		Set<Triple<String, String, String>> set = new HashSet<Triple<String, String, String>>();
		CoverabilityGraph cg = new CoverabilityGraph();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String st;
			boolean startotoread = false;
			while ((st = br.readLine()) != null) {
				if (st.equals(".state graph")) {
					startotoread = true;
					continue;
				} else if (st.startsWith(".marking")) {
					st = st.replace(".marking", "").replace("{", "").replace("}", "").trim();
					cg.setInitialMarking(st);
					startotoread = false;
					break;
				}
				if (startotoread) {
					String[] split = st.split(" ");
					for (int i = 0; i < split.length; i++) {
						if (split[1].startsWith("tau") || split[1].contains("xor_merge_")
								|| split[1].contains("xor_split") || split[1].matches("start_(.*)_start")
								|| split[1].matches("end_(.*)_end") || split[1].contains("XOR")
								|| split[1].contains("AND") || split[1].equals("start") || split[1].equals("end"))
							cg.addTriple(split[0], split[2], "tau");
						else
							cg.addTriple(split[0], split[2], split[1]);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cg.solveNameConflicts();
		return cg;
	}

	public static EventLog parseXes(File f) throws Exception {
		XesXmlParser parseXes = new XesXmlParser();
		List<XLog> logs = parseXes.parse(f);
		// Set<EventLog> eventLgs = new HashSet<>();
		List<Case> tmpCases = new ArrayList<Case>();
		for (XLog log : logs) {
			for (XTrace xtrace : log) {
				// XAttributeMap attributes = xtrace.getAttributes();
				List<Event> tmpEvents = new ArrayList<Event>();
				Iterator<XEvent> it = xtrace.iterator();
				while (it.hasNext()) {
					XEvent xe = it.next();
					// System.out.println(xe.getAttributes().get(cn).toString());
					XAttributeLiteralImpl att = (XAttributeLiteralImpl) xe.getAttributes().get(lct);

					if (att.getValue().equals("complete")) {
						Event e = new Event(xe.getAttributes().get(cn).toString());
						tmpEvents.add(e);
					}
				}

				Case tmpCase = new Case(tmpEvents);
				tmpCase.computeHappenedBeforeRel(xtrace);
				tmpCases.add(tmpCase);
			}
		}
		return new EventLog(tmpCases);
	}

	public static Multimap<Activity, Activity> parseXesMessage(File f) {
		XesXmlParser parseXes = new XesXmlParser();
		List<XLog> logs;
		try {
			logs = parseXes.parse(f);

			Multimap<Activity, Activity> map = HashMultimap.create();
			for (XLog log : logs) {
				for (XTrace xtrace : log) {
					Iterator<XEvent> it = xtrace.iterator();
					while (it.hasNext()) {
						XEvent xe = it.next();
						if (xe.getAttributes().containsKey(mes)) {
							String xename = Utils.removeblankspaceAndDots(xe.getAttributes().get(cn).toString());
							String xemessage = Utils.removeblankspaceAndDots(xe.getAttributes().get(mes).toString());
							map.put(new Activity(xemessage), new Activity(xename));
						}
					}

				}
			}
			return map;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * Check for the emerging behavior starting from the pattern plus, then . then
	 * || First of all we need to change string because we want to see the major
	 * external behavior 1° check if a plus is matched
	 */

	public static String linearizeProcessWithKUnfolding(String tmpLinearization, Map<String, String> procspec) {
		boolean changes = false;
		for (String s : procspec.keySet()) {
			if (tmpLinearization.contains(s)) {
				if (!openedProcess.containsKey(s)) {

					tmpLinearization = tmpLinearization.replaceAll(s, "(" + procspec.get(s) + ")");
					openedProcess.put(s, 0);
					changes = true;
				} else if (openedProcess.get(s) < kUnfolding) {
					tmpLinearization = tmpLinearization.replaceAll(s, "(" + procspec.get(s) + ")");
					openedProcess.put(s, openedProcess.get(s) + 1);
					changes = true;
				} else {
					tmpLinearization = tmpLinearization.replaceAll(s, findLoopEnd(new Activity(s), procspec).getName());
					// .replaceAll("\\s", "");
				}
			}
		}
		if (changes) {
			return linearizeProcessWithKUnfolding(tmpLinearization, procspec);
		}
		return tmpLinearization;
	}

	/*
	 * Try to find the finite part of the loop process,i.e. the only way to go out
	 * from it if exist Otherwhise we stop it
	 */
	private static Activity findLoopEnd(Activity process, Map<String, String> procspec) {

		Activity specificationProcess = new Activity(tranformStringInAnActivity(procspec.get(process.getName())));
		// = new Activity(new Task(procspec.get(process.getName())));
		if (specificationProcess.contains(Operator.CHOICE)) {
			Activity[] splittedProcess = specificationProcess.splittingSy(Operator.CHOICE);
			Activity toBeReturned = new Activity();
			for (int i = 0; i < splittedProcess.length; i++) {
				if (!splittedProcess[i].contains(process)) {
					toBeReturned = toBeReturned.concat("(" + splittedProcess[i].getName() + ")");
					if (i != (splittedProcess.length - 1))
						toBeReturned = toBeReturned.concat(Operator.CHOICE);
				}
			}
			return toBeReturned;
		}
		return new Activity();
	}

	/*
	 * Given a string representing a process return a set of traces of that process
	 */

	public static Set<Activity> goDeep(String s, Mcrl2 spec) {
		InputParsingUtils.spec = spec;
		Set<Activity> result = new HashSet<Activity>();
		List<String> taskList = tranformStringInAnActivity(s);

		List<Activity> activityList = createSingleTrace(new Activity(taskList));
		for (Activity a : activityList)
			result.add(a);

		System.out.println(result.toString());
		return result;
	}

	public static List<String> tranformStringInAnActivity(String s) {
		List<String> result = new ArrayList<String>();
		String toCheck = "";
		for (int i = 0; i < s.length(); i++) {
			String sp = String.valueOf(s.charAt(i));
			// Either is no an operator or is a multiaction
			if (!equalToAnOperator(sp) || sp.equals(Operator.PARALLEL.getStringOp())
					&& !equalToAnOperator(String.valueOf(s.charAt(i + 1)))) {
				toCheck = toCheck.concat(sp);
				continue;
			} else {

				if (!toCheck.isEmpty())
					result.add(toCheck);
				result.add(sp);
				// In the case is a parallel also the next string is a symbol so we have to add
				// both of them
				if ((i != (s.length() - 1)) && equalToAnOperator(String.valueOf(s.charAt(i + 1)))) {
					result.add(String.valueOf(s.charAt(i + 1)));
					i++;
				}
				toCheck = "";
			}
		}
		if (!toCheck.isEmpty())
			result.add(toCheck);
		return result;
	}

	private static boolean equalToAnOperator(String s) {
		if (s.equals(Operator.CHOICE.getStringOp()) || s.equals(Operator.SEQUENCE.getStringOp())
				|| s.equals(Operator.PARALLEL.getStringOp()) || s.equals("(") || s.equals(")"))
			return true;
		return false;
	}

	public static List<Activity> createSingleTrace(Activity s) {
		List<Activity> tmpSet = new ArrayList<Activity>();
		// System.out.println(s.toString());
		s = s.takeOfParenthesis();
		if (tmpSet.isEmpty())
			tmpSet = matchPlus(s);

		if (tmpSet.isEmpty())
			tmpSet = matchParallel(s);

		if (tmpSet.isEmpty())
			tmpSet = matchSequence(s);

		if (tmpSet.isEmpty())
			tmpSet.add(matchActivity(s));
		return tmpSet;
	}

	protected static List<Activity> matchPlus(Activity s) {
		List<Activity> tmp = new ArrayList<Activity>();
		if (!s.parseOnSymbol(Operator.CHOICE))
			return tmp;
		// try to split the string based on the plus

		Activity[] splitted = s.splittingSy(Operator.CHOICE);
		for (Activity st : splitted) {
			List<Activity> tmptmp = createSingleTrace(st);

			if (tmptmp != null)
				tmp.addAll(tmptmp);
		}
		return tmp;

	}

	protected static List<Activity> matchParallel(Activity s) {
		List<Activity> partialResult = new ArrayList<Activity>();
		if (!s.parseOnSymbol(Operator.PARALLEL))
			return partialResult;

		// try to split the string based on the parallel
		Activity[] splitted = s.splittingSy(Operator.PARALLEL);
		InterleavingPath ip = new InterleavingPath();
		instantiateThreads(splitted, ip);
		while (!remainedProcess.isEmpty()) {
			splitted = new Activity[0];
			splitted = remainedProcess.toArray(splitted);
			remainedProcess.clear();
			List<Activity> tmpStartComm = ip.getExitCommEl();
			ip = new InterleavingPath();
			ip.addStartCommunication(tmpStartComm);
			instantiateThreads(splitted, ip);
		}
		return interleaving();
	}

	protected static void instantiateThreads(Activity[] splitted, InterleavingPath ip) {
		ThreadInterleaving[] pool = new ThreadInterleaving[splitted.length];
		for (int i = 0; i < splitted.length; i++) {
			pool[i] = new ThreadInterleaving(splitted[i], ip);
			pool[i].start();
		}
		for (ThreadInterleaving t : pool) {
			try {
				t.join();
			} catch (InterruptedException e) {
				System.out.println("Someone is still working");
			}
		}
		if (!ip.isEmpty())
			totalIP.add(ip);

	}

	protected static List<Activity> interleaving() {
		Map<Activity, List<Activity>> list = new HashMap<>();
		for (int i = 0; i < totalIP.size(); i++) {
			InterleavingPath currentList = totalIP.get(i);
			// If this condition is satisfied then there is no interleaving needed
			if (currentList.getPaths().size() <= 1) {
				List<Activity> uniqueList = currentList.getPaths().get(0);
				for (int j = 0; j < uniqueList.size(); j++) {
					// There is already a list that ended with your stating communication element so
					// you need to connect your string with all the string that are in this list
					List<Activity> tmp = new ArrayList<>();
					if (list.containsKey(currentList.getStartCommEl(j))) {

						for (Activity s : list.get(currentList.getStartCommEl(j))) {
							tmp.add(s.concat(uniqueList.get(j)));
						}

						if (list.containsKey(currentList.getExitCommEl(j))) {
							list.get(currentList.getExitCommEl(j)).addAll(tmp);
						} else
							list.put(currentList.getExitCommEl(j), tmp);
						list.remove(currentList.getStartCommEl(j), list.get(currentList.getStartCommEl(j)));
					} else {
						tmp.add(uniqueList.get(j));
						list.put(currentList.getExitCommEl(j), tmp);
					}
				}
				continue;
			}
			List<Triple<Activity, List<Activity>, Activity>> tmp = totalIP.get(i).interleavingTwoList();
			tmp = unionOfSequence(tmp);
			// List<Triple<Activity, List<Activity>, Activity>> alreadyCheck = new
			// ArrayList<>();
			for (Triple<Activity, List<Activity>, Activity> triple : tmp) {
				if (list.containsKey(triple.getLeft())) {
					List<Activity> tmpList = new ArrayList<>();
					for (Activity s : list.get(triple.getLeft())) {
						for (Activity t : triple.getMiddle()) {
							Activity a = new Activity(s.getActivity());
							a.concat(t);
							tmpList.add(a);
						}
					}
					list.put(triple.getRight(), tmpList);
					list.remove(triple.getLeft());

				} else
					list.put(triple.getRight(), triple.getMiddle());
			}
		}
		List<Activity> result = new ArrayList<Activity>();
		for (List<Activity> l : list.values()) {
			result.addAll(l);
		}
		return result;
	}

	private static List<Triple<Activity, List<Activity>, Activity>> unionOfSequence(
			List<Triple<Activity, List<Activity>, Activity>> triples) {
		List<Triple<Activity, List<Activity>, Activity>> tmp = new ArrayList<>();
		List<Triple<Activity, List<Activity>, Activity>> listToUncheck = new ArrayList<>();
		for (Triple<Activity, List<Activity>, Activity> triple : triples) {
			if (listToUncheck.contains(triple))
				continue;
			Activity startActivity = triple.getLeft();
			Activity exitActivity = triple.getRight();
			List<Activity> newMiddle = new ArrayList<Activity>();
			newMiddle.addAll(triple.getMiddle());
			for (Triple<Activity, List<Activity>, Activity> t : triples) {
				if (!t.equals(triple) && startActivity.equals(t.getLeft()) && exitActivity.equals(t.getRight())) {
					newMiddle.addAll(t.getMiddle());
					listToUncheck.add(t);
				}
			}
			// triples.removeAll(listToUncheck);
			tmp.add(Triple.of(startActivity, newMiddle, exitActivity));
		}
		return tmp;

	}

	protected static List<Activity> matchSequence(Activity s) {
		List<Activity> toBeReturned = new ArrayList<Activity>();
		if (!s.parseOnSymbol(Operator.SEQUENCE))
			return toBeReturned;

		Activity[] splitted = s.splittingSy(Operator.SEQUENCE);
		List<Activity> concatResult = new ArrayList<>();
		for (int i = 0; i < splitted.length; i++) {
			List<Activity> tmp = createSingleTrace(splitted[i]);
			if (concatResult.isEmpty()) {
				concatResult.addAll(tmp);
				continue;
			}
			List<Activity> newCR = new ArrayList<Activity>();
			for (int j = 0; j < concatResult.size(); j++) {
				int k = 0;
				while (k < tmp.size()) {
					List<String> taks = new ArrayList<String>();
					taks.addAll(concatResult.get(j).getActivity());
					taks.addAll(tmp.get(k).getActivity());
					Activity a = new Activity(taks);
					newCR.add(a);
					k++;
				}
			}
			concatResult = newCR;

		}
		toBeReturned.addAll(concatResult);
		return toBeReturned;
	}

	/*
	 * Try to match the a single activity, if is succesfull then return the string
	 * that is the name of the activity
	 */

	// static private Pattern activity = Pattern.compile("[a-z]+(\\|[a-z]+)*");

	protected static Activity matchActivity(Activity s) {
		// || !activity.matcher(s.getName()).matches()
		if ((s.contains("tau") && s.size() == 1))
			return new Activity();
		return s;
	}

	public static void generateXesFromTracesSet(Set<Activity> set, String fileName, Calendar now) {
		File file = new File(fileName + ".csv");
		String caseN = "Case";
		int n = 0;
		file.getParentFile().mkdirs();
		Calendar startTime = now;
		Calendar endTime = startTime;
		// Map the interleaving with the next pair that as to be visit. i.e. the first
		// one at index zero (pair<start_time,end_time> for each activity in the ip)
		Map<InterleavingPath, Integer> mapIPTime = new HashMap<InterleavingPath, Integer>();
		totalIP.forEach(ip -> mapIPTime.put(ip, 0));
		if (!file.exists()) {
			try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
				// Write the actions sequence that compose the system
				output.write("Case,Activity,Start,End\n");
				for (Activity trace : set) {
					for (int i = 0; i < trace.size(); i++) {
						String s = String.valueOf(trace.getActivity().get(i));
						output.write(caseN + n + "," + trace.getActivity().get(i) + ",");
						if (isAnInterleavingEl(s)) {
							for (InterleavingPath ip : totalIP) {
								if (ip.getAlphabet().contains(s)) {
									Pair<Calendar, Calendar> tmpTime = ip.getTimePair(mapIPTime.get(ip), startTime);
									startTime = tmpTime.getLeft();
									endTime = tmpTime.getRight();
									output.write(startTime.get(Calendar.DAY_OF_MONTH) + "/"
											+ startTime.get(Calendar.MONTH) + "/" + startTime.get(Calendar.YEAR) + " "
											+ startTime.get(Calendar.HOUR_OF_DAY) + ":" + startTime.get(Calendar.MINUTE)
											+ "," + endTime.get(Calendar.DAY_OF_MONTH) + "/"
											+ endTime.get(Calendar.MONTH) + "/" + endTime.get(Calendar.YEAR) + " "
											+ endTime.get(Calendar.HOUR_OF_DAY) + ":" + endTime.get(Calendar.MINUTE)
											+ "\n");
									mapIPTime.put(ip, mapIPTime.get(ip) + 1);
									startTime = endTime;
									break;
								}
							}
						} else {
							Random rd = new Random();
							Calendar tmp = endTime;
							output.write(endTime.get(Calendar.DAY_OF_MONTH) + "/" + endTime.get(Calendar.MONTH) + "/"
									+ endTime.get(Calendar.YEAR) + " " + endTime.get(Calendar.HOUR_OF_DAY) + ":"
									+ endTime.get(Calendar.MINUTE) + ",");
							tmp.setTimeInMillis(endTime.getTimeInMillis() + rd.nextInt(60 * 60000) + 60000);
							output.write(tmp.get(Calendar.DAY_OF_MONTH) + "/" + tmp.get(Calendar.MONTH) + "/"
									+ tmp.get(Calendar.YEAR) + " " + tmp.get(Calendar.HOUR_OF_DAY) + ":"
									+ tmp.get(Calendar.MINUTE) + "\n");
							startTime = endTime;
							endTime = tmp;
						}

					}
					totalIP.forEach(ip -> ip.resetTime());
					mapIPTime.forEach((k, v) -> mapIPTime.put(k, 0));
					n++;
				}
				output.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("A file with this name already exist");
		}

	}

	private static boolean isAnInterleavingEl(String s) {
		for (InterleavingPath ip : totalIP) {
			if (ip.getAlphabet().contains(s))
				return true;
		}
		return false;
	}

}
