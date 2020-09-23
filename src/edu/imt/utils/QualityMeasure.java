package edu.imt.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.imt.inputData.EventLog;
import edu.imt.inputData.Trace;

public class QualityMeasure {

	private static String formulafilename = "formula";
	/*
	 * Measure the ability of algorithm to generate traces that are not in the log
	 * 1° Divide the log in k pieces Discovery the model of the k-1 pieces Measure
	 * the fitness of the mode_{k-1} over the log_k Repeat for every piece The
	 * avarage of the fitness is the value of the generalization
	 */
	/*public static double measureGeneralization(EventLog log, int k, int th) {
		double totalgeneralization = 0;
		int ncasepiece = log.size() / k;
		List<EventLog> setpieces = new ArrayList<EventLog>();
		int count = 0;
		int currentpiece = 0;
		EventLog tmpl = null;
		for (Trace c : log.getTrace()) {
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
					kminus1.addAll(setpieces.get(i).getTrace());
				}
			}
			SchimmAlgorithm sa = new SchimmAlgorithm(kminus1);
			BlockStructure schimm = sa.getFinalModel();
			MCRL2 m = OutputParsingUtils.generateMCRL2FromBlockStrcut(schimm, th);
			String spec = OutputParsingUtils.generateMcrl2File(m);
			String lpsfile = Terminal.frommcrl22lps(spec);
			double fitness = measureFitness(setpieces.get(currentpiece), lpsfile);
			try {
				Files.deleteIfExists(Path.of(spec));
				Files.deleteIfExists(Path.of(lpsfile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			totalgeneralization = totalgeneralization + fitness;
			currentpiece++;
		}
		return totalgeneralization / k;
	}*/

	/**
	 * Fitness it the ability to replicate the behaviour in the log, value of
	 * fitness rage from 0 to 1 In order to measure fitness in a mCRL2 specification
	 * we need to create a formula for each path <e1,e2,...,en>true and see if the
	 * LPS generated from it is able to replicate it
	 */
	public static double measureFitness(EventLog log, String file) {
		double numberofcase = 0;
		double riproducedtrace = 0;
		double totalfitness = 0;
		try {
			for (Trace t : log) {
				File f = File.createTempFile(formulafilename, FileExtension.MCF.toString(), new File(Terminal.path));
				BufferedWriter output = new BufferedWriter(new FileWriter(f));
				numberofcase += 1;
				output.write(Utils.writePossibilityFormula(t));
				output.close();
				String result = Terminal.proveFormula(file, f.getAbsolutePath());
				if (Boolean.valueOf(result))
					riproducedtrace += 1;
				else
					System.out.println("Trace : " + t.toString() + " non reproducible");
				f.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		totalfitness = totalfitness + (riproducedtrace / numberofcase);
		return totalfitness;
	}

}
