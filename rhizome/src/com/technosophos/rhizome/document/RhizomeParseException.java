package com.technosophos.rhizome.document;

/**
 * This exception is thrown if there is an error converting an XML document
 * to a Rhizome document.
 * @author mbutcher
 *
 */
public class RhizomeParseException 
		extends com.technosophos.rhizome.RhizomeException {
	
	private static final long serialVersionUID = 1L;
	
	public RhizomeParseException(){
		super("Parsing failed for unknown reason.");
	}
	
	public RhizomeParseException(String str){
		super(str);
	}
	
	public RhizomeParseException(String str, Throwable thr){
		super(str, thr);
	}
}
