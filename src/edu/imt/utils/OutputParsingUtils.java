package edu.imt.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import edu.imt.Applyparout.ApplyParout;
import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;
import edu.imt.specification.Mcrl2;
import edu.imt.specification.operators.Activity;
import edu.imt.specification.operators.Operator;
import edu.imt.specification.operators.Process;

public class OutputParsingUtils {

	private static Map<String, List<String>> loopProcesses;
	// Index of the last loop process generated;
	private static int indexLP = 1;
	private static Mcrl2 spec;
	protected static String t = "t";
	private static String sgttofsm = "sgfsm";
	private static String sgttomcrl2 = "sgmcrl";
	private static int sindex = 0;

	public static String getInfoLog(EventLog log) {
		return "#Trace= " + log.getTraceCardinality() + " #Event= " + log.getEventCardinality();
	}

	public static void cutLogEvents(File filelog, File cutlog, int percetage) {
		EventLog log;
		try {
			log = InputParsingUtils.parseXes(filelog);
			int sizetodelete = (log.getEventCardinality() * percetage) / 100;
			Set<Event> delete = new HashSet<Event>(sizetodelete);
			int i = 0;
			for (Event e : log.geAlphabet()) {
				if (i < sizetodelete) {
					delete.add(e);
					i++;
				} else
					break;
			}
			System.out.println(delete.toString());
			BufferedReader output = new BufferedReader(new FileReader(filelog));

			BufferedWriter writer = new BufferedWriter(new FileWriter(cutlog));
			String st;
			String trace = "";
			String event = "";
			boolean intotrace = false;
			boolean intoevent = false;
			boolean trash = false;
			while ((st = output.readLine()) != null) {
				if (st.contains("<trace>")) {
					intotrace = true;
				} else if (st.contains("</trace>")) {
					intotrace = false;
					if (!trace.isEmpty()) {
						writer.write("<trace> \n " + trace + " \n</trace> \n");
					}
					trace = "";

				} else if (st.contains("<event>")) {
					intoevent = true;
					event = st;
				} else if (st.contains("</event>")) {
					if (!trash)
						trace = trace + "\n" + event + "\n" + st;
					trash = false;
					intoevent = false;
					event = "";
				} else if (intoevent) {
					if (st.contains("concept:name")) {
						Pattern p = Pattern.compile("value=\"(.)*\"");
						Matcher m = p.matcher(st);
						if (m.find()) {
							String[] group = m.group(0).replace("\"", "").trim().split("=");
							if (delete.contains(new Event(Utils.removeblankspaceAndDots(group[1]))))
								trash = true;
						}

					}
					event = event + "\n" + st;
				} else if (!intotrace && !intoevent) {
					writer.write(st + "\n");
				}

			}
			writer.flush();
			writer.close();
			output.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Generate a .fsm file from a coverability graph
	public static String generateFSMfileFromCG(CoverabilityGraph cg, String f) {
		Map<String, String> listofnode = new HashMap<String, String>();
		List<String> nodelist = cg.getOrderedNode();
		for (int i = 0; i < nodelist.size(); i++)
			listofnode.put(nodelist.get(i), String.valueOf(++sindex));

		File file = new File(f.replace(Terminal.dotsg, "") + sgttofsm + Terminal.dotfsm);
		while (file.exists())
			file = new File(f.replace(Terminal.dotsg, "") + sgttofsm + indexLP++ + Terminal.dotfsm);
		try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
			output.write("--- \n");
			for (String s : listofnode.values())
				output.write(s + "\n");
			output.write("--- \n");
			for (Triple<String, String, String> triple : cg.getTriple()) {
				output.write(listofnode.get(triple.getLeft()) + " " + listofnode.get(triple.getMiddle()) + " " + "\""
						+ triple.getRight() + "\"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sindex = 0;
		return file.getAbsolutePath();
	}

	public static String generatemCRL2fromCG(CoverabilityGraph cg, File f) {
		List<String> listnode = cg.getOrderedNode();
		Mcrl2 spec = new Mcrl2();
		for (String node : listnode) {
			Set<Pair<String, String>> futureproc = cg.getPairWithInputNode(node);
			String processspec = "";
			int i = 0;
			for (Pair<String, String> pair : futureproc) {
				if (!cg.isFinalState(pair.getLeft()))
					processspec = processspec + pair.getRight() + Operator.SEQUENCE.getOperator() + pair.getLeft();
				else
					processspec = processspec + pair.getRight();
				Activity a = new Activity(pair.getRight());
				if (!a.equals(Mcrl2.silectAction))
					spec.addActSet(new Activity(pair.getRight()));
				if (i != futureproc.size() - 1)
					processspec = processspec + Operator.CHOICE.getOperator();
				i++;
			}
			if (!processspec.isEmpty())
				spec.addProcSpec(node, processspec);
		}
		spec.setStartingProcess(cg.getInitialMarking());
		return generateMcrl2File(f.getAbsolutePath().replace(Terminal.dotsg, "") + sgttomcrl2, spec);
	}

	/*
	 * Change the way in which we give the name of the starting process in the
	 * output of a file mcrl2
	 */
	protected static String generateMcrl2File(String fileName, Mcrl2 spec) {
		int index = 0;
		File file = new File(fileName + Terminal.dotmcrl2);
		while (file.exists()) {
			file = new File(fileName + index + Terminal.dotmcrl2);
			index++;
		}
		try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
			output.write("act\n");
			int i = 0;
			for (Activity act : spec.getActSet()) {
				if (i != (spec.getActSet().size() - 1))
					output.write(act.getName() + ",");
				else
					output.write(act.getName());
				i++;
			}
			i = 0;
			output.write(";\n" + "proc\n");
			for (Entry<String, String> entry : spec.getProcspec().entrySet()) {
				output.write(entry.getKey() + "=" + entry.getValue() + ";\n");
			}
			if (loopProcesses != null && !loopProcesses.isEmpty()) {
				for (java.util.Map.Entry<String, List<String>> entry : loopProcesses.entrySet()) {
					output.write(entry.getKey() + "=");
					for (int j = 0; j < entry.getValue().size(); j++) {
						output.write(entry.getValue().get(j));
					}
					output.write(";\n");
				}
			}
			output.write("init ");
			int par = 0;
			if (spec.getHideAction() != null && !spec.getHideAction().isEmpty()) {
				output.write("hide({");
				for (Activity hide : spec.getHideAction()) {
					if (i != (spec.getHideAction().size() - 1))
						output.write(hide.getName() + ",");
					else
						output.write(hide.getName());
					i++;
				}
				output.write("},");
				par++;
			}

			i = 0;
			if (spec.getAllowedAction() != null && !spec.getAllowedAction().isEmpty()) {
				output.write("allow({");
				for (Activity allow : spec.getAllowedAction()) {
					if (i != (spec.getAllowedAction().size() - 1))
						output.write(allow.getName() + ",");
					else
						output.write(allow.getName());
					i++;
				}
				output.write("},");
				par++;
			}
			i = 0;
			if (spec.getCommFunction() != null && !spec.getCommFunction().isEmpty()) {
				output.write("comm({");
				for (Entry<Activity[], Activity> entry : spec.getCommFunction().entrySet()) {
					for (int j = 0; j < entry.getKey().length; j++) {
						output.write(entry.getKey()[j].getName());
						if (j != entry.getKey().length - 1)
							output.write("|");
					}

					output.write("->" + entry.getValue().getName());
					if (i != spec.getCommFunction().size() - 1)
						output.write(",");
					i++;

				}
				output.write("},");
				par++;
			}

			output.write(spec.getStartingProcess());
			for (int j = 0; j < par; j++) {
				output.write(")");
			}
			output.write(";");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(file.getName() + " GENERATED!");
		return file.getAbsolutePath();
	}

	/*
	 * Given a process generate a specification written in a file.mcrl2
	 */
	public static Mcrl2 generateMCRL2FromBlockStrcut(Process process) {
		loopProcesses = new HashMap<String, List<String>>();
		loopname = new HashMap<List<String>, String>();
		spec = new Mcrl2();
		Process pparout = ApplyParout.applyparout(process, spec);

		// ---- Generate process in a line
		List<List<String>> toBeEmpty = new ArrayList<>();
		List<String> result = new ArrayList<String>();
		enterInTheProcess(pparout, result, toBeEmpty);
		adjustCommFunc();
		// ----
		spec.addStartingProcess(Utils.getName());
		String r = "";
		for (int i = 0; i < result.size(); i++) {
			r = r.concat(result.get(i));
		}
		spec.addProcSpec(spec.getStartingProcess(), r);
		return spec;
	}

	private static void adjustCommFunc() {
		Map<Activity[], Activity> replace = new HashMap<Activity[], Activity>();
		for (Entry<Activity[], Activity> fun : spec.getCommFunction().entrySet()) {
			if (spec.getActSet().contains(fun.getKey()[0]))
				replace.put(fun.getKey(), fun.getValue());
		}
		spec.setCommFunction(replace);
	}

	private static Map<List<String>, String> loopname;

	private static void enterInTheProcess(Process p, List<String> linearProcessModel,
			List<List<String>> parallelProcess) {
		// System.out.println(p.toString());
		if (p.getActivity() != null) {
			linearProcessModel.add(p.getActivity().getName());
			spec.addActSet(p.getActivity());
			if (!spec.getAllowedAction().isEmpty() && !spec.isKeyCommFunction(p.getActivity()))
				spec.addAllowedAction(p.getActivity());
			return;
		} else {
			linearProcessModel.add("(");
			for (int i = 0; i < p.size(); i++) {
				if (p.getOp().equals(Operator.LOOP)) {
					List<String> loop = new ArrayList<String>();
					enterInTheProcess(p.getProcess()[i], loop, parallelProcess);
					String index = "";
					List<String> loopspec = new ArrayList<String>();
					if (!loopname.containsKey(loop)) {
						index = "S" + indexLP;
						loopname.put(Lists.newArrayList(loop), index);
						loopspec.addAll(loop);
						loopspec.add(Operator.SEQUENCE.getOperator() + index + Operator.CHOICE.getOperator());
						// loopspec.add("tau");
						loopspec.addAll(loop);
						loopProcesses.put(index, loopspec);

					} else
						index = loopname.get(loop);
					linearProcessModel.add(index);
					linearProcessModel.add(")");
					indexLP++;
					return;
				}
				if (p.getProcess() != null) {
					enterInTheProcess(p.getProcess()[i], linearProcessModel, parallelProcess);
				}

				if (i != p.size() - 1) {
					linearProcessModel.add(String.valueOf(p.getOp().getOperator()));
					if (p.getOp().equals(Operator.PARALLEL))
						linearProcessModel.add(String.valueOf(p.getOp().getOperator()));
				}
			}
			linearProcessModel.add(")");
		}
	}

	public static void mergeMCRL2(List<Mcrl2> mcrl2list, List<String> filemcrl2) {
		Mcrl2 unicspec = new Mcrl2();
		mcrl2list.forEach(l -> {
			unicspec.addActSet(l.getActSet());
			if (l.getAllowedAction().isEmpty())
				unicspec.addAllowedAction(l.getActSet());
			else
				unicspec.addAllowedAction(l.getAllowedAction());
			unicspec.addHideAction(l.getHideAction());
			unicspec.addCommFunction(l.getCommFunction());
			unicspec.addStartingProcess(l.getStartingProcess());
			unicspec.addMessage(l.getMessage());
			unicspec.addProcSpec(l.getProcspec());
		});

		unicspec.getMessage().asMap().forEach((k, v) -> {
			Activity[] a = new Activity[v.size()];
			unicspec.addCommFunction(v.toArray(a), k);
			unicspec.addActSet(k);
			unicspec.addAllowedAction(k);
			unicspec.removedAllowedAction(a);
		});

		String name = "C:/Users/sara/eclipse-workspace/PALM-r/unionmcrl2" + Terminal.dotmcrl2;
		generateMcrl2File(name, unicspec);

	}

	/*
	 * private static void mergeSetSpecifications(Mcrl2 spec1, Mcrl2 spec2, Mcrl2
	 * unionSpec) {
	 * 
	 * unionSpec.setAllowedAction(spec1.getAllowedAction());
	 * unionSpec.setAllowedAction(spec2.getAllowedAction());
	 * 
	 * unionSpec.setHideAction(spec1.getHideAction());
	 * unionSpec.setHideAction(spec2.getHideAction());
	 * 
	 * unionSpec.setCommFunction(spec1.getCommFunction());
	 * unionSpec.addCommFunction(spec2.getCommFunction());
	 * 
	 * unionSpec.setActSet(spec1.getActSet());
	 * unionSpec.setActSet(spec2.getActSet());
	 * 
	 * /* Change the name of each process for each specification s.t all the
	 * processes are added to the new one
	 */
	/*
	 * int indexReplace = 0; indexReplace = changeNameProcessInSpecification(spec1,
	 * indexReplace); changeNameProcessInSpecification(spec2, indexReplace);
	 * 
	 * Map<String, String> pp = new HashMap<String, String>();
	 * pp.putAll(spec1.getProcspec()); pp.putAll(spec2.getProcspec());
	 * unionSpec.setProcspec(pp); }
	 */

	/*
	 * Return the lastIndex inserted in the name of the process
	 */
	/*
	 * private static int changeNameProcessInSpecification(Mcrl2 oldSpec, int i) {
	 * Map<String, String> processNewName = new HashMap<String, String>();
	 * 
	 * for (String k1 : oldSpec.getProcspec().keySet()) { if
	 * (oldSpec.getStartingProcess().contains(k1))
	 * oldSpec.setStartingProcess(oldSpec.getStartingProcess().replace(k1,
	 * k1.concat(String.valueOf(i))));
	 * 
	 * processNewName.put(k1.concat(String.valueOf(i)),
	 * oldSpec.getProcspec().get(k1)); i++; } oldSpec.setProcspec(processNewName);
	 * return i; }
	 */

	/*
	 * Generate the merge of two mclr2 specification taking into account that : if
	 * two activity have the same name => this activity communicates NO NEED TO
	 * EXECUTE A DISCOVERY ALGORITHM
	 */
	/*
	 * public static boolean mergeSpecMCRL2SameActvityNameComm(File fileName1, File
	 * fileName2, String outputFile) { Mcrl2 spec1 = new Mcrl2(fileName1); Mcrl2
	 * spec2 = new Mcrl2(fileName2); Mcrl2 unionSpec = new Mcrl2();
	 * mergeSetSpecifications(spec1, spec2, unionSpec);
	 * 
	 * // To see if there are elements that communicate Set<Activity>
	 * intersectionAlphabet = (Set<Activity>) intersection(spec1.getActSet(),
	 * spec2.getActSet());
	 * 
	 * if (!unionSpec.getAllowedAction().isEmpty()) intersectionAlphabet =
	 * (Set<Activity>) intersection(intersectionAlphabet,
	 * unionSpec.getAllowedAction()); if (!unionSpec.getHideAction().isEmpty())
	 * intersectionAlphabet.removeAll(unionSpec.getHideAction()); /* If the set is
	 * NOT EMPTY => rename all each element in the set in his traces with a
	 * temporary name and add a communication function
	 */
	/*
	 * int indexRenaming = 0; String r = "r";
	 * 
	 * for (Activity communication : intersectionAlphabet) { for (Entry<String,
	 * String> entry : unionSpec.getProcspec().entrySet()) { if
	 * (entry.getValue().contains(communication.getName())) {
	 * unionSpec.getProcspec().put(entry.getKey(),
	 * entry.getValue().replace(communication.getName(), r + indexRenaming)); } }
	 * Activity[] activityComm = new Activity[2]; activityComm[0] = new Activity(r +
	 * indexRenaming); activityComm[1] = new Activity(r + indexRenaming);
	 * unionSpec.getCommFunction().put(activityComm, communication); if
	 * (unionSpec.getAllowedAction().isEmpty()) { for (Activity act :
	 * unionSpec.getActSet()) { unionSpec.addAllowedAction(act); } }
	 * unionSpec.addActSet(new Activity(r + indexRenaming)); indexRenaming++; }
	 * unionSpec.setStartingProcess(new String[] { spec1.getStartingProcess(),
	 * spec2.getStartingProcess() });
	 * 
	 * String namemcrl2file = generateMcrl2File(outputFile, unionSpec); if
	 * (namemcrl2file != null) return true; else return false;
	 * 
	 * }
	 */

	/*
	 * private static <E> Collection<E> intersection(Collection<E> collection1,
	 * Collection<E> collection2) { Collection<E> collection = new HashSet<E>(); for
	 * (E e : collection1) { if (collection2.contains(e)) collection.add(e); }
	 * return collection; }
	 */

	/*
	 * Remove the event with <string key="lifecycle:transition" value="start"/> from
	 * the input xeslog output : xeslog
	 */
	public static void generateCopyXesFileWithotStartTime(File f, File copy) {
		try {
			BufferedReader output = new BufferedReader(new FileReader(f));
			BufferedWriter writer = new BufferedWriter(new FileWriter(copy));
			String st;
			boolean check = false;
			boolean removest = false;
			String tmp = "";
			while ((st = output.readLine()) != null) {
				if (st.contains("<event>"))
					check = true;
				if (!check)
					writer.write(st + "\n");
				if (check) {
					tmp = tmp + st + "\n";
					if (st.contains("value=\"start\""))
						removest = true;
					else if (st.contains("</event>")) {
						if (!removest)
							writer.write(tmp);
						tmp = "";
						check = false;
						removest = false;
					}
				}
			}
			writer.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void generateCopyXesMessageAttribute(File f, Map<String, String> map) {
		String copyname = f.getPath().replace(Terminal.dotxes, "+m" + Terminal.dotxes);
		File copy = new File(copyname);
		try {
			BufferedReader output = new BufferedReader(new FileReader(f));
			BufferedWriter writer = new BufferedWriter(new FileWriter(copy));
			String st;
			boolean check = false;
			while ((st = output.readLine()) != null) {
				writer.write(st + "\n");
				if (st.contains("<event>"))
					check = true;
				if (check) {
					String s;
					if (st.contains("key=\"concept:name\"") && ((s = checkEventName(map, st)) != null)) {
						writer.write("\t \t \t<string key=\"message\" value=\"" + map.get(s) + "\"/> \n");
						check = !check;
					}
				}
			}
			writer.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String checkEventName(Map<String, String> map, String st) {
		for (String s : map.keySet()) {
			if (st.contains("value=\"" + s + "\""))
				return s;
		}
		return null;
	}

	public static void cutHaldXes(File f, File copy, int size) {
		try {
			BufferedReader output = new BufferedReader(new FileReader(f));
			BufferedWriter writer = new BufferedWriter(new FileWriter(copy));
			String st;
			long sizehalf = size / 2;
			long half = 0;
			boolean startclosing = false;
			while ((st = output.readLine()) != null) {
				if (half > sizehalf) {
					startclosing = true;
				}
				writer.write(st + "\n");
				if (startclosing && st.contains("</trace>")) {
					writer.write(st + "\n");
					break;
				}
				half++;
			}
			writer.write("</log>");
			writer.flush();
			writer.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void generateCSVFile(File f, List<List<String>> rows, boolean starttime) {
		try {
			FileWriter csvWriter = new FileWriter(f);
			csvWriter.append("Case");
			csvWriter.append(",");
			csvWriter.append("Activity");
			csvWriter.append(",");
			if (starttime) {
				csvWriter.append("Start timestamp");
				csvWriter.append(",");
			}
			csvWriter.append("End timestamp");
			csvWriter.append("\n");

			for (List<String> rowData : rows) {
				csvWriter.append(String.join(",", rowData));
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
