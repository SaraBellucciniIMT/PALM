package edu.imt.specification.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

/*
 * Basic element of each process
 */

public class Activity {

	static public Pattern multiaction = Pattern.compile("[a-z]+(\\|[a-z]+)+");
	private List<String> name; 

	public Activity(List<String> tasks) {
		this.name = new ArrayList<String>();
		this.name.addAll(tasks);
	}

	public Activity(String t) {
		this.name = new ArrayList<String>();
		if (multiaction.matcher(t).matches()) {
			String check = "";
			int i = 0;
			while (i < t.length()) {
				String charat = String.valueOf(t.charAt(i));
				if (!charat.equals(Operator.PARALLEL.getStringOp()))
					check = check.concat(charat);
				else {
					this.name.add(check);
					this.name.add(Operator.PARALLEL.getStringOp());
					check = "";
				}
				i++;
			}
			this.name.add(check);
		} else
			this.name.add(t);
	}

	public Activity() {
		this.name = new ArrayList<String>();
	}

	@Override
	public String toString() {
		return this.name.toString();
	}

	public int size() {
		return this.name.size();
	}

	public boolean isSubActivity(Activity a) {
		if (a.getActivity().containsAll(name))
			return true;
		else
			return false;
	}
	
	public Activity subActivity(int startIndex) {
		return new Activity(this.name.subList(startIndex, name.size()));
	}

	public Activity subActivity(int startIndex, int endIndex) {
		return new Activity(this.name.subList(startIndex, endIndex));
	}

	public boolean isEmpty() {
		if (this.name.isEmpty())
			return true;
		return false;
	}
	
	public boolean contains(Activity a) {
		List<String> subTrace = a.getActivity();
		if(name.size()<a.size())
			return false;
		List<String> tmpName = new ArrayList<String>(name);
		for(String act : subTrace) {
			if(tmpName.contains(act)){
				tmpName.remove(act);
			}else
				return false;
		}
		return true;
	}
	
	
	public void addTask(String t) {
		this.name.add(t);
	}
	public boolean contains(Operator sy) {
		for(String t : this.name) {
			if(t.contains(sy.getStringOp()))
				return true;
		}
		return false;
	}

	public boolean contains(String s) {
		for (String t : name) {
			if (t.equals(s))
				return true;
		}
		return false;
	}

	public Activity concat(String... s) {
		for (String act : s)
			this.name.add(act);
		return new Activity(name);

	}

	public Activity concat(Activity... a) {
		for (Activity act : a)
			this.name.addAll(act.getActivity());

		return new Activity(name);
	}

	public Activity concat(Operator sy) {
		this.name.add(sy.getStringOp());
		return new Activity(name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public Activity takeOfParenthesis() {
		if (this.name.get(0).equals("(")) {
			int j = 0;
			int par = 0;
			while (j < this.name.size()) {
				if (String.valueOf(this.name.get(j)).equals("(")) {
					par++;
					j++;
					continue;
				}
				if (String.valueOf(this.name.get(j)).equals(")"))
					par--;

				if (String.valueOf(this.name.get(j)).equals(")") && par == 0 && j != (this.name.size() - 1)) {
					return this;
				}
				j++;
			}
		} else {
			return this;
		}
		return new Activity(this.name.subList(1, this.name.size() - 1));

	}

	public boolean parseOnSymbol(Operator sy) {
		if (checkPriority(sy))
			return false;
		int j = 0;
		int par = 0;
		while (j < this.name.size()) {
			if (String.valueOf(this.name.get(j)).equals("(")) {
				par++;
				j++;
				continue;
			}
			if (String.valueOf(this.name.get(j)).equals(")")) {
				par--;
				j++;
				continue;
			}

			if (par == 0 && String.valueOf(this.name.get(j)).equals(sy.getStringOp()))
				return true;
			if (par == 0 && sy.getStringOp().equals("|") && (j + 2 < this.name.size())
					&& this.name.get(j + 1).equals(sy.getStringOp()))
				return true;
			j++;
		}
		return false;
	}

	private boolean checkPriority(Operator sy) {
		boolean existence = false;
		if (sy.equals(Operator.SEQUENCE))
			existence = parseOnSymbol(Operator.CHOICE);
		else if (sy.equals(Operator.CHOICE))
			existence = parseOnSymbol(Operator.PARALLEL);
		return existence;
	}

	public Activity[] splittingSy(Operator sy) {
		Activity[] splitted = new Activity[0];
		int j = 0;
		int start = 0;
		int par = 0;
		while (j < this.name.size()) {
			if (String.valueOf(this.name.get(j)).equals("(")) {
				par++;
				j++;
				continue;
			}
			if (String.valueOf(this.name.get(j)).equals(")")) {
				par--;
				j++;
				continue;
			}

			if (par == 0 && sy.getStringOp().equals("|") && (j + 2 < this.name.size())
					&& this.name.get(j + 1).equals(sy.getStringOp()) && this.name.get(j).equals(sy.getStringOp())) {
				splitted = ArrayUtils.add(splitted, new Activity(this.name.subList(start, j )));
				j = j + 2;
				start = j;
				continue;
			}
			if (par == 0 && String.valueOf(this.name.get(j)).equals(sy.getStringOp())) {
				splitted = ArrayUtils.add(splitted, new Activity(this.name.subList(start, j)));
				start = j + 1;
			}

			j++;
		}
		if (start != this.name.size()) {
			splitted = ArrayUtils.add(splitted, new Activity(this.name.subList(start, j)));
		}
		return splitted;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Activity other = (Activity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		String s = "";
		for (int i = 0; i < name.size(); i++)
			s = s.concat(name.get(i).toString());
		return s;
	}

	public List<String> getActivity() {
		return this.name;
	}
}
