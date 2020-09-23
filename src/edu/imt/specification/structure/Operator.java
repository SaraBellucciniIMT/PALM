package edu.imt.specification.structure;

/*
 * Minum operator defining the behaviour of each specification
 */

public enum Operator {
	    
		// ;
	    SEQUENCE("."),
	    
	    //|| 
	    PARALLEL("||"),
		
		//+
		CHOICE("+"),
		
		//*
		LOOP("*");
	
	
		Operator(String op) {
	        this.op = op;
	    }

	    private final String op;

	    public String getOperator() {
	        return op;
	    }
	    
}
