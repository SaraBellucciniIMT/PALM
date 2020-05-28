package edu.imt.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.imt.inputData.Case;
import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;

public class BasicRelation {

	private RelationType[][] relationMtx;
	private Map<Event, Integer> eventNameToMatrixIndex = new HashMap<Event, Integer>();
	private Event TI = new Event("_TI_");
	private Event TO = new Event("_TO_");

	public BasicRelation(EventLog eventlog) {
		/*
		 * Associate each event to an index into the matrix and then initiliaze the
		 * matrix
		 */
		int index = 0;
		Iterator<Case> it = eventlog.getIterator();
		while (it.hasNext()) {
			Case c = it.next();
			for (Event e : c.getAlphabet()) {
				if (eventNameToMatrixIndex.containsKey(e))
					eventNameToMatrixIndex.put(e, index);
			}

		}
		relationMtx = new RelationType[index][index];
		initializeMatrix();

		computeSuccessorAndLoop(eventlog);

		compute2TLoopDirectPar();

		computeMendaciusDependecy();

	}

	private void initializeMatrix() {
		for (int i = 0; i < relationMtx.length; i++) {
			for (int j = 0; j < relationMtx.length; j++) {
				relationMtx[i][j] = RelationType.NODIRECT;
			}
		}
	}

	/*
	 * It will compute the successor relation and the lenght 2-loop relation
	 */
	private void computeSuccessorAndLoop(EventLog eventlog) {
		Iterator<Case> it = eventlog.getIterator();
		while (it.hasNext()) {
			Case c = it.next();
			for (int i = 0; i < (c.length() - 1); i++) {
				/*
				 * Compute successor and lenght2-loop relation
				 */
				if (i > 1 && c.getEvent(i - 2).equals(c.getEvent(i)))
					relationMtx[eventNameToMatrixIndex.get(c.getEvent(i))][eventNameToMatrixIndex
							.get(c.getEvent(i - 1))] = RelationType.LOOP2LEN;
				else
					relationMtx[eventNameToMatrixIndex.get(c.getEvent(i))][eventNameToMatrixIndex
							.get(c.getEvent(i + 1))] = RelationType.SUCC;
			}
		}
	}

	/*
	 * Compute if two tasks have the length 2-loop relation between each other
	 */
	private void compute2TLoopDirectPar() {
		for (int i = 0; i < relationMtx.length; i++) {
			for (int j = 0; j < relationMtx.length; j++) {
				// Check if there is a two task 2 length loop
				if (relationMtx[i][j].equals(RelationType.LOOP2LEN) && relationMtx[j][i].equals(RelationType.LOOP2LEN))
					relationMtx[i][j] = RelationType.LOOP2TLEN;
				// Then check if there is a direct casual relation
				if ((relationMtx[i][j].equals(RelationType.SUCC) && !relationMtx[j][i].equals(RelationType.SUCC))
						|| relationMtx[i][j].equals(RelationType.LOOP2TLEN))
					relationMtx[i][j] = RelationType.DIRECT;
				// Check if is a parallel Relation
				else if (relationMtx[i][j].equals(RelationType.SUCC) && relationMtx[j][i].equals(RelationType.SUCC)
						&& !relationMtx[i][j].equals(RelationType.LOOP2TLEN))
					relationMtx[i][j] = RelationType.PARALLEL;
			}
		}
	}

	private void computeMendaciusDependecy() {
		for (int i = 0; i < relationMtx.length; i++) {
			for (int j = 0; j < relationMtx.length; j++) {
				if (relationMtx[i][j].equals(RelationType.DIRECT) && mendaciusProperty(i, j))
					relationMtx[i][j] = RelationType.MENDACIUS;
			}
		}
	}

	/*
	 * Check existence of a->x and y->b s.t. not y>x and not(x||b) and not(a||y)
	 */
	private boolean mendaciusProperty(int a, int b) {

		for (int col = 0; col < relationMtx.length; col++) {
			for (int row = 0; row < relationMtx.length; row++) {
				if (relationMtx[a][col].equals(RelationType.DIRECT) && relationMtx[row][b].equals(RelationType.DIRECT)
						&& !relationMtx[row][col].equals(RelationType.SUCC)
						&& !relationMtx[col][b].equals(RelationType.PARALLEL)
						&& !relationMtx[a][row].equals(RelationType.PARALLEL))
					return true;
			}
		}
		return false;
	}

	public RelationType getRelation(Event a, Event b) {
		return relationMtx[eventNameToMatrixIndex.get(a)][eventNameToMatrixIndex.get(b)];
	}

	private boolean computeInderectDependency(Event a, Event b, Case c) {
		boolean t = true;
		if (c.contains(a) && c.contains(b)) {
			int indexa = c.getTrace().indexOf(a);
			int indexb = c.getTrace().indexOf(b);
			if (indexa < indexb) {
				for (int i = indexa + 1; i < indexb; i++) {
					if (c.getTrace().get(i).equals(a) || c.getTrace().get(i).equals(b))
						t = false;
				}
			}
		}
		return t;
	}

	/*
	 * The eventLog has to be modified before passing it here, adding TI and TO
	 */
	public boolean isCDRpre(Event a, Event b, Event x, EventLog eventLog) {
		if (relationMtx[eventNameToMatrixIndex.get(a)][eventNameToMatrixIndex.get(b)].equals(RelationType.DIRECT))
			return true;
		Iterator<Case> it = eventLog.getIterator();
		while (it.hasNext()) {
			Case c = it.next();
			int indexx = c.getTrace().indexOf(x);
			int indexa = c.getTrace().indexOf(a);
			if (indexx == indexa - 1 && computeInderectDependency(a, b, c))
				return true;
		}
		return false;
	}

	public boolean isCDRpost(Event a, Event b, Event y, EventLog eventLog) {
		if (relationMtx[eventNameToMatrixIndex.get(a)][eventNameToMatrixIndex.get(b)].equals(RelationType.DIRECT))
			return true;
		Iterator<Case> it = eventLog.getIterator();
		while (it.hasNext()) {
			Case c = it.next();
			int indexy = c.getTrace().indexOf(y);
			int indexb = c.getTrace().indexOf(b);
			if (indexy == indexb + 1 && computeInderectDependency(a, b, c))
				return true;
		}
		return false;
	}

	public boolean isCDRprepost(Event a, Event b, Event x, Event y, EventLog eventLog) {
		if (relationMtx[eventNameToMatrixIndex.get(a)][eventNameToMatrixIndex.get(b)].equals(RelationType.DIRECT))
			return true;
		Iterator<Case> it = eventLog.getIterator();
		while (it.hasNext()) {
			Case c = it.next();
			int indexx = c.getTrace().indexOf(x);
			int indexy = c.getTrace().indexOf(y);
			int indexb = c.getTrace().indexOf(b);
			int indexa = c.getTrace().indexOf(a);
			if (indexy == (indexb + 1) && indexx == (indexa - 1) && computeInderectDependency(a, b, c))
				return true;
		}
		return false;
	}

	public boolean isCDRinvisibletask(Event x, Event y, EventLog eventLog) {
		Set<Event> entrichedT = eventLog.geAlphabet();
		entrichedT.add(TI);
		entrichedT.add(TO);

		for (Event a : entrichedT) {
			for (Event m : eventLog.geAlphabet()) {
				if (invisibleTaskxm(x, a, m, eventLog, entrichedT)) {
					for (Event b : entrichedT) {
						if (invisibleTasksmx(b, x, m, eventLog, entrichedT)
								&& invisibleTaskxy(x, y, eventLog, entrichedT))
							return true;
					}
				}
			}
		}

		return false;
	}

	private boolean invisibleTaskxy(Event x, Event y, EventLog eventLog, Set<Event> Te) {
		for (Event a1 : Te) {
			for (Event b1 : Te) {
				if ((a1.equals(TI) || eventLog.geAlphabet().contains(a1)) && eventLog.geAlphabet().contains(b1)) {
					for (Event a2 : Te) {
						for (Event b2 : Te) {
							if (eventLog.geAlphabet().contains(a2)
									&& (eventLog.geAlphabet().contains(b2) || b2.equals(TO))) {
								if (relationMtx[eventNameToMatrixIndex.get(a1)][eventNameToMatrixIndex.get(x)]
										.equals(RelationType.DIRECT)
										&& relationMtx[eventNameToMatrixIndex.get(x)][eventNameToMatrixIndex.get(b1)]
												.equals(RelationType.DIRECT)
										&& relationMtx[eventNameToMatrixIndex.get(a2)][eventNameToMatrixIndex.get(y)]
												.equals(RelationType.DIRECT)
										&& relationMtx[eventNameToMatrixIndex.get(y)][eventNameToMatrixIndex.get(b2)]
												.equals(RelationType.DIRECT)
										&& isCDRprepost(b1, a2, a1, b2, eventLog))
									return true;

							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean invisibleTasksmx(Event b, Event x, Event m, EventLog eventLog, Set<Event> Te) {
		if (b.equals(TO) || eventLog.geAlphabet().contains(b)) {
			for (Event a : eventLog.geAlphabet()) {
				if (relationMtx[eventNameToMatrixIndex.get(a)][eventNameToMatrixIndex.get(x)]
						.equals(RelationType.DIRECT)
						&& relationMtx[eventNameToMatrixIndex.get(x)][eventNameToMatrixIndex.get(b)]
								.equals(RelationType.DIRECT)
						&& isCDRpost(m, a, b, eventLog))
					return true;

			}
		}
		return false;
	}

	private boolean invisibleTaskxm(Event x, Event a, Event m, EventLog eventLog, Set<Event> Te) {
		if ((a.equals(TI) || eventLog.geAlphabet().contains(a))) {
			for (Event b : eventLog.geAlphabet()) {
				if (relationMtx[eventNameToMatrixIndex.get(a)][eventNameToMatrixIndex.get(x)]
						.equals(RelationType.DIRECT)
						&& relationMtx[eventNameToMatrixIndex.get(x)][eventNameToMatrixIndex.get(b)]
								.equals(RelationType.DIRECT)
						&& isCDRpre(b, m, a, eventLog)) {
					return true;
				}
			}
		}
		return false;
	}

}
