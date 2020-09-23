package edu.imt.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Sets;
import edu.imt.specification.structure.Operator;
import edu.imt.specification.structure.BlockStructure;
import edu.imt.inputData.*;

/**
 * This is the class SchimmAlgorithm that given an event log generates a
 * block-structure The steps of the algorithm "Mining tool-independent
 * specification" are executed directly at the object construction
 * 
 * @see #SchimmAlgorithm(EventLog, int)
 * 
 *      To retrive the final result, i.e. the block-structure use the following
 *      method
 * @see #getFinalModel()
 * @author S. Belluccini
 *
 */

public class SchimmAlgorithm {

	/**
	 * The event log over which we compute the algorithm
	 */
	private EventLog log;
	/**
	 * The map that contains the relation between events and their loop
	 */
	private Set<LoopSet> loops;
	private BlockStructure modelToTransf;
	/**
	 * loop threshold to to be set by the user
	 */
	// private int loop_threshold = 0;
	protected static final String RIGHT = "right";
	protected static final String LEFT = "left";

	/**
	 * Constructor of the SchimmAlgorithm class, execute the algorithm over the
	 * given log of event
	 * 
	 * @param log the event log
	 * @param th  the threshold s.t. if f_loop < threshold than unroll it
	 * 
	 * @see #searchForLoops() , 1st step
	 * @see #creationOfClusters(), 2nd step
	 * @see #identificationAndRemovalOfPseudoDependencies(Set), 3rd step
	 * @see #unifyAllBlockStructure(Set) , 4th-th step
	 * @see #restructuringTheModel(BlockStructure), 6th step
	 * @see #replacingLoopReference(BlockStructure), 7th step
	 */
	public SchimmAlgorithm(EventLog log) {
		// Set containing all the loops found in this eventlog
		this.loops = new HashSet<LoopSet>();
		this.log = log;
		// 1st step - search for loops
		searchForLoops();
		// 2nd step - creation of clusters
		Set<Trace> cluster = creationOfClusters();
		// 3rd step - identification and removal of pseudo-dependencies
		cluster = identificationAndRemovalOfPseudoDependencies(cluster);
		// 4th step - model for each cluster && 5th step unify all block structure
		BlockStructure blockStructure = unifyAllBlockStructure(cluster);
		// 6th step - restructuring the model
		blockStructure = restructuringTheModel(blockStructure);
		// 7th step - replacing loop references
		this.modelToTransf = replacingLoopReference(blockStructure);
	}

	public BlockStructure getFinalModel() {
		return this.modelToTransf;
	}

	/**
	 * Detect all the loops in the event log, substituing them with their process
	 * reference, this process works as following: -For each detected loop l
	 * {t=e_1...e_i...e_j...e_n s.t. e_j > j_i => l =e_i...e_j} --For each instance
	 * of l in t we need to substitute the reference to the process l in t updating
	 * the happened-before relation of t in the following way {e_{i-1} > e_i and e_j
	 * > e_{j+1}}, if there is more than one instance of l next to each other, just
	 * one reference will be inserted
	 */
	private void searchForLoops() {
		for (Trace t : log) {
			for (int i = 0; i < t.length(); i++) {
				Event e = t.getEvent(i);
				Set<Event> incomingEventsofE = t.getPreEventHB(e);
				Trace tmp = t.getSubTraceFrom(i, t.length());
				for (int j = 1; j < tmp.length(); j++) {
					Event ei = tmp.getEvent(j);
					if (incomingEventsofE.contains(ei)) {
						// counts the number of occurrences of the loop inside this trace
						LoopSet loopSet = retriveLoop(e, ei);
						Trace loopTrace;
						int lenght = 0;

						int occ = 0;
						do {
							Triple<Trace, Integer, Integer> triple = t.getSubTrace(e, ei, loopSet.getName());
							loopTrace = triple.getLeft();
							lenght += loopTrace.length();
							loopTrace.setHBrel(t.getSubHBrel(loopTrace));
							loopSet.addLoop(loopTrace);
							loops.add(loopSet);
							occ += 1;
							// Remove the subtrace and compute the start and end index for the loop
							t.removeSubTrace(triple.getMiddle(), triple.getRight());
							int start_index = triple.getMiddle();
							/*
							 * If the a reference to this loop doesn't exists immediately on the left or
							 * right side of the trace then add it to the trace and updates the
							 * happened-before relation with e_i>e and e> e_j
							 */
							if (start_index == 0 || !t.getEvent(start_index - 1).equals(loopSet.getName())) {
								t.add(start_index, loopSet.getName());
								if (start_index > 0)
									t.addPreHBRelation(t.getEvent(start_index - 1), loopSet.getName());
								if ((start_index + 1) < t.length())
									t.addPostHBRelation(t.getEvent(start_index + 1), loopSet.getName());
							} else {
								if (start_index < t.length())
									t.addPostHBRelation(t.getEvent(start_index), loopSet.getName());
							}

						} while (!t.getSubTrace(e, ei,loopSet.getName()).getLeft().isEmpty());
						loopSet.setRepetition(occ);
						t.addLoopWithFrequency(loopSet.getName().getName(), lenght);
						i--;
						break;
					}
				}

			}
		}
	}

	/**
	 * Gives a loop that contains the given trace, if already exists in the variable
	 * loops, otherwise instance a new loopSet with the first and last element of
	 * the given trace
	 * 
	 * @param t the trace
	 * @return a loopset that contains the trace, otherwise a new one
	 */
	private LoopSet retriveLoop(Event efirst, Event elast) {
		for (LoopSet ls : loops) {
			if (ls.first().equals(efirst) && ls.last().equals(elast))
				return ls;
		}
		LoopSet l = new LoopSet(efirst, elast);
		return l;
	}

	/**
	 * Compute a set of trace in which each trace has a unique happened-before
	 * relation
	 * 
	 * @return set of traces
	 */
	private Set<Trace> creationOfClusters() {
		Set<Trace> cluster = new HashSet<Trace>();
		for (Trace t : log) {
			Map<Event, Set<Event>> hbt = t.getHBRel();
			boolean alreadyExist = false;
			for (Trace c : cluster) {
				if (c.equalHB(hbt)) {
					alreadyExist = true;
					break;
				}
			}
			if (!alreadyExist)
				cluster.add(t);
		}

		return cluster;
	}

	/*
	 *
	 */
	/**
	 * if a->b and exist a trace with the same alphabet s.t a -/-> b nor b-/a-> then
	 * a-> b is a psuedo dependency, the trace with the pseudo dependency should be
	 * removed
	 * 
	 * @param cluster a set of traces
	 * @return a set of traces without pseudo dependencies
	 */
	private Set<Trace> identificationAndRemovalOfPseudoDependencies(Set<Trace> cluster) {
		Set<Trace> clusterToRemove = new HashSet<Trace>();
		for (Trace t1 : cluster) {
			if (!Sets.difference(cluster, clusterToRemove).contains(t1))
				continue;
			for (int i = 0; i < t1.length(); i++) {
				Event ei = t1.getEvent(i);
				for (int j = i + 1; j < t1.length(); j++) {
					Event ej = t1.getEvent(j);
					if (t1.containsRelation(ei, ej) || t1.containsRelation(ej, ej)) {
						// If ei>ej, all the other traces should contain this connection otherwise is
						// apseudo dep
						for (Trace t2 : Sets.difference(cluster, clusterToRemove)) {
							if (!t2.equals(t1) && t2.getAlphabet().equals(t1.getAlphabet())
									&& !t2.containsRelation(ei, ej) && !t2.containsRelation(ej, ej)) {
								clusterToRemove.add(t1);
							}
						}
					} else if (!t1.containsRelation(ei, ej) && !t1.containsRelation(ej, ej)) {
						// if ei/>ej, all the other traces should NOT contains this connection othweise
						// is a pseudo dep
						for (Trace t2 : Sets.difference(cluster, clusterToRemove)) {
							if (!t2.equals(t1) && t2.getAlphabet().equals(t1.getAlphabet())
									&& (t2.containsRelation(ei, ej) || t2.containsRelation(ej, ej))) {
								clusterToRemove.add(t2);
							}
						}
					}
				}
			}

		}
		if (!clusterToRemove.isEmpty())
			cluster.removeAll(clusterToRemove);
		return cluster;
	}

	/*
	 * private boolean checkconnection(Event a, Event b, Set<Event> alphabet,
	 * Set<Trace> cluster) { for (Trace Tracec : cluster) { if (Tracec.contains(a)
	 * && Tracec.contains(b) && Tracec.getAlphabet().equals(alphabet) &&
	 * !Tracec.getHBRel().containsEntry(a, b) && !Tracec.getHBRel().containsEntry(b,
	 * a)) return true; } return false; }
	 */

	/**
	 * Step 4th of the algorithm Concatenate all the path obtained from one trace in
	 * a parallel of sequences
	 * 
	 * @param t trace of the computed model
	 * @return the model computed
	 */
	private BlockStructure modelForEachCluster(Trace t) {
		List<List<Event>> path = t.computePaths();
		BlockStructure[] toPutInParallel = new BlockStructure[path.size()];
		int i = 0;
		for (List<Event> list : path) {
			BlockStructure[] tmp = new BlockStructure[list.size()];
			for (int j = 0; j < list.size(); j++)
				tmp[j] = new BlockStructure(list.get(j));

			toPutInParallel[i] = new BlockStructure(tmp, Operator.SEQUENCE);
			i++;
		}
		if (toPutInParallel.length == 0)
			System.err.println("modello vuoto");
		if (toPutInParallel.length < 2)
			return toPutInParallel[0];
		else
			return new BlockStructure(toPutInParallel, Operator.PARALLEL);

	}

	/**
	 * Applies all the transformation rules described in step 6th of the algorithm
	 * 
	 * @param b block structure over which the transformation rules are applied
	 * @return a new block structure
	 */
	private BlockStructure restructuringTheModel(BlockStructure b) {
		BlockStructure newBlock = null;
		if (b.hasEvent())
			return b;
		else if ((b.getOp().equals(Operator.CHOICE) || b.getOp().equals(Operator.PARALLEL)
				|| b.getOp().equals(Operator.SEQUENCE)) && b.size() == 1) {
			// S{B}->B , C{B}->B , P{B}-> B
			newBlock = TransformationRule.removeOperator(b);
		} else if (b.blockWithSamrOperator()) {
			// Op{B1...Op{e1..en}...Bm} -> Op{B1...,e1,...,en,...Bm}
			newBlock = TransformationRule.identity(b);
		} else if ((b.getOp().equals(Operator.PARALLEL) || b.getOp().equals(Operator.CHOICE))
				&& b.getFirstRowOp() != null && b.getFirstRowOp().equals(Operator.SEQUENCE)) {
			int number = 0;
			// Commutative of parallel and choice operator
			if ((number = howManyBlock(b, LEFT)) != 0) {
				newBlock = TransformationRule.mergeSide(b, LEFT, number);
			} else if ((number = howManyBlock(b, RIGHT)) != 0)
				newBlock = TransformationRule.mergeSide(b, RIGHT, number);
		}
		if (newBlock == null) {
			newBlock = new BlockStructure(b.getOp());
			for (int i = 0; i < b.size(); i++)
				newBlock.addBlockAtPosition(restructuringTheModel(b.getBlock(i)), i);
		}
		if (!b.equals(newBlock))
			b = restructuringTheModel(newBlock);
		return b;
	}

	/**
	 * Check how many block are equals in the same block strucure
	 * 
	 * @param b the BlockStrure to be checked
	 * @param s left or right side
	 * @return the number of block that are equals inside this block
	 */
	private int howManyBlock(BlockStructure b, String s) {
		int i = 0;
		while (checkBlockEquivalence(b, s)) {
			i += 1;
			BlockStructure tmp = new BlockStructure();
			for (int j = 0; j < b.size(); j++) {
				if (b.getBlock(j).hasEvent() || b.isEmpty())
					tmp.addBlockAtPosition(new BlockStructure(), j);
				else {
					if (s.equals(LEFT)) {
						tmp.addBlockAtPosition(b.getBlock(j).removeBlockAtIndex(0), j);
					} else if (s.equals(RIGHT))
						tmp.addBlockAtPosition(b.getBlock(j).removeBlockAtIndex(b.getBlock(j).size() - 1), j);
				}
			}
			b = tmp;
		}
		return i;
	}

	/**
	 * Check if all the blocks at the same index are equivalent
	 * 
	 * @param b     the block that is checked
	 * @param index the number of the children block to be checked
	 * @return true if all the blocks at the same index are equivalent, false
	 *         otherwise
	 */
	private boolean checkBlockEquivalence(BlockStructure b, String s) {
		BlockStructure firstBlock = null;
		BlockStructure[] children = b.getBlock();
		for (int i = 0; i < children.length; i++) {
			if (children[i].isEmpty())
				return false;
			if (children[i].hasEvent()) {
				Event e = children[i].getEvent();
				if (firstBlock == null)
					firstBlock = new BlockStructure(e);
				else if (!firstBlock.equals(new BlockStructure(e)))
					return false;
			} else {
				int index = 0;
				if (s.equals(RIGHT))
					index = children[i].size() - 1;
				if (firstBlock == null)
					firstBlock = children[i].getBlock(index);
				else if (!firstBlock.equals(children[i].getBlock(index)))
					return false;
			}
		}
		return true;
	}

	/**
	 * This method contains steps 4th and 5th of the algorithm, first compute the
	 * model of each cluster and then put them together using the choice operator if
	 * needed, i.e. if there is more than one model
	 * 
	 * @see #modelForEachCluster(Trace)
	 * @param cluster from which we obtain the final model
	 */
	private BlockStructure unifyAllBlockStructure(Set<Trace> cluster) {
		BlockStructure[] realFinalPath = new BlockStructure[cluster.size()];
		int i = 0;
		for (Trace c : cluster) {
			BlockStructure p = modelForEachCluster(c);
			realFinalPath[i] = p;
			i++;
		}
		if (realFinalPath.length == 1)
			return realFinalPath[0];
		else
			return new BlockStructure(realFinalPath, Operator.CHOICE);
	}

	/**
	 * The algorithm is applied again to all the elements in the loops set in order
	 * to obtain the block structure of the loops. Then we substitute the loop
	 * reference with its block structure.
	 * 
	 * @see #replaceReferences(Map, BlockStructure)
	 * @param b the block structure
	 * @return a block structure where the loop references have been substituted by
	 *         their block structure representation
	 */
	private BlockStructure replacingLoopReference(BlockStructure b) {
		if (loops.isEmpty())
			return b;
		Map<Event, BlockStructure> loopNametoLoopBS = new HashMap<Event, BlockStructure>();
		for (LoopSet l : loops) {
			SchimmAlgorithm alg = new SchimmAlgorithm(l.getLoop());
			BlockStructure bl = new BlockStructure(Operator.LOOP);
			bl.addBlockAtPosition(alg.getFinalModel(), 0);
			bl.setRepetition(l.getRepetition());
			bl.setFrequency(log.getFrequencyLoop(l));
			loopNametoLoopBS.put(l.getName(), bl);
		}
		return replaceReferences(replaceReferenceLoop(loopNametoLoopBS), b);
	}
	
	private Map<Event,BlockStructure> replaceReferenceLoop(Map<Event,BlockStructure> map) {
		Map<Event, BlockStructure> copyMap = new HashMap<Event, BlockStructure>();
		for (Entry<Event, BlockStructure> entry : map.entrySet()) {
			BlockStructure oldB ;
			BlockStructure newB = entry.getValue();
			do {
				oldB = newB;
				newB = replaceReferences(map, oldB);
			}while(!newB.equals(oldB));
			copyMap.put(entry.getKey(),newB);
		}
		return copyMap;
	}

	/**
	 * Each event that is a reference to a loop is substituted with its block
	 * structure representation.
	 * 
	 * @param loopNametoLoopBS map k:loop reference, v:block structure
	 * @param b                the block structure to be modified
	 * @return a block structure without loop references
	 */
	private BlockStructure replaceReferences(Map<Event, BlockStructure> loopNametoLoopBS, BlockStructure b) {
		if (b.hasEvent()) {
			if (loopNametoLoopBS.containsKey(b.getEvent()))
				return loopNametoLoopBS.get(b.getEvent());
			else
				return b;
		}
		BlockStructure newBlock = new BlockStructure(b.getOp());
		for (int i = 0; i < b.size(); i++)
			newBlock.addBlockAtPosition(replaceReferences(loopNametoLoopBS, b.getBlock(i)), i);
		if (b.getOp() != null && b.getOp().equals(Operator.LOOP)) {
			newBlock.setFrequency(b.getFrequency());
			newBlock.setRepetition(b.getRepetition());
		}
		return newBlock;
	}
}
