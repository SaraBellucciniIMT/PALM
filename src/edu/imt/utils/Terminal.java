package edu.imt.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.synth.SynthSliderUI;

import com.google.common.collect.Lists;

import edu.imt.algorithm.SchimmAlgorithm;
import edu.imt.inputData.EventLog;
import edu.imt.specification.Mcrl2;
import edu.imt.specification.operators.Process;
import edu.imt.utils.CoverabilityGraph;
import edu.imt.utils.InputParsingUtils;
import edu.imt.utils.OutputParsingUtils;
/*
 * case 2: System.out.println("Insert path :"); scan = new Scanner(System.in);
 * if (generatexesfile(new File(scan.nextLine()))) break; else
 * System.out.println("something wrong"); case 3:
 * System.out.println("Insert path FIRST mcrl2 log \n"); scan = new
 * Scanner(System.in); File firstFile = new File(scan.nextLine());
 * System.out.println("Insert path SECOND mcrl2 log \n"); scan = new
 * Scanner(System.in); File secondFile = new File(scan.nextLine()); String
 * fileName = firstFile.getName().replace(mcrl2, "") +
 * secondFile.getName().replace(mcrl2, "") + mcrl2;
 * OutputParsingUtils.mergeSpecMCRL2SameActvityNameComm(firstFile, secondFile,
 * firstFile.getPath() + fileName); break;
 */

/* 
 *  This class provide the interface with the terminal
 * 	The user can do 3 different actions:
 * 	1 - Generate mCRL2 specification from XES
 *  2-Generate XES from mCRL specification
 *  3-Merge two mCRL2 speicification
 */
public class Terminal {

	private static Scanner scan = new Scanner(System.in);
	public final static String dotxes = ".xes";
	public final static List<String> equivalences = Lists.newArrayList("bisim", "branching-bisim", "dpbranching-bisim",
			"weak-bisim", "dpweak-bisim", "sim", "trace", "weak-trace");
	public final static String dotmcrl2 = ".mcrl2";
	public final static String dotlps = ".lps";
	public final static String dotlts = ".lts";
	public final static String dotsg = ".sg";
	public final static String dotfsm = ".fsm";
	public final static String dotpbes = ".pbes";
	public final static String error = "something went wrong";
	public final static String taustar = "tau-star";

	public static void terminal() {
		while (true) {
			System.out.println("Press: \n" + "1 -> Synthesis methodology\n" + "2 -> mCRL2 to LPS \n"
					+ "3 -> Evalutation metrics \n" + "4 -> Coverability Graph to LTS \n"
					+ "5 -> Coverability Graph to FSM \n" + "6 -> LPS to LTS \n" + "7 -> LTS to FSM\n"
					+ "8 -> Euivalence checking (inputs format: .lts,.fsm files) (both files same directory and same extension)\n"
					+ "9 -> Reduce xes file \n" + "10 -> Testing\n" + "11 -> Collaboration composition");
			int i = scan.nextInt();
			switch (i) {
			case 1:
				caseOne();
				break;
			case 2:
				caseTwo();
				break;
			case 3:
				caseThree();
				break;
			case 4:
				caseFour();
				break;
			case 5:
				caseFive();
				break;
			case 6:
				caseSix();
				break;
			case 7:
				caseSeven();
				break;
			case 8:
				caseEigth();
				break;
			case 9:
				caseNine();
				break;
			case 10:
				caseTen();
			case 11:
				caseEleven();
			default:
				break;

			}
			continueOrExit();
		}
	}

	// Compute the synthesis metholody
	private static void caseOne() {
		File f = scanFile("Insert path to EventLog file: ");
		// Only loops with a flooptotal>thresold will be treated as new process loops
		// the others are unrooled
		System.out.println("Set thresold for loop disclosure (natual number from 0 to 100): ");
		int th = scan.nextInt();
		scan = new Scanner(System.in);
		long startTime = getCurrentTime();

		System.out.println("START TIME: " + java.time.LocalTime.now());
		generatemcrl2file(f, th);
		long endTime = getCurrentTime();
		System.out.println("Time model generation: " + computeTimeSpans(startTime, endTime) + "s");

	}

	// mCRL2 to LPS file
	private static String caseTwo() {
		File fmcrl = scanFile("Insert path model");
		long startTime1 = getCurrentTime();
		System.out.println("START TIME: " + java.time.LocalTime.now());
		String mcrl22lps = frommcrl22lps(fmcrl.getAbsolutePath());
		long endTime1 = getCurrentTime();
		System.out.println("Time to generate the LPS model: " + computeTimeSpans(startTime1, endTime1) + "s");
		System.out.println("File lps generated in : " + mcrl22lps);
		return mcrl22lps;
	}

	// Measure fitness and generalization
	private static String caseThree() {
		scan = new Scanner(System.in);
		System.out.println("1-> FITNESS \n" + "2-> GENERALIZATION");
		int j = scan.nextInt();
		switch (j) {
		case 1:
			File eventlog = scanFile("Insert path to EventLog file: ");
			File f = scanFile("Insert path (mcrl2/lps) model: ");
			if (hasExtension(f, dotmcrl2))
				f = new File(frommcrl22lps(f.getAbsolutePath()));

			double fitness = QualityMeasure.measureFitness(eventlog, f);
			System.out.println(fitness);
			break;
		case 2:
			File eventlog1 = scanFile("Insert path to EventLog file: ");
			System.out.println("Insert the k coefficient to use (a natural number):");
			int k = scan.nextInt();
			System.out.println("Set thresold for loop disclosure (natual number from 0 to 100): ");
			int th1 = scan.nextInt();
			double generalization = QualityMeasure.measureGeneralization(eventlog1, k, th1);
			System.out.println(generalization);
			break;
		}
		return "";
	}

	// Coverability graph to LTS file
	private static List<String> caseFour() {
		File filesg = scanFile("Insert path sg model");
		String mcrl2fromsg = fromSGtoMCRL2(filesg);
		String mcrllps = frommcrl22lps(mcrl2fromsg);
		String lpslts = fromlps2lts(mcrllps);
		System.out.println("SG file transformed in LTS file : " + lpslts);
		return Lists.newArrayList(mcrl2fromsg, mcrllps, lpslts);
	}

	// Coverability graph to FSM
	private static String caseFive() {
		File file = scanFile("Insert path sg model");
		String fsmfromfile = OutputParsingUtils.generateFSMfileFromCG(InputParsingUtils.parseSGfile(file),
				file.getAbsolutePath());
		System.out.println("SG to FSM file : " + fsmfromfile);
		return fsmfromfile;
	}

	// LPS to LTS
	private static String caseSix() {
		File f = scanFile("Insert path LPS model");
		while (!hasExtension(f, dotlps))
			f = scanFile("Wrong file extension, insert path to LPS model");
		String lts = fromlps2lts(f.getAbsolutePath());
		System.out.println("LPS to LTS file : " + lts);
		return lts;
	}

	private static String caseSeven() {
		File f = scanFile("Insert path LTS model");
		while (!hasExtension(f, dotlts)) {
			f = scanFile("Wrong file extension, insert path to LTS model");
		}
		String s2 = fromLTStoFSM(f.getAbsolutePath());
		System.out.println("LTS file transformed in FSM file : " + s2);
		return s2;
	}

	private static void caseEigth() {
		File file1 = scanFile("Insert path first model");
		File file2 = scanFile("Insert path second model");
		for (int i = 0; i < equivalences.size(); i++) {
			if (compareModelsWithEC(file1, file2, equivalences.get(i)))
				break;
		}
	}

	private static List<String> caseNine() {
		File fullfile = scanFile("Insert path model");
		File halffile = new File(fullfile.getParent() + "/ half" + fullfile.getName());
		System.out.println("Insert percentage: ");
		int perc = scan.nextInt();
		EventLog e;
		try {
			e = InputParsingUtils.parseXes(fullfile);
			System.out.println("Trace= " + e.getTraceCardinality() + " Events= " + e.getEventCardinality());
			OutputParsingUtils.cutLogEvents(fullfile, halffile, perc);
			EventLog e1 = InputParsingUtils.parseXes(halffile);
			System.out.println("Trace= " + e1.getTraceCardinality() + " Events= " + e1.getEventCardinality());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return Lists.newArrayList();
	}

	// Take as input a sg file and a lps file and make the equivalence
	private static void caseTen() {
		File f = scanFile("---Enter coverabily graph (.sg):");
		String sgfsm = OutputParsingUtils.generateFSMfileFromCG(InputParsingUtils.parseSGfile(f), f.getAbsolutePath());
		String sglps = frommcrl22lps(fromSGtoMCRL2(f));

		System.out.println("Compute Fitness(true/false)?");
		scan = new Scanner(System.in);
		Boolean b = scan.nextBoolean();
		if (b) {
			File f2 = scanFile("---Enter EventLog (.xes) file: ");
			double fitness = QualityMeasure.measureFitness(f2, new File(sglps));
			System.out.println("Fitness: " + fitness);
		}

		boolean bisimeq = false;
		String sgmcrllts = fromlps2lts(sglps);
		String ltsfsm = fromLTStoFSM(sgmcrllts);
		System.out.println("---COMPARISON " + sgfsm + " " + ltsfsm + "---");
		for (int i = 0; i < equivalences.size(); i++) {
			if (compareModelsWithEC(new File(sgfsm), new File(ltsfsm), equivalences.get(i)))
				break;
		}

		System.out.println("Compute Equivalence(true/false)?");
		scan = new Scanner(System.in);
		b = scan.nextBoolean();
		if (b) {
			System.out.println("---------");
			File f1 = scanFile("---Enter LPS file_");
			String lpslts = fromlps2lts(f1.getAbsolutePath());
			for (int i = 0; i < equivalences.size(); i++) {
				if (compareModelsWithEC(new File(lpslts), new File(sgmcrllts), equivalences.get(i))) {
					if (i == 0)
						bisimeq = true;
					break;
				}
			}
			System.out.println("---------------");
			if (!bisimeq) {
				System.out.println("---TAU-REDACTION COMPARISON---");
				ltsconvert(lpslts, lpslts, taustar);
				ltsconvert(sgmcrllts, sgmcrllts, taustar);
				for (int i = 0; i < equivalences.size(); i++) {
					if (compareModelsWithEC(new File(lpslts), new File(sgmcrllts), equivalences.get(i)))
						break;

				}
				System.out.println("---------------");
			}
		}

	}

	private static void caseEleven() {
		List<Mcrl2> mcrl2list = new ArrayList<>();
		List<String> filemcrl = new ArrayList<>();
		while (true) {
			File f = scanFile("Insert path to EventLog file: ");
			System.out.println("Set thresold for loop disclosure (natual number from 0 to 100): ");
			int th = scan.nextInt();
			scan = new Scanner(System.in);
			String fmcrl2 = "";
			try {
				if (f.exists()) {
					SchimmAlgorithm sa = new SchimmAlgorithm(InputParsingUtils.parseXes(f), th);
					String fileName = f.getParent() + "\\" + f.getName().replace(dotxes, "");
					Mcrl2 mcrl = OutputParsingUtils.generateMCRL2FromBlockStrcut(sa.getFinalModel());
					mcrl.addMessage(InputParsingUtils.parseXesMessage(f));
					mcrl2list.add(mcrl);
					fmcrl2 = OutputParsingUtils.generateMcrl2File(fileName, mcrl);
					filemcrl.add(fmcrl2);
				} else {
					System.err.println(f + " NOT FOUND");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			System.out.println("CONTINUE (Y/N) :");
			String r = scan.nextLine();
			while (!r.equalsIgnoreCase("N") && !r.equalsIgnoreCase("Y")) {
				System.out.println("command not available, try again ... ");
				r = scan.nextLine();
			}
			if (r.equalsIgnoreCase("N"))
				break;
		}
		OutputParsingUtils.mergeMCRL2(mcrl2list, filemcrl);
	}

	private static File scanFile(String stringtoprint) {
		System.out.println(stringtoprint);
		scan = new Scanner(System.in);
		File filename1 = new File(scan.nextLine());
		while (!filename1.exists()) {
			System.out.println(filename1 + " has not been found, try again: ");
			filename1 = new File(scan.nextLine());
		}

		return filename1;
	}

	private static boolean compareModelsWithEC(File f1, File f2, String equivalence) {
		String namef1 = f1.getAbsolutePath();
		String namef2 = f2.getAbsolutePath();
		String f1mcrllps = "";
		String f2mcrllps = "";
		List<String> filelist = new ArrayList<String>();
		if (hasExtension(f1, dotmcrl2)) {
			f1mcrllps = frommcrl22lps(namef1);
			filelist.add(f1mcrllps);
			namef1 = fromlps2lts(f1mcrllps);
		} else if (hasExtension(f1, dotlps))
			namef1 = fromlps2lts(namef1);

		filelist.add(namef1);

		if (hasExtension(f2, dotmcrl2)) {
			f2mcrllps = frommcrl22lps(namef2);
			filelist.add(f2mcrllps);
			namef2 = fromlps2lts(f2mcrllps);
		} else if (hasExtension(f2, dotlps))
			namef2 = fromlps2lts(namef2);
		filelist.add(namef2);
		String result = ltscompare(namef1, namef2, equivalence);
		if (result.contains("LTSs are equal"))
			return true;
		return false;
	}

	public static boolean hasExtension(File name, String s) {
		if (name.getName().endsWith(s))
			return true;
		return false;
	}

	protected static String fromlps2lts(String filename) {
		String lps2lts = "lps2lts -v -rjitty " + filename + " " + filename.replace(dotlps, dotlts);
		runmcrlcommand(lps2lts);
		return filename.replace(dotlps, dotlts);
	}

	protected static String frommcrl22lps(String filename) {
		String mcrl22lps = "mcrl22lps -v -lstack -rjitty " + filename + " " + filename.replace(dotmcrl2, dotlps);
		runmcrlcommand(mcrl22lps);
		return filename.replace(dotmcrl2, dotlps);
	}

	protected static void removeFile(File... f) {
		for (int i = 0; i < f.length; i++) {
			f[i].delete();
		}
	}

	public static String fromLTStoFSM(String filename) {
		String namefsm = filename.replace(dotlts, dotfsm);
		String ltsconvert = "ltsconvert " + filename + " " + namefsm;
		runmcrlcommand(ltsconvert);
		List<String> memory = new ArrayList<String>();
		File f = new File(namefsm);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String st;
			while ((st = br.readLine()) != null) {
				String[] split = st.split(" ");
				if (split.length > 2 && split[2].equals("\"Terminate\""))
					continue;
				else
					memory.add(st);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		f.delete();
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(namefsm));
			for (int i = 0; i < memory.size(); i++) {
				output.write(memory.get(i) + "\n");
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return namefsm;
	}

	protected static String ltscompare(String f1, String f2, String equivalence) {
		String ltscompare = "ltscompare -e" + equivalence + " " + f1 + " " + f2;
		return runmcrlcommand(ltscompare);
	}

	protected static String proveFormulaOnLTS(String f1, String formula) {
		String lts2pbes = "lts2pbes -v -f " + formula + " " + f1 + " " + f1.replace(dotlts, dotpbes);
		runmcrlcommand(lts2pbes);
		String pbes2bool = "pbes2bool " + f1.replace(dotlts, dotpbes);
		return runmcrlcommand(pbes2bool);
	}

	protected static String proveFormulaOnLPS(String f1, String formula) {
		String lps2pbes = "lps2pbes -v -c -m -f " + formula + " " + f1 + " " + f1.replace(dotlps, dotpbes);
		runmcrlcommand(lps2pbes);
		String pbes2bool = "pbes2bool " + f1.replace(dotlps, dotpbes);
		return runmcrlcommand(pbes2bool);
	}

	protected static void ltsconvert(String inf, String outf, String reduction) {
		String ltsconvert = "ltsconvert -c -e" + reduction + " " + inf + " " + outf;
		runmcrlcommand(ltsconvert);
	}

	protected static String fromSGtoMCRL2(File f) {
		CoverabilityGraph cg = InputParsingUtils.parseSGfile(f);
		return OutputParsingUtils.generatemCRL2fromCG(cg, f);
	}

	private static String generatemcrl2file(File f, int th) {
		EventLog log = null;
		String fmcrl2 = "";
		try {
			if (f.exists()) {
				log = InputParsingUtils.parseXes(f);
				Process schimm = null;
				System.out.println(OutputParsingUtils.getInfoLog(log));
				SchimmAlgorithm sa = new SchimmAlgorithm(log, th);
				schimm = sa.getFinalModel();

				System.out.println("File: " + f.getName());
				String fileName = f.getParent() + "\\" + f.getName().replace(dotxes, "");
				Mcrl2 mcrl = OutputParsingUtils.generateMCRL2FromBlockStrcut(schimm);
				fmcrl2 = OutputParsingUtils.generateMcrl2File(fileName, mcrl);
			} else {
				System.err.println(f + " NOT FOUND");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return fmcrl2;

	}

	public static String runmcrlcommand(String command) {
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows) {
			// builder.command("cmd.exe", "/c", "cd " + dir + " &&" + command);
			builder.command("cmd.exe", "/c", command);
		} else {
			// builder.command("sh", "-c", "cd " + dir + " &&" + command);
			builder.command("sh", "-c", command);
		}
		builder.redirectErrorStream(true);
		java.lang.Process p;
		try {
			p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String lastline = null;
			String currentline;
			while (true) {
				currentline = r.readLine();
				if (currentline == null)
					break;
				else
					System.out.println(currentline);
				lastline = currentline;
			}
			return lastline;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static void continueOrExit() {
		System.out.println("CONTINUE (Y/N) :");
		scan = new Scanner(System.in);
		String r = scan.nextLine();
		while (!r.equalsIgnoreCase("N") && !r.equalsIgnoreCase("Y")) {
			System.out.println("command not available, try again ... ");
			r = scan.nextLine();
		}
		if (r.equalsIgnoreCase("Y")) {
			return;
		} else
			System.exit(0);

	}

	private static long computeTimeSpans(long startTime, long endTime) {
		return endTime - startTime;
	}

	private static long getCurrentTime() {
		return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	}

}
