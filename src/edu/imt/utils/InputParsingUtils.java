package edu.imt.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;
import edu.imt.inputData.Trace;
import edu.imt.specification.MCRL2;

/*
 * This class contains utilities to read input files like .sg, .xes and .xes with messages
 * @author Sara Belluccini
 */
public class InputParsingUtils {

	public static final String lct = "lifecycle:transition";
	public static final String mes = "message";
	public static final String cn = "concept:name";
	public static final String START = "start";
	public static final String COMPLETE = "compelte";

	// It return a map contain the converability graph where Pair<String,String> are
	// the arcs and String is the label of the arc
	public static CoverabilityGraph parseSGfile(File f) {
		if (FilenameUtils.isExtension(f.getName(), FileExtension.SG.toString())) {
			System.err.println("Wrong file extension, try again using a " + FileExtension.SG.toString() + " file");
			return null;
		}
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
							cg.addTriple(split[0], split[2], MCRL2.TAU.getName());
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

	public static EventLog parseXes(File f, Map<String, String> map) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(f));
		EventLog eventlog = new EventLog();
		if (!map.isEmpty())
			map.forEach((k, v) -> eventlog.addEventToMap(k, v));
		String st;
		boolean intrace = false;
		boolean inevent = false;
		String name = "";
		List<Trace> listTrace = new ArrayList<Trace>();
		int i = 0;
		Set<Event> oldEvent = new HashSet<Event>();
		boolean changeOld = true;
		while ((st = br.readLine()) != null) {
			if (st.contains("<trace>")) {
				listTrace.add(i, new Trace());
				intrace = true;
				oldEvent = new HashSet<Event>();
				continue;
			} else if (st.contains("<event>")) {
				inevent = true;
				continue;
			} else if (st.contains("</event>")) {
				inevent = false;
				name = "";
				continue;
			} else if (st.contains("</trace>")) {
				intrace = false;
				i += 1;
				continue;
			}
			if (intrace && inevent) {
				if (name.isEmpty() && !(name = getEventName(st)).isEmpty())
					continue;
				if (getLifecycle(st).equals("complete")) {
					Event e;
					if (eventlog.isInEventMap(name)) {
						e = new Event(name, eventlog.getIdEventMap(name));
					} else {
						e = new Event(name);
						eventlog.addEventToMap(name, e.getName());
					}
					listTrace.get(i).add(e);
					if (changeOld || oldEvent.isEmpty()) {
						oldEvent = Sets.newHashSet(e);
						changeOld = false;
					} else
						oldEvent.add(e);
				} else if (getLifecycle(st).equals("start")) {
					if (oldEvent != null) {
						Event currentEvent;
						if (eventlog.isInEventMap(name)) {
							currentEvent = new Event(name, eventlog.getIdEventMap(name));
						} else {
							currentEvent = new Event(name);
							eventlog.addEventToMap(name, currentEvent.getName());
						}
						for (Event e : oldEvent)
							listTrace.get(i).addPostHBRelation(currentEvent, e);
						changeOld = true;
					}
				}
			}
		}
		br.close();
		eventlog.addAll(listTrace);
		return eventlog;
	}

	private static String getEventName(String s) {
		String name = "";
		if (s.contains("key=\"concept:name\"")) {
			return StringUtils.substringBetween(s, "value=\"", "\"");
		}
		return name;
	}

	private static String getLifecycle(String s) {
		String name = "";
		if (s.contains("key=\"lifecycle:transition\"")) {
			return StringUtils.substringBetween(s, "value=\"", "\"");
		}
		return name;
	}

	private static String getMessage(String s) {
		String name = "";
		if (s.contains("key=\"message\""))
			return StringUtils.substringBetween(s, "value=\"", "\"");
		return name;
	}

	public static Multimap<String, Event> parseXesMessage(File f, Map<String, String> mapEventName) {
		Multimap<String, Event> multimap = HashMultimap.create();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String st;
			boolean inevent = false;
			String name = "";
			while ((st = br.readLine()) != null) {
				if (st.contains("<event>")) {
					inevent = true;
					continue;
				} else if (st.contains("</event>")) {
					inevent = false;
					name = "";
					continue;
				}
				if (inevent) {
					if (name.isEmpty() && !(name = getEventName(st)).isEmpty())
						continue;
					String message;
					if (!(message = getMessage(st)).isEmpty()) {
						multimap.put(message, new Event(name, mapEventName.get(name)));
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e = new FileNotFoundException();
		}
		return multimap;
	}

}
