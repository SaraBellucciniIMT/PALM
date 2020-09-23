package edu.imt.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.javatuples.Quartet;
import edu.imt.Applyparout.ApplyParout;
import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;
import edu.imt.specification.MCRL2;
import edu.imt.specification.structure.Operator;
import edu.imt.specification.structure.BlockStructure;

public class OutputParsingUtils {

	private static final String MODEL = "model";
	private static final String FORMULA = "f";

	public static void cutLogEvents(File filelog, File cutlog, int percetage) {
		EventLog log;
		try {
			log = InputParsingUtils.parseXes(filelog, new HashMap<>());
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
			e = new FileNotFoundException();
		}
	}

	public static String generatemCRL2fromCG(CoverabilityGraph cg, File f, Map<String, String> map) {
		List<String> listnode = cg.getOrderedNode();
		MCRL2 spec = new MCRL2();
		for (String node : listnode) {
			Set<Pair<String, String>> futureproc = cg.getPairWithInputNode(node);
			String processspec = "";
			int i = 0;
			for (Pair<String, String> pair : futureproc) {
				Event e;
				if (pair.getRight().equals(MCRL2.TAU.getName())) {
					e = MCRL2.TAU;
				} else if (map.containsKey(pair.getRight())) {
					e = new Event(pair.getRight(), map.get(pair.getRight()));
					spec.addActSet(e);
				} else {
					e = new Event(pair.getRight());
					spec.addActSet(e);
				}

				if (!cg.isFinalState(pair.getLeft()))
					processspec = processspec + e.getName() + Operator.SEQUENCE.getOperator() + pair.getLeft();
				else
					processspec = processspec + e.getName();

				if (i != futureproc.size() - 1)
					processspec = processspec + Operator.CHOICE.getOperator();
				i++;
			}
			if (!processspec.isEmpty())
				spec.addProcSpec(node, processspec);
		}
		spec.addInitSet(cg.getInitialMarking());
		return generateMcrl2File(spec);
	}

	/**
	 * Print out the mcrl2 object into a .mcrl2 file
	 * 
	 * @param fileName
	 * @param spec
	 * @return
	 */
	protected static String generateMcrl2File(MCRL2 spec) {
		int index = 0;
		File file;
		do {
			file = new File(Terminal.path + MODEL + index + "." + FileExtension.MCRL2.getExtension());
			index++;
		} while (file.exists());
		try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
			output.write("act\n");
			int i = 0;
			for (Event act : spec.getActSet()) {
				if (i != (spec.getActSet().size() - 1))
					output.write(act.getName() + ",");
				else
					output.write(act.getName());
				i++;
			}
			i = 0;
			output.write(";\n" + "proc\n");
			for (Entry<String, String> entry : spec.getProcspec().entrySet())
				output.write(entry.getKey() + "=" + entry.getValue() + ";\n");
			output.write("init ");
			int par = 0;
			if (spec.getHideAction() != null && !spec.getHideAction().isEmpty()) {
				output.write("hide({");
				for (Event hide : spec.getHideAction()) {
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
				for (Event allow : spec.getAllowedAction()) {
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
				for (Entry<Event[], Event> entry : spec.getCommFunction().entrySet()) {
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

			int k = 0;
			for (String s : spec.getInitSet()) {
				output.write(s);
				if (k != spec.getInitSet().size() - 1)
					output.write(Operator.PARALLEL.getOperator());
				k++;
			}
			for (int j = 0; j < par; j++) {
				output.write(")");
			}
			output.write(";");
		} catch (IOException e) {
			e = new IOException();
		}

		System.out.println(file.getName() + " GENERATED!");
		return file.getAbsolutePath();
	}

	/**
	 * Transformation of the block structure into a mCRL2 specification. This method
	 * use the Tp function to transform a block structure B into a block structure
	 * that describes a mCRL2 specification that respect the pCRL format. Then the T
	 * function , T: B -> (P x P(D)) to obtain the real specification
	 * 
	 * @param process
	 * @return
	 */
	public static MCRL2 generateMCRL2FromBlockStrcut(BlockStructure b, int threshold) {
		MCRL2 spec = new MCRL2();
		Quartet<BlockStructure, Map<Event[], Event>, Set<Event>, Set<Event>> pparout = ApplyParout.applyTp(b);

		// Adds all the value of the communication function and its result as actions of
		// the specification
		pparout.getValue1().forEach((k, v) -> {
			for (int i = 0; i < k.length; i++)
				spec.addActSet(k[i]);
			spec.addActSet(v);
		});
		spec.addCommFunction(pparout.getValue1());
		spec.addAllowedAction(pparout.getValue2());
		spec.addHideAction(pparout.getValue3());
		spec.fromBlockStrucutureToMcrl2Processes(pparout.getValue0(), threshold);

		return spec;
	}

	public static String generateMcfFile(String s) {
		File file;
		int index = 0;
		do {
			file = new File(Terminal.path + FORMULA + index + "." + FileExtension.MCF.getExtension());
			index++;
		} while (file.exists());
		try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
			output.write(s);
			output.flush();
			output.close();
		} catch (Exception e) {
			e = new IOException();
		}
		return file.getName();
	}

	/**
	 * Puts all the MCRL2 object all togheter and generate a unique mcrl2
	 * specification.
	 * 
	 * @param mcrl2list list of mcrl2 object to unify
	 * @return the name of the file generated
	 */
	public static String mergeMCRL2(List<MCRL2> mcrl2list) {
		MCRL2 unicspec = new MCRL2();
		mcrl2list.forEach(l -> {
			unicspec.addActSet(l.getActSet());
			if (l.getAllowedAction().isEmpty())
				unicspec.addAllowedAction(l.getActSet());
			else
				unicspec.addAllowedAction(l.getAllowedAction());
			unicspec.addHideAction(l.getHideAction());
			unicspec.addCommFunction(l.getCommFunction());
			unicspec.addInitSet(l.getInitSet());
			unicspec.appendMessage(l.getMessage());
			unicspec.addProcSpec(l.getProcspec());
		});
		for (Entry<Event, Collection<Event>> m : unicspec.getMessage().asMap().entrySet()) {
			Event[] a = new Event[m.getValue().size()];
			unicspec.addCommFunction(m.getValue().toArray(a), m.getKey());
			unicspec.addActSet(m.getKey());
			unicspec.addAllowedAction(m.getKey());
			for (Event e : a)
				unicspec.removedAllowedAction(e);
		}
		return generateMcrl2File(unicspec);
	}

	/*
	 * Remove the event with <string key="lifecycle:transition" value="start"/> from
	 * the input xeslog output : xeslog
	 */
	/**
	 * Generates the copy of this xes file while removing the event with string key="lifecycle:transition" value="start"
	 * @param f the xes file 
	 * @param copy the copy without start evetns
	 */
	public static void generateCopyXesFileWithoutStartTime(File f, File copy) {
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
			e = new FileNotFoundException();
		}

	}

	/**
	 * To generate a xes copy adding message among some of its events
	 * 
	 * @param f   the xes file
	 * @param map key=event value = message
	 */
	public static void generateCopyXesMessageAttribute(File f, Map<String, String> map) {
		String copyname = f.getPath().replace(FileExtension.XES.getExtension(),
				"+m" + FileExtension.XES.getExtension());
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
			e =  new FileNotFoundException();
		}
	}

	private static String checkEventName(Map<String, String> map, String st) {
		for (String s : map.keySet()) {
			if (st.contains("value=\"" + s + "\""))
				return s;
		}
		return null;
	}
}
