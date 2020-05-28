import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.imt.utils.OutputParsingUtils;
import edu.imt.utils.Terminal;

public class Main {

	public static void main(String[] args) {
		
		Terminal.terminal();
		
		/*Map<String,String> map = new HashMap<String,String>();
		File c = new File("C:/Users/sara/Downloads/customer.xes");
		
		map.put("Book Travel", "travel");
		map.put("Booking confirmed","confirmation");
		map.put("Pay Travel", "payment");
		map.put("Payment confirmation received", "payment_confirmation");
		
		OutputParsingUtils.generateCopyXesMessageAttribute(c, map);
		
		File ta = new File("C:/Users/sara/Downloads/travelagency.xes");
		map.put("Booking received", "travel");
		map.put("Confirm Booking","confirmation");
		map.put("Payment received", "payment");
		map.put("Order ticket", "order");
		
		OutputParsingUtils.generateCopyXesMessageAttribute(ta, map);
		
		File a = new File("C:/Users/sara/Downloads/airlane.xes");
		map.put("Ticket Order Received", "order");
		map.put("Confirm payment","payment_confirmation");
		
		OutputParsingUtils.generateCopyXesMessageAttribute(a, map);*/
		
		/*File half1 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/weekends104/weekends104.xes");
		File weekhald1 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/weekends104/half_weekends104.xes");
		EventLog e;
		
			e = InputParsingUtils.parseXes(half1);
			System.out.println("Trace= " +e.getTraceCardinality() + " Events= " + e.getEventCardinality());
			OutputParsingUtils.cutLogEvents(half1, weekhald1, 50);
			EventLog e1 = InputParsingUtils.parseXes(weekhald1);
			System.out.println("Trace= " +e1.getTraceCardinality() + " Events= " + e1.getEventCardinality());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		//prova 11 csv
			/*	List<List<String>> rows = new ArrayList<List<String>>();
				for (int i = 0; i < 200; i++) {
					rows.addAll(Arrays.asList(
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:17", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "r1", "8/11/2018 17:22", "8/11/2018 17:30"),
							Arrays.asList(String.valueOf(i), "s", "8/11/2018 17:32", "8/11/2018 17:40"),
							Arrays.asList(String.valueOf(i), "s1", "8/11/2018 17:41", "8/11/2018 17:50"),
							Arrays.asList(String.valueOf(i), "t", "8/11/2018 17:34", "8/11/2018 17:40"),
							Arrays.asList(String.valueOf(i), "t1", "8/11/2018 17:42", "8/11/2018 17:51"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:52", "8/11/2018 17:56"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:17", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "r1", "8/11/2018 17:22", "8/11/2018 17:30"),
							Arrays.asList(String.valueOf(i), "s", "8/11/2018 17:32", "8/11/2018 17:40"),
							Arrays.asList(String.valueOf(i), "s1", "8/11/2018 17:41", "8/11/2018 17:50"),
							Arrays.asList(String.valueOf(i), "t", "8/11/2018 17:34", "8/11/2018 17:43"),
							Arrays.asList(String.valueOf(i), "t1", "8/11/2018 17:44", "8/11/2018 17:51"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:52", "8/11/2018 17:56"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:17", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "r1", "8/11/2018 17:22", "8/11/2018 17:30"),
							Arrays.asList(String.valueOf(i), "t", "8/11/2018 17:32", "8/11/2018 17:40"),
							Arrays.asList(String.valueOf(i), "t1", "8/11/2018 17:41", "8/11/2018 17:50"),
							Arrays.asList(String.valueOf(i), "s", "8/11/2018 17:34", "8/11/2018 17:40"),
							Arrays.asList(String.valueOf(i), "s1", "8/11/2018 17:42", "8/11/2018 17:51"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:52", "8/11/2018 17:56"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:17", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "r1", "8/11/2018 17:22", "8/11/2018 17:30"),
							Arrays.asList(String.valueOf(i), "t", "8/11/2018 17:32", "8/11/2018 17:40"),
							Arrays.asList(String.valueOf(i), "t1", "8/11/2018 17:41", "8/11/2018 17:50"),
							Arrays.asList(String.valueOf(i), "s", "8/11/2018 17:34", "8/11/2018 17:43"),
							Arrays.asList(String.valueOf(i), "s1", "8/11/2018 17:44", "8/11/2018 17:51"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:52", "8/11/2018 17:56"),
														
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "n", "8/11/2018 17:17", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "n1", "8/11/2018 17:22", "8/11/2018 17:30"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:52", "8/11/2018 17:56"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "e", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "o", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:17", "8/11/2018 17:20"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:17", "8/11/2018 17:20"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "p", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "p1", "8/11/2018 17:16", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "q", "8/11/2018 17:14", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "q1", "8/11/2018 17:17", "8/11/2018 17:21"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "p", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "p1", "8/11/2018 17:16", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "q", "8/11/2018 17:14", "8/11/2018 17:18"),
							Arrays.asList(String.valueOf(i), "q1", "8/11/2018 17:19", "8/11/2018 17:21"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "q", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "q1", "8/11/2018 17:16", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "p", "8/11/2018 17:14", "8/11/2018 17:18"),
							Arrays.asList(String.valueOf(i), "p1", "8/11/2018 17:19", "8/11/2018 17:21"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "q", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "q1", "8/11/2018 17:16", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "p", "8/11/2018 17:14", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "p1", "8/11/2018 17:17", "8/11/2018 17:21"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30")
							
							));
				}
				File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova11/prova11.csv");
				OutputParsingUtils.generateCSVFile(filepatest, rows, true);
	*/
		//prova 10 csv
		/*List<List<String>> rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:17", "8/11/2018 17:20"),
					Arrays.asList(String.valueOf(i), "n", "8/11/2018 17:18", "8/11/2018 17:21"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "n", "8/11/2018 17:17", "8/11/2018 17:20"),
					Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:18", "8/11/2018 17:21"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "e", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "o", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:13", "8/11/2018 17:15"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "p", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "q", "8/11/2018 17:14", "8/11/2018 17:21"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "q", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "p", "8/11/2018 17:14", "8/11/2018 17:21"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30")
					));
		}
		File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova10/prova10.csv");
		OutputParsingUtils.generateCSVFile(filepatest, rows, true);*/
		//prova 9 csv
			/*	List<List<String>> rows = new ArrayList<List<String>>();
				for (int i = 0; i < 200; i++) {
					rows.addAll(Arrays.asList(
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "e", "8/11/2018 17:11", "8/11/2018 17:14"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:15", "8/11/2018 17:17"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "e", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:11", "8/11/2018 17:14"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:15", "8/11/2018 17:17"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:11", "8/11/2018 17:14"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:15", "8/11/2018 17:17"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:11", "8/11/2018 17:14"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:15", "8/11/2018 17:17")							
							
							));
				}
				File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova9/prova9.csv");
				OutputParsingUtils.generateCSVFile(filepatest, rows, true);*/
		
		//prova 8 csv
		/*List<List<String>> rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:17", "8/11/2018 17:20"),
					Arrays.asList(String.valueOf(i), "r1", "8/11/2018 17:22", "8/11/2018 17:30"),
					Arrays.asList(String.valueOf(i), "s", "8/11/2018 17:32", "8/11/2018 17:40"),
					Arrays.asList(String.valueOf(i), "s1", "8/11/2018 17:42", "8/11/2018 17:50"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:52", "8/11/2018 17:56"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:17", "8/11/2018 17:20"),
					Arrays.asList(String.valueOf(i), "r1", "8/11/2018 17:22", "8/11/2018 17:30"),
					Arrays.asList(String.valueOf(i), "t", "8/11/2018 17:32", "8/11/2018 17:40"),
					Arrays.asList(String.valueOf(i), "t1", "8/11/2018 17:42", "8/11/2018 17:50"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:52", "8/11/2018 17:56"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "n", "8/11/2018 17:17", "8/11/2018 17:20"),
					Arrays.asList(String.valueOf(i), "n1", "8/11/2018 17:22", "8/11/2018 17:30"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:52", "8/11/2018 17:56"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "e", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "o", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:17", "8/11/2018 17:20"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:17", "8/11/2018 17:20"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "p", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "p1", "8/11/2018 17:17", "8/11/2018 17:20"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "q", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "q1", "8/11/2018 17:17", "8/11/2018 17:20"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30")
					
					));
		}
		File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova8/prova8.csv");
		OutputParsingUtils.generateCSVFile(filepatest, rows, true);*/
		//prova 7 csv
				/*List<List<String>> rows = new ArrayList<List<String>>();
				for (int i = 0; i < 200; i++) {
					rows.addAll(Arrays.asList(
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "r", "8/11/2018 17:17", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "l", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "n", "8/11/2018 17:17", "8/11/2018 17:20"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "e", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "o", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:13", "8/11/2018 17:15"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "p", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30"),
							
							Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
							Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
							Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
							Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
							Arrays.asList(String.valueOf(i), "q", "8/11/2018 17:13", "8/11/2018 17:15"),
							Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:22", "8/11/2018 17:30")
							
							));
				}
				File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova7/prova7.csv");
				OutputParsingUtils.generateCSVFile(filepatest, rows, true);*/
		//prova 6 csv
		/*List<List<String>> rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:13", "8/11/2018 17:15"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "e", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:13", "8/11/2018 17:15"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:13", "8/11/2018 17:15"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "i", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:13", "8/11/2018 17:15")
					
					
					));
		}
		File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova6/prova6.csv");
		OutputParsingUtils.generateCSVFile(filepatest, rows, true);*/
		//PROVA 5 CSV ----
		/*List<List<String>> rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(Arrays.asList(String.valueOf(i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:08", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:13", "8/11/2018 17:15"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:06", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:16", "8/11/2018 17:17"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:20", "8/11/2018 17:22"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:23", "8/11/2018 17:25"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:56", "8/11/2018 17:57"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:19", "8/11/2018 17:22"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:23", "8/11/2018 17:25"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:26", "8/11/2018 17:27"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:19", "8/11/2018 17:22"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:23", "8/11/2018 17:25"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:28", "8/11/2018 17:32"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:33", "8/11/2018 17:35"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:49", "8/11/2018 17:52"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:53", "8/11/2018 17:55"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 18:19", "8/11/2018 18:22"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 18:23", "8/11/2018 18:25"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 18:26", "8/11/2018 18:27"),
					
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:19", "8/11/2018 17:22"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:23", "8/11/2018 17:25"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:28", "8/11/2018 17:32"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:33", "8/11/2018 17:35"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:49", "8/11/2018 17:52"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 17:53", "8/11/2018 17:55"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 18:19", "8/11/2018 18:22"),
					Arrays.asList(String.valueOf(i), "h", "8/11/2018 18:23", "8/11/2018 18:25"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 18:26", "8/11/2018 18:27")
					));
		}
		File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova5/prova5.csv");
		OutputParsingUtils.generateCSVFile(filepatest, rows, true);*/
		//---PORVA 4 CSV -----
		/*List<List<String>> rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(Arrays.asList(String.valueOf(i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:06", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:16", "8/11/2018 17:17"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:07", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:10", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:13", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:19", "8/11/2018 17:22"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:23", "8/11/2018 17:25"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:26", "8/11/2018 17:27")
					));
		}
		File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova4/prova4.csv");
		OutputParsingUtils.generateCSVFile(filepatest, rows, true);
		rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(Arrays.asList(String.valueOf(i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f",  "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:17"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:12"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:15"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:22"),
					Arrays.asList(String.valueOf(i), "g", "8/11/2018 17:25"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:27")
					));
		}
		File fbenchtest = new File("C:/Users/sara/Downloads/tkde_benchmark/csv files/prova4.csv");
		OutputParsingUtils.generateCSVFile(fbenchtest, rows, false);*/
		//--------------------
		//---PROVA 3 CSV ---
		/*List<List<String>> rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(Arrays.asList(String.valueOf(i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:08", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:04", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:08", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:04", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:08", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:04", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:08", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:04", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:08", "8/11/2018 17:09")
					));
		}
		File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova3/prova3.csv");
		OutputParsingUtils.generateCSVFile(filepatest, rows, true);
		rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(Arrays.asList(String.valueOf(i), "a","8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "m","8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "f", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "m", "8/11/2018 17:09")
					));
		}
		File fbenchtest = new File("C:/Users/sara/Downloads/tkde_benchmark/csv files/prova3.csv");
		OutputParsingUtils.generateCSVFile(fbenchtest, rows, false);*/
		 
		//------------
		//----PROVA 2 CSV ---
		/*List<List<String>> rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(Arrays.asList(String.valueOf(i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:04", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:08", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:00", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:03", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:04", "8/11/2018 17:07"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:08", "8/11/2018 17:09")));
		}
		File filepatest = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/prova2/prova2.csv");
		OutputParsingUtils.generateCSVFile(filepatest, rows, true);
		rows = new ArrayList<List<String>>();
		for (int i = 0; i < 200; i++) {
			rows.addAll(Arrays.asList(Arrays.asList(String.valueOf(i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:06"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:09"),
					Arrays.asList(String.valueOf(++i), "a", "8/11/2018 17:02"),
					Arrays.asList(String.valueOf(i), "c", "8/11/2018 17:05"),
					Arrays.asList(String.valueOf(i), "b", "8/11/2018 17:08"),
					Arrays.asList(String.valueOf(i), "d", "8/11/2018 17:09")));
		}
		File fbenchtest = new File("C:/Users/sara/Downloads/tkde_benchmark/csv files/prova2.csv");
		OutputParsingUtils.generateCSVFile(fbenchtest, rows, false);*/
		
		
		
		/*File half = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/labour/labour.xes");
		File weekhald = new File("C:/Users/sara/Downloads/tkde_benchmark/log/labours.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half, weekhald);
		File half8 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/weekends/weekends.xes");
		File weekhaldf8 = new File("C:/Users/sara/Downloads/tkde_benchmark/log/weekendss.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half8, weekhaldf8);*/
		/*
		File half1 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/labour102/labour102.xes");
		File weekhald1 = new File("C:/Users/sara/Downloads/tkde_benchmark/log/labour102.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half1, weekhald1);
		File half2 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/labour104/labour104.xes");
		File weekhald2 = new File("C:/Users/sara/Downloads/tkde_benchmark/log/labour104.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half2, weekhald2);
		File half3 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/labour110/labour110.xes");
		File weekhald3 = new File("C:/Users/sara/Downloads/tkde_benchmark/log/labour110.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half3, weekhald3);
		File half4 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/labour102/labour102.xes");
		File weekhald4 = new File("C:/Users/sara/Downloads/tkde_benchmark/log/labour102.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half4, weekhald4);
		File half6 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/weekends102/weekends102.xes");
		File weekhald6 = new File("C:/Users/sara/Downloads/tkde_benchmark/log/weekends102s.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half6, weekhald6);
		File half7 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/weekends104/weekends104.xes");
		File weekhaldf7 = new File("C:/Users/sara/Downloads/tkde_benchmark/log/weekends104s.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half7, weekhaldf7);
		File half8 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/weekends110/weekends110.xes");
		File weekhaldf8 = new File("C:/Users/sara/Downloads/tkde_benchmark/log/weekends110s.xes");
		OutputParsingUtils.generateCopyXesFileWithotStartTime(half8, weekhaldf8);
		*/
		
	
	
		/*Process a = new Process(new Activity("a"));
		Process b = new Process(new Activity("b"));
		Process c = new Process(new Activity("c"));
		Process d = new Process(new Activity("d"));
		Process f = new Process(new Activity("f"));
		Process g =new Process( new Activity("g"));
		Process h = new Process(new Activity("h"));
		Process i = new Process(new Activity("i"));
		
		Process seq =new Process(new Process[] {a,b,c},Operator.SEQUENCE);
		Process seq1 =new Process(new Process[] {d,f,g},Operator.SEQUENCE);
		Process seq2 =new Process(new Process[] {h,i},Operator.SEQUENCE);
		Process par = new Process(new Process[] {seq,seq1,seq2}, Operator.PARALLEL);
		Process lop = new Process(new Process[] {par}, Operator.LOOP);
		Mcrl2 mcrl = new Mcrl2();
		ApplyParout.applyparout(lop, mcrl);
		*/
		/*File p6 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/50_labour/50labour.xes");
		File p6s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/50labours.xes");
		
		File p7 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/50_labour102/halflabour102.xes");
		File p7s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/halflabour102s.xes");
		
		File p8 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/50_labour104/halflabour104.xes");
		File p8s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/halflabour104s.xes");
		
		File p9 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/50_labour110/halflabour110.xes");
		File p9s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/halflabour110s.xes");
		
		File p10 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/75_labour102/75labour102.xes");
		File p10s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/75labour102s.xes");
		
		File p11 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/75_labour104/75labour104.xes");
		File p11s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/75labour104s.xes");
		
		File p12 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/75_labour110/75labour110.xes");
		File p12s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/75labour110s.xes");
		
		File p13 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/75_weekends104/75weekends104.xes");
		File p13s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/75weekends104s.xes");
		
		File p14 = new File("C:/Users/sara/eclipse-workspace/PALM-r/Test/half_weekends104/halfweekends104.xes");
		File p14s = new File("C:/Users/sara/Downloads/tkde_benchmark/log/halfweekends104s.xes");
		
	
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p6, p6s);
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p7, p7s);
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p8, p8s);
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p9, p9s);
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p10, p10s);
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p11, p11s);
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p12, p12s);
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p13, p13s);
		OutputParsingUtils.generateCopyXesFileWithotStartTime(p14, p14s);*/
		
	}

}
