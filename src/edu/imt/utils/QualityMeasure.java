package edu.imt.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import edu.imt.algorithm.SchimmAlgorithm;
import edu.imt.inputData.Case;
import edu.imt.inputData.EventLog;
import edu.imt.specification.Mcrl2;
import edu.imt.specification.operators.Process;

public class QualityMeasure {

	private static EventLog log;
	private static String formulafilename = "formula";
	private static String dotmcf = ".mcf";
	private static String mcrlfilename = "spec";

	/*
	 * Measure the ability of algorithm to generate traces that are not in the log
	 * 1° Divide the log in k pieces Discovery the model of the k-1 pieces Measure
	 * the fitness of the mode_{k-1} over the log_k Repeat for every piece The
	 * avarage of the fitness is the value of the generalization
	 */
	public static double measureGeneralization(File logfile, int k, int th) {
		try {
			QualityMeasure.log = InputParsingUtils.parseXes(logfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double totalgeneralization = 0;
		int ncasepiece = log.size() / k;
		List<EventLog> setpieces = new ArrayList<EventLog>();
		int count = 0;
		int currentpiece = 0;
		EventLog tmpl = null;
		for (Case c : log.getCase()) {
			if (count == 0)
				tmpl = new EventLog();
			if (count < (ncasepiece - 1)) {
				tmpl.add(c);
				count += 1;
			} else {
				tmpl.add(c);
				setpieces.add(tmpl);
				if (currentpiece == k)
					break;
				currentpiece += 1;
				count = 0;
			}
		}
		currentpiece = 0;
		while (currentpiece < k) {
			EventLog kminus1 = new EventLog();
			for (int i = 0; i < setpieces.size(); i++) {
				if (i != currentpiece) {
					kminus1.addAll(setpieces.get(i).getCase());
				}
			}
			SchimmAlgorithm sa = new SchimmAlgorithm(kminus1, th);
			Process schimm = sa.getFinalModel();
			System.out.println(schimm + " process to be translated");
			String dir = logfile.getParent() + "\\";
			Mcrl2 m  = OutputParsingUtils.generateMCRL2FromBlockStrcut(schimm);
			String spec = OutputParsingUtils.generateMcrl2File(dir+mcrlfilename, m);
			String lpsfile = Terminal.frommcrl22lps(spec);
			double fitness = measureFitness(setpieces.get(currentpiece), new File(lpsfile));
			Terminal.removeFile(new File(dir + spec + Terminal.dotmcrl2), new File(lpsfile), new File(dir + lpsfile.replace(Terminal.dotlps, Terminal.dotpbes)));
			totalgeneralization = totalgeneralization + fitness;
			currentpiece++;
		}

		return totalgeneralization / k;
	}

	/*
	 * Fitness it the ability to replicate the behaviour in the log, value of
	 * fitness rage from 0 to 1 In order to measure fitness in a mCRL2 specification
	 * we need to create a formula for each path <e1,e2,...,en>true and see if the
	 * LPS generated from it is able to replicate it
	 */
	public static double measureFitness(File logfile, File file) {
		try {
			QualityMeasure.log = InputParsingUtils.parseXes(logfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double numberofcase = 0;
		double riproducedtrace = 0;
		boolean reduction = false;
		if (Terminal.hasExtension(file, Terminal.dotlts)) {
			Scanner scann = new Scanner(System.in);
			System.out.println("Use tau-redaction ? (Y/N)");
			String r = scann.nextLine();
			if (r.equalsIgnoreCase("y")) {
				Terminal.ltsconvert(file.getAbsolutePath(), file.getAbsolutePath(),Terminal.taustar);
				reduction = true;
			}
		}
		double totalfitness = 0;
		try {
			Iterator<Case> it = log.getIterator();
			while (it.hasNext()) {
				File f = File.createTempFile(formulafilename, dotmcf, new File(file.getParent()));
				BufferedWriter output = new BufferedWriter(new FileWriter(f));
				output.write("<");
				Case c = it.next();
				numberofcase += 1;
				if (!reduction)
					output.write("(tau)*.");
				for (int i = 0; i < c.length(); i++) {
					output.write(c.getTrace().get(i).getName());
					if (!reduction)
						output.write(".(tau)*");
					if (i != c.length() - 1)
						output.write(".");
				}
				output.write(">true");
				output.close();
				String result;
				if (Terminal.hasExtension(file, Terminal.dotlps))
					result = Terminal.proveFormulaOnLPS(file.getAbsolutePath(), f.getAbsolutePath());
				else
					result = Terminal.proveFormulaOnLTS(file.getAbsolutePath(), f.getAbsolutePath());
				if (Boolean.valueOf(result))
					riproducedtrace += 1;
				else
					System.out.println("Case : " + c.getTrace().toString() + " non reproducible");
				f.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		totalfitness = totalfitness + (riproducedtrace / numberofcase);
		return totalfitness;
	}

	// In this case we use derectly the tau redaction and we do the verification
	// only over the lps for this reason
	private static double measureFitness(EventLog log, File file) {
		double numberofcase = 0;
		double riproducedtrace = 0;
		Iterator<Case> it = log.getIterator();
		while (it.hasNext()) {
			File f;
			try {
				f = File.createTempFile(formulafilename, dotmcf, new File(file.getParent()));
				BufferedWriter output = new BufferedWriter(new FileWriter(f));
				output.write("<");
				Case c = it.next();
				numberofcase += 1;
				output.write("(tau)*.");
				for (int i = 0; i < c.length(); i++) {
					output.write(c.getTrace().get(i).getName() + ".(tau)*");
					if (i != c.length() - 1)
						output.write(".");
				}
				output.write(">true");
				output.close();
				String result;
				result = Terminal.proveFormulaOnLPS(file.getAbsolutePath(), f.getAbsolutePath());
				if (Boolean.valueOf(result))
					riproducedtrace += 1;
				f.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (riproducedtrace / numberofcase);
	}

	/*
	 * 
	 */

}
