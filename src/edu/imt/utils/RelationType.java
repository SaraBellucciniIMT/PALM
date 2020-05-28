package edu.imt.utils;

public enum RelationType {
	
	
	SUCC(">"),
	LOOP2LEN("2L"),
	LOOP2TLEN("2L"),
	DIRECT("->"),
	PARALLEL("||"),
	NODIRECT("#"),
	MENDACIUS("_>"),
	INDIRECTDEP(">>"),
	REACHABLEDEP("RD");
	
	private final String rel;
	
	RelationType(String r){
		this.rel = r;
	}
	
	public String getRelationType() {
		return rel;
	}

}
