package edu.imt.utils;

public class Utils {

	private static int i=0;
	private static String name = "P";
	
	public static int getInt() {
		return i++;
	}
	
	public static String getName() {
		return name + getInt();
	}
	
	public static String removeblankspaceAndDots(String s) {
		s = s.replaceAll(" ", "");
		s = s.replaceAll("[\\.\\(\\)$\\-&,]+", "");
		return s;
	}
}
