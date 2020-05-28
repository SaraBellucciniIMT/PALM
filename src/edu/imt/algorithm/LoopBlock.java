package edu.imt.algorithm;

import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import edu.imt.inputData.Case;
import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;

/*
 * The loop is represented as a choice among element
 */
public class LoopBlock {

	private Event e;
	private Event en;
	private static int index = 0;
	private Event name;
	private Case[] loop;
	private double floopcase = -1;
	private double floopeventlog = -1;
	private double flooptotal = -1;
	private double ntl = 0;
	private double NT = 0;
	private int lenghttrace =0;


	public LoopBlock(Event e, Event en) {
		this.loop = new Case[0];
		this.name = new Event();
		this.e = e;
		this.en = en;
	}

	public int getLenghtLoop() {
		return this.loop.length;
	}
	
	public boolean startEndevents(Event e, Event en) {
		if(this.e.equals(e) && this.en.equals(en))
			return true;
		return false;
	}
	
	public int repetion() {
		int repetion = (int)((getsumloopcase()/100)*(lenghttrace/ntl));
		return repetion;
	}

	// (n_l + L_l)/L_t if already a value, simply add the new one;
	public void setfloopcase(double nl, double Ll, double Lt) {
		double tmp = ((nl * Ll) / Lt)*100;
		if (this.floopcase == -1)
			this.floopcase = tmp;
		else {
			this.floopcase = this.floopcase + tmp;
		}
	}
	
	public double getsumloopcase() {
		return (this.floopcase/ntl); 
	}

	// n_t/N_t
	public void setfloopeventlog(double nt, double Nt,int lenghttrace) {
		this.lenghttrace = this.lenghttrace + lenghttrace;
		ntl += 1;
		if (this.NT == 0)
			this.NT = Nt;
		this.floopeventlog = getfloopevetlog();
	}

	public double getflooptotal() {
		if (floopeventlog == -1)
			getfloopevetlog();
		this.flooptotal = (floopcase + floopeventlog) / 2;
		return this.flooptotal;
	}

	public double getfloopevetlog() {
		return this.floopeventlog = (ntl / NT)*100;
	}

	public void setflooptotal() {
		if (flooptotal != -1) {
			this.flooptotal = (getsumloopcase() + floopeventlog) / 2;
		}
	}

	public Event getName() {
		if (name.isEmpty())
			this.name = new Event(String.valueOf(LoopBlock.index++));
		return this.name;
	}

	public EventLog[] getLoop() {
		EventLog[] logloop = new EventLog[loop.length];
		for (int i = 0; i < loop.length; i++) {
			EventLog e = new EventLog();
			e.add(loop[i]);
			logloop[i] = e;
		}
		return logloop;
	}

	// Add the loop, if is NOT already in the array in a discendent order (from
	// the longest to the shortest)
	public void addLoop(Case c) {
		if (containsloop(c))
			return;
		else
			this.loop = ArrayUtils.add(this.loop, c);

	}

	public void addLoop(Case... cases) {
		for (int i = 0; i < cases.length; i++)
			addLoop(cases[i]);
	}

	public boolean containsloop(Case c) {
		for (int i = 0; i < loop.length; i++) {
			if (loop[i].getTrace().equals(c.getTrace()))
				return true;
		}
		return false;
	}

	// Remove the corrent loops from the case c and add its name
	/*public Case removeAndAdd(Case c) {
		boolean anonymus = true;
		for (int i = 0; i < loop.length; i++) {
			int index = -1;
			while ((index = c.indexOfSubTrace(loop[i])) != -1) {
				c.removeIndex(index, index + loop[i].length() - 1);
				c.removeHB(c.getSubHBrel(loop[i]));
				if (anonymus) {
					c.add(index, name);
					anonymus = false;
				}
			}
		}
		return c;
	}*/

	public boolean isEmpty() {
		if (ArrayUtils.isEmpty(this.loop))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "LoopBlock [name=" + name + ", loop=" + Arrays.toString(loop) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + ((en == null) ? 0 : en.hashCode());
		result = prime * result + Arrays.hashCode(loop);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoopBlock other = (LoopBlock) obj;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		if (en == null) {
			if (other.en != null)
				return false;
		} else if (!en.equals(other.en))
			return false;
		if (!Arrays.equals(loop, other.loop))
			return false;
		return true;
	}


}
