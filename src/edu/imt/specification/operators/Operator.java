package edu.imt.specification.operators;

/*
 * Minum operator defining the behaviour of each specification
 */

public enum Operator {
	    
		// ;
	    SEQUENCE('.'),
	    
	    //|| 
	    PARALLEL('|'),
		
		//+
		CHOICE('+'),
		
		//*
		LOOP('*');
	
	
		Operator(char op) {
	        this.op = op;
	    }

	    private final char op;

	    public char getOperator() {
	        return op;
	    }
	    
	    public String getStringOp() {
	    	return String.valueOf(op);
	    }
}
