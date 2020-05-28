package edu.imt.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import edu.imt.specification.operators.Activity;
import edu.imt.specification.operators.Operator;

public class ThreadInterleaving extends Thread {

	static private Pattern activity = Pattern.compile("[a-z]");
	static protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	static protected String parethesis = "\\(.*\\)";
	static protected BlockingQueue<Activity[]> sharedWaitingArray = new LinkedBlockingQueue<Activity[]>();
	// static sharedCommunication =
	protected InterleavingPath ip = new InterleavingPath();
	private Activity process;
	boolean block = false;
	// private boolean doubleblock = false;
	private Deque<Activity> remainedProcess = new ArrayDeque<Activity>();

	public ThreadInterleaving(Activity process, InterleavingPath ip) {
		this.ip = ip;
		this.process = process;
	}

	public void run() {
		List<Activity> tmp = createSingleTrace(process);
		if (tmp != null && !tmp.isEmpty())
			ip.addPath(tmp);
		if (!remainedProcess.isEmpty()) {
			remainedProcess.forEach(p -> {
				try {
					InputParsingUtils.remainedProcess.put(p);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		System.out.println(remainedProcess);

	}

	private List<Activity> createSingleTrace(Activity s) {
		System.out.println(s);
		List<Activity> tmpSet = new ArrayList<Activity>();
		s = s.takeOfParenthesis();
		if (tmpSet.isEmpty())
			tmpSet = matchPlus(s);
		if (tmpSet.isEmpty())
			tmpSet = matchSequence(s);

		if (tmpSet.isEmpty()) {
			Activity t = matchActivity(s);
			if (!t.isEmpty())
				tmpSet.add(t);
		}

		return tmpSet;
	}

	protected List<Activity> matchPlus(Activity s) {
		List<Activity> tmp = new ArrayList<Activity>();
		if (!s.parseOnSymbol(Operator.CHOICE))
			return tmp;
		// try to split the string based on the plus

		Activity[] splitted = s.splittingSy(Operator.CHOICE);
		for (Activity st : splitted) {
			block = false;
			List<Activity> tmptmp = createSingleTrace(st);

			if (tmptmp != null && !tmptmp.isEmpty())
				tmp.addAll(tmptmp);
		}
		Activity tmpRP = new Activity();
		if (!remainedProcess.isEmpty()) {
			while (remainedProcess.size() != 1)
				tmpRP = tmpRP.concat(remainedProcess.poll(), new Activity(Operator.CHOICE.getStringOp()));

			tmpRP = tmpRP.concat(remainedProcess.poll());
			remainedProcess.add(tmpRP);
		}
		return tmp;

	}

	protected List<Activity> matchSequence(Activity s) {
		List<Activity> toBeReturned = new ArrayList<Activity>();
		if (!s.parseOnSymbol(Operator.SEQUENCE))
			return toBeReturned;

		Activity[] splitted = s.splittingSy(Operator.SEQUENCE);
		List<Activity> concatResult = new ArrayList<>();
		for (int i = 0; i < splitted.length; i++) {
			List<Activity> tmp = createSingleTrace(splitted[i]);
			if (concatResult.isEmpty()) {
				concatResult.addAll(tmp);
				if (block) {
					Activity tmptmp = new Activity();

					if (i < (splitted.length - 1))
						tmptmp = splitted[i + 1];
					for (int j = i + 2; j < splitted.length; j++) {
						tmptmp = tmptmp
								.concat(new Activity(Operator.SEQUENCE.getStringOp()).concat(splitted[j]));
					}
					if (!tmptmp.isEmpty()) {
						remainedProcess.add(tmptmp);
					}
					break;
				}
				continue;
			}
			List<Activity> newCR = new ArrayList<Activity>();
			if (!tmp.isEmpty()) {
				for (int j = 0; j < concatResult.size(); j++) {
					int k = 0;
					while (k < tmp.size()) {
						List<String> taks = new ArrayList<String>();
						taks.addAll(concatResult.get(j).getActivity());
						taks.addAll(tmp.get(k).getActivity());
						Activity a = new Activity(taks);
						newCR.add(a);
						k++;
					}
				}
				concatResult = newCR;
			}
			if (block) {
				toBeReturned.addAll(concatResult);
				Activity tmptmp = new Activity();
				// if (!doubleblock) {
				if (i < (splitted.length - 1))
					tmptmp = splitted[i + 1];
				for (int j = i + 2; j < splitted.length; j++) {
					tmptmp = tmptmp.concat(new Activity(Operator.SEQUENCE.getStringOp()).concat(splitted[j]));
				}
				if (!tmptmp.isEmpty())
					remainedProcess.add(tmptmp);
				return toBeReturned;

			}
		}
		toBeReturned.addAll(concatResult);
		return toBeReturned;
	}

	protected Activity matchActivity(Activity s) {
		if (s.contains(".") || s.contains("+") || s.contains("\\|\\|"))
			return new Activity();

		if (!InputParsingUtils.spec.getAllowedAction().isEmpty()
				&& !InputParsingUtils.spec.getAllowedAction().contains(s)) {
			try {
				communicationStopping(s);
			} catch (InterruptedException e) {
				System.out.println("Trying to stop the communication");
			}
			block = true;
			return new Activity();
		}
		if (s.equals(new Activity("tau")) || !activity.matcher(s.getName()).matches())
			return new Activity();
		return s;
	}

	//Forse ci andava synchronized
	protected void communicationStopping(Activity s) throws InterruptedException {
		// First step is : verify if the shared waiting array already contain the
		// temporary variable
		Activity[] checkMyPresence = null;
		for (Activity[] pair : InputParsingUtils.spec.getCommFunction().keySet()) {
			for (Activity a : pair) {
				if (a.equals(s))
					checkMyPresence = pair;
			}
		}
		synchronized (sharedWaitingArray) {

			if (sharedWaitingArray.contains(checkMyPresence)) {
				ip.addEndCommunication(InputParsingUtils.spec.getCommFunctionElement(checkMyPresence));
				sharedWaitingArray.remove(checkMyPresence);
			} else {
				// the communication can't take place because the corrispondent temporary value
				// has still not been see
				// Add to che queue that this thread is waiting on this variable

				for (Activity[] arr : sharedWaitingArray) {
					if (checkSubArray(checkMyPresence, arr)) {
						sharedWaitingArray.remove(arr);
						Activity[] arr2 = ArrayUtils.add(arr, s);
						sharedWaitingArray.add(arr2);
						break;
					}
				}
				if (sharedWaitingArray.isEmpty())
					sharedWaitingArray.add(checkMyPresence);
			}
		}
		int maxWaitTimeToCommunicate = 0;
		while (maxWaitTimeToCommunicate < 300 && isInShareWaitingArray(s)) {
			Thread.sleep(100);
			maxWaitTimeToCommunicate++;
		}

	}

	private boolean isInShareWaitingArray(Activity s) {
		for (Activity[] arr : sharedWaitingArray) {
			for (Activity a : arr) {
				if (a.equals(s)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkSubArray(Object[] arr1, Object[] arr2) {
		if (arr1.equals(arr2))
			return false;
		for (Object o : arr2) {
			if (!ArrayUtils.contains(arr1, o))
				return false;
		}
		return true;
	}

	protected static String[] splitedOnSymbol(String sy, String s) {
		int j = 0;
		int minsub = 0;
		int maxsub = 1;
		int par = 0;
		List<String> splittedtmp = new ArrayList<String>();
		while (j <= s.length()) {
			if (j == s.length()) {
				splittedtmp.add(s.substring(minsub, maxsub - 1));
				j++;
				continue;
			}
			if (String.valueOf(s.charAt(j)).equals("(")) {
				par++;
				j++;
				maxsub++;
				continue;
			}
			if (String.valueOf(s.charAt(j)).equals(")")) {
				par--;
				j++;
				maxsub++;
				continue;
			}

			if (par == 0 && String.valueOf(s.charAt(j)).equals(sy)) {
				splittedtmp.add(s.substring(minsub, maxsub - 1));
				minsub = j + 1;
				maxsub = j + 1;
			}
			maxsub++;
			j++;
		}
		String[] splitted = new String[splittedtmp.size()];
		splitted = splittedtmp.toArray(splitted);
		return splitted;
	}
}
