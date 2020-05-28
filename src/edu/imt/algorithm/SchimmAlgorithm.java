package edu.imt.algorithm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import edu.imt.inputData.Case;
import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;
import edu.imt.specification.operators.Activity;
import edu.imt.specification.operators.Operator;
import edu.imt.specification.operators.Process;

/*
 * Schimm algorithm aggregates data step by step :
 * 1° - Identify loops : separate them from the log leaving only one instance of the activities creating a new logs containing only the iterations
 * 2° - Aggregation in cluster : based on happen-before relation => same set of task ^ same relation <=> same cluster
 * 3° - Merge cluster with the same precedence relation : first identify pseudo dependency 
 * 4° - Two different steps, 
 * 4.1 =construct a model for each cluster 4.2 = merge all togheter this paths
 */
public class SchimmAlgorithm {

	private Process fatherP = null;
	private EventLog log;
	// Logs that contains all the loops of the trace
	// Reference to the loop; To bring it into the starting loop model;
	private Map<Event, LoopBlock> maploop = new HashMap<Event, LoopBlock>();
	// Index used as name reference for loop substitution
	// private static int index = 0;
	private boolean minimizeIt = false;
	private Process pastModelToTransf;
	private Process modelToTransf;
	public static Event empty = new Event("empty");
	private int threshold = 0;

	private List<Process> listProcess;

	public SchimmAlgorithm(EventLog log, int th) {
		this.threshold = th;
		this.log = new EventLog(log.getCase());
		// Scrivi reduction function of same traceses in the log
		removeSameTrace();

		/*
		 * Identify loops
		 */
		this.log.getIterator().forEachRemaining(e -> para(e));
		checkIteration();
		this.log.getIterator().forEachRemaining(e -> putagainpara(e));
		// System.out.println("ITERATION CHECKED");
		/*
		 * Construct cluster based on same relation
		 */
		Set<Case> cluster = createCluster();
		// Search for pseudo dependencies
		cluster = identifyPseudoDependency(cluster);
		// System.out.println("identify pseudodepncy");
		unroll(cluster);
		Process[] realFinalPath = new Process[cluster.size()];
		int i = 0;
		for (Case c : cluster) {
			Process p = formatNonDefinitiveModel(c);
			if (p != null) {
				realFinalPath[i] = p;
				i++;
			}
		}

		// System.out.println("first draft of final path");
		if (realFinalPath.length == 1)
			modelToTransf = realFinalPath[0];
		else
			modelToTransf = new Process(realFinalPath, Operator.CHOICE);
		/*
		 * Apply rewriting rule on the modelToTrans to minimize it
		 */

		minimizeModel();
		Map<String, Pair<Process, LoopBlock>> processfromloop = new HashMap<String, Pair<Process, LoopBlock>>();
		for (Entry<Event, LoopBlock> entry : maploop.entrySet()) {
			Process p = new Process();
			p.modifyProcessOp(Operator.CHOICE);
			// System.out.println(entry.getValue());
			EventLog[] loopslog = entry.getValue().getLoop();
			for (int j = 0; j < loopslog.length; j++) {
				SchimmAlgorithm schimm = new SchimmAlgorithm(loopslog[j], th);
				if (loopslog.length > 1)
					p.insertProcessAtPosition(schimm.getFinalModel(), j);
				else
					p = schimm.getFinalModel();
			}
			// System.out.println(p.toString());
			processfromloop.put(entry.getKey().getName(), Pair.of(p, entry.getValue()));
		}
		//if(!maploop.isEmpty())
			//System.out.println("enter here");
		if (!processfromloop.isEmpty()) {
			while (sobstitueloops(processfromloop, modelToTransf)) {
				continue;
			}
		}
	 //System.out.println(modelToTransf.toString());
	}

	private void removeSameTrace() {
		Set<Case> noequaltrace = Sets.newHashSet(log.getCase());
		this.log = new EventLog(Lists.newArrayList(noequaltrace));
	}

	private Set<Case> unroll(Set<Case> cases) {
		for (Case c : cases) {
			Multimap<Event, Event> remove = HashMultimap.create();
			Multimap<Event, Event> multimapcase = c.getHBRel();
			for (Entry<Event, Event> entri : multimapcase.entries()) {
				if (multimapcase.containsEntry(entri.getValue(), entri.getKey())) {
					if (multimapcase.get(entri.getKey()).size() > 1 && multimapcase.get(entri.getValue()).size() == 1)
						remove.put(entri.getKey(), entri.getValue());
					else if (multimapcase.get(entri.getValue()).size() > 1
							&& multimapcase.get(entri.getKey()).size() == 1)
						remove.put(entri.getValue(), entri.getKey());
					else if (c.getTrace().indexOf(entri.getKey()) < c.getTrace().indexOf(entri.getValue()))
						remove.put(entri.getKey(), entri.getValue());
					else
						remove.put(entri.getValue(), entri.getKey());
				}
			}
			multimapcase.removeAll(remove);
			c.setHBrel(multimapcase);
		}
		return cases;
	}

	private boolean sobstitueloops(Map<String, Pair<Process, LoopBlock>> loops, Process process) {
		boolean b = false;
		if (process != null) {
			if (process.getActivity() != null && loops.containsKey(process.getActivity().getName())) {

				String name = process.getActivity().getName();
				// System.out.println(name);
				if (loops.get(process.getActivity().getName()).getRight().getflooptotal() > threshold) {
					process.insertProcessAtPosition(loops.get(name).getLeft(), 0);
					process.modifyProcessOp(Operator.LOOP);
				} else {
					int repetion = loops.get(name).getRight().repetion();
					if (repetion == 0)
						repetion = 1;
					Process toberepeated = loops.get(name).getLeft();
					if (repetion == 1) {
						if (toberepeated.getActivity() != null)
							process.modifyProcessActivity(toberepeated.getActivity());
						else {
							process.modifyProcessArray(toberepeated.getProcess(), toberepeated.getOp());
							process.cleanActivity();
						}
						return true;
					}
					int j = 1;
					while (j <= repetion) {
						Process p = new Process();
						if (j == 1)
							process.insertProcessAtPosition(toberepeated, j - 1);
						else {
							int m = 0;
							while (m < j) {
								p.insertProcessAtPosition(toberepeated, m);
								m++;
							}
							process.insertProcessAtPosition(p, j - 1);
						}
						p.modifyProcessOp(Operator.SEQUENCE);
						j++;
					}
					process.modifyProcessOp(Operator.CHOICE);
				}
				process.cleanActivity();
				return true;
			} else if (process.getProcess() != null) {
				for (int i = 0; i < process.size(); i++) {
					if (!b)
						b = sobstitueloops(loops, process.getProcess()[i]);
					else
						sobstitueloops(loops, process.getProcess()[i]);
				}

			}
		}
		return b;
	}

	private void checkIteration() {
		Iterator<Case> it = log.getIterator();
		while (it.hasNext()) {
			Case c = it.next();
			int i = 0;
			while (i < c.length()) {
				Event e = c.getEvent(i);
				Set<Event> incomingevent = c.getPreEvent(e);
				boolean found = false;
				for (Event en : incomingevent) {
					Set<Event> postLconnection = new HashSet<Event>();
					Set<Event> preLconnection = new HashSet<Event>();
					LoopBlock lb = null;
					boolean b = false;
					if (e.equals(en)) {
						preLconnection.addAll(c.getPreEvent(e));
						postLconnection.addAll(c.getPostEvent(e));
						preLconnection.remove(e);
						postLconnection.remove(e);
						Case t = new Case(e);
						t.setHBrel(c.getSubHBrel(t));
						if ((lb = retrieveLoop(e, e)) == null)
							lb = new LoopBlock(e, e);
						lb.addLoop(t);
						// In order to compute the indexs for loop reduction
						computefloopcase(lb, t, c);
						lb.setfloopeventlog(1, log.size(), c.length());
						if (!b)
							b = c.substituteloop(t, lb.getName());
						else
							c.substituteloop(t, lb.getName());
						putagainpara(t);
						c.reductionlooprule(lb.getName());
						insetloopclearcondition(lb, preLconnection, postLconnection, c);
						if (!found)
							found = b;
					} else {
						Set<Case> subtrace = c.getSubTrace(e, en);
						preLconnection.addAll(c.getPreEvent(e));
						if ((lb = retrieveLoop(e, en)) == null)
							lb = new LoopBlock(e, en);
						for (Case t : subtrace) {
							postLconnection = new HashSet<Event>();
							Multimap<Event, Event> subhbrel = c.getSubHBrel(t);
							t.setHBrel(subhbrel);
							lb.addLoop(t);
							computefloopcase(lb, t, c);
							lb.setfloopeventlog(1, log.size(), c.length());
							b = c.substituteloop(t, lb.getName());
							putagainpara(t);
							c.reductionlooprule(lb.getName());
							postLconnection.addAll(c.getPostEvent(en));
							postLconnection.remove(lb.getName());
							insetloopclearcondition(lb, preLconnection, postLconnection, c);
							if (!found)
								found = b;
						}
					}
				}
				// Every time that i make a change i start again from the first elements
				if (found) {
					i = 0;
				} else
					i++;
			}

		}
	}

	private static int index = 0;
	private static String parl = "parl";
	private Map<Event, Case> parele = new HashMap<Event, Case>();

	private void para(Case c) {
		for (int i = 0; i < c.getTrace().size(); i++) {
			List<Event> set = new ArrayList<Event>();
			Event e1 = c.getTrace().get(i);
			set.add(e1);
			int last = 0;
			for (int j = i + 1; j < c.getTrace().size(); j++) {
				Event e2 = c.getTrace().get(j);
				// If e1 and e2 have the same preset and post set and the is no connection among
				// each other then are in parallel
				if (checkparallelism(e1, e2, c)) {
					set.add(e2);
					last = j;
				} else
					break;
			}
			if (set.size() > 1) {
				Case cap = new Case(set);
				Event ev = new Event(parl + index++);
				for (Event evento : cap.getTrace()) {
					cap.addHBRelation(c.getPreEvent(e1), c.getPostEvent(e1), evento);
				}
				c.removeIndex(i, last);
				c.add(i, ev);
				c.addHBRelation(c.getPreEvent(e1), c.getPostEvent(e1), ev);
				clearconnection(c);
				parele.put(ev, cap);
			}
		}
	}

	private void putagainpara(Case c) {
		for (int i = 0; i < c.length(); i++) {
			Event e = c.getTrace().get(i);
			if (parele.containsKey(e)) {
				c.removeIndex(i, i);
				for (Entry<Event, Event> entry : parele.get(e).getHBRel().entries()) {
					if ((i == c.length() && entry.getValue().equals(c.getTrace().get(0)))
							|| (i == 0 && parele.get(e).getTrace().contains(entry.getValue())))
						continue;
					else
						c.addHBRelation(entry);
				}
				Multimap<Event, Event> tmp = HashMultimap.create();
				for (Event event : parele.get(e).getTrace()) {
					for (Entry<Event, Event> entry : c.getHBRel().entries()) {
						if (entry.getKey().equals(e))
							tmp.put(event, entry.getValue());
						else if (entry.getValue().equals(e)) {
							tmp.put(entry.getKey(), event);
						}
					}
				}
				c.addHBRelation(tmp);
				c.add(i, parele.get(e).getTrace());
				clearconnection(c);
			}
		}
	}

	private void computefloopcase(LoopBlock lb, Case l, Case c) {
		// number of times that loop is repeating insed the case
		int nl = c.getMoltiplicity(l);
		// Lenght of the loop
		int Ll = lb.getLenghtLoop();
		lb.setfloopcase(nl, Ll, c.length());
	}

	private void insetloopclearcondition(LoopBlock lb, Set<Event> preLconnection, Set<Event> postLconnection, Case c) {
		maploop.put(lb.getName(), lb);
		c.addHBRelation(preLconnection, postLconnection, lb.getName());
		c = clearconnection(c);
		if (c.getHBRel().isEmpty() && !c.getTrace().isEmpty() && c.getTrace().size() == 1) {
			c.getHBRel().put(c.getTrace().get(0), SchimmAlgorithm.empty);
		}

	}

	private LoopBlock retrieveLoop(Event e, Event en) {
		for (Entry<Event, LoopBlock> entry : maploop.entrySet()) {
			if (entry.getValue().startEndevents(e, en))
				return entry.getValue();
		}
		return null;
	}

	private Case clearconnection(Case c) {
		// In order to remove old relations
		Multimap<Event, Event> tmpmap = HashMultimap.create();
		for (Entry<Event, Event> entry : c.getHBRel().entries()) {
			if (c.getAlphabet().contains(entry.getKey()) && c.getAlphabet().contains(entry.getValue()))
				tmpmap.put(entry.getKey(), entry.getValue());
		}
		c.setHBrel(tmpmap);
		return c;
	}

	private boolean checkparallelism(Event e1, Event e2, Case c) {
		Set<Event> e1pre = c.getPreEvent(e1);
		Set<Event> e2pre = c.getPreEvent(e2);
		Set<Event> e1post = c.getPostEvent(e1);
		Set<Event> e2post = c.getPostEvent(e2);
		if (!e1.equals(e2) && !(e1pre.contains(e2) || e1post.contains(e2))
				&& !(e2pre.contains(e1) || e2post.contains(e1)) && e1pre.equals(e2pre) && e1post.equals(e2post)) {
			return true;
		}
		return false;
	}

	/*
	 * IF the case has a hbrelation different from the case that already exist in
	 * the cluster => new cluster
	 */
	private Set<Case> createCluster() {
		Set<Case> cluster = new HashSet<Case>();
		Iterator<Case> it = log.getIterator();
		while (it.hasNext()) {
			Case c = it.next();
			Multimap<Event, Event> hbother = c.getHBRel();
			boolean f = false;
			for (Case cases : cluster) {
				if (cases.checkEqualRelation(hbother))
					f = !f;
			}
			if (!f)
				cluster.add(c);
		}
		return cluster;
	}

	/*
	 * pseudo-dependency: if a->b and b->a and exist a trace s.t a -/-> b and b-/a->
	 * then a,b is a psuedo dependency
	 */

	private Set<Case> identifyPseudoDependency(Set<Case> cluster) {
		List<Case> clusterToRemove = new ArrayList<Case>();
		for (Case case1 : cluster) {
			Multimap<Event, Event> hbrel = case1.getHBRel();
			if (hbrel == null)
				continue;
			for (Entry<Event, Event> entry : hbrel.entries()) {
				boolean found = false;
				for (Case case2 : cluster) {
					if (case2.getHBRel() != null) {
						if (!case2.equals(case1) && case2.getAlphabet().equals(case1.getAlphabet())
								&& case2.getHBRel().containsEntry(entry.getValue(), entry.getKey())) {
							// Then I should check that doens't exist a trace in which this two event are
							// not connected
							if (checkconnection(entry.getKey(), entry.getValue(), case1.getAlphabet(), cluster)) {
								if (!clusterToRemove.contains(case1))
									clusterToRemove.add(case1);
								found = true;
							}
							if (found && !clusterToRemove.contains(case2))
								clusterToRemove.add(case2);
						}
					}
				}
			}
		}
		cluster.removeAll(clusterToRemove);
		return cluster;
	}

	private boolean checkconnection(Event a, Event b, Set<Event> alphabet, Set<Case> cluster) {
		for (Case casec : cluster) {
			if (casec.contains(a) && casec.contains(b) && casec.getAlphabet().equals(alphabet)
					&& !casec.getHBRel().containsEntry(a, b) && !casec.getHBRel().containsEntry(b, a))
				return true;
		}
		return false;
	}

	/*
	 * Concatenate all the path found in sequence of processes in parallel
	 */
	private Process formatNonDefinitiveModel(Case casec) {

		List<List<Event>> path = casec.computePaths();
		Process[] toPutInParallel = new Process[path.size()];
		int i = 0;
		for (List<Event> list : path) {
			Process[] tmp = new Process[list.size()];
			for (int j = 0; j < list.size(); j++) {
				Event e = list.get(j);
				// if (maploop.containsKey(e))
				// tmp[j] = constructLoopBlock(e);
				// else
				tmp[j] = new Process(new Activity(e.getName()));
			}
			toPutInParallel[i] = new Process(tmp, Operator.SEQUENCE);
			i++;
		}
		if (toPutInParallel.length == 0)
			return null;

		if (toPutInParallel.length < 2)
			return toPutInParallel[0];
		else
			return new Process(toPutInParallel, Operator.PARALLEL);

	}

	private void minimizeModel() {
		// System.out.println(modelToTransf.toString());
		mergeSameBlock(modelToTransf, Operator.LOOP);
		repeatCall("mergeSameBlock", Operator.LOOP);
		/*
		 * mergeSameBlock(modelToTransf, Operator.PARALLEL);
		 * repeatCall("mergeSameBlock", Operator.PARALLEL);
		 * mergeSameBlock(modelToTransf, Operator.CHOICE); repeatCall("mergeSameBlock",
		 * Operator.CHOICE); mergeSameBlock(modelToTransf, Operator.SEQUENCE);
		 * repeatCall("mergeSameBlock", Operator.SEQUENCE);
		 */

		halfleftDistributivity(modelToTransf, Operator.PARALLEL, Operator.SEQUENCE);
		halfrightDistributivity(modelToTransf, Operator.PARALLEL, Operator.SEQUENCE);
		halfleftDistributivity(modelToTransf, Operator.PARALLEL, Operator.CHOICE);
		halfrightDistributivity(modelToTransf, Operator.PARALLEL, Operator.CHOICE);

		leftDistributivity(modelToTransf, Operator.PARALLEL);
		repeatCall("leftDistributivity", Operator.PARALLEL);
		rightDistributivity(modelToTransf, Operator.PARALLEL);
		repeatCall("rightDistributivity", Operator.PARALLEL);
		leftDistributivity(modelToTransf, Operator.CHOICE);
		repeatCall("leftDistributivity", Operator.CHOICE);
		rightDistributivity(modelToTransf, Operator.CHOICE);
		repeatCall("rightDistributivity", Operator.CHOICE);

		if (pastModelToTransf == null) {
			pastModelToTransf = new Process(modelToTransf.getProcess(), modelToTransf.getOp());
			minimizeModel();
			minimizeIt = false;
		} else if (minimizeIt) {
			minimizeIt = false;
			minimizeModel();
		}
	}

	private void repeatCall(String method, Operator op) {
		while (mod) {
			Method m;
			try {
				m = this.getClass().getDeclaredMethod(method, Process.class, Operator.class);
				fatherP = null;
				mod = false;
				m.invoke(this, modelToTransf, op);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		fatherP = null;
		return;
	}

	private boolean mod = false;

	private void mergeSameBlock(Process analyze, Operator op) {
		if (analyze.getOp() == null)
			return;
		List<Integer> indextoremove = new ArrayList<Integer>();
		for (int i = 0; i < analyze.size(); i++) {
			Process p = analyze.getProcess()[i];
			if (p == null)
				indextoremove.add(i);
			else if (p.getOp() != null && p.getOp().equals(op)) {
				for (int j = i + 1; j < analyze.size(); j++) {
					if (analyze.getProcess()[j].equals(p) && !indextoremove.contains(j))
						indextoremove.add(j);
				}
			}
		}
		if (indextoremove.isEmpty()) {
			for (int i = 0; i < analyze.size(); i++) {
				mergeSameBlock(analyze.getProcess()[i], op);
			}
			return;
		}
		int i = 0;
		if (fatherP != null) {
			int indexanalyze = fatherP.getPositionProcess(analyze);
			while (indextoremove.size() != i) {
				analyze.removeElementAtPosition(indextoremove.get(i) - i);
				i++;
			}
			fatherP.removeElementAtPosition(indexanalyze);
			fatherP.insertProcessAtPosition(analyze, indexanalyze);
		} else
			while (indextoremove.size() != i) {
				analyze.removeElementAtPosition(indextoremove.get(i) - i);
				i++;
			}

		if (analyze.getProcess().length == 1) {
			Process[] tmp = analyze.getProcess()[0].getProcess();
			analyze.modifyProcessArray(tmp, analyze.getProcess()[0].getOp());
		}
		mod = minimizeIt = true;
	}

	private void rightDistributivity(Process analyze, Operator operator) {
		if (analyze.getOp() == null)
			return;
		if (!analyze.getOp().equals(operator)) {
			for (int i = 0; i < analyze.getProcess().length; i++) {
				fatherP = analyze;
				rightDistributivity(analyze.getProcess()[i], operator);
			}
			return;
		}
		// Analyze inside the process because is not a leaf
		if (analyze.getOp().equals(operator) && analyze.getProcess() == null) {
			for (int i = 0; i < analyze.getProcess().length; i++)
				rightDistributivity(analyze.getProcess()[i], operator);
			return;
		}
		// Until there are equal activity
		listProcess = new ArrayList<Process>();
		if (analyze.size() == 1) {
			analyze.modifyProcessArray(analyze.getProcess()[0].getProcess(), analyze.getProcess()[0].getOp());
			return;
		}
		if (analyze.getProcess() != null) {
			for (int i = 0; i < analyze.getProcess().length; i++) {
				if (analyze.getProcess()[i].getOp() != null
						&& !analyze.getProcess()[i].getOp().equals(Operator.SEQUENCE))
					return;
			}
			exploitLastP(analyze);
		}
		if (listProcess.isEmpty())
			return;
		Process mergedProcess = null;
		for (Process p : listProcess) {
			if (mergedProcess == null) {
				if (p.getProcess() != null)
					mergedProcess = p.getProcess()[p.getProcess().length - 1];
				else
					mergedProcess = new Process(p.getActivity());
			} else if (p.getProcess() != null && !mergedProcess.equals(p.getProcess()[p.getProcess().length - 1]))
				return;
			else if (p.getActivity() != null && !mergedProcess.equals(new Process(p.getActivity())))
				return;
		}
		Process[] tmpProcess = null;
		Process[] tmt = new Process[2];

		tmpProcess = mergeRightAmongP(analyze);
		if (fatherP != null && fatherP.getOp().equals(Operator.SEQUENCE)) {
			fatherP.insertProcessAtPosition(mergedProcess, fatherP.getPositionProcess(analyze) + 1);
			if (!ArrayUtils.isEmpty(tmpProcess)) {
				if (tmpProcess.length == 1 && tmpProcess[0].getActivity() != null) {
					analyze.modifyProcessActivity(tmpProcess[0].getActivity());
				} else
					analyze.modifyProcessArray(tmpProcess, operator);
			} else {
				fatherP.removeElementAtPosition(fatherP.getPositionProcess(analyze));
			}

		} else {
			if (!ArrayUtils.isEmpty(tmpProcess)) {
				tmt[0] = new Process(tmpProcess, operator);
				tmt[1] = mergedProcess;
			} else {
				tmt[0] = mergedProcess;
				ArrayUtils.remove(tmt, 1);
			}
			analyze.modifyProcessArray(tmt, Operator.SEQUENCE);
		}
		mod = minimizeIt = true;
		listProcess.clear();
	}

	/*
	 * Bring the sequence as out as possible with respect to the parallel
	 * /alternative operator(depedens from the input)
	 */

	private void halfleftDistributivity(Process analyze, Operator op1, Operator op2) {
		if (analyze.getOp() == null)
			return;
		if (!analyze.getOp().equals(op1)) {
			for (int i = 0; i < analyze.getProcess().length; i++) {
				// fatherP = analyze;
				halfleftDistributivity(analyze.getProcess()[i], op1, op2);
			}
			return;
		}

		if (analyze.size() == 1)
			analyze.modifyProcessArray(analyze.getProcess()[0].getProcess(), analyze.getProcess()[0].getOp());

		List<Process> outsiders = new ArrayList<Process>();
		Map<Process, List<Process>> listlist = new HashMap<>();
		if (analyze.getProcess() != null) {
			for (int i = 0; i < analyze.size(); i++) {
				Process mergedActivity = null;
				Process p = analyze.getProcess()[i];
				if (p.getActivity() != null)
					mergedActivity = new Process(p.getActivity());
				else {
					if (!p.getOp().equals(op2)) {
						outsiders.add(p);
						continue;
					}
					mergedActivity = p.getProcess()[0];
				}
				if (listlist.containsKey(mergedActivity))
					listlist.get(mergedActivity).add(p);
				else
					listlist.put(mergedActivity, Lists.newArrayList(p));
			}
		} else
			return;

		if (listlist.isEmpty() || listlist.size() == 1 || listlist.size() == analyze.size())
			return;

		Process newme = new Process();
		for (Entry<Process, List<Process>> entry : listlist.entrySet()) {
			List<Process> list = entry.getValue();
			if (list.size() == 1)
				newme.insertProcess(list.get(0));
			else {
				Process tmt = new Process();
				tmt.modifyProcessOp(op2);
				tmt.insertProcess(entry.getKey());
				Process subchoice = new Process();
				subchoice.modifyProcessOp(Operator.CHOICE);
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getProcess() != null) {
						if (list.get(i).size() == 1)
							continue;
						else {
							list.get(i).removeElementAtPosition(0);
							subchoice.insertProcess(list.get(i));
						}
					} /*
						 * else { subchoice.insertProcess(new Process(new Activity("tau"))); }
						 */
				}
				// IF THERE IS JUST ONE PROCESS IS NOT A CHOICE
				if (!subchoice.isempty()) {
					if (subchoice.size() == 1) {
						tmt.insertProcess(subchoice.getProcess()[0]);
					} else
						tmt.insertProcess(subchoice);
				}
				if (!tmt.isempty())
					newme.insertProcess(tmt);
			}
		}
		if (!outsiders.isEmpty()) {
			for (Process p : outsiders) {
				newme.insertProcess(p);
			}
		}
		newme.modifyProcessOp(analyze.getOp());
		if (!newme.isempty()) {
			analyze.modifyProcessArray(newme.getProcess(), newme.getOp());
		} else {
			analyze = new Process();
		}
		mod = minimizeIt = true;

	}

	private void halfrightDistributivity(Process analyze, Operator op1, Operator op2) {
		if (analyze.getOp() == null)
			return;
		if (!analyze.getOp().equals(op1)) {
			for (int i = 0; i < analyze.getProcess().length; i++) {
				fatherP = analyze;
				halfrightDistributivity(analyze.getProcess()[i], op1, op2);
			}
			return;
		}
		if (analyze.size() == 1)
			analyze.modifyProcessArray(analyze.getProcess()[0].getProcess(), analyze.getProcess()[0].getOp());
		List<Process> outsiders = new ArrayList<Process>();
		Map<Process, List<Process>> listlist = new HashMap<>();
		if (analyze.getProcess() != null) {
			for (int i = 0; i < analyze.size(); i++) {
				Process mergedActivity = null;
				Process p = analyze.getProcess()[i];
				if (p.getActivity() != null)
					mergedActivity = new Process(p.getActivity());
				else {
					if (!p.getOp().equals(op2)) {
						outsiders.add(p);
						continue;
					}
					mergedActivity = p.getProcess()[p.size() - 1];
				}
				if (listlist.containsKey(mergedActivity))
					listlist.get(mergedActivity).add(p);
				else
					listlist.put(mergedActivity, Lists.newArrayList(p));
			}
		} else
			return;

		if (listlist.isEmpty() || listlist.size() == 1 || listlist.size() == analyze.size())
			return;

		Process newme = new Process();
		for (Entry<Process, List<Process>> entry : listlist.entrySet()) {
			List<Process> list = entry.getValue();
			if (list.size() == 1)
				newme.insertProcess(list.get(0));
			else {
				Process tmt = new Process();
				tmt.insertProcess(entry.getKey());
				tmt.modifyProcessOp(op2);
				Process subchoice = new Process();
				subchoice.modifyProcessOp(Operator.CHOICE);
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getProcess() != null) {
						if (list.get(i).size() == 1)
							continue;
						else {
							list.get(i).removeElementAtPosition(0);
							subchoice.insertProcess(list.get(i));
						}
					} /*
						 * else { subchoice.insertProcess(new Process(new Activity("tau"))); }
						 */
				}
				// IF THERE IS JUST ONE PROCESS IS NOT A CHOICE
				if (!subchoice.isempty()) {
					if (subchoice.size() == 1) {
						tmt.insertProcess(subchoice.getProcess()[0]);
					} else
						tmt.insertProcess(subchoice);
				}
				if (!tmt.isempty())
					newme.insertProcess(tmt);
			}
		}
		if (!outsiders.isEmpty()) {
			for (Process p : outsiders) {
				newme.insertProcess(p);
			}
		}
		newme.modifyProcessOp(analyze.getOp());
		if (!newme.isempty()) {
			analyze.modifyProcessArray(newme.getProcess(), newme.getOp());
		} else {
			analyze = new Process();
		}
		mod = minimizeIt = true;
	}

	private void leftDistributivity(Process analyze, Operator operator) {
		if (analyze.getOp() == null)
			return;
		/*
		 * Check if the external operator is what we want
		 */
		if (!analyze.getOp().equals(operator)) {
			for (int i = 0; i < analyze.getProcess().length; i++) {
				fatherP = analyze;
				leftDistributivity(analyze.getProcess()[i], operator);
			}
			return;
		}
		Process mergedActivity = null;
		listProcess = new ArrayList<Process>();
		// This means that we have to eliminate the external operator and take outside
		// the internal one
		if (analyze.size() == 1) {
			analyze.modifyProcessArray(analyze.getProcess()[0].getProcess(), analyze.getProcess()[0].getOp());
		}
		/*
		 * Now check that the internal processes has the Sequence Operator
		 */
		if (analyze.getProcess() != null) {
			for (int i = 0; i < analyze.getProcess().length; i++)
				if (analyze.getProcess()[i].getOp() != null
						&& (!analyze.getProcess()[i].getOp().equals(Operator.SEQUENCE)))
					return;
			exploitFirstP(analyze);
		}
		// if there aren't process in the list means that we cannot apply the rule of
		// distribution
		if (listProcess.isEmpty())
			return;
		// The first process is the one that we can merge if is equal for all the
		// processes in the list
		for (Process p : listProcess) {
			if (mergedActivity == null) {
				if (p.getActivity() != null)
					mergedActivity = new Process(p.getActivity());
				else
					mergedActivity = p.getProcess()[0];
			} else if ((p.getActivity() != null && !mergedActivity.equals(new Process(p.getActivity())))
					|| (p.getProcess() != null && !mergedActivity.equals(p.getProcess()[0])))
				return;
		}

		// If i am here means that i can merge on the left because there is one activity
		// in common
		Process[] tmpProcess = null;
		Process[] tmt = new Process[2];
		tmpProcess = mergeLeftAmongP(analyze);
		if (fatherP != null && fatherP.getOp().equals(Operator.SEQUENCE)) {
			fatherP.insertProcessAtPosition(mergedActivity, fatherP.getPositionProcess(analyze));
			if (tmpProcess.length == 1 && tmpProcess[0].getActivity() != null) {
				analyze.modifyProcessActivity(tmpProcess[0].getActivity());
			} else
				analyze.modifyProcessArray(tmpProcess, operator);
		} else {
			tmt[0] = mergedActivity;
			if ((tmpProcess.length == 1) && tmpProcess[0].getActivity() != null)
				tmt[1] = new Process(tmpProcess[0].getActivity());
			else
				tmt[1] = new Process(tmpProcess, operator);
			analyze.modifyProcessArray(tmt, Operator.SEQUENCE);
		}
		mod = minimizeIt = true;
		listProcess.clear();
	}

	private Process[] mergeRightAmongP(Process p) {
		Process[] tmpProcess = new Process[p.getProcess().length];
		int indexIns = 0;
		for (int i = 0; i < p.getProcess().length; i++) {
			if (p.getProcess()[i].size() == 1) {
				tmpProcess = ArrayUtils.remove(tmpProcess, tmpProcess.length - 1);
				continue;
			}
			Process t = new Process(
					ArrayUtils.subarray(p.getProcess()[i].getProcess(), 0, p.getProcess()[i].getProcess().length - 1),
					p.getProcess()[i].getOp());
			if (t.size() == 1 && !t.getOp().equals(Operator.LOOP)) {
				if (t.getProcess()[0].getProcess() != null) {
					t.modifyProcessArray(t.getProcess()[0].getProcess(), t.getProcess()[0].getOp());
				} else
					t.modifyProcessActivity(t.getProcess()[0].getActivity());
			}
			tmpProcess[indexIns] = t;
			indexIns++;
		}
		return tmpProcess;
	}

	private Process[] mergeLeftAmongP(Process p) {
		Process[] tmpProcess = new Process[0];
		for (int i = 0; i < p.getProcess().length; i++) {
			if (p.getProcess()[i].size() == 1) {
				continue;
			}
			if (p.getProcess()[i].getActivity() != null)
				continue;
			Process t = new Process(
					ArrayUtils.subarray(p.getProcess()[i].getProcess(), 1, p.getProcess()[i].getProcess().length),
					p.getProcess()[i].getOp());
			if (t.size() == 1 && !t.getOp().equals(Operator.LOOP)) {
				if (t.getProcess()[0].getProcess() != null) {
					t.modifyProcessArray(t.getProcess()[0].getProcess(), t.getProcess()[0].getOp());
				} else
					t.modifyProcessActivity(t.getProcess()[0].getActivity());
			}
			tmpProcess = ArrayUtils.add(tmpProcess, t);
		}
		return tmpProcess;
	}

	private void exploitLastP(Process p) {
		if (p.getProcess() == null || p.getProcess().length == 0) {
			// exploitLastP(p.getProcess()[p.getProcess().length - 1]);
			return;
		}
		for (int j = 0; j < p.getProcess().length; j++)
			listProcess.add(p.getProcess()[j]);
		return;
	}

	// Equals between the first elements of each process
	private void exploitFirstP(Process p) {
		for (int i = 0; i < p.getProcess().length; i++) {
			if (!listProcess.contains(p.getProcess()[i]))
				listProcess.add(p.getProcess()[i]);
		}
	}

	public Process getFinalModel() {
		return modelToTransf;
	}

}
