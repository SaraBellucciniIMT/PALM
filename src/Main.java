import java.io.File;
import java.io.FileNotFoundException;

import edu.imt.Applyparout.Loop;
import edu.imt.inputData.Event;
import edu.imt.inputData.EventLog;
import edu.imt.specification.structure.BlockStructure;
import edu.imt.specification.structure.Operator;
import edu.imt.utils.InputParsingUtils;
import edu.imt.utils.OutputParsingUtils;
import edu.imt.utils.Terminal;

/*
 * @author S. Belluccini
 * @version 1
 */
public class Main {

	public static void main(String[] args) {
	/*EventLog log = null;
		try {
			log = InputParsingUtils.parseXes(new File("C:/Users/Sara/eclipse-workspace/PALM/logs/test/synthetic/log2/log2.xes"));
			Terminal.miningSpecification(log, 90);
		} catch (Exception e) {
			e = new FileNotFoundException();
		}*/
		Terminal.terminal();	
	}

}
