package edu.imt.utils;

import edu.imt.inputData.Trace;
import edu.imt.specification.MCRL2;

public class Utils {

	private static int i = 0;
	private static int j = 0;
	private static String PROCESS_NAME = "P";
	private static String EVENT_NAME = "e";

	private static int getProcessInt() {
		return i++;
	}

	private static int getNameInt() {
		return j++;
	}

	public static String getProcessName() {
		return PROCESS_NAME + getProcessInt();
	}

	public static String getEventName() {
		return EVENT_NAME + getNameInt();
	}

	public static String removeblankspaceAndDots(String s) {
		s = s.replaceAll(" ", "");
		s = s.replaceAll("[\\.\\(\\)$\\-&,]+", "");
		return s;
	}

	/**
	 * Tranform a trace = e1...en into a possibility formula of the following form
	 * tau*.e1.tau*. ... .tau*.en.tau*
	 * 
	 * @param t the trace
	 * @return a trace = e1...en into a possibility formula of the following form
	 *         tau*.e1.tau*. ... .tau*.en.tau*
	 */
	public static String writePossibilityFormula(Trace t) {
		String s = "<(" + MCRL2.TAU.toString() + ")*.";
		for (int i = 0; i < t.length(); i++) {
			s = s + t.getEvent(i).getName() + ".(" + MCRL2.TAU.toString() + ")*";
			if (i != t.length() - 1)
				s += ".";
		}
		s += ">true";
		return s;

	}
}
