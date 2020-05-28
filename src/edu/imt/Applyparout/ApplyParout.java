package edu.imt.Applyparout;

import edu.imt.specification.Mcrl2;
import edu.imt.specification.operators.Operator;
import edu.imt.specification.operators.Process;

public class ApplyParout {

	protected static Mcrl2 spec;
	public static int somma = 0;

	public static Process applyparout(Process p, Mcrl2 spec) {
		ApplyParout.spec = spec;
		Process pold = p;
		countPar(pold);
		Process pnew = parout(p);
		while (!pold.equals(pnew)) {
			countPar(pnew);
			System.out.println("parallel : " +somma);
			somma = 0;
			pold = pnew;
			pnew = parout(pold);
		}
		return pnew;
	}

	public static void countPar(Process p) {
		if (p.getOp() != null) {
			if (p.getOp().equals(Operator.PARALLEL)) {
				ApplyParout.somma = ApplyParout.somma + 1;
				// System.out.println(somma + " "+ p.toString());
			}
			for (int i = 0; i < p.size(); i++) {
				countPar(p.getProcess()[i]);
			}
		}
	}

	protected static boolean insideloop = false;

	private static Process parout(Process p) {
		if (hasParallel(p) && insideloop)
			p = new Loop().interpreter(p);
		else if (hasParallel(p)) {
			if (p.getOp().equals(Operator.PARALLEL))
				p = new Parallel().interpreter(p);
			else if (p.getOp().equals(Operator.CHOICE))
				p = new Choice().interpreter(p);
			else if (p.getOp().equals(Operator.SEQUENCE))
				p = new Sequence().interpreter(p);
			else if (p.getOp().equals(Operator.LOOP))
				p = new Loop().interpreter(p);
		} else if (p.getProcess() != null) {
			Process fathernew = new Process();
			fathernew.modifyProcessOp(p.getOp());
			int myhas = 0;
			if (p.getOp().equals(Operator.LOOP) && !insideloop) {
				insideloop = true;
				myhas = p.hashCode();
			}
			for (int i = 0; i < p.size(); i++) {
				Process newsun = parout(p.getProcess()[i]);
				fathernew.insertProcessAtPosition(newsun, i);
			}
			if(myhas == p.hashCode())
				insideloop = false;
			return fathernew;
		}
		return p;

	}

	/*
	 * Return true if it has a son with a Parallel Operator false otherwise
	 */
	private static boolean hasParallel(Process p) {
		if (p.getProcess() == null)
			return false;
		for (Process process : p.getProcess()) {
			if (process.getOp() != null && process.getOp().equals(Operator.PARALLEL))
				return true;
		}
		return false;
	}

}
