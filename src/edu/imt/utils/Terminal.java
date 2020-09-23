package edu.imt.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import edu.imt.algorithm.SchimmAlgorithm;
import edu.imt.inputData.EventLog;
import edu.imt.specification.MCRL2;
import edu.imt.specification.structure.BlockStructure;

/**
 * This class Terminal defines all the functionalities that can be executed from
 * the terminal of the application
 * 
 * @author S. Belluccini
 */
public class Terminal {

	private static long startTime;
	private static long endTime;
	private static final String FILE_EXP = "results.csv";
	private static File dir = new File("result");
	protected static String path;
	private static Scanner scan = new Scanner(System.in);
	private final static List<String> equivalences = Lists.newArrayList("bisim", "branching-bisim", "dpbranching-bisim",
			"weak-bisim", "dpweak-bisim", "sim", "trace", "weak-trace");

	public static void terminal() {
		if (!dir.exists())
			dir.mkdir();
		path = dir.toURI().getPath();
		while (true) {
			System.out.println("Press: \n" + "1->Mining tool-indipendet specification \n"
					+ "2->Mining aggregation specification \n" + "3->Repeat experiments\n" + "4->Exit");
			int i = scan.nextInt();
			switch (i) {
			case 1: {
				File f = scanFile("Insert path to EventLog file: ");
				EventLog log = null;
				try {
					log = InputParsingUtils.parseXes(f, new HashMap<>());
				} catch (Exception e) {
					e = new FileNotFoundException();
				}
				System.out.println("Set threshold for loop disclosure (natual number from 0 to 100): ");
				int th = scan.nextInt();
				String mcrl2File = miningSpecification(log, th);
				otherMenu(log.getEventMap(), f, mcrl2File);
				continue;
			}
			case 2: {
				File directory = scanFile("Insert directory logs to aggregate:");
				System.out.println("Set threshold for loop disclosure (natual number from 0 to 100): ");
				int th = scan.nextInt();
				Pair<Map<String, String>, String> mapandfile = aggregation(directory, th);
				otherMenu(mapandfile.getKey(), directory, mapandfile.getValue());
				continue;
			}
			case 3: {
				List<String> titleXes = Lists.newArrayList("Model Name", "Discovery algorithm", "Loop frequency",
						"Mining Time (s)", "MC Fitness", "Equivalence");
				File[] arrDirectories = new File[] { new File("logs/test/synthetic/log1"),
						new File("logs/test/synthetic/log2"), new File("logs/test/synthetic/log3"),
						new File("logs\test\real\rlog3"), new File("logs\test\real\rlog4"),
						new File("logs\test\real\rlog5"), new File("logs\test\real\rlog6") };
				FileWriter fw;
				try {
					fw = new FileWriter(FILE_EXP);
					fw.append(String.join(",", titleXes));
					fw.append("\n");
					for (File f : arrDirectories)
						repeatExperiments(f, fw);
					fw.flush();
					fw.close();
				} catch (IOException e) {
					e = new IOException();
				}
			}
				break;
			case 4:
				return;
			default:
				System.out.println("Wrong insertion, try again...");
			}
		}
	}

	private static void otherMenu(Map<String, String> map, File f, String pathMCRL2) {
		EventLog log = new EventLog();
		try {
			if (f.isDirectory()) {
				for (File file : f.listFiles()) {
					if (FilenameUtils.isExtension(file.getName(), FileExtension.XES.getExtension()))
						log.addAll(InputParsingUtils.parseXes(file, map).getTrace());
				}
			} else
				log = InputParsingUtils.parseXes(f, map);
			String lps = frommcrl22lps(FilenameUtils.getName(pathMCRL2));
			while (true) {
				System.out.println("Press: \n" + "1-> Measure fitness\n" + "2-> Check equivalence\n"
						+ "3-> Check formula\n" + "4-> Check deadlock freedom \n" + "5-> Go back");
				int i = scan.nextInt();
				switch (i) {
				case 1: {
					measureFitness(log, lps);
					continue;
				}
				case 2: {
					String namef2 = scanFile("Insert path second model").getAbsolutePath();
					checkEquivalence(lps, namef2);
					continue;
				}
				case 5: {
					removeFile(new File(lps));
					return;
				}
				default:
					System.out.println("Wrong insertion, try again...");
				}
			}
		} catch (Exception e) {
			new FileNotFoundException();
		}
	}

	/**
	 * 
	 * @param f
	 * @param th
	 * @return
	 */
	private static String miningSpecification(EventLog e, int th) {

		measureStartTime();
		SchimmAlgorithm sa = new SchimmAlgorithm(e);
		BlockStructure schimm = sa.getFinalModel();
		MCRL2 mcrl2 = OutputParsingUtils.generateMCRL2FromBlockStrcut(schimm, th);

		String nameFile = OutputParsingUtils.generateMcrl2File(mcrl2);
		measureEndTime();

		System.out.println(nameFile + " generated");
		return nameFile;
	}

	private static void measureStartTime() {
		startTime = getCurrentTime();
		System.out.println("START TIME: " + java.time.LocalTime.now());
	}

	private static void measureEndTime() {
		endTime = getCurrentTime();
		System.out.println("Execution time" + computeTimeSpans(startTime, endTime) + "s");
	}

	/**
	 * Measure fitness of the log used to generate the mcrl2 file
	 * 
	 * @return
	 */

	private static double measureFitness(EventLog log, String lps) {
		double fitness = QualityMeasure.measureFitness(log, lps);
		System.out.println(fitness);
		return fitness;
	}

	/**
	 * Given two mcrl2 specification (file .mcrl2) its compare them from the
	 * strongest to the weakest equivalence relation in mcrl2 and return the
	 * strongest one if exist
	 * 
	 * @return
	 */
	private static String checkEquivalence(String namef1, String namef2) {
		String f1mcrllps = "";
		String f2mcrllps = "";
		List<String> filelist = new ArrayList<String>();
		if (FilenameUtils.isExtension(namef1, FileExtension.MCRL2.getExtension())) {
			f1mcrllps = frommcrl22lps(namef1);
			filelist.add(f1mcrllps);
			namef1 = fromlps2lts(f1mcrllps);
		} else if (FilenameUtils.isExtension(namef1, FileExtension.LPS.getExtension()))
			namef1 = fromlps2lts(namef1);
		filelist.add(namef1);
		if (FilenameUtils.isExtension(namef2, FileExtension.MCRL2.getExtension())) {
			f2mcrllps = frommcrl22lps(namef2);
			filelist.add(f2mcrllps);
			namef2 = fromlps2lts(f2mcrllps);
		} else if (FilenameUtils.isExtension(namef2, FileExtension.LPS.getExtension()))
			namef2 = fromlps2lts(namef2);
		filelist.add(namef2);

		for (int i = 0; i < equivalences.size(); i++) {
			String result = ltscompare(namef1, namef2, equivalences.get(i));
			if (Boolean.valueOf(result))
				return equivalences.get(i);
		}
		return null;
	}

	private static void repeatExperiments(File f, FileWriter fw) {
		int[] threshold = new int[] { 90, 50, 0 };
		// Each lps file correspond to a mcrl2 specification with a different loop
		// frequency, 90lps -50lps -0lps
		String[] lpsfileP = new String[3];
		String mcrl2;
		List<List<String>> arr = new ArrayList<List<String>>();
		EventLog log = null;
		for (File file : f.listFiles()) {
			if (FilenameUtils.isExtension(file.getName(), FileExtension.XES.getExtension())) {
				try {
					log = InputParsingUtils.parseXes(file, new HashMap<>());
				} catch (Exception e) {
					e = new FileNotFoundException();
				}
				for (int i = 0; i < threshold.length; i++) {
					mcrl2 = miningSpecification(log, threshold[i]);
					long miningTime = computeTimeSpans(startTime, endTime);
					lpsfileP[i] = frommcrl22lps(mcrl2);
					double fitness = measureFitness(log, lpsfileP[i]);
					// Model name, Discovery algorithm, Loop frequency, Mining time, MC Fitness,
					// Equivalence
					arr.add(Lists.newArrayList(file.getName(), "PALM", String.valueOf(threshold[i]),
							String.valueOf(miningTime), String.valueOf(fitness), "-"));
					removeFile(new File(mcrl2));
				}
			}
		}
		for (File file : f.listFiles()) {
			if (FilenameUtils.isExtension(file.getName(), FileExtension.SG.getExtension())) {
				String[] namemodelealg = FilenameUtils.getBaseName(file.getName()).split("_");
				mcrl2 = fromSGtoMCRL2(file, log.getEventMap());
				String lps = frommcrl22lps(mcrl2);
				double fitness = measureFitness(log, lps);
				// double fitness =0;
				for (String slps : lpsfileP) {
					String equivalence = checkEquivalence(lps, slps);
					// Model name, Discovery algorithm, Loop frequency, Mining time, MC Fitness,
					// Equivalence
					arr.add(Lists.newArrayList(namemodelealg[0], namemodelealg[1], "-", "-", String.valueOf(fitness),
							equivalence));
				}
				removeFile(new File(mcrl2));
			}
		}
		for (String s : lpsfileP)
			removeFile(new File(s));
		try {
			for (List<String> l : arr) {
				fw.append(String.join(",", l));
				fw.append("\n");
			}
		} catch (IOException e) {
			e = new IOException();
		}

	}

	/**
	 * For each xes file in this directory is generated a mcrl2 specification with
	 * the given threshold, at the end all the specification are merged together
	 * 
	 * @see OutputParsingUtils#mergeMCRL2(List)
	 * @param directory where the file xes are
	 * @param th        threshold used for all mining
	 */
	private static Pair<Map<String, String>, String> aggregation(File directory, int th) {
		Map<String, String> eventMap = new HashMap<>();
		List<MCRL2> mcrl2list = new ArrayList<>();
		for (File f : directory.listFiles()) {
			if (FilenameUtils.isExtension(f.getName(), FileExtension.XES.getExtension())) {
				try {
					EventLog log = InputParsingUtils.parseXes(f, eventMap);
					if (eventMap.isEmpty())
						eventMap = log.getEventMap();
					else
						eventMap.putAll(log.getEventMap());
					SchimmAlgorithm sa = new SchimmAlgorithm(log);
					MCRL2 mcrl = OutputParsingUtils.generateMCRL2FromBlockStrcut(sa.getFinalModel(), th);
					mcrl.addMessage(InputParsingUtils.parseXesMessage(f, log.getEventMap()));
					mcrl2list.add(mcrl);
				} catch (Exception e) {
					e = new FileNotFoundException(f.toString());
				}
			}
		}
		return Pair.of(eventMap, OutputParsingUtils.mergeMCRL2(mcrl2list));
	}

	/**
	 * Scan the input file
	 * 
	 * @param stringtoprint string to be printed over the terminal
	 * @return the file scanned
	 */
	private static File scanFile(String stringToPrint) {
		System.out.println(stringToPrint);
		scan = new Scanner(System.in);
		File filename1 = new File(scan.nextLine());
		while (!filename1.exists()) {
			System.out.println(filename1 + " has not been found, try again: ");
			filename1 = new File(scan.nextLine());
		}

		return filename1;
	}

	private static String fromlps2lts(String filename) {
		String lps2lts = "lps2lts -v -rjitty " + filename + " "
				+ filename.replace(FileExtension.LPS.getExtension(), FileExtension.LTS.getExtension());
		runmcrlcommand(lps2lts);
		return filename.replace(FileExtension.LPS.getExtension(), FileExtension.LTS.getExtension());
	}

	private static String frommcrl22lps(String filename) {
		String mcrl22lps = "mcrl22lps -v -lstack -rjitty " + filename + " "
				+ filename.replace(FileExtension.MCRL2.getExtension(), FileExtension.LPS.getExtension());
		runmcrlcommand(mcrl22lps);
		return FilenameUtils.getBaseName(filename) + "." + FileExtension.LPS.getExtension();
	}

	protected static void removeFile(File... f) {
		for (int i = 0; i < f.length; i++) {
			f[i].delete();
		}
	}

	private static String ltscompare(String f1, String f2, String equivalence) {
		String ltscompare = "ltscompare -e" + equivalence + " " + f1 + " " + f2;
		return runmcrlcommand(ltscompare);
	}

	protected static String proveFormula(String f1, String formula) {
		String command = "";
		if (FilenameUtils.isExtension(f1, FileExtension.LPS.getExtension())) {
			command = "lps2pbes -v -c -m -f " + formula + " " + f1 + " " + FilenameUtils.getBaseName(f1) + "."
					+ FileExtension.PBES.getExtension();
		} else if (FilenameUtils.isExtension(f1, FileExtension.LTS.toString())) {
			command = "lts2pbes -v -f " + formula + " " + f1 + " "
					+ f1.replace(FileExtension.LTS.getExtension(), FileExtension.PBES.getExtension());
		}
		runmcrlcommand(command);
		String pbes2bool = "pbes2bool " + FilenameUtils.getBaseName(f1) + "." + FileExtension.PBES.getExtension();
		String result = runmcrlcommand(pbes2bool);
		removeFile(new File(f1.replace(FileExtension.LTS.getExtension(), FileExtension.PBES.getExtension())));
		return result;
	}

	private static String fromSGtoMCRL2(File f, Map<String, String> map) {
		CoverabilityGraph cg = InputParsingUtils.parseSGfile(f);
		return OutputParsingUtils.generatemCRL2fromCG(cg, f, map);
	}

	private static String runmcrlcommand(String command) {
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows) {
			builder.command("cmd.exe", "/c", "cd " + dir + "&&" + command);
		} else {
			builder.command("sh", "-c", "cd " + dir + "&&" + command);
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

	/**
	 * If the input is Y than true, if is N false
	 * 
	 * @param r the string
	 * @return true if the user want to exist, F otherwise
	 */
	private static boolean isExit(String r) {
		while (!r.equalsIgnoreCase("N") && !r.equalsIgnoreCase("Y")) {
			System.out.println("command not available, try again ... ");
			r = scan.nextLine();
		}
		if (r.equalsIgnoreCase("Y"))
			return false;
		else
			return true;
	}

	private static long computeTimeSpans(long startTime, long endTime) {
		return endTime - startTime;
	}

	private static long getCurrentTime() {
		return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	}

}
