package edu.imt.Applyparout;

import edu.imt.specification.operators.Activity;
import edu.imt.specification.operators.Operator;
import edu.imt.specification.operators.Process;

public class Sequence extends AbstractParout{

	@Override
	public Process interpreter(Process p) {
		//System.out.println("Eliminando la sequenza " + ApplyParout.countPar(p));
		/*ApplyParout.somma =0;
		ApplyParout.countPar(p);
		System.out.println("inizio seq par inizion con par: " + ApplyParout.somma);
		ApplyParout.somma =0;*/
		
		Process parallelProcess = null;
		for (Process son : p.getProcess()) {
			if (son.getOp()!= null && son.getOp().equals(Operator.PARALLEL)) {
				parallelProcess = son;
				break;
			}
		}
		Process[] tmpnewfather = new Process[parallelProcess.size()];
		Process tmpnewson = new Process();
		//int currentindex = 0;
		Activity t0 = new Activity(gettemporary());
		int j=0;
		for (int i = 0; i < p.size(); i++) {
			if (p.getProcess()[i].equals(parallelProcess)) {
				tmpnewson.insertProcessAtPosition(new Process(t0), j);
				j++;
				tmpnewson.insertProcessAtPosition(parallelProcess.getProcess()[0], j);
				j++;
				tmpnewson.insertProcessAtPosition(new Process(t0), j);
				j++;
				continue;
			}
			tmpnewson.insertProcessAtPosition(p.getProcess()[i], j);
			j++;
		}
		tmpnewson.modifyProcessOp(Operator.SEQUENCE);
		tmpnewfather[0] = tmpnewson;
		for (int i = 1; i < parallelProcess.size(); i++) {
			Process tmp = new Process();
			tmp.modifyProcessOp(Operator.SEQUENCE);
			tmp.insertProcessAtPosition(new Process(t0), 0);
			tmp.insertProcessAtPosition(parallelProcess.getProcess()[i], 1);
			tmp.insertProcessAtPosition(new Process(t0), 2);
			tmpnewfather[i] = tmp;
		}
		updateCommAllHide(t0,parallelProcess.size());
		//System.out.println("elimando la scelta fine " + ApplyParout.countPar(new Process(tmpnewfather,Operator.PARALLEL)));
		/*ApplyParout.somma =0;
		ApplyParout.countPar(new Process(tmpnewfather, Operator.PARALLEL));
		System.out.println("rimuovi seq par finisco con par: " + ApplyParout.somma);
		ApplyParout.somma =0;*/
		
		return new Process(tmpnewfather, Operator.PARALLEL);	
	}

	

	
	
}
