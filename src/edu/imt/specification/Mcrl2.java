package edu.imt.specification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import edu.imt.specification.operators.Activity;
import edu.imt.specification.operators.Operator;
import edu.imt.utils.InputParsingUtils;
import edu.imt.utils.Utils;

/*
 * This is the class that represent a MCRL2 specification: its structure is the same as in a .mcrl2 file
 */
public class Mcrl2 {

	private Set<Activity> actSet;
	// Map of all the specification processes : name = specification
	private Map<String, String> procspec;
	// Set of all the action in the allowed set
	private Set<Activity> allowedAction;
	// This map says which is the product between two variabile i.e. "t0|t0->t1"
	private Map<Activity[], Activity> commFunction;
	// Set of all the action in the hide set
	private Set<Activity> hideAction;
	private String startingProcess;
	private Set<Activity> traces;
	public static Activity silectAction = new Activity("tau");
	private Multimap<Activity,Activity> messages ;

	public Mcrl2() {
		initializeBasicSet();
	}

	/*
	 * Create an object Mcrl2 from its specification file "example.mcrl2"
	 */
	public Mcrl2(File fileName) {
		initializeBasicSet();
		try {
			parseMCRL2File(fileName);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void initializeBasicSet() {
		this.actSet = new HashSet<Activity>();
		this.procspec = new HashMap<String, String>();
		this.allowedAction = new HashSet<Activity>();
		this.commFunction = new HashMap<Activity[], Activity>();
		this.hideAction = new HashSet<Activity>();
		this.startingProcess = "";
		this.traces = new HashSet<Activity>();
		this.messages = HashMultimap.create();
	}

	public Set<Activity> getActSet() {
		return this.actSet;
	}

	public void setActSet(Set<Activity> set) {
		this.actSet.addAll(set);
	}

	public void addActSet(Activity a) {
		if (Activity.multiaction.matcher(a.getName()).matches()) {
			for (String t : a.getActivity()) {
				if (!t.equals(Operator.PARALLEL.getStringOp()))
					this.actSet.add(new Activity(t));
			}
		} else
			this.actSet.add(a);
	}

	public void addActSet(Set<Activity> set) {
		this.actSet.addAll(set);
	}

	/*
	 * Compute all the traces for this specification
	 */
	public Set<Activity> getTraces() {
		if (traces.isEmpty()) {
			String linearProcess = InputParsingUtils.linearizeProcessWithKUnfolding(procspec.get(startingProcess),
					procspec);
			traces = InputParsingUtils.goDeep(linearProcess, this);
		}
		return traces;
	}

	/*
	 * Generate a map that contain Key: name of the process , Value: definition of
	 * it Ex => (SO,a+b) key = S0 value = a+b
	 */
	public void parseMCRL2File(File fileName) throws Exception {
		// File f = new File(fileName);
		FileReader file = new FileReader(fileName);
		BufferedReader b = new BufferedReader(file);
		String currentLine;
		boolean analyzingTheProc = false;
		Pattern act = Pattern.compile("act");
		Pattern proc = Pattern.compile("proc");
		Pattern init = Pattern.compile("init");
		while ((currentLine = b.readLine()) != null) {
			if (!act.matcher(currentLine).matches() && !analyzingTheProc && !proc.matcher(currentLine).matches()) {
				String[] tmpAct = currentLine.split("[,;]");
				for (String s : tmpAct) {
					actSet.add(new Activity(s));
				}
			}
			if (!analyzingTheProc) {
				if (proc.matcher(currentLine).matches()) {
					analyzingTheProc = true;
				}
				continue;
			}

			// Put the elements before the "=" as the name of the process and the part after
			// as its definition
			if (!init.matcher(currentLine).lookingAt()) {
				String[] tmpprocSpec = currentLine.split("[=;]");
				procspec.put(tmpprocSpec[0].trim(), tmpprocSpec[1].trim());
			} else {
				String remainedString = StringUtils.remove(currentLine, "(");
				remainedString = StringUtils.remove(remainedString, ")");
				if (remainedString.contains("comm")) {
					String[] tmpprocSpec = remainedString.split("comm");
					String[] noParenthesis = StringUtils.split(tmpprocSpec[1], "[{}]");
					String[] tmp = noParenthesis[0].split(",");
					for (int i = 0; i < tmp.length; i++) {
						String[] input = StringUtils.split(tmp[i], "->");
						String[] inputelements = StringUtils.split(input[0], "|");
						Activity[] activities = new Activity[inputelements.length];
						int a = 0;
						for (String s : inputelements) {
							activities[a] = new Activity(s);
							a++;
						}

						commFunction.put(activities, new Activity(input[1]));
					}
					if (startingProcess.isEmpty())
						setStartingProcess(noParenthesis[1]);
					remainedString = "";
					for (int i = 0; i < tmpprocSpec[0].length(); i++)
						remainedString = remainedString.concat(String.valueOf(tmpprocSpec[0].charAt(i)));
				}
				if (remainedString.contains("allow")) {
					String[] tmpprocSpec = remainedString.split("allow");
					String[] noParenthesis = StringUtils.split(tmpprocSpec[1], "[{}]");
					String[] tmp = noParenthesis[0].split(",");
					for (int i = 0; i < tmp.length; i++) {
						if (!tmp[i].isEmpty())
							allowedAction.add(new Activity(tmp[i]));
					}
					if (startingProcess.isEmpty())
						setStartingProcess(noParenthesis);
					remainedString = "";
					for (int i = 0; i < tmpprocSpec[0].length(); i++)
						remainedString = remainedString.concat(String.valueOf(tmpprocSpec[0].charAt(i)));

				}

				if (remainedString.contains("hide")) {
					String[] tmpprocSpec = remainedString.split("hide");
					String[] noParenthesis = StringUtils.split(tmpprocSpec[1], "[{}]");
					String[] tmp = noParenthesis[0].split(",");
					for (int i = 0; i < tmp.length; i++)
						hideAction.add(new Activity(tmp[i]));
					if (startingProcess.isEmpty())
						setStartingProcess(noParenthesis);
					remainedString = "";
					for (int i = 0; i < tmpprocSpec[0].length(); i++)
						remainedString = remainedString.concat(String.valueOf(tmpprocSpec[0].charAt(i)));
				}
				if (startingProcess.isEmpty())
					startingProcess = StringUtils.remove(remainedString, "init").split(";")[0].trim();

			}

		}
		b.close();
	}

	public Map<String, String> getProcspec() {
		return procspec;
	}


	public void addProcSpec(String nameProc, String defProc) {
		this.procspec.put(nameProc, defProc);
	}

	public void addProcSpec(Map<String,String> proc) {

		this.procspec.putAll(proc);
		
	}

	public Set<Activity> getAllowedAction() {
		return allowedAction;
	}

	public void setAllowedAction(Set<Activity> allowedAction) {
		this.allowedAction.addAll(allowedAction);
	}

	public void addAllowedAction(Activity action) {
		if (!this.allowedAction.contains(action))
			this.allowedAction.add(action);
	}

	public void addAllowedAction(Set<Activity> set) {
		this.allowedAction.addAll(set);
	}

	public void removedAllowedAction(Activity... a) {
		for(Activity act : a)
			this.allowedAction.remove(act);
	}
	public Map<Activity[], Activity> getCommFunction() {
		return commFunction;
	}

	public Activity getCommFunctionElement(Activity[] arr) {
		return commFunction.get(arr);
	}

	public boolean isKeyCommFunction(Activity a) {
		for (Activity[] key : commFunction.keySet()) {
			if (ArrayUtils.contains(key, a)) {
				return true;
			}
		}
		return false;
	}

	public void addCommFunction(Map<Activity[], Activity> commFunction) {
		this.commFunction.putAll(commFunction);
	}

	public void setCommFunction(Map<Activity[], Activity> commFunction) {
		this.commFunction.clear();
		this.commFunction.putAll(commFunction);
	}

	public void addCommFunction(Activity[] inputf, Activity outputf) {
		if (!(this.commFunction.containsKey(inputf) && this.commFunction.get(inputf).equals(outputf)))
			this.commFunction.put(inputf, outputf);
	}

	public Set<Activity> getHideAction() {
		return hideAction;
	}

	public void setHideAction(Set<Activity> hideAction) {
		this.hideAction.addAll(hideAction);
	}

	public void addHideAction(Activity action) {
		this.hideAction.add(action);
	}

	public Multimap<Activity,Activity> getMessage() {
		return this.messages;
	}
	public void addHideAction(Set<Activity> set) {
		this.hideAction.addAll(set);
	}

	public void addMessage(Activity message, Activity task) {
		messages.put(message, task);
	}
	
	public void addMessage(Multimap<Activity,Activity> map) {
		this.messages.putAll(map);
	}
	public String getStartingProcess() {
		return startingProcess;
	}

	public void setStartingProcess(String startingProcess) {
		startingProcess = StringUtils.remove(startingProcess, ",");
		startingProcess = StringUtils.remove(startingProcess, ";");
		startingProcess = startingProcess.trim();
		this.startingProcess = startingProcess;
	}

	public void addStartingProcess(String p) {
		if (!startingProcess.isEmpty())
			this.startingProcess = startingProcess + Operator.PARALLEL.getOperator() + Operator.PARALLEL.getOperator() + p;
		else
			this.startingProcess = p;
	}

	public void setStartingProcess(String... strings) {
		if (strings.length > 1) {
			for (int i = 0; i < (strings.length - 1); i++) {
				startingProcess = startingProcess
						.concat(strings[i] + Operator.PARALLEL.getOperator() + Operator.PARALLEL.getOperator());
			}
			startingProcess = startingProcess.concat(strings[strings.length - 1]);
			String nameStart = Utils.getName();
			procspec.put(nameStart, startingProcess);
			startingProcess = nameStart;
			return;
		}
		startingProcess = strings[0];
	}

}
