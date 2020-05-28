package edu.imt.Applyparout;

import edu.imt.specification.operators.Operator;
import edu.imt.specification.operators.Process;

public class Parallel extends AbstractParout{

	@Override
	public Process interpreter(Process p) {
		/*System.out.println("Eliminando il parallel");
		ApplyParout.somma =0;
		ApplyParout.countPar(p);
		System.out.println("inizio par inizion con par: " + ApplyParout.somma);
		ApplyParout.somma =0;*/
		
		Process parallelProcess = null;
		for (Process process : p.getProcess()) {
			if (process.getOp()!= null && process.getOp().equals(Operator.PARALLEL)) {
				parallelProcess = process;
				break;
			}
		}
		Process father = new Process();
		father.modifyProcessOp(Operator.PARALLEL);
		int currentIndex = 0;
		for (int i = 0; i < p.size(); i++) {
			if (p.getProcess()[i].equals(parallelProcess)) {
				for (Process pson : parallelProcess.getProcess()) {
					father.insertProcessAtPosition(pson, currentIndex);
					currentIndex += 1;
				}
			} else {
				father.insertProcessAtPosition(p.getProcess()[i], currentIndex);
				currentIndex += 1;
			}
		}
		/*ApplyParout.somma =0;
		ApplyParout.countPar(father);
		System.out.println("rimuovi par finisco con par: " + ApplyParout.somma);
		ApplyParout.somma =0;*/
		
		return father;
	}

}
