# PALM
PALM, Process ALgebraic specification Mining, is a tool to mine mCRL2 formal specification from logs of events in the XES format.
The tool is a command-line Java jar file.

## Prerequisite
It needs [mCRL2](https://www.mcrl2.org/web/user_manual/download.html) installed.

## Using
To run the jar file on the terminal use the command: java -jar palm_v1.jar.
On running the jar file a first menu is opened, insert in the terminal the number corresponding to the action that you want to execute (1, 2 or 3):

**1 -> Mining too-independent specification**, to generate from an evnetlog a mCRL2 specification. As a first step the user is asked to insert the path of the eventlog to analyze, that is the absolute path to the file ".xes"(C:\Users\myuser\...\logtoanalyze.xes) and then to set the loop threshold, i.e. every loop with frequency < threshold will be unrolled, else loop with frequency > threshold are written has recursive processes.  This process generates the mCRL2 specification and at the end opens a new menu that gives the possibility to:

	 -1 -> Measure fitness, measure the fitness value of the specification against the log from which was generated.
	 -2 -> Check equivalence, given the absolute path of another model with exstension .mcrl2 or .lps, checks if an equivalence among the following one exists: strong bisimilarity, branching-bisimilarity, divergence-preserving branching bisimilarity, weak-bisimilarity, simulation, strong trace and weak trace equivalence.
	 -3 -> Check formula, write the mu-calculus formula that you want to analyze in the terminal and the answer will be true if it is satisfied, false otherwise.
	 -4 -> Check deadlock freedom, it checks if the specification has deadlock or not.


**2 -> Mining aggregation specification**, to mine the overall specification of a set of logs. Insert the path to the folder that contains all the logs that you want analyze together (C:\Myfolder\...\aggregatiolfolder), a mcrl2 specification is generated. The same menu as above will be open.

**3 -> Repeat tests**, to replicate part of the validation results. Automatically replicate part of the validation phase of the paper, the results are saved in a csv file called "results.csv"

All the files generated by the tool are saved in a folder called "result".





 Example logs can found in the zip file called "logs".
