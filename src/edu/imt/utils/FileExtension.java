/**
 * 
 */
package edu.imt.utils;

/**
 * This class describe all the file extensions used in this tool
 * @author Sara
 *
 */
enum FileExtension {
	XES("xes"),
	MCRL2("mcrl2"),
    LPS("lps"),
	LTS("lts"),
	SG("sg"),
	FSM("fsm"),
	PBES("pbes"),
	MCF("mcf");

    private final String FINAL_EXTENSION;
	
    private FileExtension(String s) {
    	this.FINAL_EXTENSION = s;
    }
    
    public String getExtension() {
    	return FINAL_EXTENSION;
    }
    
}
