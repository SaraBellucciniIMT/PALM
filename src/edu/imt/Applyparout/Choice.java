package edu.imt.Applyparout;

import java.util.ArrayList;
import java.util.List;
import edu.imt.specification.operators.Activity;
import edu.imt.specification.operators.Operator;
import edu.imt.specification.operators.Process;

public class Choice extends AbstractParout {

	@Override
	public Process interpreter(Process p) {

		Process parallelProcess = null;
		for (Process process : p.getProcess()) {
			if (process.getOp() != null && process.getOp().equals(Operator.PARALLEL)) {
				parallelProcess = process;
				break;
			}
		}
		List<Process> listofson = new ArrayList<Process>(parallelProcess.size());

		Activity t0 = new Activity(gettemporary());
		for (int i = 0; i < parallelProcess.size(); i++) {
			Process tmp = new Process();
			tmp.modifyProcessOp(Operator.SEQUENCE);
			tmp.insertProcessAtPosition(new Process(t0), 0);
			tmp.insertProcessAtPosition(parallelProcess.getProcess()[i], 1);
			tmp.insertProcessAtPosition(new Process(t0), 2);
			listofson.add(i, tmp);
		}

		updateCommAllHide(t0, parallelProcess.size());

		for (int j = 0; j < p.size(); j++) {
			if (!p.getProcess()[j].equals(parallelProcess)) {
				Process tmp = new Process();
				Activity t1 = new Activity(gettemporary());
				tmp.modifyProcessOp(Operator.SEQUENCE);
				tmp.insertProcessAtPosition(new Process(t1), 0);
				tmp.insertProcessAtPosition(p.getProcess()[j], 1);
				tmp.insertProcessAtPosition(new Process(t1), 2);
				Process old = listofson.get(0);
				if (old.getOp().equals(Operator.CHOICE))
					listofson.get(0).insertProcessAtPosition(tmp, old.size());
				else {
					Process[] pr = { old, tmp };
					Process choice = new Process(pr, Operator.CHOICE);
					listofson.set(0, choice);
				}
				Process tt = new Process(tmp.getProcess(), tmp.getOp());
				tt.removeElementAtPosition(1);
				for (int k = 1; k < listofson.size(); k++) {
					if (old.getOp().equals(Operator.CHOICE)) {
						listofson.get(k).insertProcessAtPosition(tt, old.size());
					} else {
						Process[] pr = { listofson.get(k), tt };
						Process choice = new Process(pr, Operator.CHOICE);
						listofson.set(k, choice);
					}
				}
				updateCommAllHide(t1, parallelProcess.size());
			}

		}

		Process[] sons = new Process[listofson.size()];
		for (int i = 0; i < sons.length; i++)
			sons[i] = listofson.get(i);
		Process father = new Process(sons, Operator.PARALLEL);
		return father;
	}

}
