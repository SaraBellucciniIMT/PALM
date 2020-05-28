package edu.imt.Applyparout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import edu.imt.specification.operators.Activity;
import edu.imt.specification.operators.Operator;
import edu.imt.specification.operators.Process;
import edu.imt.utils.InputParsingUtils;
import edu.imt.utils.InterleavingPath;

public class Loop extends AbstractParout {

	@Override
	public Process interpreter(Process p) {
		//System.out.println(p.toString());
		Process parallelProcess = null;
		for (Process process : p.getProcess()) {
			if (process.getOp() != null && process.getOp().equals(Operator.PARALLEL)) {
				parallelProcess = process;
				break;
			}
		}
		List<Activity> allinterleaving = new ArrayList<Activity>(findAllInterleaving(parallelProcess));
		Process[] tmpfather = new Process[allinterleaving.size()];
		for (int i = 0; i < allinterleaving.size(); i++) {
			List<String> a = allinterleaving.get(i).getActivity();
			Process[] tmpson = new Process[a.size()];
			for (int j = 0; j < a.size(); j++) {
				if (loopref.containsKey(a.get(j))) {
					Process ref = loopref.get(a.get(j));
					if (ref.getActivity() != null) {
						tmpson[j] = new Process(ref.getActivity());
						tmpson[j].modifyProcessOp(ref.getOp());
					} else {
						tmpson[j] = new Process(ref.getProcess(), ref.getOp());
					}
				} else
					tmpson[j] = new Process(new Activity(a.get(j)));
			}
			tmpfather[i] = new Process(tmpson, Operator.SEQUENCE);
		}
		Process father = new Process(tmpfather, Operator.CHOICE);
		if (ApplyParout.insideloop) {
			Process news = new Process();
			news.modifyProcessOp(p.getOp());
			for (int i = 0; i < p.size(); i++) {
				if (p.getProcess()[i] != parallelProcess)
					news.insertProcessAtPosition(p.getProcess()[i], i);
				else
					news.insertProcessAtPosition(father, i);
			}
			father = news;
		}

		return father;
	}

	public Set<Activity> findAllInterleaving(Process p) {
		List<Activity> l1 = InputParsingUtils.createSingleTrace(
				new Activity(InputParsingUtils.tranformStringInAnActivity(tranformInASingleString(p.getProcess()[0]))));
		List<Activity> l2 = InputParsingUtils.createSingleTrace(
				new Activity(InputParsingUtils.tranformStringInAnActivity(tranformInASingleString(p.getProcess()[1]))));

		Set<Activity> partialresult = twoList(l1, l2);
		Set<Activity> finalResult = new HashSet<Activity>();
		for (int i = 2; i < p.size(); i++) {
			List<Activity> l3 = InputParsingUtils.createSingleTrace(new Activity(
					InputParsingUtils.tranformStringInAnActivity(tranformInASingleString(p.getProcess()[i]))));
			finalResult.addAll(twoList(new ArrayList<Activity>(partialresult), l3));
			if (i != p.size() - 1) {
				partialresult = new HashSet<Activity>(finalResult);
			}
		}
		if (finalResult.isEmpty() && !partialresult.isEmpty())
			finalResult.addAll(partialresult);
		finalResult = removeTemp(finalResult);
		return finalResult;

	}

	private Set<Activity> twoList(List<Activity> l1, List<Activity> l2) {
		Set<Activity> total = new HashSet<>();
		for (int i = 0; i < l1.size(); i++) {
			int j = 0;
			while (j < l2.size()) {
				Set<Activity> tmp = new HashSet<>();
				tmp.addAll(InterleavingPath.combinaTwoString(l1.get(i), l2.get(j)));
				total.addAll(tmp);
				j++;
			}
		}
		return total;
	}

	private Map<String, Process> loopref = new HashMap<String, Process>();
	private static String ref = "S";

	/*
	 * Create a String that represent the process Whi do that? in order to give it
	 * in input to the method InputParsingUtils.createSingleTrace method
	 */
	private String tranformInASingleString(Process p) {
		if (p.getOp() != null && p.getOp().equals(Operator.LOOP)) {
			String generateref = ref + gettemporary();
			loopref.put(generateref, p);
			return generateref;
		}
		if (p.getActivity() != null)
			return p.getActivity().getName();
		String s = "";
		for (int i = 0; i < p.size(); i++) {
			s = s.concat(tranformInASingleString(p.getProcess()[i]));
			if (i != (p.size() - 1))
				s = s.concat(p.getOp().getStringOp());
		}
		return s;
	}
	
	static private Pattern temp = Pattern.compile("t\\d+");
	private Set<Activity> removeTemp(Set<Activity> result) {
		Set<Activity> setactivity = new HashSet<Activity>();
		for (Activity a : result) {
			int indexstart = 0;
			List<String> newa = new ArrayList<String>();
			for (int i = 0; i < a.size(); i++) {
				if (temp.matcher(String.valueOf(a.getActivity().get(i))).matches()) {
					newa.addAll(a.getActivity().subList(indexstart, i));
					indexstart = i + 1;
				}
			}
			if (indexstart != a.size())
				newa.addAll(a.getActivity().subList(indexstart, a.size()));
			Activity activity = new Activity(newa);
			if (!setactivity.contains(activity))
				setactivity.add(activity);
		}
		return setactivity;
	}
}
